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

package io.cfp.service.admin.config;

import io.cfp.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lhuet on 21/11/15.
 */
@Service
public class ApplicationConfigService {

    @Value("${authServer}")
    private String authServer;

    @Autowired
    private EventMapper eventMapper;

    @Transactional
    public void openCfp(String eventId) {
        io.cfp.model.Event event = eventMapper.findOne(eventId);
        event.setOpen(true);
        eventMapper.update(event);
    }

    @Transactional
    public void closeCfp(String eventId) {
        io.cfp.model.Event event = eventMapper.findOne(eventId);
        event.setOpen(false);
        eventMapper.update(event);
    }

}
