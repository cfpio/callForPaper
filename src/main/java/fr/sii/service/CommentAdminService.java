package fr.sii.service;

import fr.sii.dto.CommentUser;
import fr.sii.entity.AdminUser;
import fr.sii.entity.Comment;
import fr.sii.entity.Event;
import fr.sii.entity.Talk;
import fr.sii.entity.User;
import fr.sii.repository.CommentRepo;
import fr.sii.repository.TalkRepo;
import fr.sii.repository.UserRepo;
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
    public CommentUser addComment(AdminUser admin, int talkId, CommentUser commentUser, boolean internal) {
        Talk talk = talkRepo.findByIdAndEventId(talkId, Event.current());
        if (talk == null) return null;
        List<User> users = userRepo.findByEmail(admin.getEmail());
        if (users.isEmpty()) throw new IllegalStateException("Admin with email [" + admin.getEmail() + "] doesn't exists in user table");

        Comment comment = mapper.map(commentUser, Comment.class);
        comment.setAdded(new Date());
        comment.setTalk(talk);
        comment.setUser(users.get(0));
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
