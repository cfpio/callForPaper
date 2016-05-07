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

import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Comment on a talk
 */
@Entity
@Table(name = "comments")
public class Comment {

    private int id;
    private String comment;
    private Date added;
    private boolean internal;

    private Talk talk;
    private User user;
    private Event event;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    @ManyToOne
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @NotNull
    @Type(type="text")
    public String getComment() {
        return comment;
    }

    @NotNull
    public Date getAdded() {
        return added;
    }

    public boolean isInternal() {
        return internal;
    }

    @ManyToOne
    @JoinColumn(name = "talk")
    public Talk getTalk() {
        return talk;
    }

    @ManyToOne
    @JoinColumn(name = "user")
    public User getUser() {
        return user;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setAdded(Date added) {
        this.added = added;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public void setTalk(Talk talk) {
        this.talk = talk;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
