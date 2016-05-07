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
import io.cfp.entity.User;
import io.cfp.service.CommentAdminService;
import io.cfp.service.admin.user.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value="api/admin/sessions/{talkId}/comments", produces = "application/json; charset=utf-8")
public class AdminCommentController {

    @Autowired
    private AdminUserService adminUserServiceCustom;

    @Autowired
    private CommentAdminService commentService;

    /**
     * Get all comment message for a given session
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<CommentUser> getAll(@PathVariable int talkId) {
        return commentService.findAll(talkId, true);
    }

    /**
     * Add new comment message to a session
     */
    @RequestMapping(method=RequestMethod.POST)
    public CommentUser postComment(@Valid @RequestBody CommentUser comment, @PathVariable int talkId) throws NotFoundException, IOException {
        User admin = adminUserServiceCustom.getCurrentUser();
        return commentService.addComment(admin, talkId, comment, true);
    }

    /**
     * Edit comment message
     */
    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
    public CommentUser putComment(@PathVariable int id, @Valid @RequestBody CommentUser comment) throws NotFoundException, ForbiddenException {
        comment.setId(id);
        return commentService.editComment(comment);
    }

    /**
     * Delete comment message
     */
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public void deleteComment(@PathVariable int id) throws NotFoundException, ForbiddenException {
        commentService.delete(id);
    }

}
