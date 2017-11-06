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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ReCaptchaService {

    private static final String RECAPTCHA_VERIF_URL = "https://www.google.com/recaptcha/api/siteverify";

	@Value("${cfp.auth.captchapublic}")
    private String recaptchaKey;

	@Value("${cfp.auth.captchasecret}")
    private String recaptchaSecret;

	private RestTemplate restTemplate = new RestTemplate();

    public String getRecaptchaKey() {
    	return recaptchaKey;
    }

    public boolean isCaptchaValid(String response) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("secret", recaptchaSecret);
        map.add("response", response);

        @SuppressWarnings("unchecked")
		Map<String,Object> result = restTemplate.postForObject(RECAPTCHA_VERIF_URL, map, Map.class);

        return (boolean) result.get("success");
    }
}
