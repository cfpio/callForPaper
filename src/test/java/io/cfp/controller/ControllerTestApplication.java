package io.cfp.controller;

import io.cfp.SecurityConfiguration;
import io.cfp.WebConfiguration;
import io.cfp.config.exception.GlobalControllerExceptionHandler;
import io.cfp.config.filter.AuthFilter;
import io.cfp.mapper.RoleMapper;
import io.cfp.service.auth.AuthUtils;
import io.cfp.service.user.SecurityUserService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.mock;

@SpringBootApplication
@EnableWebMvc
@Import({SecurityConfiguration.class, WebConfiguration.class })
class ControllerTestApplication {

    @Bean
    public SecurityUserService securityUserService() {
        return new SecurityUserService();
    }

    @Bean
    public RoleMapper roleMapper() {
        return mock(RoleMapper.class);
    }

    @Bean
    public AuthFilter authFilter() {
        AuthFilter authFilter = new AuthFilter();
        authFilter.setAuthUtils(authUtils());
        authFilter.setRoleMapper(roleMapper());
        return authFilter;
    }

    @Bean
    public AuthUtils authUtils() {
        return new AuthUtils();
    }

    @Bean
    public GlobalControllerExceptionHandler exceptionHandler() {
        return new GlobalControllerExceptionHandler();
    }

}
