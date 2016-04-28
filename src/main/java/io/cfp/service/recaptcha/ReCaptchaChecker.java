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

package io.cfp.service.recaptcha;

import io.cfp.domain.recaptcha.ReCaptchaCheckerReponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Created by tmaugin on 22/05/2015.
 * SII
 */
abstract public class ReCaptchaChecker {
    public static final String RECAPTCHA_VERIF_URL = "https://www.google.com/recaptcha/api/siteverify";

    public static ReCaptchaCheckerReponse checkReCaptcha(String secret, String response) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("secret", secret);
        map.add("response", response);

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.postForObject(RECAPTCHA_VERIF_URL, map, ReCaptchaCheckerReponse.class);
    }
}
