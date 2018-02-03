package io.cfp.controller.config.security;

import io.cfp.SecurityConfiguration;
import io.cfp.repository.UserRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.mock;

@Configuration
@EnableWebMvc
@Import(SecurityConfiguration.class)
public class SecurityConfig {

    @Bean
    public UserRepo userRepo() {
        return mock(UserRepo.class);
    }

}
