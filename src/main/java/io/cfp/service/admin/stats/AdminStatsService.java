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

package io.cfp.service.admin.stats;

import io.cfp.domain.admin.meter.AdminMeter;
import io.cfp.entity.Event;
import io.cfp.repository.SubmissionRepository;
import io.cfp.repository.TalkRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;

import static io.cfp.entity.Talk.State.*;

@Service
public class AdminStatsService {

    @Autowired
    private TalkRepo talks;

    @Autowired
    private SubmissionRepository submissions;

    @Transactional(readOnly = true)
    public AdminMeter getAdminMeter() {
        AdminMeter meter = new AdminMeter();
        meter.setSpeakers(submissions.countByEventId(Event.current()));
        meter.setTalks((int) talks.countByEventIdAndStateIn(Event.current(), Arrays.asList(CONFIRMED, ACCEPTED, REFUSED)));
        meter.setDrafts((int) talks.countByEventIdAndStateIn(Event.current(), Collections.singletonList(DRAFT)));
        meter.setSubmitted((int) talks.countByEventIdAndStateIn(Event.current(), Collections.singletonList(CONFIRMED)));
        meter.setAccepted((int) talks.countByEventIdAndStateIn(Event.current(), Collections.singletonList(ACCEPTED)));
        meter.setRejected((int) talks.countByEventIdAndStateIn(Event.current(), Collections.singletonList(RuntimeException)));
        return meter;
    }
}
