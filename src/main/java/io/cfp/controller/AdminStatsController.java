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

import io.cfp.domain.admin.meter.AdminMeter;
import io.cfp.entity.Role;
import io.cfp.service.admin.stats.AdminStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Secured({Role.REVIEWER, Role.ADMIN})
@RequestMapping(value = { "/v0/admin/stats", "/api/admin/stats" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class AdminStatsController {

    @Autowired
    private AdminStatsService adminStatsService;

    /**
     * Get meter stats (talks count, draft count, ...)
     */
    @RequestMapping(value="/meter", method= RequestMethod.GET)
    @ResponseBody
    public AdminMeter getMeter() {
        return adminStatsService.getAdminMeter();
    }
}
