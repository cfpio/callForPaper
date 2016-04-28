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

package io.cfp.controller.oauth;

import com.nimbusds.jose.JOSEException;
import io.cfp.domain.token.Token;
import io.cfp.entity.User;
import io.cfp.service.auth.AuthUtils;
import io.cfp.service.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

abstract class OAuthController {

    private static final Logger logger = LoggerFactory.getLogger(OAuthController.class);

    private final String CONFLICT_MSG = "There is already a %s account that belongs to you";

    private final String NOT_FOUND_MSG = "User not found";

    @Autowired
    UserService userService;

    /**
     * Return JWT token and eventually persist user according to providerId and provider
     *
     * @param httpServletResponse
     * @param httpServletRequest
     * @param provider
     * @param providerId
     * @param userInfos
     * @return JWT Token
     * @throws IOException
     * @throws JOSEException
     */
    protected Token processUser(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, User.Provider provider, String providerId, User userInfos) throws IOException, JOSEException {
        User user = userService.findByProvider(provider, providerId);

        // If user is already signed in then link accounts.
        User userToSave;
        String authHeader = httpServletRequest.getHeader(AuthUtils.AUTH_HEADER_KEY);
        if (StringUtils.isNotBlank(authHeader)) {
            Integer subject;
            try {
                subject = Integer.valueOf(AuthUtils.getSubject(authHeader));
            } catch (ParseException e) {
                logger.warn(e.getMessage());
                httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                logger.warn("Pb with Auth Header : {}", authHeader);
                return null;
            }

            if (user != null && user.getId() != subject) {
                httpServletResponse.setStatus(HttpServletResponse.SC_CONFLICT);
                httpServletResponse.getWriter().write(String.format(CONFLICT_MSG, provider));
                httpServletResponse.getWriter().flush();
                httpServletResponse.getWriter().close();
                logger.warn("Account conflict {} - {}", provider, providerId);
                return null;
            }

            User foundUser = userService.findById(subject);
            if (foundUser == null) {
                httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                httpServletResponse.getWriter().write(NOT_FOUND_MSG);
                httpServletResponse.getWriter().flush();
                httpServletResponse.getWriter().close();
                logger.warn("User id {} not found during authentication", subject);
                return null;
            }

            userToSave = foundUser;
            userToSave.setProviderId(provider, providerId);
            // merge user informations
            userToSave.mergeData(userInfos);

            userToSave = userService.save(userToSave);
        } else {
            // Create a new user account or return an existing one.
            if (user != null) {
                // Actually not saved
                userToSave = user;
            } else {
                // if email already taken
                user = userService.findByemail(userInfos.getEmail());
                if (user != null) {
                    // User already exist : we merge data
                    userToSave = user;
                } else {
                    userToSave = new User();
                }

                userToSave.setVerified(true);
                userToSave.setProviderId(provider, providerId);
                // merge user informations
                userToSave.mergeData(userInfos);

                userToSave = userService.save(userToSave);
            }
        }

        logger.info("User [{}] logged in with [{}]", userToSave.getEmail(), provider);
        return AuthUtils.createToken(httpServletRequest.getRemoteHost(), "" + userToSave.getId(), true);
    }
}
