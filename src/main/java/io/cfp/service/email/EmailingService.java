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

package io.cfp.service.email;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import io.cfp.dto.TalkUser;
import io.cfp.dto.user.CospeakerProfil;
import io.cfp.dto.user.UserProfil;
import io.cfp.entity.Event;
import io.cfp.entity.Talk;
import io.cfp.entity.User;
import io.cfp.mapper.EventMapper;
import io.cfp.mapper.UserMapper;
import io.cfp.model.Proposal;
import io.cfp.model.Role;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailingService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    @Qualifier("mailTemplate")
    private Configuration freemarker;

    @Value("${cfp.email.sendgrid.apikey}")
    private String sendgridApiKey;

    @Value("${cfp.app.hostname}")
    private String hostname;

    @Value("${cfp.email.emailsender}")
    private String emailSender;

    @Value("${cfp.email.send}")
    private boolean send;

    private Map<String, Map<String, String>> subjects = new HashMap<>();

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void loadSubjects() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] yamls = resolver.getResources("classpath:mails/*/subjects.yml");
        Pattern langPattern = Pattern.compile(".*/mails/([^/]+)/subjects\\.yml");

        Yaml parser = new Yaml();
        for (Resource yaml : yamls) {
            Map<String, String> subs = (Map<String, String>) parser.load(yaml.getInputStream());
            Matcher matcher = langPattern.matcher(yaml.getURL().getPath());
            if (matcher.matches()) { //forced to call matches() to execute regex...
                subjects.put(matcher.group(1), subs);
            }
        }
    }

    /**
     * Send Confirmation of your session.
     */
    @Async
    @Transactional
    public void sendConfirmed(io.cfp.model.User user, Proposal proposal) {
        LOGGER.debug("Sending email confirmation e-mail to '{}'", user.getEmail());

        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getFirstname());
        params.put("talk", proposal);
        params.put("id", String.valueOf(proposal.getId()));
        params.put("subject", getSubject("confirmed", user.getLocale()));

        createAndSendEmail(proposal.getEventId(), "confirmed.html", user.getEmail(), params, null, null, user.getLocale(), "");
    }

    /**
     * Send a mail to inform speaker his proposal is available back to editing.
     */
    @Async
    @Transactional
    public void sendBackToEdit(Proposal proposal) {
        io.cfp.model.User speaker = proposal.getSpeaker();

        LOGGER.debug("Sending \"back to edit\" e-mail to '{}'", speaker.getEmail());

        Map<String, Object> params = new HashMap<>();
        params.put("name", speaker.getFirstname());
        params.put("talk", proposal);
        params.put("id", String.valueOf(proposal.getId()));
        params.put("subject", getSubject("backToEdit", speaker.getLocale()));

        createAndSendEmail(proposal.getEventId(), "backToEdit.html", speaker.getEmail(), params, null, null, speaker.getLocale(), "");
    }

    /**
     * Send Confirmation of your session.
     */
    @Async
    @Transactional
    public void sendConfirmedPresence(io.cfp.model.User user, Proposal proposal) {
        LOGGER.debug("Sending email confirmation of presence to '{}'", user.getEmail());

        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getFirstname());
        params.put("talk", proposal);
        params.put("id", String.valueOf(proposal.getId()));
        params.put("subject", getSubject("confirmedPresence", user.getLocale()));

        createAndSendEmail(proposal.getEventId(), "confirmedPresence.html", user.getEmail(), params, null, null, user.getLocale(), "");
    }

    @Async
    @Transactional
    @Deprecated
    public void sendConfirmed(io.cfp.model.User user, TalkUser talk, Locale locale) {
        LOGGER.debug("Sending email confirmation e-mail to '{}'", user.getEmail());

        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getFirstname());
        params.put("talk", talk.getName());
        params.put("id", String.valueOf(talk.getId()));
        params.put("subject", getSubject("confirmed", locale));

        createAndSendEmail(Event.current(), "confirmed.html", user.getEmail(), params, null, null, locale, "");
    }

    /**
     * Send an email to a speaker to notify him that an administrator wrote a
     * new comment about his talk.
     *
     * @param speaker the speaker to write to
     */
    @Async
    @Transactional
    public void sendNewCommentToSpeaker(io.cfp.model.User speaker, Proposal proposal, String comment) {
        LOGGER.debug("Sending new comment email to speaker '{}' for talk '{}'", speaker.getEmail(), proposal.getName());

        List<String> bcc = userMapper.findEmailByRole(Role.ADMIN, proposal.getEventId());

        Map<String, Object> params = new HashMap<>();
        params.put("name", speaker.getFirstname());
        params.put("talk", proposal.getName());
        params.put("id", String.valueOf(proposal.getId()));
        params.put("subject", getSubject("newMessage", speaker.getLocale(), proposal.getName()));

        createAndSendEmail(proposal.getEventId(), "newMessage.html", speaker.getEmail(), params, null, bcc, speaker.getLocale(), comment);
    }

    /**
     * Send an email to administrators to notify them that a speaker wrote a
     * new comment on his talk.
     *
     * @param speaker the speaker writing this message
     */
    @Async
    @Transactional
    public void sendNewCommentToAdmins(io.cfp.model.User speaker, Proposal proposal, String comment) {
        LOGGER.debug("Sending new comment email to admins for talk '{}'", proposal.getName());

        List<String> bcc = userMapper.findEmailByRole(Role.ADMIN, proposal.getEventId());
        String speakerName = speaker.getFirstname() + " " + speaker.getLastname();

        Map<String, Object> params = new HashMap<>();
        params.put("name", speakerName);
        params.put("talk", proposal.getName());
        params.put("id", String.valueOf(proposal.getId()));
        params.put("subject", getSubject("newMessageAdmin", speaker.getLocale(), speakerName, proposal.getName()));

        createAndSendEmail(proposal.getEventId(), "newMessageAdmin.html", emailSender, params, null, bcc, speaker.getLocale(), comment);
    }


    /**
     * Send Confirmation of selection.
     *
     * @param talk
     * @param locale
     */
    @Async
    @Transactional
    @Deprecated
    public void sendNotSelectionned(Talk talk, Locale locale) {
        User user = talk.getUser();

        LOGGER.debug("Sending not selectionned e-mail to '{}'", user.getEmail());

        List<String> cc = new ArrayList<>();
        if (talk.getCospeakers() != null) {
            for (User cospeaker : talk.getCospeakers()) {
                cc.add(cospeaker.getEmail());
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getFirstname());
        params.put("talk", talk.getName());
        params.put("subject", getSubject("notSelectionned", locale));

        createAndSendEmail(talk.getEvent().getName(), "notSelectionned.html", user.getEmail(), params, cc, null, locale, "");
    }

    /**
     * Send Confirmation of selection.
     *
     * @param proposal
     * @param locale
     */
    @Async
    @Transactional
    public void sendNotSelectionned(Proposal proposal, Locale locale) {
        io.cfp.model.User user = proposal.getSpeaker();

        LOGGER.debug("Sending not selectionned e-mail to '{}'", user.getEmail());

        List<String> cc = new ArrayList<>();
        if (proposal.getCospeakers() != null) {
            for (io.cfp.model.User cospeaker : proposal.getCospeakers()) {
                cc.add(cospeaker.getEmail());
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getFirstname());
        params.put("talk", proposal.getName());
        params.put("subject", getSubject("notSelectionned", locale));

        createAndSendEmail(proposal.getEventId(), "notSelectionned.html", user.getEmail(), params, cc, null, locale, "");
    }

    @Async
    @Transactional
    public void sendPending(TalkUser talk, Locale locale) {
        UserProfil user = talk.getSpeaker();

        LOGGER.debug("Sending pending e-mail to '{}'", user.getEmail());

        List<String> cc = new ArrayList<>();
        if (talk.getCospeakers() != null) {
            for (CospeakerProfil cospeakerProfil : talk.getCospeakers()) {
                cc.add(cospeakerProfil.getEmail());
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getFirstname());
        params.put("talk", talk.getName());
        params.put("subject", getSubject("pending", locale));

        createAndSendEmail(Event.current(), "pending.html", user.getEmail(), params, cc, null, locale, "");
    }

    @Async
    @Transactional
    @Deprecated
    public void sendSelectionned(Talk talk, Locale locale) {
        final User user = talk.getUser();
        LOGGER.debug("Sending selectionned e-mail to '{}'", user.getEmail());

        List<String> cc = new ArrayList<>();
        if (talk.getCospeakers() != null) {
            for (User cospeakerProfil : talk.getCospeakers()) {
                cc.add(cospeakerProfil.getEmail());
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getFirstname());
        params.put("talk", talk.getName());
        params.put("subject", getSubject("selectionned", locale));
        params.put("id", String.valueOf(talk.getId()));

        createAndSendEmail(talk.getEvent().getName(), "selectionned.html", user.getEmail(), params, cc, null, locale, "");
    }

    @Async
    @Transactional
    public void sendSelectionned(Proposal proposal, Locale locale) {
        final io.cfp.model.User user = proposal.getSpeaker();
        LOGGER.debug("Sending selectionned e-mail to '{}'", user.getEmail());

        List<String> cc = new ArrayList<>();
        if (proposal.getCospeakers() != null) {
            for (io.cfp.model.User cospeakerProfil : proposal.getCospeakers()) {
                cc.add(cospeakerProfil.getEmail());
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getFirstname());
        params.put("talk", proposal.getName());
        params.put("subject", getSubject("selectionned", locale));
        params.put("id", String.valueOf(proposal.getId()));

        createAndSendEmail(proposal.getEventId(), "selectionned.html", user.getEmail(), params, cc, null, locale, "");
    }

    protected void createAndSendEmail(String event, String template, String email, Map<String, Object> parameters, List<String> cc, List<String> bcc, Locale locale, String message) {

        String templatePath = getTemplatePath(template, locale);

        String content = processTemplate(templatePath, parameters, event, message);
        String subject = (String) parameters.get("subject");

        sendEmail(parameters.get("contactMail").toString(), email, subject, content, cc, bcc);
    }

    protected String getTemplatePath(final String emailTemplate, final Locale locale) {
        String language = locale.getLanguage();
        if (!"fr".equals(language)) {
            language = "en";
        }
        return language + "/" + emailTemplate;
    }

    protected String processTemplate(String templatePath, Map<String, Object> parameters, String event, String message) {

        // adds global params
        parameters.put("hostname", StringUtils.replace(hostname, "{{event}}", event));
        io.cfp.model.Event curEvent = eventMapper.findOne(event);
        parameters.put("event", curEvent);
        parameters.put("contactMail", curEvent.getContactMail() != null ? curEvent.getContactMail() : "contact@cfp.io");
        parameters.put("message", message);

        StringWriter writer;
        try {
            freemarker.template.Template tpl = freemarker.getTemplate(templatePath);
            writer = new StringWriter();
            tpl.process(parameters, writer);
        } catch (IOException e) {
            LOGGER.error("Unable to find or parse the template [{}]", templatePath, e);
            return null;
        } catch (TemplateException e) {
            LOGGER.error("Unable to process the template [{}]", templatePath, e);
            return null;
        }

        return writer.toString();
    }

    public void sendEmail(String from, String to, String subject, String content, List<String> cc, List<String> bcc) {
        if (!send) {
            String fileName = saveLocally(content);
            LOGGER.warn("Mail [{}] to [{}] not sent as mail is disabled but can be found at [{}]", subject, to, fileName);
            return;
        }

        if (content == null) {
            LOGGER.error("Mail content is null, don't send it to [{}] with subject [{}]", to, subject);
            return;
        }

        SendGrid sendgrid = new SendGrid(sendgridApiKey);

        SendGrid.Email email = new SendGrid.Email();

        email.setFrom(emailSender)
            .setFromName("CFP.io")
            .setReplyTo(from)
            .addTo(to)
            .setSubject(subject)
            .setHtml(content);
        if (cc != null) {
            email.addCc(cc.toArray(new String[cc.size()]));
        }
        if (bcc != null) {
            email.addBcc(bcc.toArray(new String[bcc.size()]));
        }


        try {
            SendGrid.Response response = sendgrid.send(email);
            LOGGER.debug("Sent e-mail to User '{}' with status {}", to, response.getStatus());
        } catch (SendGridException e) {
            LOGGER.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
        }
    }


    private String saveLocally(String content) {
        try {
            File tempFile = File.createTempFile("cfpio-", ".html");
            FileUtils.writeStringToFile(tempFile, content, StandardCharsets.UTF_8);
            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            LOGGER.error("Unable to save temp mail file", e);
            return null;
        }
    }

    private String getSubject(String template, Locale locale, Object... args) {
        String language = locale.getLanguage();
        if (!"fr".equals(language)) {
            language = "en";
        }

        String subject = subjects.get(language).get(template);

        if (subject == null) {
            return null;
        }
        MessageFormat msg = new MessageFormat(subject);
        return msg.format(args);
    }
}
