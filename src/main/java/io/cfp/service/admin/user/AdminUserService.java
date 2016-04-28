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

import java.util.List;

import io.cfp.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.cfp.entity.AdminUser;
import io.cfp.entity.User;
import io.cfp.repository.AdminUserRepo;
import io.cfp.repository.UserRepo;

@Service
@Transactional
public class AdminUserService {

    @Autowired
    private AdminUserRepo adminUserRepo;

    @Autowired
    private UserRepo userRepo;

    /** Current loggued admin, scoped request object */
    @Autowired
    private AdminUser currentAdmin;

    public List<AdminUser> findAll()
    {
        return adminUserRepo.findByEventId(Event.current());
    }

    /**
     * Retrieve an admin if the user e-mail match an admin e-mail
     * @param userId Id of the connected user
     * @return AdminUser if existing, null otherwise
     */
    public AdminUser findFromUserId(int userId) {
        User user = userRepo.findOne(userId);
        if (user == null) return null;
        return adminUserRepo.findByEventIdAndEmail(Event.current(), user.getEmail());
    }

    /**
     * Set connected admin for the current request
     * @param admin Admin to set
     */
    public void setCurrentAdmin(AdminUser admin) {
        //warning, do not replace the autowired object because it's a proxy around an object created by spring in the request
        currentAdmin.setId(admin.getId());
        currentAdmin.setEmail(admin.getEmail());
        currentAdmin.setName(admin.getName());
    }

    public AdminUser getCurrentUser() {
        if (currentAdmin.getEmail() == null) return null;
        return currentAdmin;
    }

}
