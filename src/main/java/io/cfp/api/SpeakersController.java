package io.cfp.api;


import io.cfp.mapper.ProposalMapper;
import io.cfp.model.Proposal;
import io.cfp.model.User;
import io.cfp.model.queries.ProposalQuery;
import io.cfp.multitenant.TenantId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.cfp.entity.Role.ADMIN;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = {"/api/speakers", "/v1/speakers"}, produces = APPLICATION_JSON_UTF8_VALUE)
public class SpeakersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpeakersController.class);

    @Autowired
    private ProposalMapper proposals;

    @GetMapping
    @Secured(ADMIN)
    public List<User> search(@AuthenticationPrincipal User user,
                             @TenantId String event,
                             @RequestParam(name = "states", required = false) String states,
                             @RequestParam(name = "userId", required = false) Integer userId,
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
            .setUserId(userId)
            .setSort(sort)
            .setOrder(order.equalsIgnoreCase("desc") ? "desc" : "asc");

        LOGGER.info("Search Speakers : {}", query);
        List<Proposal> p = proposals.findAll(query);
        LOGGER.debug("Found {} Proposals", p.size());

        Set<User> speakers = p.stream().map(Proposal::getSpeaker).collect(Collectors.toSet());
        p.stream().map(Proposal::getCospeakers).forEach(speakers::addAll);
        LOGGER.debug("Found {} Speakers", speakers.size());
        return new ArrayList<>(speakers);
    }
}
