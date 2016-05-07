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

import io.cfp.domain.exception.NotFoundException;
import io.cfp.dto.RateAdmin;
import io.cfp.service.RateAdminService;
import io.cfp.service.admin.user.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value="api/admin/rates", produces = "application/json; charset=utf-8")
public class AdminRateController {

    @Autowired
    private AdminUserService adminUserServiceCustom;

    @Autowired
    private RateAdminService rateService;

    /**
     * Get all ratings
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<RateAdmin> getRates() {
        return rateService.getAll();
    }

    /**
     * Delete all ratings
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteRates() {
        rateService.deleteAll();
    }

    /**
     * Add a new rating
     */
    @RequestMapping(method=RequestMethod.POST)
    public RateAdmin postRate(@Valid @RequestBody RateAdmin rate) throws NotFoundException {
        return rateService.add(rate, adminUserServiceCustom.getCurrentUser(), rate.getTalkId());
    }

    /**
     * Edit a rating
     */
    @RequestMapping(value= "/{rateId}", method=RequestMethod.PUT)
    public RateAdmin putRate(@PathVariable int rateId, @Valid @RequestBody RateAdmin rate) {
        rate.setId(rateId);
        return rateService.edit(rate);
    }

    /**
     * Get a specific rating
     */
    @RequestMapping(value= "/{rateId}", method= RequestMethod.GET)
    public RateAdmin getRate(@PathVariable int rateId) {
        return rateService.get(rateId);
    }

    /**
     * Get all rating for a given user
     * @param userId
     * @return
     */
    @RequestMapping(value= "/user/{userId}", method= RequestMethod.GET)
    public List<RateAdmin> getRateByUserId(@PathVariable int userId) {
        return rateService.findForUser(userId);
    }

    /**
     * Get all ratings for a given session
     */
    @RequestMapping(value= "/session/{talkId}", method= RequestMethod.GET)
    public List<RateAdmin> getRatesByTalkId(@PathVariable int talkId) {
        return rateService.findForTalk(talkId);
    }

    /**
     * Get rating for current user and a session
     */
    @RequestMapping(value= "/session/{talkId}/user/me", method = RequestMethod.GET)
    public RateAdmin getRateByRowIdAndUserId(@PathVariable int talkId) throws NotFoundException {
        int adminId = adminUserServiceCustom.getCurrentUser().getId();
        return rateService.findForTalkAndAdmin(talkId, adminId);
    }

    /**
     * Delete specific rating
     */
    @RequestMapping(value= "/{rateId}", method= RequestMethod.DELETE)
    public void deleteRate(@PathVariable int rateId) {
        rateService.delete(rateId);
    }
}
