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

import io.cfp.domain.exception.NotVerifiedException;
import io.cfp.entity.User;
import io.cfp.service.auth.AuthUtils;
import io.cfp.service.auth.AuthUtils.InvalidTokenException;

public abstract class RestrictedController {

    
    @Autowired
    private AuthUtils authUtils;

    /**
     * Retrieve user from request token
     * @param req Request
     * @return User from token
     * @throws NotVerifiedException If token invalid and user id can't be read
     */
    protected User retrieveUser(HttpServletRequest req) throws NotVerifiedException {
        User user = null;
        try {
        	user = authUtils.getAuthUser(req);
        }
        catch (InvalidTokenException ex) {
        	//Nothing?
        }
        if (user == null) {
            throw new NotVerifiedException("Claims Set is null");
        }

        return user;
    }
}
