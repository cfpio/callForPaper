package io.cfp.api;

import io.cfp.SecurityConfiguration;
import io.cfp.WebConfiguration;
import io.cfp.config.filter.AuthFilter;
import io.cfp.repository.RoleRepository;
import io.cfp.repository.UserRepo;
import io.cfp.service.auth.AuthUtils;
import io.cfp.service.user.SecurityUserService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@SpringBootApplication
@Import({SecurityConfiguration.class, WebConfiguration.class })
class ApiTestApplication {

    @Bean
    public SecurityUserService securityUserService() {
        return new SecurityUserService();
    }

    @Bean
    public UserRepo userRepo() {
        return mock(UserRepo.class);
    }

    @Bean
    public RoleRepository roleRepository() {
        return mock(RoleRepository.class);
    }

    @Bean
    public AuthFilter authFilter() {
        AuthFilter authFilter = new AuthFilter();
        authFilter.setAuthUtils(authUtils());
        authFilter.setRoleRepository(roleRepository());
        return authFilter;
    }

    @Bean
    public AuthUtils authUtils() {
        return new AuthUtils();
    }



}
