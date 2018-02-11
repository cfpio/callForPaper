package io.cfp.mapper;

import io.cfp.model.Room;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Mapper
public interface RoomMapper {

    List<Room> findByEvent(String eventId);

    Room findById(int id);

    void insert(Room room);

    void updateForEvent(@Param("it") Room room, @Param("eventId") String eventId);

    void updateEventId(@Param("id") int id, @Param("eventId") String eventId);

    void deleteForEvent(@Param("id") int id, @Param("eventId") String eventId);
}
