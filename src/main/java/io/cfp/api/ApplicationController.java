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

package io.cfp.api;

import io.cfp.domain.exception.BadRequestException;
import io.cfp.domain.exception.NotFoundException;
import io.cfp.dto.ApplicationSettings;
import io.cfp.entity.Role;
import io.cfp.mapper.EventMapper;
import io.cfp.model.Event;
import io.cfp.multitenant.TenantId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping(value = { "/v1/application", "/api/application" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ApplicationController {

    @Value("${authServer}")
    private String authServer;

    @Autowired
    private EventMapper events;

    /**
     * Obtain application settings, (name, dates, ...)
     * @return
     */
    @GetMapping
    public ApplicationSettings getApplicationSettings(@TenantId String eventId) throws NotFoundException {

        Event event = events.findOne(eventId);
        if (event == null) {
            throw new NotFoundException("No event with ID: "+eventId);
        }

        ApplicationSettings applicationSettings = new ApplicationSettings(event);
        applicationSettings.setAuthServer(authServer);
        return applicationSettings;
    }


    @PostMapping
    @Secured(Role.OWNER)
    public void setApplicationSettings(@RequestBody ApplicationSettings settings,
                                       @TenantId String eventId) throws NotFoundException, BadRequestException {

        Event event = events.findOne(eventId);
        if (event == null) {
            throw new NotFoundException("No event with ID: " + eventId);
        }

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            event = event.setDate(format.parse(settings.getDate()))
                .setDuration(settings.getDuration())
                .setName(settings.getEventName())
                .setLogoUrl(settings.getLogo())
                .setContactMail(settings.getContact())
                .setUrl(settings.getWebsite())
                .setShortDescription(settings.getShortDescription())
                .setDecisionDate(format.parse(settings.getDecisionDate()))
                .setReleaseDate(format.parse(settings.getReleaseDate()))
                .setOpen(settings.isOpen());
        } catch (ParseException e) {
            throw new BadRequestException("Invalid data " + e.getMessage());
        }
        events.update(event);
    }
}
