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

package io.cfp.api.auth;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.api.DefaultApi20;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(value="/google")
public class GoogleAuthController extends Oauth20Controller {

    @Value("${cfp.google.clientid}")
    private String clientId;

    @Value("${cfp.google.clientsecret}")
    private String clientSecret;

	@Override
	protected String getClientId() {
		return clientId;
	}

	@Override
	protected String getClientSecret() {
		return clientSecret;
	}

	@Override
	protected String getScope() {
		return "https://www.googleapis.com/auth/userinfo.email";
	}

	@Override
	protected DefaultApi20 getApi() {
		return GoogleApi20.instance();
	}

	@Override
	protected String getEmailInfoUrl() {
		return "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=";
	}

	@Override
	protected String getEmailProperty() {
		return "email";
	}
}
