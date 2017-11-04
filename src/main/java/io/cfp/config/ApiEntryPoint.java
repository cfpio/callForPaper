package io.cfp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiEntryPoint implements AuthenticationEntryPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiEntryPoint.class);

    @Value("${authServer}")
    private String hostname;


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws ServletException, IOException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        LOGGER.debug("Pre-authenticated entry point called. Rejecting access");
        response.setHeader("Location", hostname);
        LOGGER.debug("Add Header Location to redirect to {}", hostname);
        LOGGER.debug("Add Header Location to redirect to {}", response.getHeaderNames());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "{'message':'Access Unauthorized'}");
    }
}
