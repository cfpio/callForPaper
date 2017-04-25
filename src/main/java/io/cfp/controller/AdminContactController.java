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

import io.cfp.domain.exception.ForbiddenException;
import io.cfp.domain.exception.NotFoundException;
import io.cfp.dto.CommentUser;
import io.cfp.dto.TalkAdmin;
import io.cfp.entity.Role;
import io.cfp.entity.User;
import io.cfp.service.CommentAdminService;
import io.cfp.service.TalkAdminService;
import io.cfp.service.admin.user.AdminUserService;
import io.cfp.service.email.EmailingService;
import io.cfp.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Manages comments sent by administrators to speakers about a talk
 */
@RestController
@Secured(Role.REVIEWER)
@RequestMapping(value = { "/v0/admin/sessions/{talkId}/contacts", "/api/admin/sessions/{talkId}/contacts" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class AdminContactController {

    @Autowired
    private AdminUserService adminUserServiceCustom;

    @Autowired
    private EmailingService emailingService;

    @Autowired
    private CommentAdminService commentService;

    @Autowired
    private TalkAdminService talkService;

    @Autowired
    private UserService userService;

    /**
     * Get all contact message for a given session
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<CommentUser> getAll(@PathVariable int talkId) {
        return commentService.findAll(talkId, false);
    }

    /**
     * Add new contact message to a session
     */
    @RequestMapping(method = RequestMethod.POST)
    public CommentUser postContact(@Valid @RequestBody CommentUser comment, @PathVariable int talkId, HttpServletRequest httpServletRequest) throws NotFoundException, IOException {

        User admin = adminUserServiceCustom.getCurrentUser();
        TalkAdmin talk = talkService.getOne(talkId);
        User user = userService.findById(talk.getUserId());

        CommentUser saved = commentService.addComment(admin, talkId, comment, false);

        // Send new message email
        if (user != null) {
            Locale userPreferredLocale = httpServletRequest.getLocale();
            emailingService.sendNewCommentToSpeaker(user, talk, userPreferredLocale);
        }

        return saved;
    }

    /**
     * Edit contact message
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public CommentUser putContact(@PathVariable int id, @Valid @RequestBody CommentUser comment) throws NotFoundException, ForbiddenException {
        comment.setId(id);
        return commentService.editComment(comment);
    }
}
