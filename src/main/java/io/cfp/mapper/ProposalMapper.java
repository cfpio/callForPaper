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

package io.cfp.mapper;

import io.cfp.model.Proposal;
import io.cfp.model.queries.ProposalQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProposalMapper {

    List<Proposal> findAll(ProposalQuery userQuery);
    Proposal findById(Integer id);
    int insert(Proposal proposal);
    int updateForEvent(@Param("it") Proposal proposal, @Param("eventId") String eventId);
    int delete(Proposal proposal);

    int count(ProposalQuery proposalQuery);

//    List<Talk> findByEventId(String id);
//
//    Talk findByIdAndEventId(int integer, String eventId);
//
//    List<Talk> findByEventIdAndUserIdAndStateIn(String eventId, int userId, Collection<Talk.State> states);
//
//    long countByEventIdAndStateIn(String eventId, Collection<Talk.State> states);
//
//    int countByEventIdAndUserId(String eventId, int userId);
//
//    int countByEventIdAndTrack(String eventId, Track track);
//
//    int countByEventIdAndFormat(String eventId, Format format);
//
//    int countByEventIdAndRoom(String eventId, Room room);
//
//
//    Talk findByIdAndEventIdAndUserId(int talkId, String eventId, int userId);
//
//    @Query("SELECT t FROM Talk t JOIN FETCH t.cospeakers c WHERE t.event.id = :eventId AND c.id = :userId AND t.id = :talkId")
//    Talk findByIdAndEventIdAndCospeakers(@Param("talkId") int talkId, @Param("eventId") String eventId, @Param("userId") int userId);
//
//    @Query("SELECT t FROM Talk t JOIN FETCH t.cospeakers c WHERE  t.event.id = :eventId AND c.id = :userId AND t.state IN (:states)")
//    List<Talk> findByEventIdAndCospeakerIdAndStateIn(@Param("eventId") String eventId, @Param("userId") int userId, @Param("states") Collection<Talk.State> states);
//
//    @Query("SELECT DISTINCT t FROM Talk t " +
//        "JOIN FETCH t.user " +
//        "JOIN FETCH t.format " +
//        "JOIN FETCH t.track " +
//        "LEFT JOIN FETCH t.cospeakers " +
//        "WHERE  t.event.id = :eventId " +
//        "AND t.state IN (:states) " +
//        "order by t.id")
//    List<Talk> findByEventIdAndStatesFetch(@Param("eventId") String eventId, @Param("states") Collection<Talk.State> states);
//
//
//    @Transactional
//    @Modifying
//    @Query("UPDATE Talk t SET t.state = :state WHERE t.event.id = :eventId AND t.id = :talkId ")
//    void setState(@Param("talkId") int talkId, @Param("eventId") String eventId, @Param("state") Talk.State state);
//
//    @Transactional
//    @Modifying
//    @Query("UPDATE Talk t SET t.state = :targetState WHERE t.event.id = :eventId AND t.state = :initialState ")
//    void setStateWhere(@Param("eventId") String current, @Param("targetState") Talk.State targetState, @Param("initialState") Talk.State initialState);
//
//    @Query("SELECT t.track.libelle, COUNT(t) FROM Talk t WHERE t.event.id = :eventId GROUP BY t.track")
//    List<Object[]> countByTrack(@Param("eventId") String eventId);
//
//    @Transactional
//    @Modifying
//    @Query("delete from Talk t where t.event.id = :eventId")
//    void deleteAllByEventId(@Param("eventId") String eventId);
}
