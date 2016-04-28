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

import com.nimbusds.jwt.JWTClaimsSet;
import io.cfp.entity.AdminUser;
import io.cfp.service.admin.user.AdminUserService;
import io.cfp.service.auth.AuthUtils;
import org.slf4j.MDC;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter reading auth token and check if user is admin
 */
@WebFilter(urlPatterns = "/api/admin/*")
public class AuthAdminFilter extends AuthFilter {

    private AdminUserService adminUserService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        adminUserService = webApplicationContext.getBean(AdminUserService.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String authHeader = httpRequest.getHeader(AuthUtils.AUTH_HEADER_KEY);

        try {
            JWTClaimsSet token = readToken(authHeader);
            int userId = Integer.parseInt(token.getSubject());

            AdminUser admin = adminUserService.findFromUserId(userId);
            if (admin == null) throw new InvalidTokenException(HttpServletResponse.SC_UNAUTHORIZED, AUTH_ERROR_MSG);

            MDC.put(USER_ID, String.valueOf(userId));
            adminUserService.setCurrentAdmin(admin);
            chain.doFilter(request, response);

        } catch (InvalidTokenException e) {
            httpResponse.sendError(e.getResponseCode(), e.getMessage());

        } finally {
            MDC.remove(USER_ID);
        }
    }
}
