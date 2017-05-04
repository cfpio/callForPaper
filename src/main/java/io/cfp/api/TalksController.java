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

import io.cfp.mapper.ProposalMapper;
import io.cfp.model.Proposal;
import io.cfp.model.queries.ProposalQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = { "/v1" }, produces = APPLICATION_JSON_UTF8_VALUE)
public class TalksController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TalksController.class);

    @Autowired
    private ProposalMapper proposalMapper;


    /**
     * API pour exposer publiquement les proposals acceptés, aucune écriture disponible.
     * @param userId critère pour filtrer les proposals acceptés d'un utilisateur
     * @return Liste de proposition
     */
    @GetMapping("/talks")
    public List<Proposal> search(@RequestParam(name = "userId", required = false) Integer userId) {
        ProposalQuery query = new ProposalQuery()
            .setState(Proposal.State.ACCEPTED.name())
            .setUserId(userId);

        LOGGER.info("Search accepted Proposals : {}", query);
        List<Proposal> proposals = proposalMapper.findAll(query);
        LOGGER.debug("Found {} Proposals", proposals.size());
        return proposals;
    }

}
