package io.cfp.mapper;

import io.cfp.model.Role;
import io.cfp.model.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Mapper
public interface RoleMapper {

    void insert(Role role);
}
