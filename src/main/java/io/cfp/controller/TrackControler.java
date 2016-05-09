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

import io.cfp.dto.TrackDto;
import io.cfp.entity.Event;
import io.cfp.entity.Track;
import io.cfp.repository.EventRepository;
import io.cfp.repository.TrackRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@RestController
@RequestMapping(value = "/api/tracks", produces = APPLICATION_JSON_UTF8_VALUE)
public class TrackControler {

    @Autowired
    private TrackRepo tracks;

    @Autowired
    private EventRepository events;

    @RequestMapping(method = GET)
    public Collection<TrackDto> all() {
        return tracks
            .findByEventId(Event.current())
            .stream()
            .map( t -> new TrackDto(t) )
            .collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}", method = GET)
    public TrackDto getOne(@PathVariable int id) {
        return new TrackDto(tracks.findByIdAndEventId(id, Event.current()));
    }

    @RequestMapping(method = POST)
    @Secured("ADMIN")
    public TrackDto create(@RequestBody TrackDto track) {
        return new TrackDto(
            tracks.save(
                new Track()
                    .withEvent(events.findOne(Event.current()))
                    .withLibelle(track.getLibelle())
                    .withDescription(track.getDescription())
            ));
    }

    @RequestMapping(value = "/{id}", method = PUT)
    @Secured("ADMIN")
    public void update(@PathVariable int id, @RequestBody TrackDto track) {
        tracks.save(
            tracks.getOne(id)
                .withLibelle(track.getLibelle())
                .withDescription(track.getDescription())

        );
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @Secured("ADMIN")
    public void delete(@PathVariable int id) {
        tracks.delete(id);
    }
}
