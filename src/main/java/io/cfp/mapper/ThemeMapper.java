package io.cfp.mapper;

import io.cfp.model.Theme;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;


/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Mapper
public interface ThemeMapper {

    Collection<Theme> findByEvent(String eventId);

    void insert(Theme theme);

    void updateForEvent(@Param("it") Theme theme, @Param("eventId") String eventId);

    void deleteForEvent(@Param("id") int id, @Param("eventId") String eventId);
}
