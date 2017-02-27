package io.cfp.dto;

import io.cfp.entity.Room;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class RoomDto {

    private int id;
    private String name;

    public RoomDto() {
    }

    public RoomDto(Room room) {
        this.id = room.getId();
        this.name = room.getName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
