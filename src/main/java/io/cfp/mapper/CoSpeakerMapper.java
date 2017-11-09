package io.cfp.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Mapper
public interface CoSpeakerMapper {

    void insert(@Param("proposal") int proposal, @Param("user") int user);

    void delete(@Param("proposal") int id);
}
