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

import com.itextpdf.text.DocumentException;
import io.cfp.domain.exception.BadRequestException;
import io.cfp.domain.exception.CospeakerNotFoundException;
import io.cfp.domain.exception.ForbiddenException;
import io.cfp.domain.exception.NotFoundException;
import io.cfp.dto.user.CospeakerProfil;
import io.cfp.entity.Role;
import io.cfp.mapper.CoSpeakerMapper;
import io.cfp.mapper.ProposalMapper;
import io.cfp.mapper.RateMapper;
import io.cfp.mapper.UserMapper;
import io.cfp.model.Proposal;
import io.cfp.model.Rate;
import io.cfp.model.User;
import io.cfp.model.queries.ProposalQuery;
import io.cfp.model.queries.RateQuery;
import io.cfp.multitenant.TenantId;
import io.cfp.service.PdfCardService;
import io.cfp.service.email.EmailingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static io.cfp.entity.Role.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = {"/v1", "/api"}, produces = APPLICATION_JSON_UTF8_VALUE)
public class ProposalsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProposalsController.class);

    @Autowired
    private ProposalMapper proposals;

    @Autowired
    private RateMapper rates;

    @Autowired
    private CoSpeakerMapper cospeakers;

    @Autowired
    private UserMapper users;

    @Autowired
    private EmailingService emailingService;

    @Autowired
    private PdfCardService pdfCardService;

    @GetMapping("/proposals")
    @Secured({REVIEWER, ADMIN})
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
            .setOrder(order.equalsIgnoreCase("desc") ? "desc" : "asc");

        LOGGER.info("Search Proposals : {}", query);
        List<Proposal> p = proposals.findAll(query);
        LOGGER.debug("Found {} Proposals", p.size());

        List<Rate> rates = this.rates.findAllWithTalk(event);
        Map<Integer, List<Rate>> ratesByTalk = rates.stream().collect(Collectors.groupingBy(rate -> rate.getTalk().getId()));

        for (Proposal proposal : p) {
            List<String> emails = new ArrayList<>();
            float total = 0;
            int votes = 0;
            for (Rate rate : ratesByTalk.get(proposal.getId())) {
                emails.add(rate.getUser().getEmail());
                if (rate.getRate() > 0) {
                    total += rate.getRate();
                    votes++;
                }
            }
            proposal.setVoteUsersEmail(emails);
            if (votes > 0) {
                proposal.setMean(String.valueOf(total / votes));
            }
        }

        return p;
    }


    @GetMapping("/proposals/nextToRate")
    @Secured({REVIEWER, ADMIN})
    public Proposal getNextToRate(@AuthenticationPrincipal User user,
                                  @TenantId String event) {

        List<Proposal> p = this.search(user, event, null, null, "added", "asc");

        Proposal nextProposalToVote = p.stream()
            .filter(prop -> !prop.getVoteUsersEmail().contains(user.getEmail()))
            .findFirst()
            .orElseThrow(NotFoundException::new);

        return nextProposalToVote;
    }


    @GetMapping("/proposals/{id}")
    @Secured({AUTHENTICATED})
    public Proposal get(@AuthenticationPrincipal User user,
                        @TenantId String event,
                        @PathVariable Integer id) {
        LOGGER.info("Get Proposal with id {}", id);
        Proposal proposal = proposals.findById(id, event);

        if (proposal == null) {
            throw new NotFoundException();
        }

        if (!user.hasRole(REVIEWER)
            && !user.hasRole(ADMIN)
            && user.getId() != proposal.getSpeaker().getId()) {
            throw new ForbiddenException("Vous n'êtes pas autorisé à accéder au Proposal " + id);
        }

        LOGGER.debug("Found Proposal {}", proposal);
        return proposal;
    }

    @PostMapping("/proposals")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured(AUTHENTICATED)
    @Transactional
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
        if (!user.hasRole(ADMIN)
            && user.getId() != proposal.getSpeaker().getId()) {
            throw new BadRequestException();
        }

        proposal.setState(Proposal.State.DRAFT) // when created, a talk is a Draft. Need to be confirmed
            .setAdded(new Date());
        proposals.insert(proposal);

        createCospeakers(proposal);

        return proposal;
    }

    private void createCospeakers(Proposal proposal) {
        cospeakers.delete(proposal.getId());
        if (proposal.getCospeakers() != null) {
            for (User cs : proposal.getCospeakers()) {
                final User cospeaker = users.findByEmail(cs.getEmail());
                if (cospeaker == null) throw new CospeakerNotFoundException(new CospeakerProfil(cs.getEmail()));
                cospeakers.insert(proposal.getId(), cospeaker.getId());
            }
        }
    }

    @PutMapping("/proposals/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured(AUTHENTICATED)
    @Transactional
    public void update(@AuthenticationPrincipal User user,
                       @TenantId String event,
                       @PathVariable Integer id,
                       @Valid @RequestBody Proposal proposal) {

        if (proposal.getSpeaker() == null) {
            proposal.setSpeaker(user);
        }

        // A user can't change proposal's speaker
        if (!user.hasRole(ADMIN)
            && user.getId() != proposal.getSpeaker().getId()) {
            throw new ForbiddenException("Vous n'êtes pas autorisé à modifier le Proposal " + id);
        }
        proposal.setId(id);
        LOGGER.info("User {} update the proposal {}", user.getId(), proposal.getName());

        // A non-admin user can only update his proposals
        Integer userId = !user.hasRole(ADMIN) ? user.getId() : null;
        proposals.updateForEvent(proposal, event, userId);

        createCospeakers(proposal);
    }

    @DeleteMapping("/proposals/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured(ADMIN)
    public void delete(@AuthenticationPrincipal User user,
                       @TenantId String event,
                       @PathVariable Integer id) {
        LOGGER.info("User {} delete the Proposal {}", user.getId(), id);
        proposals.deleteForEvent(id, event);
    }

    /**
     * Delete all sessions (aka reset CFP)
     */
    @DeleteMapping(value = "/proposals")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured(Role.ADMIN)
    public void deleteAll(@TenantId String event) {
        proposals.deleteAllByEventId(event);
    }


    @PutMapping("/proposals/{id}/confirm")
    @Secured(AUTHENTICATED)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirm(@AuthenticationPrincipal User user,
                        @TenantId String event,
                        @PathVariable int id) {

        LOGGER.info("Proposal {} change state to CONFIRMED", id);

        Proposal proposal = proposals.findById(id, event);
        proposal.setId(id);
        proposal.setEventId(event);
        proposal.setState(Proposal.State.CONFIRMED);

        //FIXME check proposal is in DRAFT state
        proposals.updateState(proposal);

        emailingService.sendConfirmed(user, proposal);
    }

    @PutMapping("/proposals/{id}/back-to-edit")
    @Secured(ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void backToEdit(@TenantId String event,
                           @PathVariable int id) {

        LOGGER.info("Proposal {} change state to DRAFT", id);

        Proposal proposal = proposals.findById(id, event);
        proposal.setId(id);
        proposal.setEventId(event);
        proposal.setState(Proposal.State.DRAFT);

        //FIXME check proposal is in CONFIRMED state
        proposals.updateState(proposal);

        emailingService.sendBackToEdit(proposal);
    }

    @PutMapping("/proposals/{id}/confirmPresence")
    @Secured(AUTHENTICATED)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirmPresence(@AuthenticationPrincipal User user,
                                @TenantId String event,
                                @PathVariable int id) {

        LOGGER.info("Proposal {} change state to CONFIRMED_PRESENCE", id);

        Proposal proposal = proposals.findById(id, event);
        proposal.setId(id);
        proposal.setEventId(event);
        proposal.setState(Proposal.State.PRESENT);

        proposals.updateState(proposal);

        emailingService.sendConfirmedPresence(user, proposal);
    }


    @PutMapping("/proposals/{id}/accept")
    @Secured(ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void accept(@TenantId String event,
                       @PathVariable int id) {

        LOGGER.info("Proposal {} change state to ACCEPTED", id);
        Proposal proposal = proposals.findById(id, event);
        proposal.setId(id);
        proposal.setEventId(event);
        proposal.setState(Proposal.State.ACCEPTED);

        proposals.updateState(proposal);
    }

    @PutMapping("/proposals/{id}/backup")
    @Secured(ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void backup(@TenantId String event,
                       @PathVariable int id) {
        LOGGER.info("Proposal {} change state to BACKUP", id);
        Proposal proposal = proposals.findById(id, event);
        proposal.setId(id);
        proposal.setEventId(event);
        proposal.setState(Proposal.State.BACKUP);

        proposals.updateState(proposal);
    }

    @PutMapping("/proposals/{id}/reject")
    @Secured(ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@TenantId String event,
                       @PathVariable int id) {
        LOGGER.info("Proposal {} change state to REJECT", id);
        Proposal proposal = proposals.findById(id, event);
        proposal.setId(id);
        proposal.setEventId(event);
        proposal.setState(Proposal.State.REFUSED);

        proposals.updateState(proposal);
    }

    @PutMapping("/proposals/{id}/retract")
    @Secured(ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retract(@TenantId String event,
                        @PathVariable int id) {
        LOGGER.info("Proposal {} change state to CONFIRMED", id);
        Proposal proposal = proposals.findById(id, event);
        proposal.setId(id);
        proposal.setEventId(event);
        proposal.setState(Proposal.State.CONFIRMED);

        proposals.updateState(proposal);
    }

    @PutMapping("/proposals/rejectOthers")
    @Secured(ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectOthers(@TenantId String event) {
        LOGGER.info("All CONFIRMED Proposals change state to REJECT");
        int updatedProposals = proposals.updateAllStateWhere(event, Proposal.State.REFUSED, Proposal.State.CONFIRMED);
        LOGGER.info("{} Proposals have changed to REJECT", updatedProposals);
    }

    /**
     * Add a new rating
     */
    @PostMapping("/proposals/{proposalId}/rates")
    @Secured({REVIEWER, ADMIN})
    public Rate addRate(@PathVariable int proposalId,
                        @AuthenticationPrincipal User user,
                        @Valid @RequestBody Rate rate,
                        @TenantId String eventId) {
        rate.setEventId(eventId);
        rate.setUser(user);
        rate.setTalk(new Proposal().setId(proposalId));
        rate.setAdded(new Date());
        rates.insert(rate);
        return rate;
    }

    /**
     * Edit a rating
     */
    @PutMapping("/proposals/{proposalId}/rates/{rateId}")
    @Secured({REVIEWER, ADMIN})
    public Rate update(@PathVariable int proposalId,
                       @PathVariable int rateId,
                       @AuthenticationPrincipal User user,
                       @Valid @RequestBody Rate rate,
                       @TenantId String eventId) {
        rate.setId(rateId);
        rate.setUser(user);
        rate.setEventId(eventId);
        rate.setTalk(new Proposal().setId(proposalId));
        rate.setAdded(new Date());
        rates.update(rate);
        return rate;
    }


    /**
     * Get a specific rating
     */
    @GetMapping("/proposals/{proposalId}/rates")
    @Secured(Role.ADMIN)
    public List<Rate> getRate(@PathVariable int proposalId,
                              @TenantId String eventId) {
        RateQuery rateQuery = new RateQuery()
            .setEventId(eventId)
            .setProposalId(proposalId);
        return rates.findAll(rateQuery);
    }

    /**
     * Get a specific rating
     */
    @GetMapping("/proposals/{proposalId}/rates/me")
    @Secured({REVIEWER, ADMIN})
    public Rate getMyRate(@PathVariable int proposalId,
                          @AuthenticationPrincipal User user,
                          @TenantId String eventId) {
        return rates.findMyRate(proposalId, user.getId(), eventId);
    }

    @GetMapping(path = "/proposals/export/cards.pdf", produces = "application/pdf")
    @Secured(ADMIN)
    public void exportPdf(@TenantId String eventId,
                          HttpServletResponse response) throws IOException, DocumentException {
        response.addHeader(HttpHeaders.CONTENT_TYPE, "application/pdf");
        pdfCardService.export(eventId, response.getOutputStream());
    }


}
