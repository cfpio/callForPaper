package io.cfp.api;

import io.cfp.dto.RoomDto;
import io.cfp.entity.Role;
import io.cfp.mapper.RoomMapper;
import io.cfp.model.Room;
import io.cfp.multitenant.TenantId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@RestController
@RequestMapping(value = { "/v1/rooms", "/api/rooms" }, produces = APPLICATION_JSON_UTF8_VALUE)
public class RoomsController {


    @Autowired
    private RoomMapper rooms;

    @RequestMapping(method = GET)
    public Collection<Room> all(@TenantId String eventId) {
        return rooms.findByEvent(eventId);
    }

    @RequestMapping(method = POST)
    @Transactional
    @Secured(Role.OWNER)
    public Room create(@RequestBody Room room, @TenantId String eventId) {
        rooms.insert(room.setEvent(eventId));
        return room;
    }

    @RequestMapping(value = "/{id}", method = PUT)
    @Transactional
    @Secured(Role.OWNER)
    public void update(@PathVariable int id, @RequestBody Room room, @TenantId String eventId) {
        rooms.updateForEvent(room.setId(id), eventId);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @Transactional
    @Secured(Role.OWNER)
    public void delete(@PathVariable int id, @TenantId String eventId) {
        rooms.deleteForEvent(id, eventId);
    }

}
