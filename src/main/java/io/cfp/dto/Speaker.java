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

import io.cfp.entity.User;

/**
 * Created by sylvain on 02/03/16.
 */
public class Speaker {

    private int id;
    private String lastname;
    private String firstname;
    private String company;
    private String bio;
    private String social;
    private String twitter;
    private String googleplus;
    private String github;
    private String imageProfilURL;

    public Speaker() { }

    public Speaker(User user) {
        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.company = user.getCompany();
        this.bio = user.getBio();
        this.social = user.getSocial();
        this.twitter = user.getTwitter();
        this.googleplus = user.getGoogleplus();
        this.github = user.getGithub();
        this.imageProfilURL = user.getImageProfilURL();
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSocial() {
        return social;
    }

    public void setSocial(String social) {
        this.social = social;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getGoogleplus() {
        return googleplus;
    }

    public void setGoogleplus(String googleplus) {
        this.googleplus = googleplus;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getImageProfilURL() {
        return imageProfilURL;
    }

    public void setImageProfilURL(String imageProfilURL) {
        this.imageProfilURL = imageProfilURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Speaker speaker = (Speaker) o;

        return id == speaker.id;

    }

    @Override
    public int hashCode() {
        return id;
    }


    public Speaker id(int id) {
        this.id = id;
        return this;
    }

    public Speaker lastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public Speaker firstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public Speaker company(String company) {
        this.company = company;
        return this;
    }

    public Speaker bio(String bio) {
        this.bio = bio;
        return this;
    }

    public Speaker social(String social) {
        this.social = social;
        return this;
    }

    public Speaker twitter(String twitter) {
        this.twitter = twitter;
        return this;
    }

    public Speaker googleplus(String googleplus) {
        this.googleplus = googleplus;
        return this;
    }

    public Speaker github(String github) {
        this.github = github;
        return this;
    }

    public Speaker imageProfilURL(String imageProfilURL) {
        this.imageProfilURL = imageProfilURL;
        return this;
    }
}
