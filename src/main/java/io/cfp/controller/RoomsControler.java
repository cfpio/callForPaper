/*
 * Copyright (c) 2016 BreizhCamp
 * [http://breizhcamp.org]
 *
 * This file is part of CFP.io.
 *
 * CFP.io is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.cfp.controller;

import io.cfp.dto.RoomDto;
import io.cfp.entity.Event;
import io.cfp.entity.Role;
import io.cfp.entity.Room;
import io.cfp.repository.EventRepository;
import io.cfp.repository.RoomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(value = { "/v0/rooms", "/api/rooms" }, produces = APPLICATION_JSON_UTF8_VALUE)
public class RoomsControler {

    @Autowired
    private RoomRepo rooms;

    @Autowired
    private EventRepository events;

    @RequestMapping(method = GET)
    public Collection<RoomDto> all() {
        return rooms
            .findByEventId(Event.current())
            .stream()
            .map( r -> new RoomDto(r) )
            .collect(Collectors.toList());
    }

    @RequestMapping(method = POST)
    @Transactional
    @Secured(Role.OWNER)
    public RoomDto create(@RequestBody RoomDto room) {
        return new RoomDto(
            rooms.save(
                new Room()
                    .withEvent(events.findOne(Event.current()))
                    .withName(room.getName())));
    }

    @RequestMapping(value = "/{id}", method = PUT)
    @Transactional
    @Secured(Role.OWNER)
    public void update(@PathVariable int id, @RequestBody RoomDto update) {
    	Room room = rooms.findByIdAndEventId(id, Event.current()); // make sure the track belongs to the current event
    	if (room != null) {
            rooms.save(
                room.withName(update.getName()));
        }
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @Transactional
    @Secured(Role.OWNER)
    public void delete(@PathVariable int id) {
    	Room room = rooms.findByIdAndEventId(id, Event.current()); // make sure the track belongs to the current event
    	if (room != null && !isReferenced(room)) {
            rooms.delete(id);
    	}
    }

    private boolean isReferenced(Room room) {
    	return false; /** TODO check schedule */
    }
}
