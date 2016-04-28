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

package io.cfp.controller.common.user;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jwt.JWTClaimsSet;

import io.cfp.dto.AdminUserInfo;
import io.cfp.entity.AdminUser;
import io.cfp.service.admin.user.AdminUserService;
import io.cfp.service.auth.AuthUtils;

@RestController
@RequestMapping(value="/api/adminUser")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;


    /**
     * Obtain current admin user information
     */
    @RequestMapping(value="/currentUser", method= RequestMethod.GET)
    public AdminUserInfo getCurrentUser(HttpServletRequest req) {
        JWTClaimsSet claimsSet = AuthUtils.getTokenBody(req);

        if (claimsSet == null) {
            return new AdminUserInfo("./", false, false, false, "");
        }

        AdminUser adminUser = adminUserService.findFromUserId(Integer.parseInt(claimsSet.getSubject()));

        if (adminUser == null) {
            return new AdminUserInfo("./", false, false, false, "");
        }

        return new AdminUserInfo("./logout", true, true, true, adminUser.getEmail());
    }
}
