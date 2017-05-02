package io.cfp.mapper;

import io.cfp.model.User;
import io.cfp.model.queries.UserQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@MybatisTest
public class UserMapperTest {

    private static final int USER_ID = 10;
    private static final int USER_ID_TO_DELETE = 11;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void should_find_all_users() {
        List<User> allUsers = userMapper.findAll(new UserQuery());
        assertThat(allUsers).isNotEmpty();
    }

    @Test
    public void should_find_a_user_by_id() {
        User user = new User();
        user.setId(USER_ID);
        User foundUser = userMapper.findById(user);
        assertThat(foundUser).isNotNull();
    }

    @Test
    public void should_find_all_users_with_email() {
        UserQuery userQuery = new UserQuery();
        userQuery.setEmail("EMAIL");
        List<User> allUsersWithEmail = userMapper.findAll(userQuery);
        assertThat(allUsersWithEmail).isNotEmpty();
    }

    @Test
    public void should_find_a_user_by_email() {
        User foundUser = userMapper.findByEmail("EMAIL");
        assertThat(foundUser).isNotNull();
    }

    @Test
    public void should_find_emails_for_a_roles_and_event() {
        List<String> foundEmails = userMapper.findEmailByRole("ROLE_ADMIN", "EVENT_ID");
        assertThat(foundEmails).isNotEmpty();
    }

    @Test
    public void should_find_users_with_accepted_proposals_by_event() {
        List<User> foundUsers = userMapper.findUserWithAcceptedProposal( "EVENT_ID");
        assertThat(foundUsers).isNotEmpty();
    }

    @Test
    public void should_create_a_user() {
        User user = new User();
        user.setEmail("CREATED_EMAIL");
        int createdLines = userMapper.insert(user);

        assertThat(createdLines).isEqualTo(1);
        assertThat(user.getId()).isGreaterThan(0);
    }

    @Test
    public void should_update_a_user() {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail("UPDATED_EMAIL");
        int updatedLines = userMapper.update(user);

        assertThat(updatedLines).isEqualTo(1);
    }

    @Test
    public void should_delete_a_user() {
        User user = new User();
        user.setId(USER_ID_TO_DELETE);
        int deletedLines = userMapper.delete(user);

        assertThat(deletedLines).isEqualTo(1);
    }


}
