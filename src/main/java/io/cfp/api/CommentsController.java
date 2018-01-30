package io.cfp.api;

import io.cfp.domain.exception.ForbiddenException;
import io.cfp.entity.Role;
import io.cfp.mapper.CommentMapper;
import io.cfp.mapper.ProposalMapper;
import io.cfp.model.Comment;
import io.cfp.model.Proposal;
import io.cfp.model.User;
import io.cfp.model.queries.CommentQuery;
import io.cfp.multitenant.TenantId;
import io.cfp.service.email.EmailingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Date;

import static io.cfp.model.Role.REVIEWER;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = { "/v1/proposals/{proposalId}/comments", "/api/proposals/{proposalId}/comments" }, produces = APPLICATION_JSON_UTF8_VALUE)
public class CommentsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentsController.class);

    @Autowired
    private CommentMapper comments;

    @Autowired
    private ProposalMapper proposals;

    @Autowired
    private EmailingService emailingService;

    @GetMapping
    @Secured(Role.AUTHENTICATED)
    public Collection<Comment> all(@AuthenticationPrincipal User user,
                                   @PathVariable int proposalId,
                                   @TenantId String eventId) {
        LOGGER.info("Get comments of proposal {}", proposalId);
        CommentQuery query = new CommentQuery();
        query.setEventId(eventId);
        query.setProposalId(proposalId);

        if (!user.hasRole(REVIEWER)) {
            query.setInternal(false);

            // FIXME speaker should not be able to read comments on other's proposals
        }

        return comments.findAll(query);
    }

    @PostMapping
    @Transactional
    @Secured(Role.AUTHENTICATED)
    @ResponseStatus(HttpStatus.CREATED)
    public Comment create(@AuthenticationPrincipal User user,
                          @PathVariable int proposalId,
                          @RequestBody Comment comment,
                          @TenantId String eventId) {
        Proposal proposal = proposals.findById(proposalId, eventId);

        // si on est pas reviewer, on ne peut poster de commentaires que sur son propre proposal
        if (!user.hasRole(Role.REVIEWER)) {
            if (proposal.getSpeaker().getId() != user.getId()) {
                throw new ForbiddenException();
            }
        }

        LOGGER.info("User {} add a comment on proposal {}", user.getId(), proposalId);
        comment.setEventId(eventId);
        comment.setUser(user);
        comment.setProposalId(proposalId);
        comment.setAdded(new Date());

        comments.insert(comment);

        if (comment.isInternal()) {
            emailingService.sendNewCommentToAdmins(user, proposal, comment.getComment());
        } else {
            emailingService.sendNewCommentToSpeaker(user, proposal, comment.getComment());
        }
        return comment;
    }

    @PutMapping(value = "/{id}")
    @Transactional
    @Secured(Role.AUTHENTICATED)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable int proposalId,
                       @PathVariable int id,
                       @AuthenticationPrincipal User user,
                       @RequestBody Comment comment,
                       @TenantId String eventId) {
        LOGGER.info("User {} update its comment on proposal {}", user.getId(), proposalId);
        Proposal proposal = proposals.findById(proposalId, eventId);

        comment.setId(id);
        comment.setEventId(eventId);
        comment.setUser(user);
        comment.setProposalId(proposalId);

        if (!user.hasRole(REVIEWER)) {
            comment.setInternal(false);
        }

        comments.update(comment);

        if (comment.isInternal()) {
            emailingService.sendNewCommentToAdmins(user, proposal, comment.getComment());
        } else {
            emailingService.sendNewCommentToSpeaker(user, proposal, comment.getComment());
        }
    }

    @DeleteMapping(value = "/{id}")
    @Transactional
    @Secured(Role.OWNER)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int proposalId,
                       @PathVariable int id,
                       @AuthenticationPrincipal User user,
                       @TenantId String eventId) {
        LOGGER.info("User {} delete its comment", user.getId());
        Comment comment = new Comment();
        comment.setId(id);
        comment.setUser(user);
        comment.setEventId(eventId);
        comment.setProposalId(proposalId);

        comments.delete(comment);
    }

}
