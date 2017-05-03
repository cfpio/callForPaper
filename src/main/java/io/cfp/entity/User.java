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

import io.cfp.dto.user.AdminUserDTO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import java.util.*;

/**
 * Speaker account
 */
@Entity
@Table(name = "users")
public class User {

    private static final ThreadLocal<User> current = new ThreadLocal<>();

    public static User getCurrent() {
        return current.get();
    }

    public static void setCurrent(User user) {
        current.set(user);
    }

    public static void resetCurrent() {
        current.remove();
    }


    public enum Gender {
		MALE, FEMALE
	}

	public enum TshirtSize {
		XS, S, M, L, XL, XXL
	}

    private int id;
    private String email;

    /* ****  USER PROFILE  **** */
    private String lastname;
    private String firstname;
    private String company;
    private String phone;
    private String bio;
    private String twitter;
    private String googleplus;
    private String github;
    private String language;
    private Gender gender = Gender.MALE;
    private TshirtSize tshirtSize = TshirtSize.M;

    /**
     * other url (blog, linkedin...)
     */
    private String social;

    /**
     * local stored image for user
     */
    private String imageProfilURL;

    private Set<String> roles;

    public User() {
    }

    public User(io.cfp.model.User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.lastname = user.getLastname();
        this.firstname = user.getFirstname();
        this.company = user.getCompany();
        this.phone = user.getPhone();
        this.bio = user.getBio();
        this.twitter = user.getTwitter();
        this.googleplus = user.getGoogleplus();
        this.github = user.getGithub();
        this.language = user.getLanguage();
        this.gender = Gender.valueOf(user.getGender().name());
        this.tshirtSize = TshirtSize.valueOf(user.getTshirtSize().name());
        this.social = user.getSocial();
        this.imageProfilURL = user.getImageProfilURL();
        this.roles = user.getRoles();
    }

    @Transient
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    @Transient
    public Set<String> getRoles() {
        return roles != null ? roles : Collections.EMPTY_SET;
    }

    public User addRole(String ... role) {
        if (roles == null) roles = new HashSet<>();
        for (String r : role) {
            roles.add(r);
        }
        return this;
    }

    public User addRoles(Collection<String> r) {
        if (roles == null) roles = new HashSet<>();
        roles.addAll(r);
        return this;
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

    @Type(type = "string")
    public String getLanguage() {
        return language;
    }

    @Transient
    public Locale getLocale() {
        if (language != null && language.equalsIgnoreCase("français")) return Locale.FRENCH;
        return Locale.ENGLISH;
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

    @Enumerated(EnumType.STRING)
    public Gender getGender() {
		return gender;
	}

    @Column(name = "tshirt_size")
    @Enumerated(EnumType.STRING)
	public TshirtSize getTshirtSize() {
		return tshirtSize;
	}


    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setLanguage(String language) {
        this.language= language;
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

    public void setGender(Gender gender) {
		this.gender = gender;
	}

	public void setTshirtSize(TshirtSize tshirtSize) {
		this.tshirtSize = tshirtSize;
	}

    public User id(int id) {
        this.id = id;
        return this;
    }

    public User email(String email) {
        this.email = email;
        return this;
    }

    public User lastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public User firstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public User company(String company) {
        this.company = company;
        return this;
    }

    public User phone(String phone) {
        this.phone = phone;
        return this;
    }

    public User bio(String bio) {
        this.bio = bio;
        return this;
    }

    public User twitter(String twitter) {
        this.twitter = twitter;
        return this;
    }

    public User googleplus(String googleplus) {
        this.googleplus = googleplus;
        return this;
    }

    public User github(String github) {
        this.github = github;
        return this;
    }

    public User language(String language) {
        this.language = language;
        return this;
    }

    public User social(String social) {
        this.social = social;
        return this;
    }

    public User imageProfilURL(String imageProfilURL) {
        this.imageProfilURL = imageProfilURL;
        return this;
    }

    public User gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public User tshirtSize(TshirtSize tshirtSize) {
        this.tshirtSize = tshirtSize;
        return this;
    }

    public AdminUserDTO toAdminUserDTO() {
        AdminUserDTO result = new AdminUserDTO();
        result.setEmail(this.getEmail());
        result.setName(this.getFirstname()+" "+this.getLastname());
        return result;
    }

    // Used to trace current user in application logs
    public String toLog() {
        return email + '(' + StringUtils.join(roles) + ')';
    }
}

