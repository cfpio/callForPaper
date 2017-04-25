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

import io.cfp.entity.Event;
import io.cfp.entity.Role;
import io.cfp.entity.User;
import io.cfp.repository.EventRepository;
import io.cfp.repository.RoleRepository;
import io.cfp.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Secured(Role.OWNER)
@RequestMapping(value= { "/v0/reviewers", "api/reviewers" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OwnerReviewersController {

    @Autowired
    private UserRepo users;

    @Autowired
    private RoleRepository roles;

    @Autowired
    private EventRepository events;

    @RequestMapping(method=RequestMethod.GET)
    @ResponseBody
    public List<String> getReviewers() {
        return users.findEmailByRole(Role.REVIEWER, Event.current());
    }

    @RequestMapping(method=RequestMethod.POST)
    public boolean addReviewer(@RequestBody String email) {
    	User user = users.findByEmail(email);
    	if (user == null) {
    		user = new User();
    		user.setEmail(email);
    		user = users.save(user);
    	}

    	List<Role> userRoles = roles.findByUserIdAndEventId(user.getId(), Event.current());
    	boolean alreadyAdmin = false;
    	for (Role role : userRoles) {
    		if (Role.ADMIN.equals(role.getName())) {
    			alreadyAdmin = true;
    			break;
    		}
    	}

    	if (!alreadyAdmin) {
    		Role adminRole = new Role();
    		adminRole.setName(Role.REVIEWER);
    		adminRole.setUser(user);
    		adminRole.setEvent(events.getOne(Event.current()));
    		roles.save(adminRole);
    		return true;
    	}
    	return false;
    }

    @RequestMapping(value="/{email:.+}", method=RequestMethod.DELETE)
    public boolean deleteReviewer(@PathVariable String email) {
        User user = users.findByEmail(email);
    	if (user != null) {
            roles.deleteByUserIdAndEventIdAndName(user.getId(), Event.current(), Role.REVIEWER);
    	}
    	return false;
    }

}
