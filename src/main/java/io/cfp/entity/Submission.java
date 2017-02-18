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

package io.cfp.entity;

import javax.persistence.*;

/**
 * View used to retrieved talk count by status and events
 */
@Entity
@Table(name = "submissions")
public class Submission {

    private int userId;
    private Event event;
    private Talk.State state;

    @Id
    public int getUserId() {
        return userId;
    }

    @ManyToOne
    public Event getEvent() {
        return event;
    }

    @Enumerated(EnumType.STRING)
    public Talk.State getState() {
        return state;
    }

    // As we map a view, setters don't actually make sense, but Hibernate require them.

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setState(Talk.State state) {
        this.state = state;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

