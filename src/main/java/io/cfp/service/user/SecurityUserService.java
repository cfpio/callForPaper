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

package io.cfp.service.user;

import io.cfp.entity.Event;
import io.cfp.mapper.RoleMapper;
import io.cfp.mapper.UserMapper;
import io.cfp.model.Role;
import io.cfp.model.User;
import io.cfp.model.queries.RoleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SecurityUserService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
	private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.debug("Authenticating {}", username);
        String lowercaseLogin = username.toLowerCase();
        Optional<User> userFromDatabase = Optional.ofNullable(userMapper.findByEmail(lowercaseLogin));
        return userFromDatabase.map(user -> {
            List<Role> roles = roleMapper.findAll(new RoleQuery().setEventId(Event.current()).setUserId(user.getId()));
            List<GrantedAuthority> grantedAuthorities = roles.stream()
                                                             .map(role -> new SimpleGrantedAuthority(role.getName()))
                                                             .collect(Collectors.toList());
            return new org.springframework.security.core.userdetails.User(lowercaseLogin, "****", grantedAuthorities);
        }).orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the " + "database"));
    }
}
