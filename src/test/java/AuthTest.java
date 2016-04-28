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

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import io.cfp.domain.token.Token;
import io.cfp.service.auth.AuthUtils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by tmaugin on 20/05/2015.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuthTest {

    String secretToken = "secretTestsecretTestsecretTestsecretTestsecretTestsecretTestsecretTestsecretTest";

    @Before
    public void setUp() {
        AuthUtils.TOKEN_SECRET = secretToken;
    }

    @Test
    public void test1_createToken() {
        Token token = null;
        try {
            token = AuthUtils.createToken("testHost", "1", true);
            token.getToken();
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        assertNotEquals(null, token);
    }

    @Test
    public void test2_decodeToken() {
        Token token = null;
        try {
            token = AuthUtils.createToken("testHost", "1", true);
            token.getToken();
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token.getToken());
        JWTClaimsSet body = AuthUtils.getTokenBody(request);

        assertEquals("1",body.getSubject());
        assertEquals(true, body.getClaim("verified"));
    }

    @Test
    public void test3_verifyToken() {
        Token token = null;
        try {
            token = AuthUtils.createToken("testHost", "1", true);
            token.getToken();
        } catch (JOSEException e) {
            e.printStackTrace();
        }

        boolean error = false;
        try {
            AuthUtils.decodeToken("Bearer " + token.getToken());
        } catch (ParseException e) {
            e.printStackTrace();
            error = true;
        } catch (JOSEException e) {
            e.printStackTrace();
            error = true;
        }

        assertEquals(false, error);
    }

    @Test
    public void test4_verifyTokenFail() {
        Token token = null;
        try {
            token = AuthUtils.createToken("testHost", "1", true);
            token.getToken();
        } catch (JOSEException e) {
            e.printStackTrace();
        }

        AuthUtils.TOKEN_SECRET = secretToken + "fail";

        boolean error = false;
        try {
            AuthUtils.decodeToken("Bearer " + token.getToken());
        } catch (ParseException e) {
            error = true;
        } catch (JOSEException e) {
            error = true;
        }

        assertEquals(true, error);
    }
}
