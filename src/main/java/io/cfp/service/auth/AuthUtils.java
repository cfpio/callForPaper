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

import java.sql.Date;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.cfp.domain.token.Token;
import io.cfp.entity.User;
import io.cfp.service.user.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public final class AuthUtils {
	
	public static final String AUTH_ERROR_MSG = "Please make sure your request has an Authorization header",
            EXPIRE_ERROR_MSG = "Token has expired",
            JWT_ERROR_MSG = "Unable to parse JWT",
            JWT_INVALID_MSG = "Invalid JWT token";

    private static final String TOKEN_COOKIE_NAME = "token";

    @Autowired
    private UserService userService;
    
    @Value("${token.signing-key}")
    private String signingKey;

    /**
     * Get user from JWT token
     * @param httpRequest
     * @return User
     * @throws InvalidTokenException
     */
    public User getAuthUser(HttpServletRequest httpRequest) throws InvalidTokenException {
        Claims claims = getToken(httpRequest);
        if (claims != null) {
        	String email = claims.getSubject();
        	User user = userService.findByemail(email);
        	if (user == null) {
        		user = new User();
        		user.setEmail(email);
        		user = userService.save(user);
        	}
        	return user;
        }
        return null;
    }

    /**
     * Return get JWT claims set from http request
     * @param httpRequest
     * @return JWT claims set
     */
    private Claims getToken(HttpServletRequest httpRequest) throws InvalidTokenException
    {
    	String tokenValue = null;
    	if (httpRequest.getCookies() != null) {
	    	for (Cookie cookie : httpRequest.getCookies()) {
	    		if (TOKEN_COOKIE_NAME.equals(cookie.getName())) {
	    			tokenValue = cookie.getValue();
	    			break;
	    		}
	    	}
    	}

    	if (tokenValue != null) {
    		try {
                return decodeToken(tokenValue);
            } 
    		catch (ExpiredJwtException ex) {
    			throw new InvalidTokenException(HttpServletResponse.SC_UNAUTHORIZED, EXPIRE_ERROR_MSG);
            }
    		catch (Exception ex) {
                throw new InvalidTokenException(HttpServletResponse.SC_BAD_REQUEST, JWT_ERROR_MSG);
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

    /**
     * Create JWT token
     * @param host
     * @param sub
     * @return
     */
    public Token createToken(String host, String sub) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(sub)
                .setExpiration(Date.from(Instant.now().plus(12, ChronoUnit.HOURS)))
                .signWith(SignatureAlgorithm.HS512, signingKey);

        return new Token(builder.compact());
    }
    
    /** Thrown if token is invalid */
    public static class InvalidTokenException extends Exception {
        private int responseCode;
        private String message;

        public InvalidTokenException(int responseCode, String message) {
            this.responseCode = responseCode;
            this.message = message;
        }

        public int getResponseCode() {
            return responseCode;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
