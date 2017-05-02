package io.cfp.repository;

import io.cfp.JpaTestConfig;
import io.cfp.entity.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JpaTestConfig.class)
@DirtiesContext
public class RoleRepositoryTest {

    private static final int USER_ID = 10;
    private static final String EVENT_ID = "EVENT_ID";

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void should_find_all_roles_of_users_for_an_event() {
        final List<Role> returnedRoles = roleRepository.findByUserIdAndEventId(USER_ID, EVENT_ID);
        assertThat(returnedRoles).isNotEmpty();
    }


}
