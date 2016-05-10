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

import io.cfp.dto.ApplicationSettings;
import io.cfp.entity.CfpConfig;
import io.cfp.entity.Event;
import io.cfp.repository.CfpConfigRepo;
import io.cfp.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lhuet on 21/11/15.
 */
@Service
public class ApplicationConfigService {

    private final Logger log = LoggerFactory.getLogger(ApplicationConfigService.class);

    private static final String COMMUNITY = "community";
    private static final String EVENT_NAME = "eventName";
    private static final String DATE = "date";
    private static final String DECISION_DATE = "decisionDate";
    private static final String RELEASE_DATE = "releaseDate";
    private static final String OPEN = "open";

    @Autowired
    private CfpConfigRepo cfpConfigRepo;
    
    @Value("${authServer}")
    private String authServer;

    @Autowired
    private EventRepository events;

    @Cacheable("applicationSettings")
    public ApplicationSettings getAppConfig() {
        ApplicationSettings applicationSettings = new ApplicationSettings(events.findOne(Event.current()));
        applicationSettings.setAuthServer(authServer);

        return applicationSettings;
    }

    @Cacheable("isCfpOpen")
    public boolean isCfpOpen() {
        return events.findOne(Event.current()).isOpen();
    }

    @Transactional
    @CacheEvict("isCfpOpen")
    public void openCfp() {
        Event event = events.findOne(Event.current());
        event.setOpen(true);
        events.save(event);
    }

    @Transactional
    @CacheEvict("isCfpOpen")
    public void closeCfp() {
        Event event = events.findOne(Event.current());
        event.setOpen(false);
        events.save(event);

    }

    @CacheEvict("applicationSettings")
    @Transactional
    public void saveConf(String key, String value) {
        CfpConfig conf = cfpConfigRepo.findByKeyAndEventId(key, Event.current());
        conf.setValue(value);
        cfpConfigRepo.save(conf);

        log.debug(conf.getKey() + " -> " + conf.getValue());
    }

}
