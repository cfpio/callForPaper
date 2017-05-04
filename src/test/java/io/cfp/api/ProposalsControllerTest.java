package io.cfp.api;

import io.cfp.entity.Role;
import io.cfp.mapper.ProposalMapper;
import io.cfp.mapper.UserMapper;
import io.cfp.model.Proposal;
import io.cfp.model.User;
import io.cfp.model.queries.ProposalQuery;
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ProposalsController.class)
public class ProposalsControllerTest {

    @MockBean
    private ProposalMapper proposalMapper;

    @MockBean
    private UserMapper userMapper;

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
            .setFormatId(11)
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


        mockMvc.perform(get("/v1/proposals")
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

        when(proposalMapper.findById(eq(10))).thenReturn(proposal);

        mockMvc.perform(get("/v1/proposals/10")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
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
            .andExpect(jsonPath("$.formatId").value("11"))
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

        mockMvc.perform(post("/v1/proposals")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andDo(print())
            .andExpect(status().isForbidden())
        ;
    }

    @Test
    public void should_create_proposals() throws Exception {

        User user = new User();
        user.setEmail("EMAIL");
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        String newProposal = Utils.getContent("/json/proposals/new_proposal.json");

        mockMvc.perform(post("/v1/proposals")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(newProposal)
                .header("Authorization", "Bearer "+token)
            )
            .andDo(print())
            .andExpect(status().isCreated())
        ;
    }

}
