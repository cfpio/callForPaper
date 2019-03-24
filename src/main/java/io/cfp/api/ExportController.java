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

package io.cfp.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.cfp.dto.EventSched;
import io.cfp.dto.TalkAdmin;
import io.cfp.dto.TalkAdminCsv;
import io.cfp.entity.Talk;
import io.cfp.model.Proposal;
import io.cfp.model.Role;
import io.cfp.model.User;
import io.cfp.multitenant.TenantId;
import io.cfp.service.TalkAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value= { "/v1/admin", "/api/admin" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ExportController {

    @Autowired
    private TalkAdminService talkService;

    @GetMapping("/sessions/export/sched.json")
    @Secured(Role.ADMIN)
    @ResponseBody
    public List<EventSched> exportSched(@RequestParam(required = false) Proposal.State[] states,
                                        @TenantId String eventId) {
        if (states == null) {
            states = new Proposal.State[] { Proposal.State.ACCEPTED };
        }
        return talkService.exportSched(eventId, states);
    }

    @RequestMapping(path = "/sessions/export/sessions.csv", produces = "text/csv")
    @Secured(io.cfp.model.Role.ADMIN)
    public void exportCsv(@AuthenticationPrincipal User user,
                          HttpServletResponse response,
                          @RequestParam(name = "status", required = false) String status,
                          @TenantId String eventId) throws IOException {
        response.addHeader(HttpHeaders.CONTENT_TYPE, "text/csv");

        CsvMapper mapper = new CsvMapper();
        mapper.addMixIn(TalkAdmin.class, TalkAdminCsv.class);

        CsvSchema schema = mapper.schemaFor(TalkAdmin.class).withHeader();
        ObjectWriter writer = mapper.writer(schema);
        writer.getFactory().disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);

        Talk.State[] accept;
        if (status == null) {
            accept = new Talk.State[] { Talk.State.CONFIRMED, Talk.State.ACCEPTED, Talk.State.REFUSED, Talk.State.BACKUP };
        } else {
            accept = new Talk.State[] { Talk.State.valueOf(status) };
        }

        List<TalkAdmin> sessions =  talkService.findAll(eventId, user.getId(), accept);


        final ServletOutputStream out = response.getOutputStream();
        writer.writeValues(out).writeAll(sessions);
    }


}
