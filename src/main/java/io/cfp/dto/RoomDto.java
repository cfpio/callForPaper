package io.cfp.dto;

import io.cfp.entity.Room;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class RoomDto {

    public final int id;
    public final String name;

    public RoomDto(Room room) {
        this.id = room.getId();
        this.name = room.getName();
    }


}
