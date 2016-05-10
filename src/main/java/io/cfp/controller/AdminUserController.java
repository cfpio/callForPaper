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

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.cfp.dto.AdminUserInfo;
import io.cfp.entity.User;
import io.cfp.service.admin.user.AdminUserService;
import io.cfp.service.auth.AuthUtils;
import io.cfp.service.auth.AuthUtils.InvalidTokenException;

@RestController
@RequestMapping(value="/api/adminUser")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private AuthUtils authUtils;

    /**
     * Obtain current admin user information
     */
    @RequestMapping(value="/currentUser", method= RequestMethod.GET)
    public AdminUserInfo getCurrentUser(HttpServletRequest req) {
    	
    	User user = null;
    	try {
    		user = authUtils.getAuthUser(req);
    	}
    	catch (InvalidTokenException ex) {
    		//Nothing?
    	}

        if (user == null) {
            return new AdminUserInfo("./", false, false, false, "");
        }

        User adminUser = adminUserService.findFromEmail(user.getEmail());

        if (adminUser == null) {
            return new AdminUserInfo("./logout", true, false, true, user.getEmail());
        }

        return new AdminUserInfo("./logout", true, true, true, adminUser.getEmail());
    }
}
