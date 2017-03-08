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

import io.cfp.dto.user.CospeakerProfil;
import io.cfp.dto.user.UserProfil;
import io.cfp.entity.Talk;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Talk DTO for user view
 */
public class TalkUser {

    private int id;
    private Talk.State state;
    private String name;
    private String language;
    private int format;
    private Integer trackId;
    private String trackLabel;
    private String description;
    private String references;
    private Integer difficulty;
    private Date added;
    private UserProfil speaker;
    private Set<CospeakerProfil> cospeakers;

    private String schedule;
    private Integer room;


    public TalkUser() {

    }

    public TalkUser(Talk t) {
        this.id = t.getId();
        this.state = t.getState();
        this.name = t.getName();
        this.language = t.getLanguage();
        this.format = t.getFormat().getId();
        this.trackId = t.getTrack().getId();
        this.trackLabel = t.getTrack().getLibelle();
        this.description = t.getDescription();
        this.references = t.getReferences();
        this.difficulty = t.getDifficulty();
        this.added = t.getAdded();
        this.speaker = new UserProfil(t.getUser().getFirstname(), t.getUser().getLastname());
        this.cospeakers = t.getCospeakers().stream().map( u -> new CospeakerProfil(u.getEmail()) ).collect(Collectors.toSet());
        this.room = t.getRoom() != null ? t.getRoom().getId() : null;
        if (t.getDate() != null) {
            this.schedule = DateTimeFormatter.ISO_INSTANT.format(t.getDate().toInstant());
        }
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Talk.State getState() {
        return state;
    }

    public void setState(Talk.State state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public Integer getTrackId() {
        return trackId;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Date getAdded() {
        return added;
    }

    public void setAdded(Date added) {
        this.added = added;
    }

    public UserProfil getSpeaker() {
        return speaker;
    }

    public void setSpeaker(UserProfil speaker) {
        this.speaker = speaker;
    }

    public String getTrackLabel() {
        return trackLabel;
    }

    public void setTrackLabel(String trackLabel) {
        this.trackLabel = trackLabel;
    }

    public void setCospeaker(Set<CospeakerProfil> cospeakers) {
        this.cospeakers = cospeakers;
    }

    public Set<CospeakerProfil> getCospeakers() {
        return cospeakers;
    }

    public void setCospeakers(Set<CospeakerProfil> cospeakers) {
        this.cospeakers = cospeakers;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public void setRoom(Integer room) {
        this.room = room;
    }

    public Integer getRoom() {
        return room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TalkUser talkUser = (TalkUser) o;

        return id == talkUser.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
