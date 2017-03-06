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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Entity
@Table(name = "events")
@Data @Builder(toBuilder = true) @NoArgsConstructor @AllArgsConstructor
public class Event {
    private static final Logger logger = LoggerFactory.getLogger(Event.class);

    private static ThreadLocal<String> current = new ThreadLocal<String>();

    public static String current() {
        String s = current.get();
        if (s == null) {
            logger.warn("current event is not set. Falling back to 'demo'");
            s = "demo";
        }
        return s ;
    }

    @Id
    private String id;

    private String name;

    @Column(name = "short_description")
    private String shortDescription;

    private boolean published;

    private String url;

    private String logoUrl;

    private String videosUrl;

    private String contactMail;

    @Type(type="date")
    private Date date;

    private int duration;

    @Type(type="date")
    private Date releaseDate;

    @Type(type="date")
    private Date decisionDate;

    private boolean open = true;

    public static void setCurrent(String tenant) {
        current.set(tenant);
    }

    public static void unsetCurrent() {
        current.remove();
    }


}
