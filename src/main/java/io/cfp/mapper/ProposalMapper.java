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

    List<Proposal> findAll(ProposalQuery proposalQuery);
    Proposal findById(@Param("id") int id, @Param("eventId") String eventId);
    int insert(Proposal proposal);
    int updateForEvent(@Param("it") Proposal proposal, @Param("eventId") String eventId, @Param("userId") Integer userId);
    int deleteForEvent(@Param("id") int id, @Param("eventId") String eventId);
    int updateState(Proposal proposal);
    int updateAllStateWhere(@Param("eventId") String event, @Param("newState") Proposal.State refused, @Param("oldState") Proposal.State confirmed);

    int count(ProposalQuery proposalQuery);

    int deleteAllByEventId(String event);
}
