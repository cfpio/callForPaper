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
import io.cfp.mapper.UserMapper;
import io.cfp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = { "/v1/users" }, produces = APPLICATION_JSON_UTF8_VALUE)
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserMapper userMapper;

    @GetMapping(value = "/me")
    @Secured(Role.AUTHENTICATED)
    public User getUserProfil(@AuthenticationPrincipal User user) {
        return user;
    }

    @PutMapping(value = "/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Integer id, @RequestBody User userUpdate, @AuthenticationPrincipal User user) {
        // A user should only modify his own profil
        if (!id.equals(userUpdate.getId()) || !id.equals(user.getId())) {
            throw new ForbiddenException();
        }

        LOGGER.info("update: {}", userUpdate);

        userMapper.update(userUpdate);
    }
}
