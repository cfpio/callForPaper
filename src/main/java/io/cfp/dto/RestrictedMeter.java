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

package io.cfp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created by Thomas on 24/08/2015.
 */
public class RestrictedMeter implements Serializable  {
    private Counts meter;

    public Counts getMeter() {
        return meter;
    }

    public RestrictedMeter() {
        this.meter = new Counts();
    }

    @JsonIgnore
    public Integer getTalks() {
        return this.meter.talks;
    }

    public void setTalks(Integer talks) {
        this.meter.talks = talks;
    }

    private class Counts  implements Serializable {
        private Integer talks;

        public Integer getTalks() {
            return talks;
        }

        public Counts() {
        }
    }
}
