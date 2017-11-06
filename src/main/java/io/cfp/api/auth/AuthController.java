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

import io.cfp.entity.Humanity;
import io.cfp.service.CookieService;
import io.cfp.service.HumanityService;
import io.cfp.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;

public abstract class AuthController {
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	protected HumanityService userService;

	@Autowired
	protected TokenService tokenService;

	@Autowired
	private CookieService cookieService;

	protected RestTemplate restTemplate = new RestTemplate();

	@Value("${cfp.app.hostname}")
    protected String hostname;

	/**
	 * Return JWT token and eventually persist user according to providerId and
	 * provider
	 */
	protected String processUser(HttpServletResponse response, String email, String returnTo) {

		if (email == null) {
			logger.warn("[LOGIN_NO_MAIL] Unable to log user with [{}] because no e-mail was provided", getProvider());
			return "redirect:/noEmail";
		}

		Humanity user = userService.findByemail(email);

		if (user == null) {
			logger.info("[USER_CREATION] Trying to login with a not existent user, creating it");
			user = new Humanity();
			user.setEmail(email);
			user = userService.save(user);
		}

		// add a token for the user
		String token = tokenService.create(email, user.isSuperAdmin());
		response.addCookie(cookieService.getTokenCookie(token));

		if (returnTo == null || returnTo.isEmpty()) {
			returnTo = "http://www.cfp.io";
		}

		logger.info("[LOGGED_IN] User logged in with [{}] and redirected to [{}]", getProvider(), returnTo);

		return "redirect:"+returnTo;
	}

	/**
	 * @return Name of the auth provider
	 */
	protected String getProvider() {
		String className = getClass().getSimpleName();
		return className.substring(0, className.length() - 10); //removing Controller suffix
	}

	protected String getProviderPath() {
		return this.getClass().getAnnotation(RequestMapping.class).value()[0];
	}
}
