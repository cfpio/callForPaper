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

package io.cfp.config.filter;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import io.cfp.service.auth.AuthUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

/**
 * Filter reading auth token (JWT) to verify if user is correctly logged
 */
@WebFilter(urlPatterns = "/api/restricted/*")
public class AuthFilter implements Filter {

    protected static final String AUTH_ERROR_MSG = "Please make sure your request has an Authorization header",
            EXPIRE_ERROR_MSG = "Token has expired",
            JWT_ERROR_MSG = "Unable to parse JWT",
            JWT_INVALID_MSG = "Invalid JWT token";

    public static final String USER_ID = "user.id";

    /**
     * Do Auth filter according to JWT token
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String authHeader = httpRequest.getHeader(AuthUtils.AUTH_HEADER_KEY);

        try {
            JWTClaimsSet claimsSet = readToken(authHeader);

            if (claimsSet != null) {
                MDC.put(USER_ID, claimsSet.getSubject());
            }

            chain.doFilter(request, response);

        } catch (InvalidTokenException e) {
            httpResponse.sendError(e.getResponseCode(), e.getMessage());
        } finally {
            MDC.remove(USER_ID);
        }
    }

    @Override
    public void destroy() { /* unused */ }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { /* unused */ }

    /**
     * Retrieve auth token from auth header
     * @param authHeader Auth header to read
     * @return Token
     * @throws InvalidTokenException If token is not valid
     */
    protected JWTClaimsSet readToken(String authHeader) throws InvalidTokenException {
        JWTClaimsSet claimSet;
        if (StringUtils.isBlank(authHeader) || authHeader.split(" ").length != 2) {
            throw new InvalidTokenException(HttpServletResponse.SC_UNAUTHORIZED, AUTH_ERROR_MSG);
        }

        try {
            claimSet = (JWTClaimsSet) AuthUtils.decodeToken(authHeader);
        } catch (ParseException e) {
            throw new InvalidTokenException(HttpServletResponse.SC_BAD_REQUEST, JWT_ERROR_MSG);
        } catch (JOSEException e) {
            throw new InvalidTokenException(HttpServletResponse.SC_BAD_REQUEST, JWT_INVALID_MSG);
        }

        // ensure that the token is not expired
        if (new DateTime(claimSet.getExpirationTime()).isBefore(DateTime.now())) {
            throw new InvalidTokenException(HttpServletResponse.SC_UNAUTHORIZED, EXPIRE_ERROR_MSG);
        }
        return claimSet;
    }

    /** Thrown if token is invalid */
    protected class InvalidTokenException extends Exception {
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
