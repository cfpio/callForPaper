package io.cfp.api;


import io.cfp.mapper.ProposalMapper;
import io.cfp.mapper.UserMapper;
import io.cfp.model.Proposal;
import io.cfp.model.User;
import io.cfp.model.queries.ProposalQuery;
import io.cfp.model.queries.UserQuery;
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

import java.util.*;
import java.util.stream.Collectors;

import static io.cfp.entity.Role.ADMIN;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = {"/api/speakers", "/v1/speakers"}, produces = APPLICATION_JSON_UTF8_VALUE)
public class SpeakersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpeakersController.class);

    @Autowired
    private UserMapper users;

    @GetMapping
    public List<User> search(@AuthenticationPrincipal User user,
                             @TenantId String event,
                             @RequestParam(name = "states", required = false) String states,
                             @RequestParam(name = "sort", required = false, defaultValue = "lastname,firstname") String sort,
                             @RequestParam(name = "order", required = false, defaultValue = "asc") String order) {
        List<Proposal.State> stateList = new ArrayList<>();
        if (user != null && user.hasRole(ADMIN)) {
            if (states != null) {
                stateList = Arrays.stream(states.split(","))
                    .map(Proposal.State::valueOf)
                    .collect(Collectors.toList());
            }
        } else {
            LOGGER.info("User is not admin, can only get public informations of PRESENT speakers");
            stateList.add(Proposal.State.PRESENT);
        }

        UserQuery query = new UserQuery()
            .setEventId(event)
            .setStates(stateList)
            .setSort(sort)
            .setOrder(order);

        LOGGER.info("Search Speakers {}", query);
        List<User> u = users.findAll(query);
        LOGGER.debug("Found {} Speakers", u.size());

        if (user == null || !user.hasRole(ADMIN)) {
            u.forEach(User::cleanPrivatesInformations);
        }

        return u;
    }
}
