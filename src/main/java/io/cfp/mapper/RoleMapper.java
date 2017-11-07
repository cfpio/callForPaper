package io.cfp.mapper;

import io.cfp.model.Role;
import io.cfp.model.queries.RoleQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Mapper
public interface RoleMapper {

    int insert(Role role);
    int delete(Role role);
    List<Role> findAll(RoleQuery roleQuery);

    void updateEventId(@Param("id") int id, @Param("eventId") String eventId);
}
