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

import io.cfp.entity.Talk;
import io.cfp.model.Proposal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Talk DTO for admin view
 */
public class TalkAdmin extends TalkUser {

    private int userId;

    private boolean reviewed;

    private BigDecimal mean;
    private List<String> voteUsersEmail;

    public TalkAdmin() {
    }

    public TalkAdmin(Talk t) {
        super(t);
    }

    public TalkAdmin(Proposal p) {
        super(p);
    }

    public void setMean(Double mean) {
        if (mean == null) return;

        if (mean == 0) {
            this.mean = BigDecimal.ZERO;
        } else {
            this.mean = new BigDecimal(mean).setScale(2, RoundingMode.HALF_EVEN);
        }
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }

    public BigDecimal getMean() {
        return mean;
    }


    public List<String> getVoteUsersEmail() {
        return voteUsersEmail;
    }

    public void setVoteUsersEmail(List<String> voteUsersEmail) {
        this.voteUsersEmail = voteUsersEmail;
    }
}
