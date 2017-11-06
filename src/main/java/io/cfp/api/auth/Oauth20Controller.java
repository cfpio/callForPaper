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

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import io.cfp.log.Log;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static io.cfp.log.MDCKey.USER;


public abstract class Oauth20Controller extends AuthController {
	private static final Logger logger = LoggerFactory.getLogger(Oauth20Controller.class);

	private OAuth20Service authService;

	@PostConstruct
	private void init() {
		authService = new ServiceBuilder()
				.apiKey(getClientId())
				.apiSecret(getClientSecret())
				.scope(getScope())
				.callback(hostname + getProviderPath() + "/auth")
				.build(getApi());
	}

	@RequestMapping(value = "/login")
    public String login() {
			return "redirect:" + authService.getAuthorizationUrl();
    }

    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
	@Log(USER)
    public String auth(HttpServletResponse httpServletResponse, @RequestParam String code,
                       @CookieValue(required = false, value = "returnTo") String returnTo) throws IOException {

		logger.info("[OAUTH2_GET_TOKEN] Retrieving access token from [{}] for code [{}]", getProvider(), code);
		OAuth2AccessToken accessToken = authService.getAccessToken(code);
    	Map<String, Object> user = restTemplate.getForObject(getEmailInfoUrl() + accessToken.getAccessToken(), Map.class);
		String email = (String) user.get(getEmailProperty());

		MDC.put(USER, email);
		return processUser(httpServletResponse, email, returnTo);
    }

	protected abstract String getClientId();

	protected abstract String getClientSecret();

	protected abstract String getScope();

	protected abstract DefaultApi20 getApi();

	protected abstract String getEmailInfoUrl();

	protected abstract String getEmailProperty();
}
