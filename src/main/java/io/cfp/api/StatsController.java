package io.cfp.api;

import io.cfp.domain.admin.meter.AdminMeter;
import io.cfp.dto.RestrictedMeter;
import io.cfp.mapper.ProposalMapper;
import io.cfp.model.Role;
import io.cfp.model.User;
import io.cfp.model.queries.ProposalQuery;
import io.cfp.multitenant.TenantId;
import io.cfp.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

import static io.cfp.model.Proposal.State.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = { "/v1/stats", "/api/stats" }, produces = APPLICATION_JSON_UTF8_VALUE)
public class StatsController {

    @Autowired
    private ProposalMapper proposals;

    @Autowired
    private SubmissionRepository submissions;

    /**
     * Get meter stats (talks count, draft count, ...)
     */
    @GetMapping(value="/meter")
    @ResponseBody
    @Secured(Role.AUTHENTICATED)
    @Transactional(readOnly = true)
    public Serializable getMeter(@AuthenticationPrincipal User user,
                                 @TenantId String event) {

        if (user.hasRole(Role.REVIEWER)) {
            AdminMeter meter = new AdminMeter();
            meter.setSpeakers(submissions.countByEventId(event));

            ProposalQuery totalQuery = new ProposalQuery().setEventId(event).addStates(CONFIRMED, ACCEPTED, REFUSED, BACKUP);
            meter.setTalks(proposals.count(totalQuery));

            ProposalQuery draftQuery = new ProposalQuery().setEventId(event).addStates(DRAFT);
            meter.setDrafts(proposals.count(draftQuery));

            ProposalQuery submittedQuery = new ProposalQuery().setEventId(event).addStates(CONFIRMED);
            meter.setSubmitted(proposals.count(submittedQuery));

            ProposalQuery acceptedQuery = new ProposalQuery().setEventId(event).addStates(ACCEPTED);
            meter.setAccepted(proposals.count(acceptedQuery));

            ProposalQuery rejectedQuery = new ProposalQuery().setEventId(event).addStates(REFUSED);
            meter.setRejected(proposals.count(rejectedQuery));

            ProposalQuery backupQuery = new ProposalQuery().setEventId(event).addStates(BACKUP);
            meter.setBackup(proposals.count(backupQuery));

            return meter;
        } else {
            RestrictedMeter meter = new RestrictedMeter();

            ProposalQuery mine = new ProposalQuery().setEventId(event).setUserId(user.getId()).addStates(CONFIRMED, ACCEPTED, REFUSED, BACKUP);
            meter.setTalks(proposals.count(mine));
            return meter;
        }
    }

}
