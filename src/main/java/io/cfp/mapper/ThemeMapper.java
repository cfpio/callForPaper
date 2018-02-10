package io.cfp.mapper;

import io.cfp.model.Stat;
import io.cfp.model.Theme;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Mapper
public interface ThemeMapper {

    List<Theme> findByEvent(String eventId);

    void insert(Theme theme);

    int updateForEvent(@Param("it") Theme theme, @Param("eventId") String eventId);

    void updateEventId(@Param("id") int id, @Param("eventId") String eventId);

    int deleteForEvent(@Param("id") int id, @Param("eventId") String eventId);

    List<Stat> countProposalsByTheme(String eventId);
}
