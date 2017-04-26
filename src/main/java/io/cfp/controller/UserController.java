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

import io.cfp.dto.user.UserProfil;
import io.cfp.entity.Role;
import io.cfp.entity.User;
import io.cfp.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = { "/v0/users", "/api/users" }, produces = APPLICATION_JSON_UTF8_VALUE)
public class UserController {

    @Autowired
    private UserRepo users;

    /**
     * Get current user profil
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/me", method = RequestMethod.GET)
    @Secured(Role.AUTHENTICATED)
    public UserProfil getUserProfil(@AuthenticationPrincipal User user) {
        return new UserProfil(user, true); // safe, this is my own data
    }

    /**
     * Edit current user profil
     *
     * @param user
     * @param profil
     * @return
     */
    @RequestMapping(value = "/me", method = RequestMethod.PUT)
    @Secured(Role.AUTHENTICATED)
    @Transactional
    public UserProfil putUserProfil(@AuthenticationPrincipal User user, @RequestBody UserProfil profil) {
        user.firstname(profil.getFirstname())
            .lastname(profil.getLastname())
            .email(profil.getEmail())
            .language(profil.getLanguage())
            .bio(profil.getBio())
            .phone(profil.getPhone())
            .company(profil.getCompany())
            .language(profil.getLanguage())
            .github(profil.getGithub())
            .twitter(profil.getTwitter())
            .googleplus(profil.getGoogleplus())
            .imageProfilURL(profil.getImageProfilURL())
            .social(profil.getSocial())
            .gender(profil.getGender())
            .tshirtSize(profil.getTshirtSize());
        users.saveAndFlush(user);
        return new UserProfil(user, true);

    }
}
