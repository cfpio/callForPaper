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

package io.cfp.dto.user;

/**
 * Created by Nicolas on 30/01/2016.
 */

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "event_key",
    "active",
    "name",
    "event_start",
    "event_end",
    "event_type",
    "seats",
    "goers",
    "invite_only",
    "venue",
    "id",
    "venue_id",
    "speakers",
    "description",
    "media_url"
})
public class Schedule {

    @JsonProperty("event_key")
    private String eventKey;
    @JsonProperty("active")
    private String active;
    @JsonProperty("name")
    private String name;
    @JsonProperty("event_start")
    private String eventStart;
    @JsonProperty("event_end")
    private String eventEnd;
    @JsonProperty("event_type")
    private String eventType;
    @JsonProperty("seats")
    private String seats;
    @JsonProperty("goers")
    private String goers;
    @JsonProperty("invite_only")
    private String inviteOnly;
    @JsonProperty("venue")
    private String venue;
    @JsonProperty("id")
    private int id;
    @JsonProperty("venue_id")
    private String venueId;
    @JsonProperty("speakers")
    private String speakers;
    @JsonProperty("description")
    private String description;
    @JsonProperty("media_url")
    private String mediaUrl;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Schedule() {
        super();
    }

    public Schedule(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;

        // default values
        this.eventKey = "tobedefined";
        this.active = "Y";
        this.eventStart = "tobedefined";
        this.eventEnd = "tobedefined";
        this.seats = "tobedefined";
        this.goers = "tobedefined";
        this.inviteOnly = "N";
        this.venue = "tobedefined";
        this.venueId = "tobedefined";
    }

    /**
     * @return The eventKey
     */
    @JsonProperty("event_key")
    public String getEventKey() {
        return eventKey;
    }

    /**
     * @param eventKey The event_key
     */
    @JsonProperty("event_key")
    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    /**
     * @return The active
     */
    @JsonProperty("active")
    public String getActive() {
        return active;
    }

    /**
     * @param active The active
     */
    @JsonProperty("active")
    public void setActive(String active) {
        this.active = active;
    }

    /**
     * @return The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The eventStart
     */
    @JsonProperty("event_start")
    public String getEventStart() {
        return eventStart;
    }


    @JsonProperty("start")
    public String getStart() { return eventStart; }

    @JsonProperty("end")
    public String getEnd() { return eventEnd; }


    /**
     * @param eventStart The event_start
     */
    @JsonProperty("event_start")
    public void setEventStart(String eventStart) {
        this.eventStart = eventStart;
    }

    /**
     * @return The eventEnd
     */
    @JsonProperty("event_end")
    public String getEventEnd() {
        return eventEnd;
    }

    /**
     * @param eventEnd The event_end
     */
    @JsonProperty("event_end")
    public void setEventEnd(String eventEnd) {
        this.eventEnd = eventEnd;
    }

    /**
     * @return The eventType
     */
    @JsonProperty("event_type")
    public String getEventType() {
        return eventType;
    }

    /**
     * @param eventType The event_type
     */
    @JsonProperty("event_type")
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * @return The seats
     */
    @JsonProperty("seats")
    public String getSeats() {
        return seats;
    }

    /**
     * @param seats The seats
     */
    @JsonProperty("seats")
    public void setSeats(String seats) {
        this.seats = seats;
    }

    /**
     * @return The goers
     */
    @JsonProperty("goers")
    public String getGoers() {
        return goers;
    }

    /**
     * @param goers The goers
     */
    @JsonProperty("goers")
    public void setGoers(String goers) {
        this.goers = goers;
    }

    /**
     * @return The inviteOnly
     */
    @JsonProperty("invite_only")
    public String getInviteOnly() {
        return inviteOnly;
    }

    /**
     * @param inviteOnly The invite_only
     */
    @JsonProperty("invite_only")
    public void setInviteOnly(String inviteOnly) {
        this.inviteOnly = inviteOnly;
    }

    /**
     * @return The venue
     */
    @JsonProperty("venue")
    public String getVenue() {
        return venue;
    }

    /**
     * @param venue The venue
     */
    @JsonProperty("venue")
    public void setVenue(String venue) {
        this.venue = venue;
    }

    /**
     * @return The id
     */
    @JsonProperty("id")
    public int getId() {
        return id;
    }

    /**
     * @param id The id
     */
    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The venueId
     */
    @JsonProperty("venue_id")
    public String getVenueId() {
        return venueId;
    }

    /**
     * @param venueId The venue_id
     */
    @JsonProperty("venue_id")
    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    /**
     * @return The speakers
     */
    @JsonProperty("speakers")
    public String getSpeakers() {
        return speakers;
    }

    /**
     * @param speakers The speakers
     */
    @JsonProperty("speakers")
    public void setSpeakers(String speakers) {
        this.speakers = speakers;
    }

    /**
     * @return The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The mediaUrl
     */
    @JsonProperty("media_url")
    public String getMediaUrl() {
        return mediaUrl;
    }

    /**
     * @param mediaUrl The media_url
     */
    @JsonProperty("media_url")
    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}

