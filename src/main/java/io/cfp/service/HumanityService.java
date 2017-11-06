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

import io.cfp.entity.Humanity;
import io.cfp.repository.HumanityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HumanityService {

    @Autowired
	private HumanityRepo userRepo;

	public Humanity save(Humanity user) {
		return userRepo.save(user);
	}

	public void delete(Integer id) {
		userRepo.delete(id);
	}

	public List<Humanity> findAll() {
		return userRepo.findAll();
	}

	public Humanity findById(Integer id) {
		return userRepo.findOne(id);
	}

	public Humanity findByemail(String email) {
		return userRepo.findByEmail(email);
	}

}
