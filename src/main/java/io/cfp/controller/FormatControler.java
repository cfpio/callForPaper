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

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.cfp.dto.FormatDto;
import io.cfp.entity.Event;
import io.cfp.entity.Format;
import io.cfp.entity.Role;
import io.cfp.repository.EventRepository;
import io.cfp.repository.FormatRepo;
import io.cfp.repository.TalkRepo;

@RestController
@RequestMapping(value = { "/v0/formats", "/api/formats" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class FormatControler {

    @Autowired
    private FormatRepo formats;

    @Autowired
    private EventRepository events;

    @Autowired
    private TalkRepo talks;

    @RequestMapping(method = GET)
    public Collection<FormatDto> all() {
        return formats
            .findByEventId(Event.current())
            .stream()
            .map( t -> new FormatDto(t, isReferenced(t)) )
            .collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}", method = GET)
    public FormatDto getOne(@PathVariable int id) {
    	Format format = formats.findByIdAndEventId(id, Event.current());
        return new FormatDto(format, isReferenced(format));
    }

    @RequestMapping(method = POST)
    @Transactional
    @Secured(Role.OWNER)
    public FormatDto create(@RequestBody FormatDto format) {
        return new FormatDto(
            formats.save(
                new Format()
                    .event(events.findOne(Event.current()))
                    .name(format.getName())
                    .duration(format.getDuration())
                    .description(format.getDescription())
            ), false);
    }

    @RequestMapping(value = "/{id}", method = PUT)
    @Transactional
    @Secured(Role.OWNER)
    public void update(@PathVariable int id, @RequestBody FormatDto update) {
    	Format format = formats.findByIdAndEventId(id, Event.current()); // make sure the format belongs to the current event
    	if (format != null) {
	        formats.save(
	            format
	                .name(update.getName())
	                .duration(update.getDuration())
	                .description(update.getDescription())
	        );
    	}
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @Transactional
    @Secured(Role.OWNER)
    public void delete(@PathVariable int id) {
    	Format format = formats.findByIdAndEventId(id, Event.current()); // make sure the format belongs to the current event
    	if (format != null && !isReferenced(format)) {
    		formats.delete(id);
    	}
    }

    private boolean isReferenced(Format format) {
    	return talks.countByEventIdAndFormat(Event.current(), format) > 0;
    }
}
