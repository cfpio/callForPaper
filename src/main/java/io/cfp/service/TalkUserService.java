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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.cfp.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.cfp.domain.exception.CospeakerNotFoundException;
import io.cfp.dto.Speaker;
import io.cfp.dto.TalkUser;
import io.cfp.dto.TrackDto;
import io.cfp.dto.user.CospeakerProfil;
import io.cfp.entity.Talk;
import io.cfp.entity.TalkFormat;
import io.cfp.entity.Track;
import io.cfp.entity.User;
import io.cfp.repository.TalkFormatRepo;
import io.cfp.repository.TalkRepo;
import io.cfp.repository.TrackRepo;
import io.cfp.repository.UserRepo;
import ma.glasnost.orika.MapperFacade;

/**
 * Service for managing talks by the user
 */
@Service
@Transactional
public class TalkUserService {

    @Autowired
    private TalkRepo talkRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TalkFormatRepo talkFormatRepo;

    @Autowired
    private TrackRepo trackRepo;

    @Autowired
    private MapperFacade mapper;

    /**
     * Retrieve all talks for a User
     *
     * @param states
     *            List of states the talk must be
     * @return List of talks
     */
    public List<TalkUser> findAll(Talk.State... states) {
        List<Talk> talks = talkRepo.findByEventIdAndStatesFetch(Event.current(), Arrays.asList(states));
        return mapper.mapAsList(talks, TalkUser.class);
    }

    public  List<Speaker> findAllSpeaker(Talk.State... states) {
        List<Talk> talks = talkRepo.findByEventIdAndStatesFetch(Event.current(), Arrays.asList(states));
        Set<Speaker> speakers = new HashSet<>();
        for(Talk talk : talks){
            if(talk.getUser() != null) {
                Speaker speaker = mapper.map(talk.getUser(), Speaker.class);
                if (speaker != null && !speakers.contains(speaker)) {
                    speakers.add(speaker);
                }
            }

            if(talk.getCospeakers() != null && !talk.getCospeakers().isEmpty()) {
                List<Speaker> coSpeaker = mapper.mapAsList(talk.getCospeakers(), Speaker.class);
                if (coSpeaker != null ) {
                    speakers.addAll(coSpeaker);
                }
            }
        }

        return new ArrayList<Speaker>(speakers);
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
        List<Talk> talks = talkRepo.findByEventIdAndUserIdAndStateIn(Event.current(), userId, Arrays.asList(states));
        return mapper.mapAsList(talks, TalkUser.class);
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
        List<Talk> talks = talkRepo.findByEventIdAndCospeakerIdAndStateIn(Event.current(), userId, Arrays.asList(states));
        return mapper.mapAsList(talks, TalkUser.class);
    }

    // /**
    // * Retrieve the talk list whom the user is cospeaker
    // * @param userId Id of the user to retrieve cospeaker talks
    // * @return List of talks
    // */
    // public List<TalkUser> getCospeakerTalks(int userId) {
    // List<Talk> talks = talkRepo.findByCospeakers(userId);
    // return mapper.mapAsList(talks, TalkUser.class);
    // }

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

    /**
     * Retrieve a talk which belong to the user
     *
     * @param userId
     *            Id of the user to retrieve
     * @param talkId
     *            Id of the talk to retrieve
     * @return Talk or null if not found
     */
    public TalkUser getOne(int userId, int talkId) {
        Talk talk = talkRepo.findByIdAndEventIdAndUserId(talkId, Event.current(), userId);
        return mapper.map(talk, TalkUser.class);
    }

    public TalkUser getOneCospeakerTalk(int userId, int talkId) {
        Talk talk = talkRepo.findByIdAndEventIdAndCospeakers(talkId, Event.current(), userId);
        return mapper.map(talk, TalkUser.class);
    }

    /**
     * Add a submitted talk
     *
     * @param userId
     *            user who submitted the talk
     * @param talkUser
     *            Talk to add
     * @return Talk added
     */
    public TalkUser submitTalk(int userId, TalkUser talkUser) throws CospeakerNotFoundException {
        return newTalk(userId, talkUser, Talk.State.CONFIRMED);
    }

    /**
     * @param talkId
     * @param eventStart
     * @return updated talk
     */
    public TalkUser updateConfirmedTalk(int talkId, LocalDateTime eventStart) {

        Date eventDate = Date.from(eventStart.atZone(ZoneId.systemDefault()).toInstant());
        String hour = eventStart.format(DateTimeFormatter.ofPattern("HH:mm"));

        Talk talk = talkRepo.findByIdAndEventId(talkId, Event.current());
        talk.setState(Talk.State.ACCEPTED);
        talk.setDate(eventDate);
        talk.setHeure(hour);
        talk = talkRepo.saveAndFlush(talk);

        return mapper.map(talk, TalkUser.class);
    }

    /**
     * Convert a draft into a submitted talk
     *
     * @param userId
     *            Id of user who submitted the talk
     * @param talkUser
     *            Talk to submit
     * @return Talk submitted or null if not exists
     */
    public TalkUser submitDraftToTalk(int userId, TalkUser talkUser) throws CospeakerNotFoundException {
        return editTalk(userId, talkUser, Talk.State.CONFIRMED);
    }

    /**
     * Add a new draft talk
     *
     * @param userId
     *            User who submitted the talk
     * @param talkUser
     *            Talk to save
     * @return talk saved
     */
    public TalkUser addDraft(int userId, TalkUser talkUser) throws CospeakerNotFoundException {
        return newTalk(userId, talkUser, Talk.State.DRAFT);
    }

    /**
     * Save an update draft
     *
     * @param userId
     *            User who submitted the talk
     * @param talkUser
     *            Draft to update
     * @return Draft updated or null if doesn't exists
     */
    public TalkUser editDraft(int userId, TalkUser talkUser) throws CospeakerNotFoundException {
        return editTalk(userId, talkUser, Talk.State.DRAFT);
    }

    /**
     * Delete a draft talk
     *
     * @param userId
     *            Id of the user who submit the talk
     * @param talkId
     *            Id of the draft to delete
     * @return Talk deleted or null if inexistant
     */
    public TalkUser deleteDraft(int userId, int talkId) {
        Talk talk = talkRepo.findByIdAndEventIdAndUserId(talkId, Event.current(), userId);

        if (talk.getState() != Talk.State.DRAFT)
            return null;
        TalkUser deleted = mapper.map(talk, TalkUser.class);
        talkRepo.delete(talk);
        return deleted;
    }

    /**
     * Get all talk formats
     *
     * @return List of talk formats
     */
    public List<TalkFormat> getTalkFormat() {
        return talkFormatRepo.findByEventId(Event.current());
    }

    /**
     * Get all tracks
     *
     * @return List of talk tracks
     */
    public List<TrackDto> getTracks() {
        List<Track> tracks = trackRepo.findByEventId(Event.current());
        return mapper.mapAsList(tracks, TrackDto.class);
    }

    /**
     * Add a new talk into the database
     *
     * @param userId
     *            User who submit the talk
     * @param talkUser
     *            Talk to add
     * @param state
     *            New state of the talk
     * @return Talk added
     */
    private TalkUser newTalk(int userId, TalkUser talkUser, Talk.State state) throws CospeakerNotFoundException {
        Talk talk = mapper.map(talkUser, Talk.class);

        talk.setAdded(new Date());
        talk.setUser(userRepo.getOne(userId));
        talk.setState(state);
        setCoSpeaker(talkUser, talk);

        Talk save = talkRepo.save(talk);
        talkRepo.flush();
        talkUser.setId(save.getId());
        return talkUser;
    }

    /**
     * Update an existing draft talk
     *
     * @param userId
     *            Id of the user who submit the talk
     * @param talkUser
     *            Talk to update
     * @param newState
     *            New state of the talk
     * @return Talk updated or null if not existing
     */
    private TalkUser editTalk(int userId, TalkUser talkUser, Talk.State newState) throws CospeakerNotFoundException {
        Talk talk = talkRepo.findByIdAndEventIdAndUserId(talkUser.getId(), Event.current(), userId);
        if (talk == null)
            return null;
        if (talk.getState() != Talk.State.DRAFT)
            return null;

        setCoSpeaker(talkUser, talk);

        talkUser.setState(newState);
        talk.setTrack(trackRepo.findByIdAndEventId(talkUser.getTrackId(), Event.current()));
        talk.setTalkFormat(talkFormatRepo.findByIdAndEventId(talkUser.getFormat(), Event.current()));
        mapper.map(talkUser, talk);

        talkRepo.save(talk);
        talkRepo.flush();

        return talkUser;
    }

    private void setCoSpeaker(TalkUser talkUser, Talk talk) throws CospeakerNotFoundException {

        if (talkUser.getCospeakers() == null)
            return;
        HashSet<User> users = new HashSet<User>();
        for (CospeakerProfil cospeaker : talkUser.getCospeakers()) {
            List<User> existingUser = userRepo.findByEmail(cospeaker.getEmail());
            if (existingUser.isEmpty()) {
                throw new CospeakerNotFoundException("error cospeaker not found", new CospeakerProfil(cospeaker.getEmail()));
            }
            users.add(existingUser.get(0));
        }
        talk.setCospeakers(users);
    }


}
