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

import io.cfp.mapper.RoleMapper;
import io.cfp.mapper.UserMapper;
import io.cfp.model.Role;
import io.cfp.model.User;
import io.cfp.model.queries.RoleQuery;
import io.cfp.multitenant.TenantId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Secured(Role.OWNER)
@RequestMapping(value= { "/v1/reviewers", "api/reviewers" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ReviewersController {

    @Autowired
    private UserMapper users;

    @Autowired
    private RoleMapper roles;

    @GetMapping
    public List<String> getReviewers(@TenantId String eventId) {
        return users.findEmailByRole(Role.REVIEWER, eventId);
    }

    @PostMapping
    public boolean addReviewer(@RequestBody String email,
                               @TenantId String eventId) {
    	User user = users.findByEmail(email);
    	if (user == null) {
    		user = new User();
    		user.setEmail(email);
    		users.insert(user);
    	}

        RoleQuery roleQuery = new RoleQuery().setUserId(user.getId()).setEventId(eventId);
    	List<Role> userRoles = roles.findAll(roleQuery);
    	boolean alreadyReviewer = false;
    	for (Role role : userRoles) {
    		if (Role.REVIEWER.equals(role.getName())) {
                alreadyReviewer = true;
    			break;
    		}
    	}

    	if (!alreadyReviewer) {
    		Role adminRole = new Role();
    		adminRole.setName(Role.REVIEWER);
    		adminRole.setUser(user.getId());
    		adminRole.setEvent(eventId);
    		roles.insert(adminRole);
    		return true;
    	}
    	return false;
    }

    @DeleteMapping(value="/{email:.+}")
    public boolean deleteReviewer(@PathVariable String email,
                                  @TenantId String eventId) {
    	User user = users.findByEmail(email);
    	if (user != null) {
            RoleQuery roleQuery = new RoleQuery().setUserId(user.getId()).setEventId(eventId);
            List<Role> userRoles = roles.findAll(roleQuery);

        	for (Role role : userRoles) {
        		if (Role.REVIEWER.equals(role.getName())) {
        			roles.delete(role);
        			return true;
        		}
        	}
    	}
    	return false;
    }

}
