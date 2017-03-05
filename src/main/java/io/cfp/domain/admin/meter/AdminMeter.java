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

package io.cfp.domain.admin.meter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created by tmaugin on 16/07/2015.
 * SII
 */
public class AdminMeter implements Serializable {

    private Counts meter;

    public Counts getMeter() {
        return meter;
    }

    public AdminMeter() {
        this.meter = new Counts();
    }

    @JsonIgnore
    public Integer getSpeakers() {
        return this.meter.speakers;
    }

    public void setSpeakers(Integer speakers) {
        this.meter.speakers = speakers;
    }

    @JsonIgnore
    public Integer getDrafts() {
        return this.meter.drafts;
    }

    public void setDrafts(Integer drafts) {
        this.meter.drafts = drafts;
    }

    @JsonIgnore
    public Integer getTalks() {
        return this.meter.talks;
    }

    public void setTalks(Integer talks) {
        this.meter.talks = talks;
    }

    public void setSubmitted(Integer submitted) {
        meter.submitted = submitted;
    }

    public void setAccepted(Integer accepted) {
        meter.accepted = accepted;
    }

    public void setRejected(Integer rejected) {
        meter.rejected = rejected;
    }

    public void setBackup(Integer backup) {
        meter.backup = backup;
    }

    private class Counts  implements Serializable{
        private Integer speakers;
        private Integer drafts;
        private Integer submitted;
        private Integer accepted;
        private Integer rejected;
        private Integer backup;
        private Integer talks;


        public Integer getSpeakers() {
            return speakers;
        }

        public Integer getDrafts() {
            return drafts;
        }

        public Integer getSubmitted() {
            return submitted;
        }

        public Integer getAccepted() {
            return accepted;
        }

        public Integer getBackup() {
            return backup;
        }

        public Integer getRejected() {
            return rejected;
        }
        public Integer getTalks() {
            return talks;
        }

        public Counts() {
        }

    }
}
