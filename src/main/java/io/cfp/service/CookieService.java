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

package io.cfp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.time.Duration;

@Service
public class CookieService {

	@Value("${token.cookie-domain}")
	private String cookieDomain;

	public Cookie getTokenCookie(String tokenValue) {
		Cookie tokenCookie = new Cookie("token", tokenValue);
		tokenCookie.setPath("/");
		tokenCookie.setHttpOnly(true); // secure Token to be invisible from
										// javascript in the browser
		tokenCookie.setDomain(cookieDomain);
		tokenCookie.setMaxAge((int) Duration.ofHours(TokenService.TOKEN_EXPIRATION).getSeconds());
		return tokenCookie;
	}
}
