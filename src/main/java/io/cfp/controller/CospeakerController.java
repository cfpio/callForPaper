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

import io.cfp.domain.exception.NotVerifiedException;
import io.cfp.dto.TalkUser;
import io.cfp.entity.Role;
import io.cfp.entity.Talk;
import io.cfp.model.User;
import io.cfp.service.TalkUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = { "/v0", "/api" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CospeakerController {

    @Autowired
    private TalkUserService talkService;

    /**
     * Get all co-draft for the current user
     */
    @RequestMapping(value="/codrafts", method= RequestMethod.GET)
    @Secured(Role.AUTHENTICATED)
    public List<TalkUser> getCoDrafts(@AuthenticationPrincipal User user) throws NotVerifiedException {
        return talkService.findAllCospeakerTalks(user.getId(), Talk.State.DRAFT);
    }

    /**
     * Get a co-draft for the current user
     */
    @RequestMapping(value="/codrafts/{talkId}", method= RequestMethod.GET)
    @Secured(Role.AUTHENTICATED)
    public TalkUser getCoDraft(@AuthenticationPrincipal User user, @PathVariable Integer talkId) throws NotVerifiedException {
        TalkUser talk = talkService.getOneCospeakerTalk(user.getId(), talkId);
        return talk;
    }


    /**
     * Get all co-session for the current user
     */
    @RequestMapping(value="/cosessions", method= RequestMethod.GET)
    @Secured(Role.AUTHENTICATED)
    public List<TalkUser> getCoSessions(@AuthenticationPrincipal User user) throws NotVerifiedException {
        return talkService.findAllCospeakerTalks(user.getId(), Talk.State.CONFIRMED, Talk.State.ACCEPTED, Talk.State.REFUSED);
    }

    /**
     * Get a co-session for the current user
     */
    @RequestMapping(value = "/cosessions/{talkId}", method = RequestMethod.GET)
    @Secured(Role.AUTHENTICATED)
    public TalkUser getCoSession(@AuthenticationPrincipal User user, @PathVariable Integer talkId) throws NotVerifiedException {
        TalkUser talk = talkService.getOneCospeakerTalk(user.getId(), talkId);

        return talk;
    }
}
