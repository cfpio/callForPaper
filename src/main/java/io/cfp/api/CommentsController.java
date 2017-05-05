package io.cfp.api;

import io.cfp.entity.Role;
import io.cfp.mapper.CommentMapper;
import io.cfp.model.Comment;
import io.cfp.model.User;
import io.cfp.model.queries.CommentQuery;
import io.cfp.multitenant.TenantId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static io.cfp.model.Role.ADMIN;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(value = { "/v1/proposals/{proposalId}/comments", "/api/proposals/{proposalId}/comments" }, produces = APPLICATION_JSON_UTF8_VALUE)
public class CommentsController {


    @Autowired
    private CommentMapper comments;

    @RequestMapping(method = GET)
    public Collection<Comment> all(@AuthenticationPrincipal User user,
                                   @PathVariable int proposalId,
                                   @TenantId String eventId) {
        CommentQuery query = new CommentQuery();
        query.setEventId(eventId);
        query.setProposalId(proposalId);

        if (user.hasRole(ADMIN)) {
            query.setInternal(true);
        }

        return comments.findByEventAndProposal(query);
    }

    @RequestMapping(method = POST)
    @Transactional
    @Secured(Role.OWNER)
    public Comment create(@AuthenticationPrincipal User user,
                          @PathVariable int proposalId,
                          @RequestBody Comment comment,
                          @TenantId String eventId) {

        comment.setEventId(eventId);
        comment.setUser(user);
        comment.setProposalId(proposalId);

        comments.insert(comment);
        return comment;
    }

    @RequestMapping(value = "/{id}", method = PUT)
    @Transactional
    @Secured(Role.OWNER)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable int proposalId,
                       @PathVariable int id,
                       @AuthenticationPrincipal User user,
                       @RequestBody Comment comment,
                       @TenantId String eventId) {
        comment.setId(id);
        comment.setEventId(eventId);
        comment.setUser(user);
        comment.setProposalId(proposalId);

        comments.update(comment);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @Transactional
    @Secured(Role.OWNER)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int proposalId,
                       @PathVariable int id,
                       @AuthenticationPrincipal User user,
                       @TenantId String eventId) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setUser(user);
        comment.setEventId(eventId);
        comment.setProposalId(proposalId);

        comments.delete(comment);
    }

}
