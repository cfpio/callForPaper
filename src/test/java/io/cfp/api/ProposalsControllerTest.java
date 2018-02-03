package io.cfp.api;

import io.cfp.mapper.CoSpeakerMapper;
import io.cfp.mapper.ProposalMapper;
import io.cfp.mapper.RateMapper;
import io.cfp.mapper.UserMapper;
import io.cfp.model.Proposal;
import io.cfp.model.Role;
import io.cfp.model.User;
import io.cfp.model.queries.ProposalQuery;
import io.cfp.service.PdfCardService;
import io.cfp.service.email.EmailingService;
import io.cfp.utils.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ProposalsController.class)
public class ProposalsControllerTest {

    @MockBean
    private ProposalMapper proposalMapper;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private RateMapper rateMapper;

    @MockBean
    private CoSpeakerMapper coSpeakerMapper;

    @MockBean
    private EmailingService emailingService;

    @MockBean
    private PdfCardService pdfCardService;

    @Autowired
    private MockMvc mockMvc;

    private Proposal proposal;

    @Before
    public void setUp() {

        User speaker = new User()
            .setId(20)
            .setEmail("EMAIL");

        proposal = new Proposal()
            .setId(10)
            .setState(Proposal.State.ACCEPTED)
            .setAdded(new Date())
            .setDescription("DESCRIPTION")
            .setEventId("EVENT_ID")
            .setFormat(11)
            .setLanguage("LANGUAGE")
            .setDifficulty(1)
            .setName("NAME")
            .setReferences("REFERENCES")
            .setRoomId(12)
            .setSchedule(new Date())
            .setSlides("SLIDES")
            .setVideo("VIDEO")
            .setTrackId(13)
            .setTrackLabel("TRACK_LABEL")
            .setSpeaker(speaker);
    }

    @Test
    public void should_get_proposals() throws Exception {

        List<Proposal> proposals = new ArrayList<>();
        proposals.add(proposal);

        when(proposalMapper.findAll(any(ProposalQuery.class))).thenReturn(proposals);

        User user = new User();
        user.setEmail("EMAIL");
        user.addRole(Role.ADMIN);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);


        mockMvc.perform(get("/api/proposals")
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
    public void should_get_proposal_by_id() throws Exception {

        when(proposalMapper.findById(eq(10), anyString())).thenReturn(proposal);

        User user = new User();
        user.setId(20);
        user.setEmail("EMAIL");
        user.addRole(Role.ADMIN);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);


        mockMvc.perform(get("/api/proposals/10")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value("10"))
            .andExpect(jsonPath("$.state").value("ACCEPTED"))
            .andExpect(jsonPath("$.description").value("DESCRIPTION"))
            .andExpect(jsonPath("$.eventId").value("EVENT_ID"))
            .andExpect(jsonPath("$.language").value("LANGUAGE"))
            .andExpect(jsonPath("$.roomId").value("12"))
            .andExpect(jsonPath("$.format").value("11"))
            .andExpect(jsonPath("$.difficulty").value("1"))
            .andExpect(jsonPath("$.name").value("NAME"))
            .andExpect(jsonPath("$.references").value("REFERENCES"))
            .andExpect(jsonPath("$.slides").value("SLIDES"))
            .andExpect(jsonPath("$.video").value("VIDEO"))
            .andExpect(jsonPath("$.trackId").value("13"))
            .andExpect(jsonPath("$.trackLabel").value("TRACK_LABEL"))
            .andExpect(jsonPath("$.speaker.id").value("20"))
        ;
    }

    @Test
    public void should_not_authorise_anonymous_to_create_proposals() throws Exception {

        String newProposal = Utils.getContent("/json/proposals/new_proposal.json");

        mockMvc.perform(post("/api/proposals")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(newProposal)
        )
            .andDo(print())
            .andExpect(status().isUnauthorized())
        ;
    }

    @Test
    public void should_create_proposals() throws Exception {

        User user = new User();
        user.setId(20);
        user.setEmail("EMAIL");
        user.addRole(Role.AUTHENTICATED);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        String newProposal = Utils.getContent("/json/proposals/new_proposal.json");

        mockMvc.perform(post("/api/proposals")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
            .content(newProposal)
        )
            .andDo(print())
            .andExpect(status().isCreated())
        ;
    }

    @Test
    public void should_not_create_invalid_proposals() throws Exception {

        User user = new User();
        user.setId(20);
        user.setEmail("EMAIL");
        user.addRole(Role.AUTHENTICATED);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        String invalidProposal = Utils.getContent("/json/proposals/invalid_proposal.json");

        mockMvc.perform(post("/api/proposals")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
            .content(invalidProposal)
        )
            .andDo(print())
            .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void should_update_my_proposals() throws Exception {

        User user = new User();
        user.setId(20);
        user.setEmail("EMAIL");
        user.addRole(Role.AUTHENTICATED);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        String updatedProposal = Utils.getContent("/json/proposals/other_proposal.json");

        mockMvc.perform(put("/api/proposals/25")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
            .content(updatedProposal)
        )
            .andDo(print())
            .andExpect(status().isNoContent())
        ;
    }

    @Test
    public void should_not_update_others_proposals() throws Exception {

        User user = new User();
        user.setId(21);
        user.setEmail("EMAIL");
        user.addRole(Role.AUTHENTICATED);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        String updatedProposal = Utils.getContent("/json/proposals/other_proposal.json");

        mockMvc.perform(put("/api/proposals/25")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
            .content(updatedProposal)
        )
            .andDo(print())
            .andExpect(status().isForbidden())
        ;
    }

    @Test
    public void should_email_confirmation_when_proposal_is_confirmed() throws Exception {

        User user = new User();
        user.setId(21);
        user.setEmail("EMAIL");
        user.setFirstname("FIRSTNAME");
        user.addRole(Role.AUTHENTICATED);
        String token = Utils.createTokenForUser(user);
        Proposal proposal = new Proposal().setId(25)
            .setName("PROPOSAL_NAME")
            .setSpeaker(new User().setId(21));

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);
        when(proposalMapper.findById(eq(25), anyString())).thenReturn(proposal);

        String updatedProposal = Utils.getContent("/json/proposals/other_proposal.json");

        mockMvc.perform(put("/api/proposals/25/confirm")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
            .content(updatedProposal)
        )
            .andDo(print())
            .andExpect(status().isNoContent())
        ;

        verify(emailingService).sendConfirmed(eq(user), eq(proposal));
    }

    /* FIXME will need to make it clearer what we consider an "invalid proposal"
    @Test
    public void should_not_update_invalid_proposals() throws Exception {

        User user = new User();
        user.setId(21);
        user.setEmail("EMAIL");
        user.addRole(Role.AUTHENTICATED);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        String invalidProposal = Utils.getContent("/json/proposals/invalid_proposal.json");

        mockMvc.perform(put("/api/proposals/25")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
            .content(invalidProposal)
        )
            .andDo(print())
            .andExpect(status().isBadRequest())
        ;
    }           */

}
