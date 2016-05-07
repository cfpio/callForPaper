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
import io.cfp.domain.exception.NotVerifiedException;
import io.cfp.dto.CommentUser;
import io.cfp.dto.TalkUser;
import io.cfp.entity.User;
import io.cfp.service.CommentUserService;
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
@RequestMapping(value = "api/restricted/sessions/{talkId}/contacts", produces = "application/json; charset=utf-8")
public class ContactController extends RestrictedController {

    @Autowired
    private EmailingService emailingService;

    @Autowired
    private CommentUserService commentService;

    @Autowired
    private TalkUserService talkUserService;

    /**
     * Get all contact message for a given session
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<CommentUser> getAll(@PathVariable int talkId, HttpServletRequest req) throws NotVerifiedException {
        User user = retrieveUser(req);

        return commentService.findAll(user.getId(), talkId);
    }

    /**
     * Add a contact message for the given session
     */
    @RequestMapping(method = RequestMethod.POST)
    public CommentUser postContact(@Valid @RequestBody CommentUser commentUser, @PathVariable int talkId, HttpServletRequest httpServletRequest)
            throws NotVerifiedException, NotFoundException {
        User user = retrieveUser(httpServletRequest);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        TalkUser talk = talkUserService.getOne(user.getId(), talkId);
        CommentUser saved = null;
        if (talk != null) {
            saved = commentService.addComment(user.getId(), talkId, commentUser);

            Locale userPreferredLocale = httpServletRequest.getLocale();
            emailingService.sendNewCommentToAdmins(user, talk, userPreferredLocale);
        }

        return saved;
    }

    /**
     * Edit contact message
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public CommentUser putContact(@PathVariable int id, @Valid @RequestBody CommentUser commentUser, @PathVariable int talkId, HttpServletRequest req)
            throws NotVerifiedException {
        User user = retrieveUser(req);

        commentUser.setId(id);
        return commentService.editComment(user.getId(), talkId, commentUser);
    }

}
