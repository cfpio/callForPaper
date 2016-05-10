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

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;

import io.cfp.TestConfig;
import io.cfp.controller.ApplicationController;
import io.cfp.repository.EventRepository;


/**
 * Created by tmaugin on 08/04/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestConfig.class})
@WebIntegrationTest("server.port:0")
public class ApplicationTest {

    @Autowired
    private EventRepository events;

    private ApplicationController applicationController;

    @Before
    public void setUp() {
        applicationController = new ApplicationController();
        ReflectionTestUtils.setField(applicationController, "events", events);
        RestAssuredMockMvc.standaloneSetup(applicationController);
    }

    @Test
    public void test1_getApplication() {

        given()
                .contentType("application/json")
                .when()
                .get("/api/application")
                .then()
                .statusCode(200)
                .body("size()", equalTo(7));
    }
}
