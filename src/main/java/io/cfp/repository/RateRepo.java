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

import io.cfp.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;


public interface RateRepo extends JpaRepository<Rate, Integer> {

    List<Rate> findByEventIdAndTalkUserId(String eventId, int userId);

    @Query("SELECT r FROM Rate r JOIN FETCH r.adminUser WHERE r.event.id = :eventId AND r.talk.id = :talkId")
    List<Rate> findByEventIdAndTalkIdFetchAdmin(@Param("eventId") String eventId, @Param("talkId") int talkId);

    List<Rate> findByEventIdAndTalkId(String eventId, int talkId);

    Rate findByEventIdAndTalkIdAndAdminUserId(String eventId, int talkId, int adminId);

    @Query("SELECT r FROM Rate r JOIN FETCH r.adminUser WHERE r.event.id = :eventId")
    List<Rate> findAllFetchAdmin(@Param("eventId") String eventId);

    Rate findByIdAndEventId(int integer, String eventId);

    List<Rate> findByEventId(String eventId);

    void deleteByEventId(String eventId);

    @Query("SELECT r.adminUser.email, COUNT(r) FROM Rate r WHERE r.event.id = :eventId GROUP BY r.adminUser")
    List<Object[]> findNbRateByAdminUser(@Param("eventId") String eventId);

}
