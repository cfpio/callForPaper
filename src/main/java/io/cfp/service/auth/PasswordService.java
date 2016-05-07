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

package io.cfp.service.auth;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by tmaugin on 15/05/2015.
 */
public final class PasswordService {
    /**
     * Hash the given plain text password
     * @param plaintext
     * @return hashed password
     */
    public static String hashPassword(String plaintext) {
        return BCrypt.hashpw(plaintext, BCrypt.gensalt());
    }

    /**
     * Verify if hashed password and plain text password correspond
     * @param plaintext
     * @param hashed
     * @return Passwords match or not
     */
    public static boolean checkPassword(String plaintext , String hashed) {
        return BCrypt.checkpw(plaintext, hashed);
    }

}
