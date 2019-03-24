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
import io.cfp.domain.exception.EntityExistsException;
import io.cfp.mapper.*;
import io.cfp.model.*;
import io.cfp.model.queries.CommentQuery;
import io.cfp.model.queries.ProposalQuery;
import io.cfp.model.queries.RateQuery;
import io.cfp.model.queries.RoleQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static io.cfp.model.Role.MAINTAINER;
import static io.cfp.model.Role.OWNER;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@RestController
@RequestMapping(value = { "/v1", "/api" }, produces = APPLICATION_JSON_UTF8_VALUE)
public class EventsController {

    @Autowired
    private RoleMapper roles;

    @Autowired
    private EventMapper events;

    @Autowired
    private UserMapper users;

    @Autowired
    private FormatMapper formats;

    @Autowired
    private ThemeMapper themes;

    @Autowired
    private RoomMapper rooms;

    @Autowired
    private ProposalMapper proposals;

    @Autowired
    private RateMapper rates;

    @Autowired
    private CommentMapper comments;


    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public List<Event> all(@RequestParam(name = "state", required = false, defaultValue = "open") String state) throws BadRequestException {
        switch (state) {
            case "passed":
                return events.findPassed();
            case "open":
                return events.findOpen();
            default:
                throw new BadRequestException("Unsupported state filter :"+state);
        }
    }

    @Secured(MAINTAINER)
    @PostMapping("/events")
    @Transactional
    public Event create(@RequestParam(name = "id") String id,
                        @RequestParam(name = "owner") String owner) throws EntityExistsException {

        if (events.exists(id)) {
            throw new EntityExistsException();
        }
        Date now = new Date();

        Event e = new Event()
            .setId(id)
            .setName(id)
            .setContactMail(owner)
            .setPublished(false)
            .setOpen(false)
            .setShortDescription(id)
            .setDate(now)
            .setDecisionDate(now)
            .setReleaseDate(now);

        events.insert(e);

        User u;
        if (!users.exists(owner)) {
            u = new User()
                .setEmail(owner);
            users.insert(u);
        } else {
            u = users.findByEmail(owner);
        }

        roles.insert(new Role()
            .setName(OWNER)
            .setEvent(id)
            .setUser(u.getId()));

        return e;
    }



    @GetMapping("/users/me/events")
    public List<Event> mines(@AuthenticationPrincipal User user) throws BadRequestException {
        return events.findByUser(user.getId());
    }

    @Secured(OWNER)
    @PostMapping("/events/{id}/archive")
    @Transactional
    public void archive(@AuthenticationPrincipal User user,
                        @PathVariable String id,
                        @RequestParam(name = "edition") String edition) {

        String archive = id + '-' + edition;

        // Clone Event with archived eventId
        final Event event = events.findOne(id);
        events.insert(event.setId(archive));

        // Clone roles for archive event
        for (Role role : roles.findAll(new RoleQuery().setEventId(id))) {
            role.setEvent(archive);
            roles.insert(role);
        }

        // Move formats|themes|room|roles to archive event
        // Then re-create for the 'new' one
        // we update existing rows to set archived event ID so we don't have to update FKs
        for (Format format : formats.findByEvent(id)) {
            formats.updateEventId(format.getId(), archive);
            formats.insert(format);
        }

        for (Theme theme : themes.findByEvent(id)) {
            themes.updateEventId(theme.getId(), archive);
            themes.insert(theme);
        }

        for (Room room : rooms.findByEvent(id)) {
            rooms.updateEventId(room.getId(), archive);
            rooms.insert(room);
        }

        // Move Proposals|Rates|Comments to archived event
        for (Proposal proposal : proposals.findAll(new ProposalQuery().setEventId(id))) {
            proposals.updateEventId(proposal.getId(), archive);
        }

        for (Rate rate : rates.findAll(new RateQuery().setEventId(id))) {
            rates.updateEventId(rate.getId(), archive);
        }

        for (Comment comment : comments.findAll(new CommentQuery().setEventId(id))) {
            comments.updateEventId(comment.getId(), archive);
        }
    }

}
