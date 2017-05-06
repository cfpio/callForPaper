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

package io.cfp.service.auth;

import io.cfp.entity.Role;
import io.cfp.mapper.UserMapper;
import io.cfp.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

@Component
public final class AuthUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthUtils.class);
    private static final String TOKEN_COOKIE_NAME = "token";
    private static final String AUTH_HEADER_NAME = "Authorization";

    @Autowired
    private UserMapper userMapper;

    @Value("${cfpio.authentication_hack:false}")
    private boolean authHack;

    @Value("${token.signing-key}")
    private String signingKey;

    /**
     * Get user from JWT token
     * @param httpRequest
     * @return User
     */
    public User getAuthUser(HttpServletRequest httpRequest) {
        String email;

        Claims claims = getToken(httpRequest);
        if (claims != null) {
            email = claims.getSubject();

        } else if (authHack) {
            email = "john@doe.net";
            User user = new User();
            user.setEmail(email);
            user.addRole(Role.OWNER, Role.ADMIN, Role.REVIEWER);
            return user;
        } else {
            return null;
        }

        User user = userMapper.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            try {
                userMapper.insert(user);
            } catch (DataIntegrityViolationException e) {
                // Handle concurrent insert by parallel requests to API
                user = userMapper.findByEmail(email);
                if (user == null) throw e; // other error
            }
        }
        return user.addRole(Role.AUTHENTICATED);
    }

    /**
     * Return get JWT claims set from http request
     * @param httpRequest
     * @return JWT claims set
     */
    private Claims getToken(HttpServletRequest httpRequest) {
    	String tokenValue = null;
    	if (httpRequest.getCookies() != null) {
	    	for (Cookie cookie : httpRequest.getCookies()) {
	    		if (TOKEN_COOKIE_NAME.equals(cookie.getName())) {
	    			tokenValue = cookie.getValue();
	    			break;
	    		}
	    	}
    	}

        if (tokenValue == null) {
            final String header = httpRequest.getHeader(AUTH_HEADER_NAME);
            if (header != null) {
                tokenValue = getTokenFromHeader(header);
                LOGGER.debug("Found token in Header Authorization with value {}", tokenValue);
            }
        }

    	if (tokenValue != null) {
    		try {
                return decodeToken(tokenValue);
            }
    		catch (Exception ex) {
            }
        }
        return null;
    }

    /**
     * Return get JWT claims set from jwt header
     * @param tokenValue
     * @return
     * @throws ParseException
     */
    private Claims decodeToken(String tokenValue) throws ExpiredJwtException {
        return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(tokenValue).getBody();
    }

    private String getTokenFromHeader(String header) {
        return header.replace("Bearer", "").trim();
    }
}
