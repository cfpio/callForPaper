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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Proposal {

    public static final List<String> AUTHORIZED_SORTS = Arrays.asList("state", "name", "language", "tracklabel", "difficulty", "added");

    public enum State { DRAFT, CONFIRMED, PRESENT, ACCEPTED, REFUSED, BACKUP }

    private int id;
    private State state;
    @NotNull(message = "Session name field is required")
    private String name;
    private String language;
    private String eventId;
    private Integer trackId;
    private String trackLabel;
    private String description;
    private String references;
    private Integer difficulty;
    private Date added;
    private Integer format;
    private String formatName;
    private User speaker;

    private Date schedule;
    private String scheduleHour;
    private Integer roomId;

    private Set<User> cospeakers = new HashSet<>();

    private String video;
    private String slides;

    private List<String> voteUsersEmail;
    private String mean;


    public String buildSpeakersList() {
        String res = this.getSpeaker().getFullName();
        if (isNotEmpty(this.getCospeakers())) {
            for (User user : this.getCospeakers()) {
                res += ", " + user.getFullName();
            }
        }
        return res;
    }

    @JsonIgnore
    public Set<Integer> getSpeakersIds() {
        Set<Integer> speakersIds = new HashSet<>();
        speakersIds.add(this.getSpeaker().getId());
        speakersIds.addAll(this.getCospeakers().stream().map(User::getId).collect(Collectors.toSet()));
        return speakersIds;
    }

}
