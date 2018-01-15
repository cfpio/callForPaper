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

import com.fasterxml.jackson.annotation.JsonInclude;
import io.cfp.dto.user.CospeakerProfil;
import io.cfp.dto.user.UserProfil;
import io.cfp.entity.Talk;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Talk DTO for user view
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private String video;
    private String slides;


    public TalkUser(Talk t) {
        this.id = t.getId();
        this.state = t.getState();
        this.name = t.getName() != null ? t.getName() : "undefined";
        this.language = t.getLanguage();
        this.format = t.getFormat().getId();
        this.trackId = t.getTrack().getId();
        this.trackLabel = t.getTrack().getLibelle();
        this.description = t.getDescription();
        this.references = t.getReferences();
        this.difficulty = t.getDifficulty();
        this.added = t.getAdded();
        this.speaker = new UserProfil(t.getId(), t.getUser().getFirstname(), t.getUser().getLastname(),  t.getUser().getEmail());
        this.cospeakers = t.getCospeakers().stream().map( u -> new CospeakerProfil(u.getEmail()) ).collect(Collectors.toSet());
        this.room = t.getRoom() != null ? t.getRoom().getId() : null;
        if (t.getDate() != null) {
            this.schedule = DateTimeFormatter.ISO_INSTANT.format(t.getDate().toInstant());
        }
        this.video = t.getVideo();
        this.slides = t.getSlides();
    }
}
