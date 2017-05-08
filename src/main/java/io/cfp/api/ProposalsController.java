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

import io.cfp.domain.exception.BadRequestException;
import io.cfp.domain.exception.ForbiddenException;
import io.cfp.domain.exception.NotFoundException;
import io.cfp.entity.Role;
import io.cfp.mapper.ProposalMapper;
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

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = { "/v1", "/api" }, produces = APPLICATION_JSON_UTF8_VALUE)
public class ProposalsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProposalsController.class);

    @Autowired
    private ProposalMapper proposals;


    @GetMapping("/proposals")
    @Secured({Role.REVIEWER, Role.ADMIN})
    public List<Proposal> search(@AuthenticationPrincipal User user,
                                 @TenantId String event,
                                 @RequestParam(name = "states", required = false) String states,
                                 @RequestParam(name = "userId", required = false) Integer userId,
                                 @RequestParam(name = "sort", required = false, defaultValue = "added") String sort,
                                 @RequestParam(name = "order", required = false, defaultValue = "asc") String order
                                 ) {

        List<Proposal.State> stateList = new ArrayList<>();
        if (states != null) {
            stateList = Arrays.stream(states.split(","))
                .map(Proposal.State::valueOf)
                .collect(Collectors.toList());
        }

        ProposalQuery query = new ProposalQuery()
            .setEventId(event)
            .setStates(stateList)
            .setUserId(userId)
            .setSort(sort)
            .setOrder(order.equalsIgnoreCase("desc")?"desc":"asc");

        LOGGER.info("Search Proposals : {}", query);
        List<Proposal> p = proposals.findAll(query);
        LOGGER.debug("Found {} Proposals", p.size());
        return p;
    }

    @GetMapping("/proposals/{id}")
    @Secured({Role.AUTHENTICATED})
    public Proposal get(@AuthenticationPrincipal User user,
                        @TenantId String event,
                        @PathVariable Integer id) {
        LOGGER.info("Get Proposal with id {}", id);
        Proposal proposal = proposals.findById(id, event);

        if (proposal == null) {
            throw new NotFoundException();
        }

        if (!user.hasRole(Role.REVIEWER)
            && !user.hasRole(Role.ADMIN)
            && user.getId() != proposal.getSpeaker().getId()) {
            throw new ForbiddenException();
        }

        LOGGER.debug("Found Proposal {}", proposal);
        return proposal;
    }

    @PostMapping("/proposals")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured(io.cfp.entity.Role.AUTHENTICATED)
    public Proposal create(@TenantId String event,
                           @AuthenticationPrincipal User user,
                           @Valid @RequestBody Proposal proposal) {
        LOGGER.info("User {} create a proposal : {}", user.getId(), proposal.getName());
        // FIXME manage drfat state client side without use of /drafts API
        proposal.setEventId(event);

        if (proposal.getSpeaker() == null) {
            proposal.setSpeaker(user);
        }

        // A user can only create proposals for himself
        if (!user.hasRole(Role.ADMIN)
            && user.getId() != proposal.getSpeaker().getId()) {
            throw new BadRequestException();
        }

        if (proposal.getState() == null) proposal.setState(Proposal.State.CONFIRMED);

        proposal.setAdded(new Date());
        proposals.insert(proposal);

        return proposal;
    }

    @PutMapping("/proposals/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured(io.cfp.entity.Role.AUTHENTICATED)
    public void update(@AuthenticationPrincipal User user,
                       @TenantId String event,
                       @PathVariable Integer id,
                       @Valid @RequestBody Proposal proposal) {

        if (proposal.getSpeaker() == null) {
            proposal.setSpeaker(user);
        }

        // A user can't change proposal's speaker
        if (!user.hasRole(Role.ADMIN)
            && user.getId() != proposal.getSpeaker().getId()) {
            throw new ForbiddenException();
        }
        proposal.setId(id);
        LOGGER.info("User {} update the proposal {}", user.getId(), proposal.getName());

        // A non-admin user can only update his proposals
        Integer userId = !user.hasRole(Role.ADMIN) ? user.getId() : null;
        proposals.updateForEvent(proposal, event, userId);
    }

    @DeleteMapping("/proposals/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured(io.cfp.entity.Role.ADMIN)
    public void delete(@AuthenticationPrincipal User user,
                       @TenantId String event,
                       @PathVariable Integer id) {
        LOGGER.info("User {} delete the Proposal {}", user.getId(), id);
        proposals.deleteForEvent(id, event);
    }


    @PutMapping("/proposals/{id}/confirm")
    @Secured(Role.AUTHENTICATED)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirm(@TenantId String event,
                       @PathVariable int id) {

        LOGGER.info("Proposal {} change state to CONFIRMED", id);
        Proposal proposal = new Proposal();
        proposal.setId(id);
        proposal.setEventId(event);
        proposal.setState(Proposal.State.CONFIRMED);

        //FIXME check proposal is in DRAFT state
        proposals.updateState(proposal);
    }


    @PutMapping("/proposals/{id}/accept")
    @Secured(Role.ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void accept(@TenantId String event,
                       @PathVariable int id) {

        LOGGER.info("Proposal {} change state to ACCEPTED", id);
        Proposal proposal = new Proposal();
        proposal.setId(id);
        proposal.setEventId(event);
        proposal.setState(Proposal.State.ACCEPTED);

        proposals.updateState(proposal);
    }

    @PutMapping("/proposals/{id}/backup")
    @Secured(Role.ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void backup(@TenantId String event,
                       @PathVariable int id) {
        LOGGER.info("Proposal {} change state to BACKUP", id);
        Proposal proposal = new Proposal();
        proposal.setId(id);
        proposal.setEventId(event);
        proposal.setState(Proposal.State.BACKUP);

        proposals.updateState(proposal);
    }

    @PutMapping("/proposals/{id}/reject")
    @Secured(Role.ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@TenantId String event,
                       @PathVariable int id) {
        LOGGER.info("Proposal {} change state to REJECT", id);
        Proposal proposal = new Proposal();
        proposal.setId(id);
        proposal.setEventId(event);
        proposal.setState(Proposal.State.REFUSED);

        proposals.updateState(proposal);
    }

    @PutMapping("/proposals/{id}/retract")
    @Secured(Role.ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retract(@TenantId String event,
                       @PathVariable int id) {
        LOGGER.info("Proposal {} change state to CONFIRMED", id);
        Proposal proposal = new Proposal();
        proposal.setId(id);
        proposal.setEventId(event);
        proposal.setState(Proposal.State.CONFIRMED);

        proposals.updateState(proposal);
    }

    @PutMapping("/proposals/rejectOthers")
    @Secured(Role.ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectOthers(@TenantId String event) {
        LOGGER.info("All CONFIRMED Proposal {} change state to REJECT");
        proposals.updateAllStateWhere(event, Proposal.State.REFUSED, Proposal.State.CONFIRMED);
    }

}
