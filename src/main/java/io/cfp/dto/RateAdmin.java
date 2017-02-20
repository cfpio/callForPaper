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

import io.cfp.dto.user.AdminUserDTO;
import io.cfp.entity.Rate;

import java.util.Date;

/**
 * Rate DTO for admin view
 */
public class RateAdmin {

    private int id;
    private int rate;
    private Date added;
    private boolean love;
    private boolean hate;

    private int talkId;
    private AdminUserDTO user;

    public RateAdmin() {
    }

    public RateAdmin(Rate e) {
        this.id = e.getId();
        this.rate = e.getRate();
        this.added = e.getAdded();
        this.love = e.isLove();
        this.hate = e.isHate();
        this.talkId = e.getTalk().getId();
        this.user = new AdminUserDTO(e.getAdminUser());
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public Date getAdded() {
        return added;
    }

    public void setAdded(Date added) {
        this.added = added;
    }

    public boolean isLove() {
        return love;
    }

    public void setLove(boolean love) {
        this.love = love;
    }

    public boolean isHate() {
        return hate;
    }

    public void setHate(boolean hate) {
        this.hate = hate;
    }

    public int getTalkId() {
        return talkId;
    }

    public void setTalkId(int talkId) {
        this.talkId = talkId;
    }

    public AdminUserDTO getUser() {
        return user;
    }

    public void setUser(AdminUserDTO user) {
        this.user = user;
    }
}
