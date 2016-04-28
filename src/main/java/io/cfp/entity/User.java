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

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.springframework.util.StringUtils;

/**
 * Speaker account
 */
@Entity
@Table(name = "users")
public class User {
    public enum Provider {GOOGLE, GITHUB}

    private int id;
    private String email;

    /**
     * github oauth id if user connect with his account
     */
    private String githubId;
    /**
     * google oauth id if user connect with his account
     */
    private String googleId;

    /**
     * password if user have a local account
     */
    private String password;
    /**
     * for local account, true if user validate his e-mail address
     */
    private boolean verified;
    /**
     * token to verify local e-mail address, empty when e-mail verified
     */
    private String verifyToken;


    /* ****  USER PROFILE  **** */
    private String lastname;
    private String firstname;
    private String company;
    private String phone;
    private String bio;
    private String twitter;
    private String googleplus;
    private String github;
    /**
     * other url (blog, linkedin...)
     */
    private String social;

    /**
     * local stored image for user
     */
    private String imageProfilURL;

    /**
     * Count sign in provider the user has
     *
     * @return Number of sign in provider
     */
    @Transient
    public int getSignInMethodCount() {
        int count = 0;
        if (password != null) count++;
        if (githubId != null) count++;
        if (googleId != null) count++;
        return count;
    }

    @Transient
    public void setProviderId(Provider provider, String providerId) {
        switch (provider) {
            case GOOGLE:
                this.setGoogleId(providerId);
                break;
            case GITHUB:
                this.setGithubId(providerId);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    @Email
    public String getEmail() {
        return email;
    }

    @Column(name = "github_id")
    public String getGithubId() {
        return githubId;
    }

    @Column(name = "google_id")
    public String getGoogleId() {
        return googleId;
    }

    public String getPassword() {
        return password;
    }

    public boolean isVerified() {
        return verified;
    }

    @Column(name = "verify_token")
    public String getVerifyToken() {
        return verifyToken;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getCompany() {
        return company;
    }

    public String getPhone() {
        return phone;
    }

    @Type(type = "text")
    public String getBio() {
        return bio;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getGoogleplus() {
        return googleplus;
    }

    public String getGithub() {
        return github;
    }

    public String getSocial() {
        return social;
    }

    @Column(name = "image_profil_url")
    public String getImageProfilURL() {
        return imageProfilURL;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setVerifyToken(String verifyToken) {
        this.verifyToken = verifyToken;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setGoogleplus(String googleplus) {
        this.googleplus = googleplus;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public void setSocial(String social) {
        this.social = social;
    }

    public void setImageProfilURL(String imageProfilURL) {
        this.imageProfilURL = imageProfilURL;
    }

    public void mergeData(User userToMerge) {
        if (this.getFirstname() == null) {
            this.setFirstname(userToMerge.getFirstname());
        }

        if (this.getLastname() == null) {
            this.setLastname(userToMerge.getLastname());
        }

        if (this.getEmail() == null) {
            this.setEmail(userToMerge.getEmail());
        }

        if (this.getGithub() == null) {
            this.setGithub(userToMerge.getGithub());
        }

        if (this.getGoogleplus() == null) {
            this.setGoogleplus(userToMerge.getGoogleplus());
        }

        if (this.getBio() == null) {
            this.setBio(userToMerge.getBio());
        }

        if (this.getCompany() == null) {
            this.setCompany(userToMerge.getCompany());
        }

        if (this.getImageProfilURL() == null) {
            if (userToMerge.getImageProfilURL() != null && !userToMerge.getImageProfilURL().equals("")) {
                this.setImageProfilURL(userToMerge.getImageProfilURL());
            }
        }
    }
}
