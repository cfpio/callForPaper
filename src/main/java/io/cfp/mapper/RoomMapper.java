package io.cfp.mapper;

import io.cfp.model.Room;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Mapper
public interface RoomMapper {

    Collection<Room> findByEvent(String eventId);

    void insert(Room room);

    void updateForEvent(@Param("it") Room room, @Param("eventId") String eventId);

    void deleteForEvent(@Param("id") int id, @Param("eventId") String eventId);

}
