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

package io.cfp.service;

import io.cfp.dto.CommentUser;
import io.cfp.entity.Comment;
import io.cfp.entity.Event;
import io.cfp.entity.Talk;
import io.cfp.entity.User;
import io.cfp.repository.CommentRepo;
import io.cfp.repository.TalkRepo;
import io.cfp.repository.UserRepo;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Service for managing comment by the admin
 */
@Service
@Transactional
public class CommentAdminService {

    @Autowired
    private TalkRepo talkRepo;

    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MapperFacade mapper;

    /**
     * Retrieve all comments for the talk
     * @param talkId Talk to retrieve comments from
     * @param internal Is the comment is internal to admins or visible to the user
     * @return List of comments
     */
    public List<CommentUser> findAll(int talkId, boolean internal) {
        List<Comment> comments = commentRepo.findByEventIdAndTalkIdAndInternal(Event.current(), talkId, internal);
        return mapper.mapAsList(comments, CommentUser.class);
    }

    /**
     * Add a comment to a talk
     * @param admin Admin which add the comment
     * @param talkId Talk to associated with the comment
     * @param commentUser Comment to add
     * @param internal Is the comment is internal to admins or visible to the user
     * @return Added comment or null if talk doesn't exists
     */
    public CommentUser addComment(User admin, int talkId, CommentUser commentUser, boolean internal) {
        Talk talk = talkRepo.findByIdAndEventId(talkId, Event.current());
        if (talk == null) return null;
        User user = userRepo.findByEmail(admin.getEmail());
        if (user == null) throw new IllegalStateException("Admin with email [" + admin.getEmail() + "] doesn't exists in user table");

        Comment comment = mapper.map(commentUser, Comment.class);
        comment.setAdded(new Date());
        comment.setTalk(talk);
        comment.setUser(user);
        comment.setInternal(internal);

        Comment saved = commentRepo.save(comment);
        commentRepo.flush();

        return mapper.map(saved, CommentUser.class);
    }

    /**
     * Edit a comment associated to a talk
     * @param commentUser Comment to edit
     * @return Edited comment or null if talk doesn't
     */
    public CommentUser editComment(CommentUser commentUser) {
        Comment comment = commentRepo.findByIdAndEventId(commentUser.getId(), Event.current());
        if (comment == null) return null;

        mapper.map(commentUser, comment);
        commentRepo.flush();

        return mapper.map(comment, CommentUser.class);
    }

    /**
     * Delete a id
     * @param id Comment to delete
     * @return Deleted id
     */
    public void delete(int id) {
        commentRepo.deleteByIdAndEventId(id, Event.current());
    }
}
