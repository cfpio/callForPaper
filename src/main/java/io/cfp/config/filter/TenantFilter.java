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

import java.io.IOException;
import java.net.URL;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import io.cfp.entity.Event;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class TenantFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String eventId =  extractTenant(request);
        MDC.put("event.id", eventId);
        Event.setCurrent(eventId.toLowerCase());
        try {
            filterChain.doFilter(request, response);
        } finally {
            Event.unsetCurrent();
            MDC.remove("event.id");
        }
    }

    String extractTenant(HttpServletRequest request) {

        String origin = request.getHeader(HttpHeaders.ORIGIN);
        String referer = request.getHeader(HttpHeaders.REFERER);
        String header = request.getHeader(TENANT_HEADER);
        String path = request.getPathInfo();
        String host = request.getServerName();
        String param = request.getParameter("event");

        if (!StringUtils.isEmpty(param)) {
            // Explicit event request
            return param;
        }

        int i;
        if (path != null && path.startsWith("/events/") && (i = path.indexOf('/', 8)) > 0) {
            // Explicit event URL
            return path.substring(8, i);
        }
        if (host.endsWith(".cfp.io") && !"api.cfp.io".equals(host)) {
            // Requested as https://foo-bar.cfp.io
            return host.substring(0, host.indexOf('.'));
        }
        if (origin != null && origin.endsWith(".cfp.io")) {
            // Origin: https://foo-bar.cfp.io
            return origin.substring(8, origin.indexOf('.'));
        }
        if (referer != null && referer.endsWith(".cfp.io/")) {
            // Referer: https://foo-bar.cfp.io/
            return referer.substring(8, referer.indexOf('.'));
        }
        if (header != null) {
            return header;
        }

        logger.warn("Can't determine current event. Fallback to 'demo'");
        return "demo";
    }

    private static final Logger logger = LoggerFactory.getLogger(TenantFilter.class);
}
