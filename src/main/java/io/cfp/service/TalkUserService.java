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

package io.cfp.service;

import io.cfp.dto.TalkUser;
import io.cfp.entity.Event;
import io.cfp.entity.Talk;
import io.cfp.repository.RoomRepo;
import io.cfp.repository.TalkRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing talks by the user
 */
@Service
@Transactional
public class TalkUserService {

    @Autowired
    private TalkRepo talkRepo;

    @Autowired
    private RoomRepo rooms;

    /**
     * Retrieve all talks for a User
     *
     * @param states
     *            List of states the talk must be
     * @return List of talks
     */
    public List<TalkUser> findAll(Talk.State... states) {
        return talkRepo.findByEventIdAndStatesFetch(Event.current(), Arrays.asList(states))
            .stream().map(t -> new TalkUser(t))
            .collect(Collectors.toList());
    }

    /**
     * Retrieve all talks for a User
     *
     * @param userId
     *            Id of the user
     * @param states
     *            List of states the talk must be
     * @return List of talks
     */
    public List<TalkUser> findAll(int userId, Talk.State... states) {
        return talkRepo.findByEventIdAndUserIdAndStateIn(Event.current(), userId, Arrays.asList(states))
            .stream().map(t -> new TalkUser(t))
            .collect(Collectors.toList());
    }

    /**
     * Retrieve all talks for a User
     *
     * @param userId
     *            Id of the user to retrieve cospeaker talks
     * @param states
     *            List of states the talk must be
     * @return List of talks
     */
    public List<TalkUser> findAllCospeakerTalks(int userId, Talk.State... states) {
        return talkRepo.findByEventIdAndCospeakerIdAndStateIn(Event.current(), userId, Arrays.asList(states))
            .stream().map(t -> new TalkUser(t))
            .collect(Collectors.toList());
    }

    /**
     * Count number of talks the users has submitted (drafts included)
     *
     * @param userId
     *            Id of the user
     * @return Number of talks
     */
    public int count(int userId) {
        return talkRepo.countByEventIdAndUserId(Event.current(), userId);
    }

    public TalkUser getOneCospeakerTalk(int userId, int talkId) {
        return new TalkUser(
            talkRepo.findByIdAndEventIdAndCospeakers(talkId, Event.current(), userId)
        );
    }

    /**
     * @param talkId
     * @param eventStart
     * @return updated talk
     */
    @Deprecated
    public TalkUser updateConfirmedTalk(int talkId, LocalDateTime eventStart, String room) {

        Date eventDate = Date.from(eventStart.atZone(ZoneId.systemDefault()).toInstant());
        String hour = eventStart.format(DateTimeFormatter.ofPattern("HH:mm"));

        Talk talk = talkRepo.findByIdAndEventId(talkId, Event.current());
        talk.setState(Talk.State.ACCEPTED);
        talk.setDate(eventDate);
        talk.setHeure(hour);
        if (room != null) {
            talk.setRoom(rooms.getOne(Integer.parseInt(room)));
        }
        talk = talkRepo.saveAndFlush(talk);

        return new TalkUser(talk);
    }

}
