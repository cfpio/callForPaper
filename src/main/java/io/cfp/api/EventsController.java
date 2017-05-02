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
import io.cfp.mapper.EventMapper;
import io.cfp.mapper.RoleMapper;
import io.cfp.mapper.UserMapper;
import io.cfp.model.Event;
import io.cfp.model.Role;
import io.cfp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
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
    private RoleMapper roles;

    @Autowired
    private EventMapper events;

    @Autowired
    private UserMapper users;


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

    @Secured(io.cfp.entity.Role.MAINTAINER)
    @RequestMapping(value = "/events", method = RequestMethod.POST)
    @Transactional
    public Event create(@RequestParam(name = "id", required=true) String id, @RequestParam(name = "owner", required=true) String owner) throws EntityExistsException {

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
            .setName(io.cfp.entity.Role.OWNER.toString())
            .setEvent(id)
            .setUser(u.getId()));

        return e;
    }



    @RequestMapping(value = "/users/me/events", method = RequestMethod.GET)
    public List<Event> mines(@AuthenticationPrincipal User user) throws BadRequestException {
        return events.findByUser(user.getId());
    }

}
