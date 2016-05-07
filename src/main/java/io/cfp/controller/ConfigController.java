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

import io.cfp.service.admin.config.ApplicationConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * Created by tmaugin on 16/07/2015.
 * SII
 */
@RestController
@RequestMapping(value="api/admin/config", produces = "application/json; charset=utf-8")
public class ConfigController {

    @Autowired
    private ApplicationConfigService applicationConfigService;

    /**
     * Disable or enable submission of new talks
     * @param key enable submission if true, else disable
     * @return key
     */
    @RequestMapping(value="/enableSubmissions", method= RequestMethod.POST)
    public io.cfp.domain.common.Key postEnableSubmissions(@Valid @RequestBody io.cfp.domain.common.Key key) {

        if (key.getKey().equals("true"))
            applicationConfigService.openCfp();
        if (key.getKey().equals("false"))
            applicationConfigService.closeCfp();

        return key;
    }
}
