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

import io.cfp.dto.TalkUser;
import io.cfp.dto.user.Schedule;
import io.cfp.dto.user.UserProfil;
import io.cfp.entity.Talk;
import io.cfp.service.TalkUserService;
import io.cfp.service.email.EmailingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Created by Nicolas on 30/01/2016.
 */

@RestController
@RequestMapping(value = "api/admin", produces = "application/json; charset=utf-8")
public class ScheduleController {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleController.class);

    private final TalkUserService talkUserService;

    private final EmailingService emailingService;

    @Autowired
    public ScheduleController(TalkUserService talkUserService, EmailingService emailingService) {
        super();
        this.talkUserService = talkUserService;
        this.emailingService = emailingService;
    }

    /**
     * Get all All talks.
     *
     * @return Confirmed talks in "LikeBox" format.
     */
    @RequestMapping(value = "/scheduledtalks/confirmed", method = RequestMethod.GET)
    public List<Schedule> getConfirmedScheduleList() {
        LOG.info("GET /scheduledtalks/confirmed");

        List<TalkUser> talkUserList = talkUserService.findAll(Talk.State.CONFIRMED);

        return getSchedules(talkUserList);
    }

    /**
     * Get all ACCEPTED talks'speakers .
     *
     * @return Speakers Set
     */
    @RequestMapping(value = "/scheduledtalks/accepted/speakers", method = RequestMethod.GET)
    public Set<UserProfil> getSpeakerList() {
        LOG.info("GET /scheduledtalks/accepted/speakers");

        List<TalkUser> talkUserList = talkUserService.findAll(Talk.State.ACCEPTED);

        return talkUserList.stream().map(t -> t.getSpeaker()).collect(toSet());
    }

    /**
     * Get all ACCEPTED talks.
     *
     * @return Accepted talks in "LikeBox" format.
     */
    @RequestMapping(value = "/scheduledtalks/accepted", method = RequestMethod.GET)
    public List<Schedule> getScheduleList() {
        LOG.info("GET /scheduledtalks/accepted");

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

    /**
     * Update talks with schedule data.
     *
     * @param scheduleList Accepted talks in "LikeBox" format.
     */
    @RequestMapping(value = "/scheduledtalks", method = RequestMethod.PUT)
    public Map<String, List<TalkUser>> putScheduleList(@RequestBody List<Schedule> scheduleList, @RequestParam(defaultValue = "false", required = false) boolean sendMail) {
        LOG.info("PUT /scheduledtalks");

        scheduleList.forEach(s -> {
            LocalDateTime dateEventStart = LocalDateTime.parse(s.getEventStart(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            // update database
            TalkUser talkUser = talkUserService.updateConfirmedTalk(s.getId(), dateEventStart);
        });

        Map<String, List<TalkUser>> result = new HashMap<>();

        List<TalkUser> refused = talkUserService.findAll(Talk.State.REFUSED);
        List<TalkUser> accepted = talkUserService.findAll(Talk.State.ACCEPTED);

        result.put("confirmed", talkUserService.findAll(Talk.State.CONFIRMED));
        result.put("draft", talkUserService.findAll(Talk.State.DRAFT));
        result.put("accepted", accepted);
        result.put("refused", refused);
        result.put("backup", talkUserService.findAll(Talk.State.BACKUP));

        if (sendMail) {
            sendMailsWithTempo(accepted, refused);
        }

        return result;
    }


    /**
     * Notify by mails scheduling result.
     */
    @RequestMapping(value = "/scheduledtalks/notification", method = RequestMethod.POST)
    public Map<String, List<TalkUser>> notifyScheduling() {
        LOG.info("POST /scheduledtalks/notification");


        Map<String, List<TalkUser>> result = new HashMap<>();

        List<TalkUser> refused = talkUserService.findAll(Talk.State.REFUSED);
        List<TalkUser> accepted = talkUserService.findAll(Talk.State.ACCEPTED);

        result.put("confirmed", talkUserService.findAll(Talk.State.CONFIRMED));
        result.put("draft", talkUserService.findAll(Talk.State.DRAFT));
        result.put("accepted", accepted);
        result.put("refused", refused);
        result.put("backup", talkUserService.findAll(Talk.State.BACKUP));

        sendMailsWithTempo(accepted, refused);

        return result;
    }

    /**
     * To help Google Compute Engine we wait 2 s between 2 mails.
     */
    private void sendMailsWithTempo(List<TalkUser> accepted, List<TalkUser> refused) {
        accepted.forEach(t -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        LOG.warn("Thread Interrupted Exception", e);
                    }
                    emailingService.sendSelectionned(t, Locale.FRENCH);
                }
        );
        refused.forEach(t -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                LOG.warn("Thread Interrupted Exception", e);
            }
            emailingService.sendNotSelectionned(t, Locale.FRENCH);
        });
    }


}
