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

package io.cfp.controller.admin;

import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import com.jayway.restassured.module.mockmvc.response.MockMvcResponse;
import io.cfp.controller.ScheduleController;
import io.cfp.mapper.ProposalMapper;
import io.cfp.mapper.RoomMapper;
import io.cfp.mapper.UserMapper;
import io.cfp.model.Proposal;
import io.cfp.model.User;
import io.cfp.model.queries.ProposalQuery;
import io.cfp.repository.TalkRepo;
import io.cfp.service.TalkUserService;
import io.cfp.service.email.EmailingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Nicolas on 30/01/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class ScheduleControllerTest {

    @Mock
    private TalkUserService talkUserService;

    @Mock
    private EmailingService emailingService;

    @Mock
    private TalkRepo talks;

    @Mock
    private RoomMapper roomMapper;

    @Mock
    private ProposalMapper proposalMapper;

    @Mock
    private UserMapper userMapper;

    private ScheduleController scheduleController;

    @Before
    public void setup() {
        scheduleController = new ScheduleController(talkUserService, proposalMapper, talks, roomMapper, userMapper, emailingService);
        RestAssuredMockMvc.standaloneSetup(scheduleController);
    }

    @Test
    public void testGetScheduleList() {

        // Given
        User userProfil = new User().setId(0)
            .setFirstname("John")
            .setLastname("Doe")
            .setEmail("john@doe.net");

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

        User cospeakerProfil1 = new User()
        .setId(1)
        .setFirstname("Johnny")
        .setLastname("Deep");

        User cospeakerProfil2 = new User()
        .setId(2)
        .setFirstname("Alain")
        .setLastname("Connu");

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

        when(proposalMapper.findAll(any(ProposalQuery.class))).thenReturn(talkList);

        MockMvcResponse mockMvcResponse = given().contentType("application/json").when().get("/v0/schedule/confirmed");

        System.out.println(mockMvcResponse.asString());

        mockMvcResponse.then().statusCode(200).body("size()", equalTo(3)).body("[0].speakers", equalTo("John Doe"))
                .body("[1].speakers", containsString("John Doe")).body("[1].speakers", containsString("Johnny Deep"))
                .body("[1].speakers", containsString("Alain Connu")).body("[2].speakers", equalTo("John Doe"));
    }

}
