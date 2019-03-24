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

import io.cfp.entity.Role;
import io.cfp.service.admin.config.ApplicationConfigService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.slf4j.LoggerFactory.getLogger;


/**
 * Created by tmaugin on 16/07/2015.
 * SII
 */
@RestController
@RequestMapping(value = { "/v0/config" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Deprecated
public class ConfigController {

    private static final Logger LOGGER = getLogger(ConfigController.class);

    @Autowired
    private ApplicationConfigService applicationConfigService;

    public ConfigController() {
     LOGGER.info("EHLLLO");
    }

    /**
     * Disable or enable submission of new talks
     * @param key enable submission if true, else disable
     * @return key
     */
    @RequestMapping(value="/enableSubmissions", method= RequestMethod.POST)
    @Secured(Role.ADMIN)
    public io.cfp.domain.common.Key postEnableSubmissions(@Valid @RequestBody io.cfp.domain.common.Key key) {

        if (key.getKey().equals("true"))
            applicationConfigService.openCfp();
        if (key.getKey().equals("false"))
            applicationConfigService.closeCfp();

        return key;
    }
}
