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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Cacheable("applicationSettings")
    public ApplicationSettings getAppConfig() {
        ApplicationSettings applicationSettings = new ApplicationSettings();

        applicationSettings.setEventName(cfpConfigRepo.findValueByKey(EVENT_NAME, Event.current()));
        applicationSettings.setCommunity(cfpConfigRepo.findValueByKey(COMMUNITY, Event.current()));
        applicationSettings.setDate(cfpConfigRepo.findValueByKey(DATE, Event.current()));
        applicationSettings.setDecisionDate(cfpConfigRepo.findValueByKey(DECISION_DATE, Event.current()));
        applicationSettings.setReleaseDate(cfpConfigRepo.findValueByKey(RELEASE_DATE, Event.current()));
        applicationSettings.setOpen(Boolean.valueOf(cfpConfigRepo.findValueByKey(OPEN, Event.current())));
        applicationSettings.setConfigured(true);

        return applicationSettings;
    }

    @Cacheable("isCfpOpen")
    public boolean isCfpOpen() {
        CfpConfig conf = cfpConfigRepo.findByKeyAndEventId(OPEN, Event.current());
        return Boolean.valueOf(conf.getValue());
    }

    @Transactional
    @CacheEvict("isCfpOpen")
    public void openCfp() {
        saveConf(OPEN, "true");
    }

    @Transactional
    @CacheEvict("isCfpOpen")
    public void closeCfp() {
        saveConf(OPEN, "false");
    }

    @CacheEvict("applicationSettings")
    @Transactional
    public void saveConf(String key, String value) {
        CfpConfig conf = cfpConfigRepo.findByKeyAndEventId(key, Event.current());
        conf.setValue(value);
        cfpConfigRepo.save(conf);

        log.debug(conf.getKey() + " -> " + conf.getValue());
    }

    @CacheEvict("applicationSettings")
    @Transactional
    public void saveConfiguration(ApplicationSettings settings) {
        saveConf(COMMUNITY, settings.getCommunity());
        saveConf(EVENT_NAME, settings.getEventName());
        saveConf(DATE, settings.getDate());
        saveConf(DECISION_DATE, settings.getDecisionDate());
        saveConf(RELEASE_DATE, settings.getReleaseDate());
        saveConf(OPEN, String.valueOf(settings.isOpen()));
    }
}
