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

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.cfp.dto.TrackDto;
import io.cfp.entity.Event;
import io.cfp.entity.Role;
import io.cfp.entity.Track;
import io.cfp.repository.EventRepository;
import io.cfp.repository.TalkRepo;
import io.cfp.repository.TrackRepo;

@RestController
@RequestMapping(value = { "/v0/tracks", "/api/tracks" }, produces = APPLICATION_JSON_UTF8_VALUE)
public class TrackControler {

    @Autowired
    private TrackRepo tracks;

    @Autowired
    private EventRepository events;

    @Autowired
    private TalkRepo talks;

    @RequestMapping(method = GET)
    public Collection<TrackDto> all() {
        return tracks
            .findByEventId(Event.current())
            .stream()
            .map( t -> new TrackDto(t, isReferenced(t)) )
            .collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}", method = GET)
    public TrackDto getOne(@PathVariable int id) {
    	Track track = tracks.findByIdAndEventId(id, Event.current());
        return new TrackDto(track, isReferenced(track));
    }

    @RequestMapping(method = POST)
    @Transactional
    @Secured(Role.OWNER)
    public TrackDto create(@RequestBody TrackDto track) {
        return new TrackDto(
            tracks.save(
                new Track()
                    .event(events.findOne(Event.current()))
                    .libelle(track.getLibelle())
                    .color(track.getColor())
                    .description(track.getDescription())
            ), false);
    }

    @RequestMapping(value = "/{id}", method = PUT)
    @Transactional
    @Secured(Role.OWNER)
    public void update(@PathVariable int id, @RequestBody TrackDto update) {
    	Track track = tracks.findByIdAndEventId(id, Event.current()); // make sure the track belongs to the current event
    	if (track != null) {
	        tracks.save(
	            track
	                .libelle(update.getLibelle())
                    .color(track.getColor())
	                .description(update.getDescription())

	        );
    	}
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @Transactional
    @Secured(Role.OWNER)
    public void delete(@PathVariable int id) {
    	Track track = tracks.findByIdAndEventId(id, Event.current()); // make sure the track belongs to the current event
    	if (track != null && !isReferenced(track)) {
    		tracks.delete(id);
    	}
    }

    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    @Secured(Role.ADMIN)
    public Map<String, Long> getStats() {
        return talks.countByTrack(Event.current()).stream()
            .collect(Collectors.toMap(t -> (String) t[0], t -> (Long) t[1]));
    }


    private boolean isReferenced(Track track) {
    	return talks.countByEventIdAndTrack(Event.current(), track) > 0;
    }
}
