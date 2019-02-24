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

package io.cfp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cfp.domain.exception.CospeakerNotFoundException;
import io.cfp.dto.user.Schedule;
import io.cfp.mapper.FormatMapper;
import io.cfp.mapper.ProposalMapper;
import io.cfp.mapper.RoomMapper;
import io.cfp.mapper.ThemeMapper;
import io.cfp.model.*;
import io.cfp.model.queries.ProposalQuery;
import io.cfp.multitenant.TenantId;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@RestController
@RequestMapping(value = { "/v1/schedule", "/api/schedule" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ScheduleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleController.class);

    @Autowired
    private ProposalMapper proposals;

    @Autowired
    private FormatMapper formats;

    @Autowired
    private ThemeMapper themes;

    @Autowired
    private RoomMapper rooms;

    @Autowired
    private EmailingService emailingService;

    @GetMapping
    public List<Schedule> getSchedule(@TenantId String eventId) {
        final List<Proposal> all = proposals.findAll(new ProposalQuery().setEventId(eventId).setStates(Arrays.asList(Proposal.State.ACCEPTED, Proposal.State.PRESENT)));

        return all.stream().
            filter(t -> t.getSchedule() != null)
            .map(t -> {
                Schedule schedule = new Schedule(t.getId(), t.getName(), t.getDescription());

                // speakers
                String speakers = t.getSpeaker().getFirstname() + " " + t.getSpeaker().getLastname();
                if (isNotEmpty(t.getCospeakers())) {
                    speakers += ", " + t.getCospeakers().stream().map(c -> c.getFirstname() + " " + c.getLastname()).collect(Collectors.joining(", "));
                }
                schedule.setSpeakers(speakers);

                schedule.setEventType(t.getTrackLabel());
                Format format = formats.findById(t.getFormat());

                schedule.setFormat(format.getName());

                schedule.setEventStart(DateTimeFormatter.ISO_INSTANT.format(t.getSchedule().toInstant()));
                schedule.setEventEnd(DateTimeFormatter.ISO_INSTANT.format(t.getSchedule().toInstant().plus(format.getDuration(), ChronoUnit.MINUTES)));

                Room room = rooms.findById(t.getRoomId());

                schedule.setVenue(room != null ? room.getName() : "TBD");
                schedule.setVenueId(room != null ? String.valueOf(room.getId()) : null);
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

    @GetMapping(value = "fullcalendar/unscheduled")
    public List<FullCalendar.Event> getUnscheduledEvents(@TenantId String eventId) {
        LOGGER.info("Get unscheduled Proposals");
        final List<Proposal> all = proposals.findAll(new ProposalQuery().setEventId(eventId).setStates(Arrays.asList(Proposal.State.ACCEPTED, Proposal.State.PRESENT)));
        LOGGER.info("Found {} accepted Proposals", all.size());

        final List<Theme> allThemes = themes.findByEvent(eventId);
        final List<Format> allFormats = formats.findByEvent(eventId);

        final List<FullCalendar.Event> events = all.stream()
            .filter(t -> t.getRoomId() == null || t.getSchedule() == null)
            .map(t -> {

                Format format = allFormats.stream()
                    .filter(f -> f.getId() == t.getFormat())
                    .findFirst()
                    .orElse(new Format());

                Theme theme = allThemes.stream()
                    .filter(th -> th.getId() == t.getTrackId())
                    .findFirst()
                    .orElse(new Theme());
                return new FullCalendar.Event(t, format, theme);
            })
            .collect(Collectors.toList());
        LOGGER.info("Found {} unscheduled Proposals", events.size());
        return events;
    }

    @GetMapping(value = "fullcalendar")
    public FullCalendar getFullCalendar(@TenantId String eventId) {
        LOGGER.info("Get schedule Calendar");
        final List<Room> roomList = rooms.findByEvent(eventId);
        final List<Proposal> all = proposals.findAll(new ProposalQuery().setEventId(eventId).setStates(Arrays.asList(Proposal.State.ACCEPTED, Proposal.State.PRESENT)));
        final List<Theme> allThemes = themes.findByEvent(eventId);
        final List<Format> allFormats = formats.findByEvent(eventId);
        return new FullCalendar(all, roomList, allFormats, allThemes);
    }

    @PutMapping(value = "fullcalendar")
    public void getFullCalendar(@RequestBody FullCalendar calendar,
                                @TenantId String eventId) {
        LOGGER.info("Update all events of Calendar");
        calendar.getEvents().forEach(
            e -> {

                LocalDateTime eventStart = LocalDateTime.parse(e.getStart(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                Date eventDate = Date.from(eventStart.atZone(ZoneId.systemDefault()).toInstant());

                String hour = eventStart.format(DateTimeFormatter.ofPattern("HH:mm"));

                Proposal talk = proposals.findById(Integer.parseInt(e.getId()), eventId);
                talk.setState(Proposal.State.ACCEPTED);
                talk.setSchedule(eventDate);
                talk.setScheduleHour(hour);
                if (e.getResourceId() != null) {
                    talk.setRoomId(Integer.parseInt(e.getResourceId()));
                }
                proposals.updateSchedule(talk);
            });
    }


    @PutMapping(value= "/sessions/{talkId}")
    @Secured(Role.ADMIN)
    public void scheduleTalk(@PathVariable int talkId,
                             @RequestBody FullCalendar.Event e,
                             @TenantId String eventId) throws CospeakerNotFoundException {
        // sanity check
        if (!String.valueOf(talkId).equals(e.getId())) throw new IllegalArgumentException("wrong event ID "+e.getId());



        LocalDateTime eventStart = LocalDateTime.parse(e.getStart(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        Date eventDate = Date.from(eventStart.atZone(ZoneId.systemDefault()).toInstant());

        String hour = eventStart.format(DateTimeFormatter.ofPattern("HH:mm"));

        Proposal talk = proposals.findById(talkId, eventId);
        talk.setState(Proposal.State.ACCEPTED);
        talk.setSchedule(eventDate);
        talk.setScheduleHour(hour);
        if (e.getResourceId() != null) {
            talk.setRoomId(Integer.parseInt(e.getResourceId()));
        }
        LOGGER.info("Schedule Proposal {} at {} on {}", talk.getId(), talk.getScheduleHour(), talk.getSchedule());
        proposals.updateSchedule(talk);

    }


    /**
     * Get all All talks.
     *
     * @return Confirmed talks in "LikeBox" format.
     */
    @GetMapping(value = "/confirmed")
    @Secured(Role.ADMIN)
    public List<Schedule> getConfirmedScheduleList() {
        LOGGER.info("Get confirmed scheduled Proposals");
        List<Proposal> talkUserList = proposals.findAll(new ProposalQuery().setStates(Arrays.asList(Proposal.State.CONFIRMED)));
        LOGGER.info("Found {} confirmed scheduled Proposals", talkUserList.size());
        return getSchedules(talkUserList);
    }


    /**
     * Get all ACCEPTED talks.
     *
     * @return Accepted talks in "LikeBox" format.
     */
    @GetMapping(value = "/accepted")
    @Secured(Role.ADMIN)
    public List<Schedule> getScheduleList() {
        LOGGER.info("Get accepted scheduled Proposals");
        List<Proposal> talkUserList = proposals.findAll(new ProposalQuery().setStates(Arrays.asList(Proposal.State.ACCEPTED, Proposal.State.PRESENT)));
        LOGGER.info("Found {} accepted scheduled Proposals", talkUserList.size());
        return getSchedules(talkUserList);
    }

    private List<Schedule> getSchedules(List<Proposal> talkUserList) {
        return talkUserList.stream().map(t -> {
            Schedule schedule = new Schedule(t.getId(), t.getName(), t.getDescription());

            // speakers
            String speakers = t.getSpeaker().getFirstname() + " " + t.getSpeaker().getLastname();
            if (isNotEmpty(t.getCospeakers())) {
                speakers += ", " + t.getCospeakers().stream().map(c -> c.getFirstname() + " " + c.getLastname()).collect(Collectors.joining(", "));
            }
            schedule.setSpeakers(speakers);

            // event_type
            schedule.setEventType(t.getTrackLabel());

            return schedule;
        }).collect(toList());
    }

    @PostMapping(consumes = {"multipart/form-data", "multipart/mixed"})
    @Secured(Role.ADMIN)
    public ResponseEntity uploadSchedule(@RequestParam("file") MultipartFile file,
                                         @TenantId String eventId) throws IOException {

        final Schedule[] schedules = new ObjectMapper().readValue(file.getBytes(), Schedule[].class);
        for (Schedule schedule : schedules) {


            LocalDateTime eventStart = LocalDateTime.parse(schedule.getEventStart(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            Date eventDate = Date.from(eventStart.atZone(ZoneId.systemDefault()).toInstant());

            String hour = eventStart.format(DateTimeFormatter.ofPattern("HH:mm"));

            Proposal talk = proposals.findById(schedule.getId(), eventId);
            talk.setState(Proposal.State.ACCEPTED);
            talk.setSchedule(eventDate);
            talk.setScheduleHour(hour);
            if (schedule.getVenueId() != null) {
                talk.setRoomId(Integer.parseInt(schedule.getVenueId()));
            }
            proposals.updateSchedule(talk);

        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }




    /**
     * Notify by mails scheduling result.
     * @param filter , can be "accepted" or "refused", default is "all"
     *
     */
    @PostMapping(value = "/notification")
    @Secured(Role.ADMIN)
    public void notifyScheduling(@RequestParam(defaultValue = "all", name = "filter") String filter,
                                 @RequestBody List<Integer> ids,
                                 @TenantId String eventId) {
        switch (filter) {
            case  "refused" :
                List<Proposal> refused = proposals.findAll(new ProposalQuery().setEventId(eventId).setStates(Arrays.asList(Proposal.State.REFUSED)));
                LOGGER.debug("Found {} refused talks", refused.size());
                if (ids != null && !ids.isEmpty()) {
                    LOGGER.info("Filter notification for talks {}", ids);

                    refused = refused.stream()
                        .filter(p -> ids.contains(p.getId()))
                        .collect(toList());
                }
                LOGGER.info("Send notifications for {} refused talks", refused.size());
                sendRefusedMailsWithTempo(refused);
                break;
            case "accepted"  :
                List<Proposal> accepted = proposals.findAll(new ProposalQuery().setEventId(eventId).setStates(Arrays.asList(Proposal.State.ACCEPTED)));
                LOGGER.debug("Found {} accepted talks", accepted.size());
                if (ids != null && !ids.isEmpty()) {
                    LOGGER.info("Filter notification for talks {}", ids);
                    accepted = accepted.stream()
                        .filter(p -> ids.contains(p.getId()))
                        .collect(toList());
                }
                LOGGER.info("Send notifications for {} accepted talks", accepted.size());
                sendAcceptedMailsWithTempo(accepted);
                break;
            case "all"  :
                sendAcceptedMailsWithTempo(proposals.findAll(new ProposalQuery().setEventId(eventId).setStates(Arrays.asList(Proposal.State.ACCEPTED))));
                sendRefusedMailsWithTempo(proposals.findAll(new ProposalQuery().setEventId(eventId).setStates(Arrays.asList(Proposal.State.REFUSED))));
                break;

        }
    }

    /**
     * To help Google Compute Engine we wait 2 s between 2 mails.
     * @param accepted
     */
    private void sendAcceptedMailsWithTempo(List<Proposal> accepted) {
        accepted.forEach(t -> {
                LOGGER.info("Envoi du mail accepté pour {}:{}", t.getId(), t.getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    LOGGER.warn("Thread Interrupted Exception", e);
                }
                emailingService.sendSelectionned(t, t.getSpeaker().getLocale());
            }
        );
    }

    private void sendRefusedMailsWithTempo(List<Proposal> refused) {
        refused.forEach(t -> {
            LOGGER.info("Envoi de mail refusé pour {}:{}", t.getId(), t.getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                LOGGER.warn("Thread Interrupted Exception", e);
            }
            emailingService.sendNotSelectionned(t, t.getSpeaker().getLocale());
        });
    }


}
