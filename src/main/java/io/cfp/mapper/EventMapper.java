package io.cfp.mapper;

import io.cfp.model.Event;
import io.cfp.model.queries.EventQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Mapper
public interface EventMapper {

    List<Event> findOpen();

    List<Event> findPassed();

    List<Event> findByUser(int user);

    List<Event> findAll(EventQuery eventQuery);

    boolean exists(String id);

    int insert(Event event);
}
