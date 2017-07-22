package io.cfp.mapper;

import io.cfp.model.Role;
import io.cfp.model.queries.RoleQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@MybatisTest
public class RoleMapperTest {

    private static final int USER_ID = 10;
    private static final int ROLE_ID_TO_DELETE = 11;

    @Autowired
    private RoleMapper roleMapper;

    @Test
    public void should_find_all_roles() {
        List<Role> allRoles = roleMapper.findAll(new RoleQuery());
        assertThat(allRoles).isNotEmpty();
    }

    @Test
    public void should_find_all_roles_with_event() {
        RoleQuery roleQuery = new RoleQuery();
        roleQuery.setEventId("EVENT_ID");
        List<Role> allRolesWithEvent = roleMapper.findAll(roleQuery);
        assertThat(allRolesWithEvent).isNotEmpty();
    }

    @Test
    public void should_find_all_roles_with_user() {
        RoleQuery roleQuery = new RoleQuery();
        roleQuery.setUserId(USER_ID);
        List<Role> allRolesWithUser = roleMapper.findAll(roleQuery);
        assertThat(allRolesWithUser).isNotEmpty();
    }

    @Test
    public void should_create_a_role() {
        Role role = new Role();
        role.setName("CREATED_ROLE");
        int createdLines = roleMapper.insert(role);

        assertThat(createdLines).isEqualTo(1);
        assertThat(role.getId()).isGreaterThan(0);
    }

    @Test
    public void should_delete_a_role() {
        Role role = new Role();
        role.setId(ROLE_ID_TO_DELETE);
        role.setEvent("EVENT_ID");

        int deletedLines = roleMapper.delete(role);

        assertThat(deletedLines).isEqualTo(1);
    }


}
