package io.cfp.config.filter;

import io.cfp.entity.Event;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class TenantFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String host = new URL(request.getRequestURL().toString()).getHost();

        int i = host.indexOf('.');
        String tenant = (i > 0 ? host.substring(0, i) : "default");
        Event.setCurrent(tenant);
    }
}
