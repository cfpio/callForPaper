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

import com.google.common.annotations.VisibleForTesting;
import io.cfp.domain.common.UserAuthentication;
import io.cfp.entity.Event;
import io.cfp.entity.Role;
import io.cfp.entity.User;
import io.cfp.repository.RoleRepository;
import io.cfp.service.auth.AuthUtils;
import org.apache.log4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Filter reading auth token (JWT) to verify if user is correctly logged
 */
public class AuthFilter implements Filter {

    private static final String USER = "user";

    private AuthUtils authUtils;
    private RoleRepository roleRepository;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        authUtils = webApplicationContext.getBean(AuthUtils.class);
        roleRepository = webApplicationContext.getBean(RoleRepository.class);
    }

    /**
     * Do Auth filter according to JWT token
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // FIXME Hack temporaire le temps de basculer l'ensemble du modele
        io.cfp.model.User newUser = authUtils.getAuthUser(httpRequest);
        User user = null;
        if (newUser != null) {
            user = new User(newUser);
        }
        User.setCurrent(user);

        if (user != null) {
            List<Role> roles = roleRepository.findByUserIdAndEventId(user.getId(), Event.current());
            for (Role role : roles) {
                if (Role.ADMIN.equals(role.getName())) {
                    // until exact roles are well set on API, Admin implies reviewer role
                    user.addRole(Role.REVIEWER);
                }
            	user.addRole(role.getName());
            }
            MDC.put(USER, user.toLog());
            SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
        }

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(USER);
            SecurityContextHolder.getContext().setAuthentication(null);
            User.resetCurrent();
        }
    }

    @Override
    public void destroy() { /* unused */ }

    @VisibleForTesting
    public void setAuthUtils(AuthUtils authUtils) {
        this.authUtils = authUtils;
    }

    @VisibleForTesting
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
}
