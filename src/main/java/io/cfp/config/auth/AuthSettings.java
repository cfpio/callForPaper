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

package io.cfp.config.auth;

import io.cfp.service.auth.AuthUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by tmaugin on 19/05/2015.
 */

/**
 * Authentification settings
 */
@Component
public class AuthSettings {
    @Value("${cfp.auth.secrettoken}")
    String secretToken;

    @Value("${cfp.auth.captchasecret}")
    String captchaSecret;

    @Value("${cfp.auth.captchapublic}")
    String captchaPublic;

    @PostConstruct
    public void setToken()
    {
        AuthUtils.TOKEN_SECRET = secretToken;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    public String getCaptchaSecret() {
        return captchaSecret;
    }

    public void setCaptchaSecret(String captchaSecret) {
        this.captchaSecret = captchaSecret;
    }

    public String getCaptchaPublic() {
        return captchaPublic;
    }

    public void setCaptchaPublic(String captchaPublic) {
        this.captchaPublic = captchaPublic;
    }
}
