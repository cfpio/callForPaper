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

package io.cfp.controller.oauth;

import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;
import com.nimbusds.jose.JOSEException;
import io.cfp.config.google.GoogleSettings;
import io.cfp.domain.token.Token;
import io.cfp.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping(value = "/auth/google", produces = "application/json; charset=utf-8")
public class GoogleAuthController extends OAuthController {

    private final Logger log = LoggerFactory.getLogger(GoogleAuthController.class);

    private static final String ACCESS_TOKEN_URL = "https://accounts.google.com/o/oauth2/token";

    // private static final String peopleApiUrl = "https://www.googleapis.com/plus/v1/people/me/openIdConnect";

    @Autowired
    GoogleSettings googleSettings;

    @Autowired
    OAuthController OAuthController;

    /**
     * Log in with Google
     *
     * @param httpServletResponse
     * @param httpServletRequest
     * @param info
     * @return
     * @throws IOException
     * @throws JOSEException
     * @throws ParseException
     */
    @RequestMapping(method = RequestMethod.POST)
    public Token loginGoogle(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @RequestBody Map<String, String> info) throws ParseException, JOSEException, IOException {
        Person person = getPerson(info.get("code"), info.get("redirectUri"));

        User userFromGoogle = new User();
        userFromGoogle.setFirstname(person.getName().getGivenName());
        userFromGoogle.setLastname(person.getName().getFamilyName());
        userFromGoogle.setEmail(person.getEmails().get(0).getValue());
        userFromGoogle.setProviderId( User.Provider.GOOGLE, ""+person.getId());
        userFromGoogle.setGoogleplus(person.getUrl());
        userFromGoogle.setImageProfilURL(person.getImage().getUrl().replace("z=50", "z=360"));

        String userId = person.getId();

        return OAuthController.processUser(httpServletResponse, httpServletRequest, User.Provider.GOOGLE, userId, userFromGoogle);
    }

    private Person getPerson(String code, String redirectUri) {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        String client_id = googleSettings.getClientId();
        String client_secret = googleSettings.getClientSecret();

        Person person = null;
        try {
            TokenResponse response = new AuthorizationCodeTokenRequest(httpTransport, jsonFactory, new GenericUrl(ACCESS_TOKEN_URL), code)
                .setRedirectUri(redirectUri)
                .setCode(code)
                .setGrantType("authorization_code")
                .setClientAuthentication(new BasicAuthentication(client_id, client_secret))
                .execute();

            GoogleCredential credential = new GoogleCredential.Builder()
                .setJsonFactory(jsonFactory)
                .setTransport(httpTransport)
                .setClientSecrets(client_id, client_secret)
                .build()
                .setFromTokenResponse(response);

            Plus plus = new Plus.Builder(httpTransport, jsonFactory, credential).setApplicationName("Call For Paper").build();
            person = plus.people().get("me").execute();
        } catch (TokenResponseException tre) {
            if (tre.getDetails() != null) {
                log.warn("Error: " + tre.getDetails().getError());
                if (tre.getDetails().getErrorDescription() != null) {
                    log.warn(tre.getDetails().getErrorDescription());
                }
                if (tre.getDetails().getErrorUri() != null) {
                    log.warn(tre.getDetails().getErrorUri());
                }
            } else {
                log.warn(tre.getMessage());
            }
        } catch (IOException ioe) {
            log.warn("General I/O exception: " + ioe.getMessage());
        }

        return person;
    }
}
