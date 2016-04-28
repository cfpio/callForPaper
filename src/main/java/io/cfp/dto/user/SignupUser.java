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

package io.cfp.dto.user;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by tmaugin on 18/05/2015.
 */
public class SignupUser {

    @NotNull(message = "Email field is required")
    private String email;
    @Size(min = 6)
    @NotNull(message = "Password field is required")
    private String password;
    @NotNull(message = "Captcha field is required")
    private String captcha;

    public SignupUser(String email, String password, String captcha) {
        this.email = email;
        this.password = password;
        this.captcha = captcha;
    }

    public SignupUser() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
