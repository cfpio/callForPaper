package io.cfp.mapper;

import io.cfp.model.Comment;
import io.cfp.model.Proposal;
import io.cfp.model.User;
import io.cfp.model.queries.CommentQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@MybatisTest
public class CommentMapperTest {

    private static final int COMMENT_ID = 60;
    private static final int COMMENT_ID_TO_DELETE = 61;

    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void should_find_all_public_comments() {
        CommentQuery query = new CommentQuery();
        query.setEventId("EVENT_ID");
        query.setProposalId(20);
        query.setInternal(false);
        Collection<Comment> publicComments = commentMapper.findAll(query);
        assertThat(publicComments).hasSize(1);
    }

    @Test
    public void should_find_all_comments() {
        CommentQuery query = new CommentQuery();
        query.setEventId("EVENT_ID");
        query.setProposalId(20);
        Collection<Comment> allComments = commentMapper.findAll(query);
        assertThat(allComments).hasSize(2);
    }

    @Test
    public void should_find_a_comment_by_id() {
        Comment foundComment = commentMapper.findById(COMMENT_ID, "EVENT_ID");
        assertThat(foundComment).isNotNull();
        assertThat(foundComment.getId()).isEqualTo(COMMENT_ID);
        assertThat(foundComment.getAdded()).isEqualTo("2042-12-31");
        assertThat(foundComment.getComment()).isEqualTo("COMMENT");
        assertThat(foundComment.getUser()).isNotNull();
        assertThat(foundComment.getUser().getId()).isEqualTo(10);
        assertThat(foundComment.getUser().getEmail()).isEqualTo("EMAIL");
        assertThat(foundComment.getUser().getFirstname()).isEqualTo("FIRSTNAME");
        assertThat(foundComment.getUser().getLastname()).isEqualTo("LASTNAME");
        assertThat(foundComment.getEventId()).isEqualTo("EVENT_ID");
        assertThat(foundComment.getProposalId()).isEqualTo(20);

    }

    @Test
    public void should_create_a_comment() {
        Comment comment = new Comment();
        comment.setEventId("EVENT_ID");
        comment.setProposalId(20);
        comment.setUser(new User().setId(10));
        comment.setComment("CREATED_COMMENT");
        comment.setAdded(new Date());

        int createdLines = commentMapper.insert(comment);

        assertThat(createdLines).isEqualTo(1);
        assertThat(comment.getId()).isGreaterThan(0);
    }

    @Test
    public void should_update_a_comment() {
        Comment comment = new Comment();
        comment.setId(COMMENT_ID);
        comment.setComment("UPDATED_COMMENT");
        comment.setEventId("EVENT_ID");
        comment.setProposalId(20);
        comment.setUser(new User().setId(10));
        comment.setAdded(new Date());

        int updatedLines = commentMapper.update(comment);

        assertThat(updatedLines).isEqualTo(1);
    }

    @Test
    public void should_delete_a_comment() {
        Comment comment = new Comment();
        comment.setId(COMMENT_ID_TO_DELETE);
        comment.setEventId("EVENT_ID");
        comment.setProposalId(20);
        comment.setUser(new User().setId(10));

        int deletedLines = commentMapper.delete(comment);

        assertThat(deletedLines).isEqualTo(1);
    }


}
