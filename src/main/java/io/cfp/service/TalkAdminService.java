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

import static java.util.stream.Collectors.averagingInt;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.cfp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.cfp.domain.exception.CospeakerNotFoundException;
import io.cfp.dto.TalkAdmin;
import io.cfp.dto.user.CospeakerProfil;
import io.cfp.dto.user.UserProfil;
import io.cfp.entity.Event;
import io.cfp.entity.Rate;
import io.cfp.entity.Talk;
import io.cfp.entity.User;
import io.cfp.service.admin.user.AdminUserService;
import ma.glasnost.orika.MapperFacade;

/**
 * Service for managing talks by the admins
 */
@Service
@Transactional
public class TalkAdminService {

    @Autowired
    private TalkRepo talkRepo;

    @Autowired
    private RateRepo rateRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FormatRepo formatRepo;

    @Autowired
    private TrackRepo trackRepo;

    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private MapperFacade mapper;

    /**
     * Retrieve all talks
     *
     * @param states List of states the talk must be
     * @return List of talks
     */
    public List<TalkAdmin> findAll(Talk.State... states) {
        List<TalkAdmin> talks = talkRepo.findByEventIdAndStatesFetch(Event.current(), Arrays.asList(states))
            .stream().map(t -> new TalkAdmin(t))
            .collect(Collectors.toList());

        List<Rate> rates = rateRepo.findAllFetchAdmin(Event.current());

        User admin = adminUserService.getCurrentUser();
        Map<Integer, List<Rate>> reviewed = rates.stream()
            .filter(r -> admin.getId() == r.getAdminUser().getId())
            .collect(groupingBy(r -> r.getTalk().getId()));

        Map<Integer, Double> averages = rates.stream()
            .filter(r -> r.getRate() > 0)
            .collect(groupingBy(r -> r.getTalk().getId(), averagingInt(Rate::getRate)));

        Map<Integer, List<String>> voters = rates.stream()
            .collect(groupingBy(r -> r.getTalk().getId(), mapping(r -> r.getAdminUser().getEmail(), toList())));

        for (TalkAdmin talk : talks) {
            int talkId = talk.getId();

            talk.setReviewed(reviewed.get(talkId) != null);
            talk.setMean(averages.get(talkId));
            talk.setVoteUsersEmail(voters.get(talkId));
        }
        return talks;
    }

    /**
     * Retrieve a talk
     *
     * @param talkId Id of the talk to retrieve
     * @return Talk or null if not found
     */
    public TalkAdmin getOne(int talkId) {
        Talk talk = talkRepo.findByIdAndEventId(talkId, Event.current());
        TalkAdmin talkAdmin = mapper.map(talk, TalkAdmin.class);
        UserProfil user = mapper.map(talk.getUser(), UserProfil.class);
        user.setImageProfilURL(talk.getUser().getImageProfilURL());
        talkAdmin.setSpeaker(user);
        talkAdmin.setUserId(user.getId());
        return talkAdmin;
    }

    /**
     * Edit a talk
     *
     * @param talkAdmin Talk to edit
     * @return Edited talk
     */
    public TalkAdmin edit(TalkAdmin talkAdmin) throws CospeakerNotFoundException {
        Talk talk = talkRepo.findByIdAndEventId(talkAdmin.getId(), Event.current());
        if (talk == null) {
            return null;
        }

        talk.name(talkAdmin.getName())
            .language(talkAdmin.getLanguage())
            .track(trackRepo.getOne(talkAdmin.getTrackId()))
            .description(talkAdmin.getDescription())
            .references(talkAdmin.getReferences())
            .difficulty(talkAdmin.getDifficulty())
            .format(formatRepo.findByIdAndEventId(talkAdmin.getFormat(), Event.current()))
            .track(trackRepo.findByIdAndEventId(talkAdmin.getTrackId(), Event.current()));
        setCoSpeaker(talkAdmin, talk);

        talkRepo.save(talk);
        talkRepo.flush();

        return mapper.map(talk, TalkAdmin.class);
    }

    /**
     * For each cospeaker, check if the user is in the CFP database and set the id on the user object
     *
     * @param talk TalkUser
     * @param talk Talk
     * @throws CospeakerNotFoundException If a cospeaker is not found
     */
    private void setCoSpeaker(TalkAdmin talkAdmin, Talk talk) throws CospeakerNotFoundException {

        if (talkAdmin.getCospeakers() == null) return;

        HashSet<User> users = new HashSet<User>();
        for (CospeakerProfil cospeaker : talkAdmin.getCospeakers()) {
            User u = userRepo.findByEmail(cospeaker.getEmail());
            if (u == null) {
                throw new CospeakerNotFoundException("error cospeaker not found", new CospeakerProfil(cospeaker.getEmail()));
            }
            users.add(u);
        }
        talk.setCospeakers(users);
    }

    /**
     * Delete a talk
     *
     * @param talkId Id of the talk to delete
     * @return Deleted talk
     */
    public TalkAdmin delete(int talkId) {
        Talk talk = talkRepo.findByIdAndEventId(talkId, Event.current());
        TalkAdmin deleted = mapper.map(talk, TalkAdmin.class);
        commentRepo.deleteByTalkId(talkId); // fix to issue #122
        talkRepo.delete(talk);
        return deleted;
    }
}
