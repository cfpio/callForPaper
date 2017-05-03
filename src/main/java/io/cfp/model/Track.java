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

package io.cfp.model;

import io.cfp.entity.Event;
import org.hibernate.annotations.Type;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

public class Track {

    private int id;
    private String libelle;
    private String description;
    private String color;
    private Event event;

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

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    @Type(type="text")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Track id(int id) {
        this.id = id;
        return this;
    }

    public Track libelle(String libelle) {
        this.libelle = libelle;
        return this;
    }

    public Track description(String description) {
        this.description = description;
        return this;
    }

    public Track color(String color) {
        this.color = color;
        return this;
    }

    public Track event(Event event) {
        this.event = event;
        return this;
    }
}
