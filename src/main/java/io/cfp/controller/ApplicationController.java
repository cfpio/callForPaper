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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import io.cfp.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.cfp.domain.exception.BadRequestException;
import io.cfp.domain.exception.NotFoundException;
import io.cfp.dto.ApplicationSettings;
import io.cfp.entity.Event;
import io.cfp.repository.EventRepository;

/**
 * Created by tmaugin on 07/05/2015.
 */
@RestController
@RequestMapping(value = { "/v0", "/api" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ApplicationController {

    @Autowired
    private EventRepository events;

    @Value("${authServer}")
    private String authServer;

    /**
     * Obtain application settings, (name, dates, ...)
     * @return
     */
    @RequestMapping(method=RequestMethod.GET, value="/application")
    public ApplicationSettings getApplicationSettings() throws NotFoundException {

        final String name = Event.current();
        Event event = events.findOne(name);
        if (event == null) {
            throw new NotFoundException("No event with ID: "+name);
        }

        ApplicationSettings applicationSettings = new ApplicationSettings(event);
        applicationSettings.setAuthServer(authServer);
        return applicationSettings;
    }


    /**
     * save application settings, (name, dates, ...)
     * @return
     */
    @RequestMapping(method=RequestMethod.POST, value="/application")
    @Secured(Role.OWNER)
    public void getApplicationSettings(@RequestBody ApplicationSettings settings) throws NotFoundException, BadRequestException {

        final String name = Event.current();
        Event event = events.findOne(name);
        if (event == null) {
            throw new NotFoundException("No event with ID: "+name);
        }

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            event = event.toBuilder().date(format.parse(settings.getDate()))
                .duration(settings.getDuration())
                .name(settings.getEventName())
                .logoUrl(settings.getLogo())
                .url(settings.getWebsite())
                .shortDescription(settings.getShortDescription())
                .decisionDate(format.parse(settings.getDecisionDate()))
                .releaseDate(format.parse(settings.getReleaseDate()))
                .open(settings.isOpen())
                .build();
        } catch (ParseException e) {
            throw new BadRequestException("Invalid data "+e.getMessage());
        }
        events.save(event);

    }
}
