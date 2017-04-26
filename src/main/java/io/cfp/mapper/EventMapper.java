package io.cfp.mapper;

import io.cfp.model.Event;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Mapper
public interface EventMapper {

    @Select("select * from events where open=true")
    List<Event> findOpen();

    @Select("select * from events e where date < current_date()")
    List<Event> findPassed();

    @Select("select * from events e where exists (select * from roles r where r.event_id = e.id AND r.user_id = #{user})")
    List<Event> findByUser(int user);

    @Select("select exists(select 1 from events where id=#{id})")
    boolean exists(String id);

    @Insert("insert into events (id, name) values (#{id}, #{name})")
    void insert(Event event);
}
