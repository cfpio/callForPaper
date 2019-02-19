package io.cfp.mapper;

import io.cfp.model.Proposal;
import io.cfp.model.User;
import io.cfp.model.queries.UserQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
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
        assertThat(foundUser.getEmail()).isEqualTo("EMAIL");
        assertThat(foundUser.getGender()).isEqualTo(User.Gender.FEMALE);
        assertThat(foundUser.getTshirtSize()).isEqualTo(User.TshirtSize.L);
    }

    @Test
    public void should_find_all_users_with_email() {
        UserQuery userQuery = new UserQuery();
        userQuery.setEmail("EMAIL");
        List<User> allUsersWithEmail = userMapper.findAll(userQuery);
        assertThat(allUsersWithEmail).isNotEmpty();
    }

    @Test
    public void should_find_all_users_with_an_accepted_proposal() {
        UserQuery userQuery = new UserQuery();
        userQuery.setStates(Arrays.asList(Proposal.State.ACCEPTED));
        userQuery.setEventId("EVENT_ID");
        List<User> allUsersWithAcceptedProposal = userMapper.findAll(userQuery);
        assertThat(allUsersWithAcceptedProposal).isNotEmpty();
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
        user.setGender(User.Gender.MALE);
        user.setTshirtSize(User.TshirtSize.M);

        int updatedLines = userMapper.update(user);

        assertThat(updatedLines).isEqualTo(1);

        User foundUser = userMapper.findById(user);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("UPDATED_EMAIL");
        assertThat(foundUser.getGender()).isEqualTo(User.Gender.MALE);
        assertThat(foundUser.getTshirtSize()).isEqualTo(User.TshirtSize.M);
    }

    @Test
    public void should_delete_a_user() {
        User user = new User();
        user.setId(USER_ID_TO_DELETE);
        int deletedLines = userMapper.delete(user);

        assertThat(deletedLines).isEqualTo(1);
    }


}
