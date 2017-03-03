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

package io.cfp.controller;

import io.cfp.domain.exception.BadRequestException;
import io.cfp.domain.exception.EntityExistsException;
import io.cfp.entity.Event;
import io.cfp.entity.Role;
import io.cfp.entity.User;
import io.cfp.repository.EventRepository;
import io.cfp.repository.RoleRepository;
import io.cfp.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InstanceAlreadyExistsException;
import java.util.Date;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@RestController
@RequestMapping(value = { "/v1", "/api" }, produces = APPLICATION_JSON_UTF8_VALUE)
public class EventsController {

    @Autowired
    private EventRepository events;

    @Autowired
    private RoleRepository roles;

    @Autowired
    private UserRepo users;



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

    @Secured(Role.MAINTAINER)
    @RequestMapping(value = "/events", method = RequestMethod.POST)
    public Event create(@RequestParam(name = "id", required=true) String id, @RequestParam(name = "owner", required=true) String owner) throws EntityExistsException {

        if (events.exists(id)) {
            throw new EntityExistsException();
        }
        Date now = new Date();

        Event e = Event.builder()
            .id(id)
            .name(id)
            .contactMail(owner)
            .published(false)
            .open(false)
            .shortDescription(id)
            .date(now)
            .decisionDate(now)
            .releaseDate(now)
            .build();

        events.saveAndFlush(e);

        User user = users.findByEmail(owner);
        if (user == null) {
            user = new User();
            user.setEmail(owner);
            users.saveAndFlush(user);
        }

        Role r = new Role();
        r.setName(Role.OWNER.toString());
        r.setEvent(e);
        r.setUser(user);
        roles.saveAndFlush(r);

        return e;
    }



    @RequestMapping(value = "/users/me/events", method = RequestMethod.GET)
    public List<Event> mines(@AuthenticationPrincipal User user) throws BadRequestException {
        return events.findByUser(user.getId());
    }

}
