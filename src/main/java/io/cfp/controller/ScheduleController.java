/*
 * Copyright (c) 2016 BreizhCamp
 * [http://breizhcamp.org]
 *
 * This file is part of CFP.io.
 *
 * CFP.io is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.cfp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cfp.domain.exception.CospeakerNotFoundException;
import io.cfp.dto.FullCalendar;
import io.cfp.dto.user.Schedule;
import io.cfp.dto.user.UserProfil;
import io.cfp.entity.Event;
import io.cfp.entity.Role;
import io.cfp.entity.Talk;
import io.cfp.entity.User;
import io.cfp.mapper.ProposalMapper;
import io.cfp.mapper.RoomMapper;
import io.cfp.model.Proposal;
import io.cfp.model.queries.ProposalQuery;
import io.cfp.multitenant.TenantId;
import io.cfp.repository.TalkRepo;
import io.cfp.repository.UserRepo;
import io.cfp.service.TalkUserService;
import io.cfp.service.email.EmailingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * Created by Nicolas on 30/01/2016.
 */

@RestController("ScheduleController_v0")
@RequestMapping(value = { "/v0/schedule" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ScheduleController {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleController.class);

    private final TalkUserService talkUserService;

    private final ProposalMapper proposalMapper;

    private final TalkRepo talks;

    private final RoomMapper roomMapper;

    private final UserRepo users;

    private final EmailingService emailingService;

    @Autowired
    public ScheduleController(TalkUserService talkUserService,
                              ProposalMapper proposalMapper,
                              TalkRepo talks,
                              RoomMapper roomMapper,
                              UserRepo users,
                              EmailingService emailingService) {
        this.proposalMapper = proposalMapper;
        this.talkUserService = talkUserService;
        this.talks = talks;
        this.roomMapper = roomMapper;
        this.users = users;
        this.emailingService = emailingService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Schedule> getSchedule() {
        final List<Talk> all = talks.findByEventIdAndStatesFetch(Event.current(), Collections.singleton(Talk.State.ACCEPTED));

        return all.stream().
            filter(t -> t.getDate() != null)
            .map(t -> {
                Schedule schedule = new Schedule(t.getId(), t.getName(), t.getDescription());

                // speakers
                String speakers = t.getUser().getFirstname() + " " + t.getUser().getLastname();
                if (isNotEmpty(t.getCospeakers())) {
                    speakers += ", " + t.getCospeakers().stream().map(c -> c.getFirstname() + " " + c.getLastname()).collect(Collectors.joining(", "));
                }
                schedule.setSpeakers(speakers);

                schedule.setEventType(t.getTrack().getLibelle());
                schedule.setFormat(t.getFormat().getName());

                schedule.setEventStart(DateTimeFormatter.ISO_INSTANT.format(t.getDate().toInstant()));
                schedule.setEventEnd(DateTimeFormatter.ISO_INSTANT.format(t.getDate().toInstant().plus(t.getDuree(), ChronoUnit.MINUTES)));
                schedule.setVenue(t.getRoom() != null ? t.getRoom().getName() : "TBD");
                schedule.setVenueId(t.getRoom() != null ? String.valueOf(t.getRoom().getId()) : null);
                schedule.setMedia(t.getVideo() != null ? t.getVideo() : t.getSlides());

                final Map<String, Object> p = schedule.getAdditionalProperties();
                if (t.getVideo() != null) {
                    p.put("video", t.getVideo());
                }
                if (t.getSlides() != null) {
                    p.put("slides", t.getSlides());
                }

                return schedule;
            }).collect(toList());
    }

    @RequestMapping(value = "fullcalendar/unscheduled", method = RequestMethod.GET)
    public List<FullCalendar.Event> getUnscheduledEvents() {
        final List<Talk> all = talks.findByEventIdAndStatesFetch(Event.current(), Collections.singleton(Talk.State.ACCEPTED));
        return all.stream()
            .filter(t -> t.getRoom() == null || t.getDate() == null)
            .map(FullCalendar.Event::new)
            .collect(Collectors.toList());
    }

    @GetMapping("fullcalendar")
    public FullCalendar getFullCalendar(@TenantId String eventId) {
        final List<io.cfp.model.Room> roomList = roomMapper.findByEvent(eventId);
        final List<Talk> all = talks.findByEventIdAndStatesFetch(eventId, Collections.singleton(Talk.State.ACCEPTED));
        return new FullCalendar(all, roomList);
    }

    @PutMapping("fullcalendar")
    @Secured(Role.ADMIN)
    public void getFullCalendar(@RequestBody FullCalendar calendar,
                                @TenantId String eventId) {
        calendar.getEvents().forEach(
            e -> {
                talkUserService.updateConfirmedTalk(
                    Integer.parseInt(e.getId()),
                    LocalDateTime.parse(e.getStart(), DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                    e.getResourceId(),
                    eventId);
            });
    }


    @PutMapping("/sessions/{talkId}")
    @Secured(Role.ADMIN)
    public void scheduleTalk(@PathVariable int talkId,
                             @RequestBody FullCalendar.Event e,
                             @TenantId String eventId) throws CospeakerNotFoundException {
        // sanity check
        if (!String.valueOf(talkId).equals(e.getId())) throw new IllegalArgumentException("wrong event ID "+e.getId());

        talkUserService.updateConfirmedTalk(
            Integer.parseInt(e.getId()),
            LocalDateTime.parse(e.getStart(), DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            e.getResourceId(),
            eventId);
    }


    /**
     * Get all All talks.
     *
     * @return Confirmed talks in "LikeBox" format.
     */
    @GetMapping("/confirmed")
    @Secured(Role.ADMIN)
    public List<Schedule> getConfirmedScheduleList() {
        List<Proposal> proposals = proposalMapper.findAll(new ProposalQuery().addStates(Proposal.State.CONFIRMED));
        return getSchedules(proposals);
    }

    /**
     * Get all ACCEPTED talks'speakers .
     *
     * @return Speakers Set
     */
    @RequestMapping(value = "/speakers", method = RequestMethod.GET)
    public List<UserProfil> getSpeakerList() {
        boolean isAdmin = User.getCurrent().hasRole(Role.ADMIN) ;
        return users.findUserWithAcceptedProposal(Event.current()).stream()
            .map(u -> new UserProfil(u, isAdmin))
            .collect(Collectors.toList());
    }

    /**
     * Get all ACCEPTED talks.
     *
     * @return Accepted talks in "LikeBox" format.
     */
    @GetMapping("/accepted")
    @Secured(Role.ADMIN)
    public List<Schedule> getScheduleList() {
        List<Proposal> proposals = proposalMapper.findAll(new ProposalQuery().addStates(Proposal.State.ACCEPTED));
        return getSchedules(proposals);
    }

    private List<Schedule> getSchedules(List<Proposal> proposals) {
        return proposals.stream().map(p -> {
            Schedule schedule = new Schedule(p.getId(), p.getName(), p.getDescription());

            // speakers
            schedule.setSpeakers(p.buildSpeakersList());

            // event_type
            schedule.setEventType(p.getTrackLabel());

            return schedule;
        }).collect(toList());
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = {"multipart/form-data", "multipart/mixed"})
    @Secured(Role.ADMIN)
    public ResponseEntity uploadSchedule(@RequestParam("file") MultipartFile file,
                                         @TenantId String eventId) throws IOException {

        final Schedule[] schedules = new ObjectMapper().readValue(file.getBytes(), Schedule[].class);
        for (Schedule talk : schedules) {
            talkUserService.updateConfirmedTalk(talk.getId(), LocalDateTime.parse(talk.getEventStart(), DateTimeFormatter.ISO_OFFSET_DATE_TIME), talk.getVenueId(), eventId);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }




    /**
     * Notify by mails scheduling result.
     * @param filter , can be "accepted" or "refused", default is "all"
     *
     */
    @RequestMapping(value = "/notification", method = RequestMethod.POST)
    @Secured(Role.ADMIN)
    public void notifyScheduling(@RequestParam(defaultValue = "all", name = "filter") String filter) {
        switch (filter) {
           case  "refused" :
               List<Talk> refused = talks.findByEventIdAndStatesFetch(Event.current(), Collections.singleton(Talk.State.REFUSED));
               sendRefusedMailsWithTempo(refused);
               break;
            case "accepted"  :
               List<Talk> accepted = talks.findByEventIdAndStatesFetch(Event.current(), Collections.singleton(Talk.State.ACCEPTED));
               sendAcceptedMailsWithTempo(accepted);
               break;
            case "all"  :
               sendAcceptedMailsWithTempo(talks.findByEventIdAndStatesFetch(Event.current(), Collections.singleton(Talk.State.ACCEPTED)));
               sendRefusedMailsWithTempo(talks.findByEventIdAndStatesFetch(Event.current(), Collections.singleton(Talk.State.REFUSED)));
               break;

        }
    }

    /**
     * To help Google Compute Engine we wait 2 s between 2 mails.
     * @param accepted
     */
    private void sendAcceptedMailsWithTempo(List<Talk> accepted) {
        accepted.forEach(t -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        LOG.warn("Thread Interrupted Exception", e);
                    }
                    emailingService.sendSelectionned(t, t.getUser().getLocale());
                }
        );
    }

    private void sendRefusedMailsWithTempo(List<Talk> refused) {
        refused.forEach(t -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                LOG.warn("Thread Interrupted Exception", e);
            }
            emailingService.sendNotSelectionned(t, t.getUser().getLocale());
        });
    }


}
