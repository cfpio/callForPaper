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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;
/**
 * Talk
 */
@Entity
@Table(name = "talks")
public class Talk {
    public enum State { DRAFT, CONFIRMED, ACCEPTED, REFUSED, BACKUP }

    private int id;
    private State state;
    private String name;
    private Track track;
    private String description;
    private String references;
    private Integer difficulty;
    private Date added;
    private TalkFormat talkFormat;
    private User user;
    private Event event;

    //schedule data
    private Date date;
    private String heure;

    private Set<User> cospeakers;

    @Transient
    public int getDuree() {
        return talkFormat.getDureeMinutes();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    @ManyToOne
    public Event getEvent() {
        return event;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    public State getState() {
        return state;
    }

    @NotNull(message = "Session name field is required")
    public String getName() {
        return name;
    }

    @ManyToOne
    @JoinColumn(name = "track")
    public Track getTrack() {
        return track;
    }

    @Type(type="text")
    public String getDescription() {
        return description;
    }

    @Type(type="text")
    @Column(name = "refs")
    public String getReferences() {
        return references;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public Date getAdded() {
        return added;
    }

    @ManyToOne
    @JoinColumn(name = "talkformat")
    public TalkFormat getTalkFormat() {
        return talkFormat;
    }

    @ManyToOne
    @JoinColumn(name = "user")
    public User getUser() {
        return user;
    }

    @Column(name="schedule_date")
    public Date getDate() {
        return date;
    }

    @Column(name="schedule_heure")
    public String getHeure() {
        return heure;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cospeakers", joinColumns = @JoinColumn(name = "talk_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    public Set<User> getCospeakers() {
        return cospeakers;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public void setAdded(Date added) {
        this.added = added;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTalkFormat(TalkFormat talkFormat) {
        this.talkFormat = talkFormat;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public void setCospeakers(Set<User> cospeakers) {
        this.cospeakers = cospeakers;
    }
}
