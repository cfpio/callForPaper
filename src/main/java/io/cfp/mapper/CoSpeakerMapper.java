package io.cfp.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Mapper
public interface CoSpeakerMapper {

    void insert(int proposal, String user);
}
