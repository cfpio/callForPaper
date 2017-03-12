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

import io.cfp.entity.Track;

public class TrackDto {
    private int id;
    private String libelle;
    private String description;
    private boolean referenced;
    private String color;

    public TrackDto() {
    }

    public TrackDto(Track track, boolean referenced) {
        this.id = track.getId();
        this.libelle = track.getLibelle();
        this.description = track.getDescription();
        this.color = track.getColor();
        this.referenced = referenced;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public boolean isReferenced() {
		return referenced;
	}

	public void setReferenced(boolean referenced) {
		this.referenced = referenced;
	}

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
