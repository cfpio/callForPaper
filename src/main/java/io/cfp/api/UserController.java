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

import io.cfp.domain.exception.ForbiddenException;
import io.cfp.entity.Role;
import io.cfp.mapper.ProposalMapper;
import io.cfp.mapper.UserMapper;
import io.cfp.model.Proposal;
import io.cfp.model.User;
import io.cfp.model.queries.ProposalQuery;
import io.cfp.multitenant.TenantId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = { "/api/users", "/v1/users" }, produces = APPLICATION_JSON_UTF8_VALUE)
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProposalMapper proposals;

    @GetMapping(value = "/me")
    @Secured(Role.AUTHENTICATED)
    public User getUserProfil(@AuthenticationPrincipal User user) {
        return user;
    }

    @GetMapping(value = "/me/proposals")
    @Secured(Role.AUTHENTICATED)
    public List<Proposal> getMyProposals(@AuthenticationPrincipal User user,
                                 @TenantId String event,
                                 @RequestParam(name = "states", required = false) String states,
                                 @RequestParam(name = "sort", required = false, defaultValue = "added") String sort,
                                 @RequestParam(name = "order", required = false, defaultValue = "asc") String order) {

        List<Proposal.State> stateList = new ArrayList<>();
        if (states != null) {
            stateList = Arrays.stream(states.split(","))
                .map(Proposal.State::valueOf)
                .collect(Collectors.toList());
        }

        ProposalQuery query = new ProposalQuery()
            .setEventId(event)
            .setStates(stateList)
            .setUserId(user.getId())
            .setSort(sort)
            .setOrder(order.equalsIgnoreCase("desc")?"desc":"asc");

        LOGGER.info("Get user {} proposals : {}", user.getId(), query);
        List<Proposal> p = proposals.findAll(query);
        LOGGER.debug("Found {} Proposals", p.size());
        return p;
    }

    @PutMapping(value = "/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMyProfil(@RequestBody User userUpdate,
                               @AuthenticationPrincipal User user) {
        update(user.getId(), userUpdate, user);
    }


    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable int id,
                               @RequestBody User userUpdate,
                               @AuthenticationPrincipal User user) {
        LOGGER.info("update: {}", userUpdate);

        if (id != user.getId() && !user.hasRole(Role.MAINTAINER)) {
            throw new ForbiddenException();
        }

        userUpdate.setId(user.getId()).setEmail(user.getEmail());

        userMapper.update(userUpdate);
    }

}
