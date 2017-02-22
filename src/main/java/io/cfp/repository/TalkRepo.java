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

package io.cfp.repository;

import io.cfp.entity.Format;
import io.cfp.entity.Talk;
import io.cfp.entity.Track;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TalkRepo extends JpaRepository<Talk, Integer> {

    Talk findByIdAndEventId(int integer, String eventId);

    List<Talk> findByEventIdAndUserIdAndStateIn(String eventId, int userId, Collection<Talk.State> states);

    long countByEventIdAndStateIn(String eventId, Collection<Talk.State> states);

    int countByEventIdAndUserId(String eventId, int userId);

    int countByEventIdAndTrack(String eventId, Track track);

    int countByEventIdAndFormat(String eventId, Format format);

    Talk findByIdAndEventIdAndUserId(int talkId, String eventId, int userId);

    @Query("SELECT t FROM Talk t JOIN FETCH t.cospeakers c WHERE t.event.id = :eventId AND c.id = :userId AND t.id = :talkId")
    Talk findByIdAndEventIdAndCospeakers(@Param("talkId") int talkId, @Param("eventId") String eventId, @Param("userId") int userId);

    @Query("SELECT t FROM Talk t JOIN FETCH t.cospeakers c WHERE  t.event.id = :eventId AND c.id = :userId AND t.state IN (:states)")
    List<Talk> findByEventIdAndCospeakerIdAndStateIn(@Param("eventId") String eventId, @Param("userId") int userId, @Param("states") Collection<Talk.State> states);

    @Query("SELECT DISTINCT t FROM Talk t " +
        "JOIN FETCH t.user " +
        "JOIN FETCH t.format " +
        "JOIN FETCH t.track " +
        "LEFT JOIN FETCH t.cospeakers " +
        "WHERE  t.event.id = :eventId " +
        "AND t.state IN (:states)")
    List<Talk> findByEventIdAndStatesFetch(@Param("eventId") String eventId, @Param("states") Collection<Talk.State> states);


    @Query("UPDATE Talk t SET t.state = ACCEPTER WHERE t.event.id = :eventId AND t.id = :talkId ")
    void setState(@Param("talkId") int talkId, @Param("eventId") String eventId, @Param("state") Talk.State state);
}
