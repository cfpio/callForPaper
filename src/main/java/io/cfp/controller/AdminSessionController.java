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

package io.cfp.controller;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.itextpdf.text.DocumentException;
import io.cfp.domain.exception.CospeakerNotFoundException;
import io.cfp.dto.EventSched;
import io.cfp.dto.TalkAdmin;
import io.cfp.dto.TalkAdminCsv;
import io.cfp.entity.Event;
import io.cfp.entity.Role;
import io.cfp.entity.Talk;
import io.cfp.repository.TalkRepo;
import io.cfp.service.PdfCardService;
import io.cfp.service.TalkAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Controller
@RequestMapping(value = { "/v0/admin", "/api/admin" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class AdminSessionController {

    @Autowired
    private TalkAdminService talkService;

    @Autowired
    private TalkRepo talks;

    @Autowired
    private PdfCardService pdfCardService;

    /**
     * Get all sessions
     */
    @RequestMapping(value="/sessions", method= RequestMethod.GET)
    @Secured({Role.REVIEWER, Role.ADMIN})
    @ResponseBody
    @Deprecated
    public List<TalkAdmin> getAllSessions(@RequestParam(name = "status", required = false) String status) {

        Talk.State[] accept;
        if (status == null) {
            accept = new Talk.State[] { Talk.State.CONFIRMED, Talk.State.ACCEPTED, Talk.State.REFUSED, Talk.State.BACKUP };
        } else {
            accept = new Talk.State[] { Talk.State.valueOf(status) };
        }

        return talkService.findAll(accept);
    }

    /**
     * Get all drafts
     */
    @RequestMapping(value="/drafts", method= RequestMethod.GET)
    @Secured(Role.ADMIN)
    @ResponseBody
    public List<TalkAdmin> getAllDrafts() {
        return talkService.findAll(Talk.State.DRAFT);
    }

    /**
     * Get a specific session
     */
    @RequestMapping(value= "/sessions/{talkId}", method= RequestMethod.GET)
    @Secured({Role.REVIEWER, Role.ADMIN})
    @ResponseBody
    public TalkAdmin getTalk(@PathVariable int talkId) {
        return talkService.getOne(talkId);
    }

    /**
     * Edit a specific session
     */
    @RequestMapping(value= "/sessions/{talkId}", method= RequestMethod.PUT)
    @Secured(Role.ADMIN)
    @ResponseBody
    public TalkAdmin editTalk(@PathVariable int talkId, @RequestBody TalkAdmin talkAdmin) throws CospeakerNotFoundException, ParseException {
        talkAdmin.setId(talkId);
        return talkService.edit(talkAdmin);
    }

    @RequestMapping(value= "/sessions/{talkId}/accept", method= RequestMethod.PUT)
    @Secured(Role.ADMIN)
    @ResponseBody
    public void accept(@PathVariable int talkId) throws CospeakerNotFoundException{
        talks.setState(talkId, Event.current(), Talk.State.ACCEPTED);
    }

    @RequestMapping(value= "/sessions/{talkId}/backup", method= RequestMethod.PUT)
    @Secured(Role.ADMIN)
    @ResponseBody
    public void backup(@PathVariable int talkId) throws CospeakerNotFoundException{
        talks.setState(talkId, Event.current(), Talk.State.BACKUP);
    }

    @RequestMapping(value= "/sessions/{talkId}/reject", method= RequestMethod.PUT)
    @Secured(Role.ADMIN)
    @ResponseBody
    public void reject(@PathVariable int talkId) throws CospeakerNotFoundException{
        talks.setState(talkId, Event.current(), Talk.State.REFUSED);
    }

    @RequestMapping(value= "/sessions/rejectOthers", method= RequestMethod.PUT)
    @Secured(Role.ADMIN)
    @ResponseBody
    public void rejectOthers() throws CospeakerNotFoundException{
        talks.setStateWhere(Event.current(), Talk.State.REFUSED, Talk.State.CONFIRMED);
    }

    @RequestMapping(value= "/sessions/{talkId}/retract", method= RequestMethod.PUT)
    @Secured(Role.ADMIN)
    @ResponseBody
    public void retract(@PathVariable int talkId) throws CospeakerNotFoundException{
        talks.setState(talkId, Event.current(), Talk.State.CONFIRMED);
    }


    /**
     * Delete a session
     */
    @RequestMapping(value="/sessions/{talkId}", method= RequestMethod.DELETE)
    @Secured(Role.ADMIN)
    @ResponseBody
    public TalkAdmin delete(@PathVariable int talkId) {
        return talkService.delete(talkId);
    }

    /**
     * Delete all sessions (aka reset CFP)
     */
    @RequestMapping(value="/sessions", method= RequestMethod.DELETE)
    @Secured(Role.ADMIN)
    @ResponseBody
    public void deleteAll() {
        talks.deleteAllByEventId(Event.current());
    }


    @RequestMapping(path = "/sessions/export/cards.pdf", produces = "application/pdf")
    @Secured(Role.ADMIN)
    public void exportPdf(HttpServletResponse response) throws IOException, DocumentException {
        response.addHeader(HttpHeaders.CONTENT_TYPE, "application/pdf");
        pdfCardService.export(response.getOutputStream());
    }

    @RequestMapping(path = "/sessions/export/sched.json", produces = "application/json")
    @Secured(Role.ADMIN)
    @ResponseBody
    public List<EventSched> exportSched(@RequestParam(required = false) Talk.State[] states) {
        if (states == null) {
            states = new Talk.State[] { Talk.State.ACCEPTED };
        }
        return talkService.exportSched(states);
    }

    @RequestMapping(path = "/sessions/export/sessions.csv", produces = "text/csv")
    @Secured(Role.ADMIN)
    public void exportCsv(HttpServletResponse response, @RequestParam(name = "status", required = false) String status) throws IOException {
        response.addHeader(HttpHeaders.CONTENT_TYPE, "text/csv");

        CsvMapper mapper = new CsvMapper();
        mapper.addMixIn(TalkAdmin.class, TalkAdminCsv.class);

        CsvSchema schema = mapper.schemaFor(TalkAdmin.class).withHeader();
        ObjectWriter writer = mapper.writer(schema);

        List<TalkAdmin> sessions = getAllSessions(status);
        for (TalkAdmin s : sessions) {
            writer.writeValue(response.getOutputStream(), s);
        }
    }
}
