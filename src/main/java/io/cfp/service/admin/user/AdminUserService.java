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

package io.cfp.service.admin.user;

import io.cfp.entity.Event;
import io.cfp.entity.Role;
import io.cfp.entity.User;
import io.cfp.repository.RoleRepository;
import io.cfp.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminUserService {

    @Autowired
    private RoleRepository roles;

    @Autowired
    private UserRepo userRepo;

    /** Current loggued admin, scoped request object */
    @Autowired
    private User currentAdmin;

    /**
     * Retrieve an admin if the user e-mail match an admin e-mail
     * @param email Email of the connected user
     * @return AdminUser if existing, null otherwise
     */
    public User findFromEmail(String email) {
        User u = userRepo.findByEmail(email);
        Role role = roles.findByUserIdAndEventIdAndName(u.getId(), Event.current(), Role.ADMIN);
        if (role == null) return null;
        return u;
    }

    /**
     * Set connected admin for the current request
     * @param admin Admin to set
     */
    public void setCurrentAdmin(User admin) {
        //warning, do not replace the autowired object because it's a proxy around an object created by spring in the request
        currentAdmin.setId(admin.getId());
        currentAdmin.setEmail(admin.getEmail());
    }

    public User getCurrentUser() {
        if (currentAdmin.getEmail() == null) return null;
        return currentAdmin;
    }

}
