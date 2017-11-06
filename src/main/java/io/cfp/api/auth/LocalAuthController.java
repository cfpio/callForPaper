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
import io.cfp.log.Log;
import io.cfp.service.ReCaptchaService;
import io.cfp.service.email.EmailingService;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static io.cfp.log.MDCKey.USER;

@Controller
public class LocalAuthController extends AuthController {
	private static final Logger logger = LoggerFactory.getLogger(LocalAuthController.class);

	@Autowired
	private ReCaptchaService recaptchaService;

	@Autowired
	private EmailingService emailService;

	/** Called when trying to authenticate a user who don't have an email address (email not received from oauth) */
	@RequestMapping(value = "/noEmail", method = RequestMethod.GET)
	public String noEmail(HttpServletResponse response, Map<String, Object> model) throws IOException {
		model.put("error", "noEmail");
		return "login";
	}

	/** Called when the user submit the login form */
	@RequestMapping(value = "/local/login", method = RequestMethod.POST)
	public String login(HttpServletResponse response, @RequestParam @Log(USER) String email, @RequestParam String password,
                        Map<String, Object> model, @CookieValue(required = false, value = "returnTo") String returnTo) {

		model.put("email", email);
		Humanity user = userService.findByemail(email);

		if (user == null) {
			logger.warn("[LOCAL_NOT_EXIST] The user doesn't exists in database");
			model.put("error", "invalidAuth");
			return "login";
		}

		if (user.getPassword() == null || !BCrypt.checkpw(password, user.getPassword())) {
			logger.warn("[LOCAL_INVALID_PASS] The password is invalid");
			model.put("error", "invalidAuth");
			return "login";
		}

		return processUser(response, user.getEmail(), returnTo);
	}

	/** Called when a user start a local signup */
	@RequestMapping(value = "/local/signup", method = RequestMethod.GET)
	public String signup(Map<String, Object> model) {
		model.put("recaptchaKey", recaptchaService.getRecaptchaKey());
		return "signup";
	}

	/** Called whe a user submit a account creation form */
	@RequestMapping(value = "/local/signup", method = RequestMethod.POST)
	public String signup(HttpServletRequest request, @RequestParam @Log(USER) String email,
                         @RequestParam(name="g-recaptcha-response") String recaptcha, Map<String, Object> model) {

		if (!recaptchaService.isCaptchaValid(recaptcha)) {
			model.put("error", "invalidCaptcha");
			return signup(model);
		}

		logger.info("[LOCAL_REGISTERING] Registering user and send validation mail");
		Humanity user = userService.findByemail(email);

		if (user == null) {
			user = new Humanity();
			user.setEmail(email);
		}

		user.setVerifyToken(UUID.randomUUID().toString());
		userService.save(user);

		try {
			emailService.sendEmailValidation(user, request.getLocale());
		} catch (IOException e) {
			logger.error("[LOCAL_CONFIRM_MAIL_ERROR] Error when sending confirmation mail", e);
			model.put("error", "mailError");
			return signup(model);
		}

		logger.info("[LOCAL_VALIDATION] Validation mail successfully sent");
		return "redirect:/local/emailSent";
	}

	/** Called when the confirmation mail is sent successfully */
	@RequestMapping(value = "/local/emailSent", method = RequestMethod.GET)
	public String emailSent() {
		return "emailSent";
	}

	/** Called when the user click on the link in the confirmation mail */
	@RequestMapping(value = "/local/register", method = RequestMethod.GET)
	public String register(@RequestParam String email, @RequestParam String token, Map<String, Object> model) {
		model.put("email", email.replaceAll("\"", ""));
		model.put("token", token.replaceAll("\"", ""));
		return "register";
	}

	/** Called when the user submit the password form after mail confirmation */
	@RequestMapping(value = "/local/register", method = RequestMethod.POST)
	public String register(HttpServletResponse response, @RequestParam @Log(USER) String email, @RequestParam String password,
                           @RequestParam String token, Map<String, Object> model, @CookieValue(required = false, value = "returnTo") String returnTo) {

        Humanity user = userService.findByemail(email);

		if (user == null) {
			logger.warn("[LOCAL_INVALID_MAIL] The user tried to activate his account with an unknown mail");
			model.put("error", "invalidEmail");
			return register(email, token, model);
		}

		if (!token.equals(user.getVerifyToken())) {
			logger.warn("[LOCAL_INVALID_TOKEN] The user tried to activate his account with an invalid token");
			model.put("error", "invalidToken");
			return register(email, token, model);
		}

		user.setVerifyToken(null);
		user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
		userService.save(user);

		logger.info("[LOCAL_ACTIVATED] The user is successfully activated");
		return processUser(response, email, returnTo);
	}

}
