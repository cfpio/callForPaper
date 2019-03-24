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

import io.cfp.dto.EventSched;
import io.cfp.dto.TalkAdmin;
import io.cfp.entity.Event;
import io.cfp.entity.Talk;
import io.cfp.entity.User;
import io.cfp.mapper.ProposalMapper;
import io.cfp.mapper.RateMapper;
import io.cfp.model.Proposal;
import io.cfp.model.queries.ProposalQuery;
import io.cfp.model.queries.RateQuery;
import io.cfp.repository.TalkRepo;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * Service for managing talks by the admins
 */
@Service
@Transactional
public class TalkAdminService {

    @Autowired
    private TalkRepo talkRepo;

    @Autowired
    private ProposalMapper proposalMapper;

    @Autowired
    private RateMapper rateMapper;

    @Autowired
    private MapperFacade mapper;

    /**
     * Retrieve all talks
     *
     * @param states List of states the talk must be
     * @return List of talks
     */
    public List<TalkAdmin> findAll(String eventId, int userId, Proposal.State... states) {

        List<TalkAdmin> talks = proposalMapper.findAll(new ProposalQuery().setEventId(eventId).addStates(states))
            .stream().map(TalkAdmin::new)
            .collect(Collectors.toList());

        List<io.cfp.model.Rate> rates = rateMapper.findAll(new RateQuery().setEventId(eventId));

        Map<Integer, List<io.cfp.model.Rate>> reviewed = rates.stream()
            .filter(r -> userId == r.getUser().getId())
            .collect(groupingBy(r -> r.getTalk().getId()));

        Map<Integer, Double> averages = rates.stream()
            .filter(r -> r.getRate() > 0)
            .collect(groupingBy(r -> r.getTalk().getId(), averagingInt(io.cfp.model.Rate::getRate)));

        Map<Integer, List<String>> voters = rates.stream()
            .collect(groupingBy(r -> r.getTalk().getId(), mapping(r -> r.getUser().getEmail(), toList())));

        for (TalkAdmin talk : talks) {
            int talkId = talk.getId();

            talk.setReviewed(reviewed.get(talkId) != null);
            talk.setMean(averages.get(talkId));
            talk.setVoteUsersEmail(voters.get(talkId));
        }
        return talks;
    }

    /**
     * Delete a talk
     *
     * @param talkId Id of the talk to delete
     * @return Deleted talk
     */
    @Deprecated
    private TalkAdmin delete(int talkId) {
        Talk talk = talkRepo.findByIdAndEventId(talkId, Event.current());
        TalkAdmin deleted = mapper.map(talk, TalkAdmin.class);
        talkRepo.delete(talk);
        return deleted;
    }

    /**
     * Export talks list into sched.org format
     * @param states State list to export
     * @return DTO in sched format
     */
    @Deprecated
    public List<EventSched> exportSched(Talk.State... states) {
        return talkRepo.findByEventIdAndStatesFetch(Event.current(), Arrays.asList(states)).stream()
            .map(t ->
                new EventSched().toBuilder()
                    .id(String.valueOf(t.getId()))
                    .name(t.getName())
                    .description(t.getDescription())
                    .speakers(buildSpeakersList(t))
                    .language(t.getLanguage())
                    .eventType(t.getTrack().getLibelle())
                    .format(t.getFormat().getName())
                    .build()
            )
            .collect(toList());
    }

    public List<EventSched> exportSched(String eventId, Proposal.State... states) {
        return proposalMapper.findAll(new ProposalQuery().setEventId(eventId).addStates(states)).stream()
            .map(p ->
                new EventSched().toBuilder()
                    .id(String.valueOf(p.getId()))
                    .name(p.getName())
                    .description(p.getDescription())
                    .speakers(p.buildSpeakersList())
                    .language(p.getLanguage())
                    .eventType(p.getTrackLabel())
                    .format(p.getFormatName())
                    .build()
            )
            .collect(toList());
    }

    /**
     * Build the name of the speakers as Speaker FirstName Lastname + Cospeakers
     * @param t Talk to build the speakers
     * @return Speakers list (ex: John Doe, Jack Bauer)
     */
    @Deprecated
    private String buildSpeakersList(Talk t) {
        String res = t.getUser().getFirstname() + " " + t.getUser().getLastname();
        if (isNotEmpty(t.getCospeakers())) {
            for (User user : t.getCospeakers()) {
                res += ", " + user.getFirstname() + " " + user.getLastname();
            }
        }
        return res;
    }
}
