package io.cfp.api;

import io.cfp.dto.EventSched;
import io.cfp.dto.TalkAdmin;
import io.cfp.mapper.UserMapper;
import io.cfp.model.Proposal;
import io.cfp.model.Role;
import io.cfp.model.User;
import io.cfp.service.TalkAdminService;
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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebMvcTest(ExportController.class)
public class ExportControllerTest {

    @MockBean
    private TalkAdminService talkAdminService;

    @MockBean
    private UserMapper userMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_export_proposals_to_sched_format() throws Exception {

        User user = new User();
        user.setId(20);
        user.setEmail("EMAIL");
        user.addRole(Role.ADMIN);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        ArrayList<EventSched> scheds = new ArrayList<>();
        EventSched eventSched = new EventSched();
        eventSched.setId("ID");
        scheds.add(eventSched);


        when(talkAdminService.exportSched(anyString(), any(Proposal.State[].class))).thenReturn(scheds);

        mockMvc.perform(get("/v1/admin/sessions/export/sched.json")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer " + token)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$[0].id").value("ID"))
        ;
    }

    @Test
    public void should_export_proposals_to_csv_format() throws Exception {

        User user = new User();
        user.setId(20);
        user.setEmail("EMAIL");
        user.addRole(Role.ADMIN);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        List<TalkAdmin> talkAdmins = new ArrayList<>();
        TalkAdmin talkAdmin = new TalkAdmin();
        talkAdmin.setId(30);
        talkAdmin.setName("NAME");
        talkAdmins.add(talkAdmin);


        when(talkAdminService.findAll(anyString(), eq(20), anyVararg())).thenReturn(talkAdmins);

        mockMvc.perform(get("/api/admin/sessions/export/sessions.csv")
            .accept("text/csv")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer " + token)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv"))
            .andExpect(content().string("added,description,difficulty,format,id,language,mean,name,references,reviewed,room,schedule,slides,bio,company,email,firstname,gender,github,googleplus,id,imageProfilURL,language,lastname,phone,shortName,social,tshirtSize,twitter,state,trackId,trackLabel,userId,video,voteUsersEmail\n" +
                ",,,0,30,,,NAME,,false,,,,,,,,,,,,,,,,,,,,,,,0,,\n"))
        ;

    }
}
