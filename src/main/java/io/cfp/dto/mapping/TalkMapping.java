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

package io.cfp.dto.mapping;

import org.springframework.stereotype.Component;

import io.cfp.config.mapping.Mapping;
import io.cfp.dto.Speaker;
import io.cfp.dto.TalkUser;
import io.cfp.dto.user.UserProfil;
import io.cfp.dto.user.CospeakerProfil;
import io.cfp.entity.Talk;
import io.cfp.dto.TalkAdmin;

import io.cfp.entity.User;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;

@Component
public class TalkMapping implements Mapping {

    @Override
    public void mapClasses(MapperFactory mapperFactory) {
        mapperFactory.classMap(Talk.class, TalkUser.class)
            .field("user", "speaker")
            .field("track.id", "trackId")
            .field("track.libelle", "trackLabel")
			.field("talkFormat.id", "format")
            .exclude("cospeakers")
            .customize(new CustomMapper<Talk, TalkUser>() {
                @Override
                public void mapAtoB(Talk talk, TalkUser talkUser, MappingContext context) {
                    if (talk.getCospeakers() != null) {
                        talkUser.setCospeaker(mapperFactory.getMapperFacade().mapAsSet(talk.getCospeakers(), CospeakerProfil.class));
                    }
                }

            })
            .byDefault()
            .register();

        mapperFactory.classMap(User.class, CospeakerProfil.class)
            .byDefault()
            .register();

        mapperFactory.classMap(User.class, UserProfil.class)
            .byDefault()
            .register();

        mapperFactory.classMap(Talk.class, TalkAdmin.class)
                .field("user", "speaker")
                .field("track.id", "trackId")
                .field("track.libelle", "trackLabel")
    			.field("talkFormat.id", "format")
                .exclude("cospeakers")
                .customize(new CustomMapper<Talk, TalkAdmin>() {
                    @Override
                    public void mapAtoB(Talk talk, TalkAdmin talkUser, MappingContext context) {
                        if (talk.getCospeakers() != null) {
                            talkUser.setCospeaker(mapperFactory.getMapperFacade().mapAsSet(talk.getCospeakers(), CospeakerProfil.class));
                        }
                    }

                })
                .byDefault()
                .register();


        mapperFactory.classMap(User.class, Speaker.class)
            .byDefault()
            .register();

        mapperFactory.classMap(User.class, Speaker.class)
                .byDefault()
                .register();


    }
}
