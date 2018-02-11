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

package io.cfp.api;

import io.cfp.mapper.*;
import io.cfp.model.Proposal;
import io.cfp.model.Role;
import io.cfp.model.User;
import io.cfp.model.queries.ProposalQuery;
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

import java.util.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ScheduleController.class)
public class ScheduleControllerTest {

    @MockBean
    private ProposalMapper proposalMapper;

    @MockBean
    private EmailingService emailingService;

    @MockBean
    private RoomMapper roomMapper;

    @MockBean
    private FormatMapper formatMapper;

    @MockBean
    private ThemeMapper themeMapper;

    @MockBean
    private UserMapper userMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_return_schedule() throws Exception {

        // Given
        User userProfil = new User().setId(0).setFirstname("John").setLastname("Doe").setEmail("john@doe.net");
        // TalkUser 1
        Proposal talkUser1 = new Proposal();
        talkUser1.setId(1);
        talkUser1.setName("A talk 1");
        talkUser1.setDescription("A description");
        talkUser1.setSpeaker(userProfil);

        // TalkUser 2
        Proposal talkUser2 = new Proposal();
        talkUser2.setId(2);
        talkUser2.setName("A talk 2");
        talkUser2.setDescription("A description");
        talkUser2.setSpeaker(userProfil);

        User cospeakerProfil1 = new User();
        cospeakerProfil1.setId(1);
        cospeakerProfil1.setFirstname("Johnny");
        cospeakerProfil1.setLastname("Deep");

        User cospeakerProfil2 = new User();
        cospeakerProfil2.setId(2);
        cospeakerProfil2.setFirstname("Alain");
        cospeakerProfil2.setLastname("Connu");

        Set<User> cospeakerProfils = new HashSet<>();
        cospeakerProfils.add(cospeakerProfil1);
        cospeakerProfils.add(cospeakerProfil2);
        talkUser2.setCospeakers(cospeakerProfils);

        // TalkUser 3
        Proposal talkUser3 = new Proposal();
        talkUser3.setId(3);
        talkUser3.setName("A talk 3");
        // no description
        talkUser3.setSpeaker(userProfil);

        List<Proposal> talkList = new ArrayList<>();
        talkList.add(talkUser1);
        talkList.add(talkUser2);
        talkList.add(talkUser3);

        when(proposalMapper.findAll(new ProposalQuery().setStates(Arrays.asList(Proposal.State.CONFIRMED)))).thenReturn(talkList);

        User user = new User();
        user.setEmail("EMAIL");
        user.addRole(Role.ADMIN);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        mockMvc.perform(get("/api/schedule/confirmed")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$[0].speakers", containsString("John Doe")))
            .andExpect(jsonPath("$[1].speakers", containsString("John Doe")))
            .andExpect(jsonPath("$[1].speakers", containsString("Alain Connu")))
            .andExpect(jsonPath("$[1].speakers", containsString("Johnny Deep")))
        ;

    }

    @Test
    public void should_send_notifications_to_all_accepted_talks() throws Exception {
        // Given
        User speaker = new User().setId(0).setFirstname("John").setLastname("Doe").setEmail("john@doe.net");
        // TalkUser 1
        Proposal talkUser1 = new Proposal();
        talkUser1.setId(1);
        talkUser1.setName("A talk 1");
        talkUser1.setDescription("A description");
        talkUser1.setSpeaker(speaker);

        Proposal talkUser2 = new Proposal();
        talkUser2.setId(1);
        talkUser2.setName("A talk 2");
        talkUser2.setDescription("A description");
        talkUser2.setSpeaker(speaker);

        List<Proposal> proposals = new ArrayList<>();
        proposals.add(talkUser1);
        proposals.add(talkUser2);

        when(proposalMapper.findAll(any(ProposalQuery.class))).thenReturn(proposals);


        User user = new User();
        user.setEmail("EMAIL");
        user.addRole(Role.ADMIN);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        mockMvc.perform(post("/api/schedule/notification?filter=accepted")
            .content("[]")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
        )
            .andDo(print())
            .andExpect(status().isOk())
        ;

        verify(emailingService, times(2)).sendSelectionned(any(Proposal.class), any(Locale.class));
    }

    @Test
    public void should_send_notifications_to_all_refused_talks() throws Exception {
        // Given
        User speaker = new User().setId(0).setFirstname("John").setLastname("Doe").setEmail("john@doe.net");
        // TalkUser 1
        Proposal talkUser1 = new Proposal();
        talkUser1.setId(1);
        talkUser1.setName("A talk 1");
        talkUser1.setDescription("A description");
        talkUser1.setSpeaker(speaker);

        Proposal talkUser2 = new Proposal();
        talkUser2.setId(2);
        talkUser2.setName("A talk 2");
        talkUser2.setDescription("A description");
        talkUser2.setSpeaker(speaker);

        List<Proposal> proposals = new ArrayList<>();
        proposals.add(talkUser1);
        proposals.add(talkUser2);

        when(proposalMapper.findAll(any(ProposalQuery.class))).thenReturn(proposals);


        User user = new User();
        user.setEmail("EMAIL");
        user.addRole(Role.ADMIN);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        mockMvc.perform(post("/api/schedule/notification?filter=refused")
            .content("[]")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
        )
            .andDo(print())
            .andExpect(status().isOk())
        ;

        verify(emailingService, times(2)).sendNotSelectionned(any(Proposal.class), any(Locale.class));
    }

    @Test
    public void should_send_notifications_to_all_talks() throws Exception {
        // Given
        User speaker = new User().setId(0).setFirstname("John").setLastname("Doe").setEmail("john@doe.net");
        // TalkUser 1
        Proposal talkUser1 = new Proposal();
        talkUser1.setId(1);
        talkUser1.setName("A talk 1");
        talkUser1.setDescription("A description");
        talkUser1.setSpeaker(speaker);

        Proposal talkUser2 = new Proposal();
        talkUser2.setId(2);
        talkUser2.setName("A talk 2");
        talkUser2.setDescription("A description");
        talkUser2.setSpeaker(speaker);

        List<Proposal> proposals = new ArrayList<>();
        proposals.add(talkUser1);
        proposals.add(talkUser2);

        when(proposalMapper.findAll(any(ProposalQuery.class))).thenReturn(proposals);


        User user = new User();
        user.setEmail("EMAIL");
        user.addRole(Role.ADMIN);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        mockMvc.perform(post("/api/schedule/notification")
            .content("[]")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
        )
            .andDo(print())
            .andExpect(status().isOk())
        ;

        verify(emailingService, times(2)).sendNotSelectionned(any(Proposal.class), any(Locale.class));
    }

    @Test
    public void should_send_notifications_to_some_talks() throws Exception {
        // Given
        User speaker = new User().setId(0).setFirstname("John").setLastname("Doe").setEmail("john@doe.net");
        // TalkUser 1
        Proposal talkUser1 = new Proposal();
        talkUser1.setId(1);
        talkUser1.setName("A talk 1");
        talkUser1.setDescription("A description");
        talkUser1.setSpeaker(speaker);

        Proposal talkUser2 = new Proposal();
        talkUser2.setId(2);
        talkUser2.setName("A talk 2");
        talkUser2.setDescription("A description");
        talkUser2.setSpeaker(speaker);

        Proposal talkUser3 = new Proposal();
        talkUser3.setId(3);
        talkUser3.setName("A talk 3");
        talkUser3.setDescription("A description");
        talkUser3.setSpeaker(speaker);

        List<Proposal> proposals = new ArrayList<>();
        proposals.add(talkUser1);
        proposals.add(talkUser2);

        when(proposalMapper.findAll(any(ProposalQuery.class))).thenReturn(proposals);


        User user = new User();
        user.setEmail("EMAIL");
        user.addRole(Role.ADMIN);
        String token = Utils.createTokenForUser(user);

        when(userMapper.findByEmail("EMAIL")).thenReturn(user);

        mockMvc.perform(post("/api/schedule/notification?filter=accepted")
            .content("[1,2]")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("Authorization", "Bearer "+token)
        )
            .andDo(print())
            .andExpect(status().isOk())
        ;

        verify(emailingService, times(2)).sendSelectionned(any(Proposal.class), any(Locale.class));
    }

}
