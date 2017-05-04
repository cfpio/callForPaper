package io.cfp.mapper;

import io.cfp.model.Format;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;


/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Mapper
public interface FormatMapper {

    Collection<Format> findByEvent(String eventId);

    void insert(Format format);

    void updateForEvent(@Param("it") Format format, @Param("eventId") String eventId);

    void deleteForEvent(@Param("id") int id, @Param("eventId") String eventId);
}
