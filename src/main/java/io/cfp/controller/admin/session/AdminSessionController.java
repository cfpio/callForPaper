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

package io.cfp.controller.admin.session;

import io.cfp.dto.TalkAdmin;
import io.cfp.entity.Talk;
import io.cfp.service.TalkAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import io.cfp.domain.exception.CospeakerNotFoundException;


import java.util.List;

@Controller
@RequestMapping(value="api/admin", produces = "application/json; charset=utf-8")
public class AdminSessionController {

    @Autowired
    private TalkAdminService talkService;

    /**
     * Get all sessions
     */
    @RequestMapping(value="/sessions", method= RequestMethod.GET)
    @ResponseBody
    public List<TalkAdmin> getAllSessions() {
        return talkService.findAll(Talk.State.CONFIRMED, Talk.State.ACCEPTED, Talk.State.REFUSED);
    }

    /**
     * Get all drafts
     */
    @RequestMapping(value="/drafts", method= RequestMethod.GET)
    @ResponseBody
    public List<TalkAdmin> getAllDrafts() {
        return talkService.findAll(Talk.State.DRAFT);
    }

    /**
     * Get a specific session
     */
    @RequestMapping(value= "/sessions/{talkId}", method= RequestMethod.GET)
    @ResponseBody
    public TalkAdmin getTalk(@PathVariable int talkId) {
        return talkService.getOne(talkId);
    }

    /**
     * Edit a specific session
     */
    @RequestMapping(value= "/sessions/{talkId}", method= RequestMethod.PUT)
    @ResponseBody
    public TalkAdmin editTalk(@PathVariable int talkId, @RequestBody TalkAdmin talkAdmin) throws CospeakerNotFoundException{
        talkAdmin.setId(talkId);
        return talkService.edit(talkAdmin);
    }

    /**
     * Delete a session
     */
    @RequestMapping(value="/sessions/{talkId}", method= RequestMethod.DELETE)
    @ResponseBody
    public TalkAdmin deleteGoogleSpreadsheet(@PathVariable int talkId) {
        return talkService.delete(talkId);
    }
}
