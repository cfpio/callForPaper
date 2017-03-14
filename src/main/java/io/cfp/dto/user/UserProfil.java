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

import com.fasterxml.jackson.annotation.JsonInclude;
import io.cfp.entity.User;
import io.cfp.entity.User.Gender;
import io.cfp.entity.User.TshirtSize;

/**
 * Created by tmaugin on 05/06/2015.
 * SII
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfil {
    private int id;
    private String lastname;
    private String firstname;
    private String company;
    private String phone;
    private String bio;
    private String social;
    private String twitter;
    private String googleplus;
    private String github;
    private String imageProfilURL;
    private String email;
    private String language = "fr";
    private Gender gender;
    private TshirtSize tshirtSize;

    public UserProfil() {
        super();
    }

    public UserProfil(String firstname, String lastname) {
        super();
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public UserProfil(User user, boolean includePrivateData) {
        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.company = user.getCompany();
        this.twitter = user.getTwitter();
        this.bio = user.getBio();
        if (includePrivateData) {
            this.email = user.getEmail();
            this.phone = user.getPhone();
            this.gender = user.getGender();
            this.tshirtSize = user.getTshirtSize();
        }
    }

    public String getShortName() {
        String res = "";
        if (firstname != null && firstname.length() > 0) {
            res += firstname.charAt(0) + ". ";
        }

        if (lastname != null) {
            res += lastname;
        }

        return res;
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

    public String getPhone() {
        return phone;
    }

    public String getLanguage() {
        return language;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLanguage(String language) {
        this.language = language;
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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public TshirtSize getTshirtSize() {
		return tshirtSize;
	}

	public void setTshirtSize(TshirtSize tshirtSize) {
		this.tshirtSize = tshirtSize;
	}

}
