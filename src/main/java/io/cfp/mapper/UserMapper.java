package io.cfp.mapper;

import io.cfp.model.User;
import io.cfp.model.queries.UserQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    List<User> findAll(UserQuery userQuery);
    User findById(User user);
    int create(User user);
    int update(User user);
    int delete(User user);

    boolean exists(String email);

    void insert(User user);

    User findByEmail(@Param("email") String email);
    List<String> findEmailByRole(@Param("role") String role, @Param("eventId") String eventId);

    List<User> findUserWithAcceptedProposal(@Param("eventId") String eventId);

}
