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

import io.cfp.domain.exception.CospeakerNotFoundException;
import io.cfp.domain.exception.NotFoundException;
import io.cfp.domain.exception.NotVerifiedException;
import io.cfp.dto.TalkAdmin;
import io.cfp.dto.TalkUser;
import io.cfp.entity.Role;
import io.cfp.entity.Talk;
import io.cfp.entity.User;
import io.cfp.repository.TalkRepo;
import io.cfp.service.TalkUserService;
import io.cfp.service.email.EmailingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = { "/v0" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class SessionController  {

    @Autowired
    private EmailingService emailingService;

    @Autowired
    private TalkUserService talkService;

    @Autowired
    private TalkRepo talks;


    /**
     * Add a session
     */
    @RequestMapping(value="/proposals", method=RequestMethod.POST)
    @Secured(Role.AUTHENTICATED)
    public TalkUser submitTalk(@AuthenticationPrincipal User user, @Valid @RequestBody TalkUser talkUser) throws Exception, CospeakerNotFoundException  {
        TalkUser savedTalk = talkService.submitTalk(user.getId(), talkUser);

        if (user != null && savedTalk != null) {
            emailingService.sendConfirmed(user, savedTalk, user.getLocale());
        }

        return savedTalk;
    }

    /**
     * Get all session for the current user
     */
    @RequestMapping(value = "/proposals", method = RequestMethod.GET)
    @Secured(Role.AUTHENTICATED)
    public List<TalkUser> listTalks(@AuthenticationPrincipal User user) throws NotVerifiedException {
        return talkService.findAll(user.getId(), Talk.State.CONFIRMED, Talk.State.ACCEPTED, Talk.State.REFUSED);
    }

    /**
     * Get a session
     */
    @RequestMapping(value = "/proposals/{talkId}", method = RequestMethod.GET)
    public TalkUser get(@AuthenticationPrincipal User user, @PathVariable Integer talkId) throws NotFoundException {
        final Talk t = talks.findOne(talkId);
        if (t.getState() == Talk.State.ACCEPTED) {
            // This is an accepted talk, so we can just expose it publicly
            return new TalkUser(t);
        }
        if (user != null) {
            if (t.getUser().getId() == user.getId()) {
                // A user can access his own data
                return new TalkAdmin(t);
            }
            if (user.hasRole(Role.REVIEWER)) {
                return new TalkAdmin(t);
            }
        }
        throw new NotFoundException();
    }

    /**
     * Change a draft to a session
     */
    @RequestMapping(value= "/proposals/{talkId}", method=RequestMethod.PUT)
    @Secured(Role.AUTHENTICATED)
    public TalkUser submitDraftToTalk(@AuthenticationPrincipal User user, @Valid @RequestBody TalkUser talkUser, @PathVariable Integer talkId) throws Exception, CospeakerNotFoundException {
        talkUser.setId(talkId);
        TalkUser savedTalk = talkService.submitDraftToTalk(user.getId(), talkUser);

        if (user != null && savedTalk != null) {
            emailingService.sendConfirmed(user, savedTalk, user.getLocale());
        }

        return savedTalk;
    }


}
