package io.cfp.controller.config.security;

import io.cfp.SecurityConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@Import(SecurityConfiguration.class)
public class SecurityConfig {

}
