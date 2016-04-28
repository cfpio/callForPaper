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

package io.cfp.controller.admin.meter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.cfp.domain.admin.meter.AdminMeter;
import io.cfp.service.admin.stats.AdminStatsService;

@Controller
@RequestMapping(value="api/admin/stats", produces = "application/json; charset=utf-8")
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
