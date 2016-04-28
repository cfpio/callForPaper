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

package io.cfp.controller.common.application;

import io.cfp.dto.ApplicationSettings;
import io.cfp.service.admin.config.ApplicationConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by tmaugin on 07/05/2015.
 */
@RestController
@RequestMapping(value="/api", produces = "application/json; charset=utf-8")
public class ApplicationController {

    @Autowired
    private ApplicationConfigService applicationConfigService;

    /**
     * Obtain application settings, (name, dates, ...)
     * @return
     */
    @RequestMapping(method=RequestMethod.GET, value="/application")
    public ApplicationSettings getApplicationSettings() {

        return applicationConfigService.getAppConfig();
    }


    /**
     * save application settings, (name, dates, ...)
     * @return
     */
    @RequestMapping(method=RequestMethod.POST, value="/admin/application")
    public void getApplicationSettings(@RequestBody ApplicationSettings settings) {
        applicationConfigService.saveConfiguration(settings);
    }
}
