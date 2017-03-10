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
import io.cfp.dto.TalkUser;
import io.cfp.dto.user.Schedule;
import io.cfp.dto.user.UserProfil;
import io.cfp.entity.Event;
import io.cfp.entity.Role;
import io.cfp.entity.Room;
import io.cfp.entity.Talk;
import io.cfp.repository.RoomRepo;
import io.cfp.repository.TalkRepo;
import io.cfp.service.TalkUserService;
import io.cfp.service.admin.user.AdminUserService;
import io.cfp.service.email.EmailingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Created by Nicolas on 30/01/2016.
 */

@RestController
@RequestMapping(value = { "/v0/schedule", "/api/schedule" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ScheduleController {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleController.class);

    private final TalkUserService talkUserService;

    private final TalkRepo talks;

    private final RoomRepo rooms;

    private final EmailingService emailingService;

    private final AdminUserService adminUserService;

    @Autowired
    public ScheduleController(TalkUserService talkUserService, TalkRepo talks, RoomRepo rooms, EmailingService emailingService, AdminUserService adminUserService) {
        super();
        this.talkUserService = talkUserService;
        this.talks = talks;
        this.rooms = rooms;
        this.emailingService = emailingService;
        this.adminUserService = adminUserService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Schedule> getSchedule() {
        final List<Talk> all = talks.findByEventIdAndStatesFetch(Event.current(), Collections.singleton(Talk.State.ACCEPTED));

        return all.stream().
            filter(t -> t.getDate() != null)
            .map(t -> {
                Schedule schedule = new Schedule(t.getId(), t.getName(), t.getDescription());

                // speakers
                String spreakers = t.getUser().getFirstname() + " " + t.getUser().getLastname();
                if (t.getCospeakers() != null) {
                    spreakers += ", " + t.getCospeakers().stream().map(c -> c.getFirstname() + " " + c.getLastname()).collect(Collectors.joining(", "));
                }
                schedule.setSpeakers(spreakers);

                schedule.setEventType(t.getTrack().getLibelle());
                schedule.setFormat(t.getFormat().getName());

                schedule.setEventStart(DateTimeFormatter.ISO_INSTANT.format(t.getDate().toInstant()));
                schedule.setEventEnd(DateTimeFormatter.ISO_INSTANT.format(t.getDate().toInstant().plus(t.getDuree(), ChronoUnit.MINUTES)));
                schedule.setVenue(t.getRoom() != null ? t.getRoom().getName() : "TBD");
                schedule.setVenueId(t.getRoom() != null ? String.valueOf(t.getRoom().getId()) : null);

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

    @RequestMapping(value = "fullcalendar", method = RequestMethod.GET)
    public FullCalendar getFullCalendar() {
        final List<Room> roomList = rooms.findByEventId(Event.current());
        final List<Talk> all = talks.findByEventIdAndStatesFetch(Event.current(), Collections.singleton(Talk.State.ACCEPTED));
        return new FullCalendar(all, roomList);
    }

    @RequestMapping(value = "fullcalendar", method = RequestMethod.PUT)
    public void getFullCalendar(FullCalendar calendar) {
        calendar.getEvents().forEach(
            e -> {
                talkUserService.updateConfirmedTalk(
                    Integer.parseInt(e.getId()),
                    LocalDateTime.parse(e.getStart(), DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                    e.getResourceId());
            });
    }


    @RequestMapping(value= "/sessions/{talkId}", method= RequestMethod.PUT)
    @Secured(Role.ADMIN)
    @ResponseBody
    public void scheduleTalk(@PathVariable int talkId, @RequestBody FullCalendar.Event e) throws CospeakerNotFoundException, ParseException {
        // sanity check
        if (!String.valueOf(talkId).equals(e.getId())) throw new IllegalArgumentException("wrong event ID "+e.getId());

        talkUserService.updateConfirmedTalk(
            Integer.parseInt(e.getId()),
            LocalDateTime.parse(e.getStart(), DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            e.getResourceId());
    }


    /**
     * Get all All talks.
     *
     * @return Confirmed talks in "LikeBox" format.
     */
    @RequestMapping(value = "/confirmed", method = RequestMethod.GET)
    @Secured(Role.ADMIN)
    public List<Schedule> getConfirmedScheduleList() {
        List<TalkUser> talkUserList = talkUserService.findAll(Talk.State.CONFIRMED);
        return getSchedules(talkUserList);
    }

    /**
     * Get all ACCEPTED talks'speakers .
     *
     * @return Speakers Set
     */
    @RequestMapping(value = "/speakers", method = RequestMethod.GET)
    public Set<UserProfil> getSpeakerList() {
        final List<Talk> all = talks.findByEventIdAndStatesFetch(Event.current(), Collections.singleton(Talk.State.ACCEPTED));
        // FIXME we miss cospeakers with this
        boolean isAdmin = adminUserService.getCurrentUser() != null;
        return all.stream().map(t -> new UserProfil(t.getUser(), isAdmin)).collect(toSet());
    }

    /**
     * Get all ACCEPTED talks.
     *
     * @return Accepted talks in "LikeBox" format.
     */
    @RequestMapping(value = "/accepted", method = RequestMethod.GET)
    @Secured(Role.ADMIN)
    public List<Schedule> getScheduleList() {
        List<TalkUser> talkUserList = talkUserService.findAll(Talk.State.ACCEPTED);
        return getSchedules(talkUserList);
    }

    private List<Schedule> getSchedules(List<TalkUser> talkUserList) {
        return talkUserList.stream().map(t -> {
            Schedule schedule = new Schedule(t.getId(), t.getName(), t.getDescription());

            // speakers
            String spreakers = t.getSpeaker().getFirstname() + " " + t.getSpeaker().getLastname();
            if (t.getCospeakers() != null) {
                spreakers += ", " + t.getCospeakers().stream().map(c -> c.getFirstname() + " " + c.getLastname()).collect(Collectors.joining(", "));
            }
            schedule.setSpeakers(spreakers);

            // event_type
            schedule.setEventType(t.getTrackLabel());

            return schedule;
        }).collect(toList());
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = {"multipart/form-data", "multipart/mixed"})
    @Secured(Role.ADMIN)
    public ResponseEntity uploadSchedule(@RequestParam("file") MultipartFile file) throws IOException {

        final Schedule[] schedules = new ObjectMapper().readValue(file.getBytes(), Schedule[].class);
        for (Schedule talk : schedules) {
            talkUserService.updateConfirmedTalk(talk.getId(), LocalDateTime.parse(talk.getEventStart(), DateTimeFormatter.ISO_OFFSET_DATE_TIME), talk.getVenueId());
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
