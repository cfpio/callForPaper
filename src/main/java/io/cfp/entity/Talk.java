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

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;
/**
 * Talk
 */
@Entity
@Table(name = "proposals")
public class Talk {

    public enum State { DRAFT, CONFIRMED, ACCEPTED, REFUSED, BACKUP }

    private int id;
    private State state;
    private String name;
    private String language;
    private Track track;
    private String description;
    private String references;
    private Integer difficulty;
    private Date added;
    private Format format;
    private User user;
    private Event event;

    //schedule data
    private Date date;
    private String heure;
    public Room room;

    private Set<User> cospeakers;

    //dependent entity to remove links when deleting talk
    private Set<Comment> comments;
    private Set<Rate> rates;


    @Transient
    public int getDuree() {
        return format.getDuration();
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

    public String getLanguage() {
        return language;
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
    @JoinColumn(name = "format")
    public Format getFormat() {
        return format;
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

    @ManyToOne
    @JoinColumn(name = "room_id")
    public Room getRoom() {
        return room;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cospeakers", joinColumns = @JoinColumn(name = "proposal_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    public Set<User> getCospeakers() {
        return cospeakers;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "talk")
    public Set<Comment> getComments() {
        return comments;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "talk")
    public Set<Rate> getRates() {
        return rates;
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

    public void setLanguage(String language) {
        this.language = language;
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

    public void setFormat(Format format) {
        this.format = format;
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

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public void setRates(Set<Rate> rates) {
        this.rates = rates;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Talk id(int id) {
        this.id = id;
        return this;
    }

    public Talk state(State state) {
        this.state = state;
        return this;
    }

    public Talk name(String name) {
        this.name = name;
        return this;
    }

    public Talk language(String language) {
        this.language = language;
        return this;
    }

    public Talk track(Track track) {
        this.track = track;
        return this;
    }

    public Talk description(String description) {
        this.description = description;
        return this;
    }

    public Talk references(String references) {
        this.references = references;
        return this;
    }

    public Talk difficulty(Integer difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public Talk added(Date added) {
        this.added = added;
        return this;
    }

    public Talk format(Format format) {
        this.format = format;
        return this;
    }

    public Talk user(User user) {
        this.user = user;
        return this;
    }

    public Talk event(Event event) {
        this.event = event;
        return this;
    }

    public Talk date(Date date) {
        this.date = date;
        return this;
    }

    public Talk heure(String heure) {
        this.heure = heure;
        return this;
    }

    public Talk cospeakers(Set<User> cospeakers) {
        this.cospeakers = cospeakers;
        return this;
    }

    public Talk room(Room room) {
        this.room = room;
        return this;
    }
}
