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

import io.cfp.entity.Event;
import org.apache.commons.lang.time.FastDateFormat;

/**
 * Public application settings
 */
public class ApplicationSettings {

    private String eventName;
    private String shortDescription;
    private String date;
    private String releaseDate;
    private String decisionDate;
    private String authServer;
    private boolean open;
    private String website;
    private String logo;

    public ApplicationSettings() { }

    public ApplicationSettings(Event event) {
        FastDateFormat format = FastDateFormat.getInstance("dd/MM/yyyy");

        eventName = event.getName();
        date = format.format(event.getDate());
        releaseDate = format.format(event.getReleaseDate());
        decisionDate = format.format(event.getDecisionDate());
        shortDescription = event.getShortDescription();
        website = event.getUrl();
        this.logo = event.getLogoUrl() != null ? event.getLogoUrl() : "/images/logo.png";
        open = event.isOpen();
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(String decisionDate) {
        this.decisionDate = decisionDate;
    }

    public String getAuthServer() {
        return authServer;
    }

    public void setAuthServer(String authServer) {
        this.authServer = authServer;
    }

    public boolean isConfigured() {
        return true;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
