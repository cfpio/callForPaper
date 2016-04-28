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

import io.cfp.service.admin.config.ApplicationConfigService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = { "/api/restricted/sessions", "/api/restricted/sessions/*" })
public class SubmissionFilter implements Filter {

    private final String SUBMISSION_DISABLED = "Submissions disabled";

    private ApplicationConfigService applicationConfigService;

    /**
     * Reject request if submissions not allowed
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        boolean submissionsAllowed = applicationConfigService.isCfpOpen();

        if(!submissionsAllowed) {
            switch(httpRequest.getMethod().toUpperCase()) {
                case "POST" :
                case "PUT" :
                    httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, SUBMISSION_DISABLED);
                    return;
                default :
                    break;
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() { /* unused */ }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        applicationConfigService = (ApplicationConfigService) webApplicationContext.getBean(ApplicationConfigService.class);
    }

}
