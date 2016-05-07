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
import io.cfp.domain.exception.NotVerifiedException;
import io.cfp.dto.TalkUser;
import io.cfp.entity.Talk;
import io.cfp.entity.User;
import io.cfp.repository.UserRepo;
import io.cfp.service.TalkUserService;
import io.cfp.service.email.EmailingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(value = "/api/restricted", produces = "application/json; charset=utf-8")
public class SessionController extends RestrictedController {

    @Autowired
    private EmailingService emailingService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TalkUserService talkService;

    /**
     * Add a session
     */
    @RequestMapping(value="/sessions", method=RequestMethod.POST)
    public TalkUser submitTalk(HttpServletRequest req, @Valid @RequestBody TalkUser talkUser) throws Exception, CospeakerNotFoundException  {
        User user = retrieveUser(req);
        TalkUser savedTalk = talkService.submitTalk(user.getId(), talkUser);

        if (user != null && savedTalk != null) {
            Locale userPreferredLocale = req.getLocale();
            emailingService.sendConfirmed(user, savedTalk, userPreferredLocale);
        }

        return savedTalk;
    }

    /**
     * Get all session for the current user
     */
    @RequestMapping(value = "/sessions", method = RequestMethod.GET)
    public List<TalkUser> listTalks(HttpServletRequest req) throws NotVerifiedException {
        User user = retrieveUser(req);

        return talkService.findAll(user.getId(), Talk.State.CONFIRMED, Talk.State.ACCEPTED, Talk.State.REFUSED);
    }

    /**
     * Get a session
     */
    @RequestMapping(value = "/sessions/{talkId}", method = RequestMethod.GET)
    public TalkUser getGoogleSpreadsheet(HttpServletRequest req, @PathVariable Integer talkId) throws NotVerifiedException {
        User user = retrieveUser(req);

        return talkService.getOne(user.getId(), talkId);
    }

    /**
     * Change a draft to a session
     */
    @RequestMapping(value= "/sessions/{talkId}", method=RequestMethod.PUT)
    public TalkUser submitDraftToTalk(HttpServletRequest req, @Valid @RequestBody TalkUser talkUser, @PathVariable Integer talkId) throws Exception, CospeakerNotFoundException {
        User user = retrieveUser(req);

        talkUser.setId(talkId);
        TalkUser savedTalk = talkService.submitDraftToTalk(user.getId(), talkUser);

        if (user != null && savedTalk != null) {
            Locale userPreferredLocale = req.getLocale();
            emailingService.sendConfirmed(user, savedTalk, userPreferredLocale);
        }

        return savedTalk;
    }

    /**
     * Add a new draft
     */
    @RequestMapping(value="/drafts", method=RequestMethod.POST)
    public TalkUser addDraft(HttpServletRequest req, @Valid @RequestBody TalkUser talkUser) throws NotVerifiedException, CospeakerNotFoundException {
        User user = retrieveUser(req);
        return talkService.addDraft(user.getId(), talkUser);
    }

    /**
     * Get all drafts for current user
     */
    @RequestMapping(value = "/drafts", method = RequestMethod.GET)
    public List<TalkUser> listDrafts(HttpServletRequest req) throws NotVerifiedException {
        User user = retrieveUser(req);

        return talkService.findAll(user.getId(), Talk.State.DRAFT);
    }

    /**
     * Get a draft
     */
    @RequestMapping(value = "/drafts/{talkId}", method = RequestMethod.GET)
    public TalkUser getDraft(HttpServletRequest req, @PathVariable Integer talkId) throws NotVerifiedException {
        User user = retrieveUser(req);

        return talkService.getOne(user.getId(), talkId);
    }

    /**
     * Delete a draft
     */
    @RequestMapping(value = "/drafts/{talkId}", method = RequestMethod.DELETE)
    public TalkUser deleteDraft(HttpServletRequest req, @PathVariable Integer talkId) throws NotVerifiedException {
        User user = retrieveUser(req);

        return talkService.deleteDraft(user.getId(), talkId);
    }

    /**
     * Edit a draft
     */
    @RequestMapping(value= "/drafts/{talkId}", method=RequestMethod.PUT)
    public TalkUser editDraft(HttpServletRequest req, @Valid @RequestBody TalkUser talkUser, @PathVariable Integer talkId) throws NotVerifiedException, CospeakerNotFoundException {
        User user = retrieveUser(req);

        talkUser.setId(talkId);
        return talkService.editDraft(user.getId(), talkUser);
    }

    // /**
    //  * Obtain list of talk formats
    //  */
    // @RequestMapping(value = "talk/formats")
    // public List<TalkFormat> getTalkFormat() {
    //     return talkService.getTalkFormat();
    // }
    //
    // /**
    //  * Get all session for the current user
    //  */
    // @RequestMapping(value = "talk/tracks", method = RequestMethod.GET)
    // public List<TrackDto> getTrack() throws NotVerifiedException {
    //     return talkService.getTracks();
    // }



    /**
     * Get all co-draft for the current user
     */
    @RequestMapping(value="/codrafts", method= RequestMethod.GET)
    public List<TalkUser> getCoDrafts(HttpServletRequest req) throws NotVerifiedException {
        User user = retrieveUser(req);
        return talkService.findAllCospeakerTalks(user.getId(), Talk.State.DRAFT);
    }
    /**
     * Get a co-draft for the current user
     */
    @RequestMapping(value="/codrafts/{talkId}", method= RequestMethod.GET)
    public TalkUser getCoDraft(HttpServletRequest req, @PathVariable Integer talkId) throws NotVerifiedException {
        User user = retrieveUser(req);
        TalkUser talk = talkService.getOneCospeakerTalk(user.getId(), talkId);
        return talk;
    }

    /**
     * Get all co-session for the current user
     */
    @RequestMapping(value="/cosessions", method= RequestMethod.GET)
    public List<TalkUser> getCoSessions(HttpServletRequest req) throws NotVerifiedException {
        User user = retrieveUser(req);
        return talkService.findAllCospeakerTalks(user.getId(), Talk.State.CONFIRMED, Talk.State.ACCEPTED, Talk.State.REFUSED);
    }

    /**
     * Get a co-session for the current user
     */
    @RequestMapping(value = "/cosessions/{talkId}", method = RequestMethod.GET)
    public TalkUser getCoSession(HttpServletRequest  req, @PathVariable Integer talkId) throws NotVerifiedException {
        User user = retrieveUser(req);
        TalkUser talk = talkService.getOneCospeakerTalk(user.getId(), talkId);

        return talk;
    }
}
