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

package io.cfp.controller.restricted.user;

import io.cfp.controller.restricted.RestrictedController;
import io.cfp.domain.exception.NotFoundException;
import io.cfp.domain.exception.NotVerifiedException;
import io.cfp.dto.user.UserProfil;
import io.cfp.entity.User;
import io.cfp.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/restricted", produces = "application/json; charset=utf-8")
public class UserController extends RestrictedController {

    @Autowired
    private UserService userService;

    /**
     * Get current user profil
     *
     * @param req
     * @return
     * @throws NotVerifiedException
     * @throws NotFoundException
     * @throws IOException
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Map<String, Object> getUserProfil(HttpServletRequest req) throws NotVerifiedException, NotFoundException, IOException {
        int userId = retrieveUserId(req);

        User u = userService.findById(userId);
        if (u == null) {
            throw new NotFoundException("User id [" + userId + "] not found");
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("email", u.getEmail());
        map.put("lastname", u.getLastname());
        map.put("firstname", u.getFirstname());
        map.put("phone", u.getPhone());
        map.put("company", u.getCompany());
        map.put("bio", u.getBio());
        map.put("social", u.getSocial());
        map.put("twitter", u.getTwitter());
        map.put("googleplus", u.getGoogleplus());
        map.put("github", u.getGithub());
        map.put("imageProfilURL", u.getImageProfilURL());

        return map;
    }

    /**
     * Edit current user profil
     *
     * @param req
     * @param profil
     * @return
     * @throws NotVerifiedException
     * @throws NotFoundException
     * @throws IOException
     */
    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public UserProfil putUserProfil(HttpServletRequest req, @RequestBody UserProfil profil) throws NotVerifiedException, NotFoundException, IOException {
        int userId = retrieveUserId(req);

        User u = userService.findById(userId);
        if (u == null) {
            throw new NotFoundException("User id [" + userId + "] not found");
        }
        profil.setEmail(u.getEmail());
        userService.update(u.getId(), profil);

        return profil;

    }
}
