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

import io.cfp.config.global.ServiceProviderSettings;
import io.cfp.domain.exception.NotVerifiedException;
import io.cfp.dto.TrackDto;
import io.cfp.entity.TalkFormat;
import io.cfp.service.TalkUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/api/settings", produces = "application/json; charset=utf-8")
public class SettingsController {

    @Autowired
    private ServiceProviderSettings serviceProviderSettings;

    @Autowired
    private TalkUserService talkService;

    @RequestMapping(value="/serviceproviders", method= RequestMethod.GET)
    public ServiceProviderSettings getServiceProviderSettings() {
        return serviceProviderSettings;
    }

    /**
     * Obtain list of talk formats
     */
    @RequestMapping(value = "/talk/formats")
    public List<TalkFormat> getTalkFormat() {
        return talkService.getTalkFormat();
    }

    /**
     * Get all session for the current user
     */
    @RequestMapping(value = "/talk/tracks", method = RequestMethod.GET)
    public List<TrackDto> getTrack() throws NotVerifiedException {
        return talkService.getTracks();
    }
}
