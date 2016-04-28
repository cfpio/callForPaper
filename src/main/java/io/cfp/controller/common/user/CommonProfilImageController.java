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

package io.cfp.controller.common.user;

import io.cfp.config.global.GlobalSettings;
import io.cfp.domain.common.Uri;
import io.cfp.domain.exception.NotFoundException;
import io.cfp.entity.User;
import io.cfp.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(value="/api/profil")
public class CommonProfilImageController {

    @Autowired
    private UserService userService;

    @Autowired
    private GlobalSettings globalSettings;

    /**
     * Get user profil image key
     * @param req
     * @param res
     * @param id
     * @throws IOException
     * @throws NotFoundException
    @RequestMapping(value="/image/user/{id}", method= RequestMethod.GET)
    public void getProfileImageByUserId(HttpServletRequest req, HttpServletResponse res,  @PathVariable Integer id) throws IOException, NotFoundException {
        User u = userService.findById(id);
        if (u == null) throw new NotFoundException("User not found");

        byte[] image = u.getImage();
        if (image == null || image.length == 0) {
            throw new NotFoundException("Image not found");
        }

        res.getOutputStream().write(image);
    }

    /**
     * Get user profil image URL
     * @param req
     * @param res
     * @param id
     * @return
     * @throws IOException
     * @throws NotFoundException
    @RequestMapping(value="/image/user/url/{id}", method= RequestMethod.GET)
    public Uri getProfilImageUrlByUserId(HttpServletRequest req, HttpServletResponse res, @PathVariable Integer id) throws IOException, NotFoundException {
        User u = userService.findById(id);
        if (u == null) throw new NotFoundException("User not found");

        byte[] image = u.getImage();

        if(image != null && image.length > 0) {
            return new Uri(globalSettings.getHostname() + "/api/profil/image/user/" + id);
        }

        if(u.getImageSocialUrl() != null && u.getImageSocialUrl().length() > 0) {
            return new Uri(u.getImageSocialUrl());
        }
        return new Uri();
    }
     */
}
