package io.cfp.api;

import io.cfp.mapper.CommentMapper;
import io.cfp.mapper.ProposalMapper;
import io.cfp.mapper.UserMapper;
import io.cfp.model.Comment;
import io.cfp.model.Proposal;
import io.cfp.model.Role;
import io.cfp.model.User;
import io.cfp.model.queries.CommentQuery;
import io.cfp.service.email.EmailingService;
import io.cfp.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CommentsController.class)
public class CommentsControllerTest {

    @MockBean
    private CommentMapper commentMapper;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private ProposalMapper proposalMapper;

    @MockBean
    private EmailingService emailingService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_get_comments() throws Exception {

        List<Comment> comments = new ArrayList<>();

        Comment comment = new Comment()
            .setId(10)
            .setComment("COMMENT");

        comments.add(comment);

        when(commentMapper.findAll(any(CommentQuery.class))).thenReturn(comments);

        User user = new User();
        user.setEmail("EMAIL");
        user.addRole(Role.ADMIN);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);


        mockMvc.perform(get("/api/proposals/25/comments")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$[0].id").value("10"))
        ;
    }

    @Test
    public void should_not_authorise_anonymous_to_create_comments() throws Exception {

        String newComment = Utils.getContent("/json/comments/new_comment.json");

        mockMvc.perform(post("/api/proposals/25/comments")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(newComment)
        )
            .andDo(print())
            .andExpect(status().isUnauthorized())
        ;
    }

    @Test
    public void should_create_comments() throws Exception {

        User user = new User();
        user.setId(20);
        user.setEmail("EMAIL");
        user.addRole(Role.AUTHENTICATED);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        when(proposalMapper.findById(eq(25), anyString())).thenReturn(new Proposal().setSpeaker(new User().setId(20)));

        String newComment = Utils.getContent("/json/comments/new_comment.json");

        mockMvc.perform(post("/api/proposals/25/comments")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
            .content(newComment)
        )
            .andDo(print())
            .andExpect(status().isCreated())
        ;
    }

    @Test
    public void should_authorize_reviewer_to_create_comments_on_all_proposals() throws Exception {

        User user = new User();
        user.setId(20);
        user.setEmail("EMAIL");
        user.addRole(Role.REVIEWER);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        when(proposalMapper.findById(eq(25), anyString())).thenReturn(new Proposal().setSpeaker(new User().setId(21)));

        String newComment = Utils.getContent("/json/comments/new_comment.json");

        mockMvc.perform(post("/api/proposals/25/comments")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
            .content(newComment)
        )
            .andDo(print())
            .andExpect(status().isCreated())
        ;
    }

    @Test
    public void should_not_authorize_user_to_create_comments_on_other_proposals() throws Exception {

        User user = new User();
        user.setId(20);
        user.setEmail("EMAIL");
        user.addRole(Role.AUTHENTICATED);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        when(proposalMapper.findById(eq(25), anyString())).thenReturn(new Proposal().setSpeaker(new User().setId(21)));

        String newComment = Utils.getContent("/json/comments/new_comment.json");

        mockMvc.perform(post("/api/proposals/25/comments")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
            .content(newComment)
        )
            .andDo(print())
            .andExpect(status().isForbidden())
        ;
    }

    @Test
    public void should_update_my_comments() throws Exception {

        User user = new User();
        user.setId(20);
        user.setEmail("EMAIL");
        user.addRole(Role.AUTHENTICATED);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);
        when(proposalMapper.findById(eq(25), anyString())).thenReturn(new Proposal().setId(25)
            .setName("PROPOSAL_NAME")
            .setSpeaker(new User().setId(21)));

        String updatedComment = Utils.getContent("/json/comments/update_comment.json");

        mockMvc.perform(put("/api/proposals/25/comments/10")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
            .content(updatedComment)
        )
            .andDo(print())
            .andExpect(status().isNoContent())
        ;
    }

    @Test
    public void should_email_admin_if_internal_comment_is_updated_by_admin() throws Exception {

        User user = new User();
        user.setId(20);
        user.setEmail("EMAIL");
        user.addRole(Role.REVIEWER);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);
        when(proposalMapper.findById(eq(25), anyString())).thenReturn(new Proposal().setId(25)
                                                                                         .setName("PROPOSAL_NAME")
                                                                                         .setSpeaker(new User().setId(21)));

        String updatedComment = Utils.getContent("/json/comments/update_internal_comment.json");

        mockMvc.perform(put("/api/proposals/25/comments/10")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
            .content(updatedComment)
        )
            .andDo(print())
            .andExpect(status().isNoContent())
        ;

        verify(emailingService).sendNewCommentToAdmins(eq(user), anyString(), anyInt(), any(Locale.class));
    }

    @Test
    public void should_email_speaker_if_comment_is_updated_by_speaker() throws Exception {

        User user = new User();
        user.setId(20);
        user.setEmail("EMAIL");
        user.addRole(Role.AUTHENTICATED);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);
        when(proposalMapper.findById(eq(25), anyString())).thenReturn(new Proposal().setId(25)
            .setName("PROPOSAL_NAME")
            .setSpeaker(new User().setId(21)));

        String updatedComment = Utils.getContent("/json/comments/update_comment.json");

        mockMvc.perform(put("/api/proposals/25/comments/10")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
            .content(updatedComment)
        )
            .andDo(print())
            .andExpect(status().isNoContent())
        ;

        verify(emailingService).sendNewCommentToSpeaker(eq(user), anyString(), anyInt(), any(Locale.class));
    }

    @Test
    public void should_email_admin_if_internal_comment_is_created_by_admin() throws Exception {

        User user = new User();
        user.setId(20);
        user.setEmail("EMAIL");
        user.addRole(Role.REVIEWER);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);
        when(proposalMapper.findById(eq(25), anyString())).thenReturn(new Proposal().setId(25)
            .setName("PROPOSAL_NAME")
            .setSpeaker(new User().setId(21)));

        String updatedComment = Utils.getContent("/json/comments/new_internal_comment.json");

        mockMvc.perform(post("/api/proposals/25/comments")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
            .content(updatedComment)
        )
            .andDo(print())
            .andExpect(status().isCreated())
        ;

        verify(emailingService).sendNewCommentToAdmins(eq(user), anyString(), anyInt(), any(Locale.class));
    }

    @Test
    public void should_email_speaker_if_comment_is_created_by_speaker() throws Exception {

        User user = new User();
        user.setId(21);
        user.setEmail("EMAIL");
        user.addRole(Role.AUTHENTICATED);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);
        when(proposalMapper.findById(eq(25), anyString())).thenReturn(new Proposal().setId(25)
            .setName("PROPOSAL_NAME")
            .setSpeaker(new User().setId(21)));

        String updatedComment = Utils.getContent("/json/comments/new_comment.json");

        mockMvc.perform(post("/api/proposals/25/comments")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
            .content(updatedComment)
        )
            .andDo(print())
            .andExpect(status().isCreated())
        ;

        verify(emailingService).sendNewCommentToSpeaker(eq(user), anyString(), anyInt(), any(Locale.class));
    }

}
