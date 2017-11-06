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

import io.cfp.service.CookieService;
import io.cfp.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpHeaders.*;

/**
 * Main controller
 */
@Controller
public class MainCtrl {

	@Autowired
	private TokenService tokenSrv;

	@Autowired
	private CookieService cookieService;

	@RequestMapping("/")
	public String main(HttpServletResponse response, @CookieValue(required=false) String token,
                       @RequestParam(required=false, value="target") String targetParam,
                       @CookieValue(required=false) String returnTo,
                       @RequestHeader(required = false, value = REFERER) String referer) {
		response.setHeader(CACHE_CONTROL,"no-cache,no-store,must-revalidate");
		response.setHeader(PRAGMA,"no-cache");
		response.setDateHeader(EXPIRES, 0);

		String target = "http://www.cfp.io";
		if (targetParam != null) {
			target = targetParam;
		} else if (returnTo != null) {
			target = returnTo;
		} else if (referer != null) {
			target = referer;
		}

		response.addCookie(new Cookie("returnTo", target));

		if (token == null || !tokenSrv.isValid(token)) {
			return "login";
		}

		// token is valid
		return "redirect:"+target;
	}

	@RequestMapping("/logout")
	public String logout(HttpServletResponse response, @CookieValue(required=false) String token,
                         @CookieValue(required=false) String returnTo) {

		Cookie tokenCookie = cookieService.getTokenCookie("");
		tokenCookie.setMaxAge(0);
		response.addCookie(tokenCookie);

		tokenSrv.remove(token);

		return "redirect:/";
	}
}
