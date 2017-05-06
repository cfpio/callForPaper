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
import io.cfp.dto.TalkUser;
import io.cfp.dto.user.CospeakerProfil;
import io.cfp.dto.user.UserProfil;
import io.cfp.entity.Talk;
import io.cfp.repository.RoomRepo;
import io.cfp.repository.TalkRepo;
import io.cfp.repository.UserRepo;
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
    private RoomRepo rooms;

    @Mock
    private UserRepo users;

    private ScheduleController scheduleController;

    @Before
    public void setup() {
        scheduleController = new ScheduleController(talkUserService, talks, rooms, users, emailingService);
        RestAssuredMockMvc.standaloneSetup(scheduleController);
    }

    @Test
    public void testGetScheduleList() throws Exception {

        // Given
        UserProfil userProfil = new UserProfil(0,"John", "Doe");
        // TalkUser 1
        TalkUser talkUser1 = new TalkUser();
        talkUser1.setId(1);
        talkUser1.setName("A talk 1");
        talkUser1.setDescription("A description");
        talkUser1.setSpeaker(userProfil);

        // TalkUser 2
        TalkUser talkUser2 = new TalkUser();
        talkUser2.setId(2);
        talkUser2.setName("A talk 2");
        talkUser2.setDescription("A description");
        talkUser2.setSpeaker(userProfil);

        CospeakerProfil cospeakerProfil1 = new CospeakerProfil();
        cospeakerProfil1.setId(1);
        cospeakerProfil1.setFirstname("Johnny");
        cospeakerProfil1.setLastname("Deep");

        CospeakerProfil cospeakerProfil2 = new CospeakerProfil();
        cospeakerProfil2.setId(2);
        cospeakerProfil2.setFirstname("Alain");
        cospeakerProfil2.setLastname("Connu");

        Set<CospeakerProfil> cospeakerProfils = new HashSet<>();
        cospeakerProfils.add(cospeakerProfil1);
        cospeakerProfils.add(cospeakerProfil2);
        talkUser2.setCospeakers(cospeakerProfils);

        // TalkUser 3
        TalkUser talkUser3 = new TalkUser();
        talkUser3.setId(3);
        talkUser3.setName("A talk 3");
        // no description
        talkUser3.setSpeaker(userProfil);

        List<TalkUser> talkList = new ArrayList<>();
        talkList.add(talkUser1);
        talkList.add(talkUser2);
        talkList.add(talkUser3);

        when(talkUserService.findAll(Talk.State.CONFIRMED)).thenReturn(talkList);

        MockMvcResponse mockMvcResponse = given().contentType("application/json").when().get("/api/schedule/confirmed");

        System.out.println(mockMvcResponse.asString());

        mockMvcResponse.then().statusCode(200).body("size()", equalTo(3)).body("[0].speakers", equalTo("John Doe"))
                .body("[1].speakers", containsString("John Doe")).body("[1].speakers", containsString("Johnny Deep"))
                .body("[1].speakers", containsString("Alain Connu")).body("[2].speakers", equalTo("John Doe"));
    }

}
