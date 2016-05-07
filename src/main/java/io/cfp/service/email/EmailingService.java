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

import io.cfp.config.email.EmailingSettings;
import io.cfp.config.email.EmailingSettings.EmailType;
import io.cfp.config.global.GlobalSettings;
import io.cfp.dto.TalkAdmin;
import io.cfp.dto.TalkUser;
import io.cfp.dto.user.CospeakerProfil;
import io.cfp.dto.user.UserProfil;
import io.cfp.entity.Event;
import io.cfp.entity.Role;
import io.cfp.entity.User;
import io.cfp.repository.RoleRepository;
import io.cfp.repository.UserRepo;
import io.cfp.service.admin.config.ApplicationConfigService;
import io.cfp.service.admin.user.AdminUserService;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

@Service
public class EmailingService {

    private final Logger log = LoggerFactory.getLogger(EmailingService.class);

    @Autowired
    ApplicationConfigService applicationConfigService;

    @Autowired
    private GlobalSettings globalSettings;

    @Autowired
    private EmailingSettings emailingSettings;

    @Autowired
    private UserRepo users;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    /**
     * EmailingService constructor.
     */
    public EmailingService() {
        super();
    }

    /**
     * Send Confirmation of your session.
     *
     * @param user
     * @param talk
     * @param locale
     */
    @Async
    public void sendConfirmed(User user, TalkUser talk, Locale locale) {
        log.debug("Sending email confirmation e-mail to '{}'", user.getEmail());

        HashMap<String, String> map = new HashMap<>();
        map.put("name", user.getFirstname());
        map.put("talk", talk.getName());
        map.put("hostname", globalSettings.getHostname());
        map.put("id", String.valueOf(talk.getId()));

        String templatePath = emailingSettings.getTemplatePath(EmailType.CONFIRMED, locale);

        String content = processContent(templatePath, map);
        String subject = emailingSettings.getSubject(EmailType.CONFIRMED, locale);

        sendEmail(user.getEmail(), subject, content, null, null);
    }

    /**
     * Send an email to a speaker to notify him that an administrator wrote a
     * new comment about his talk.
     *
     * @param speaker
     *            the speaker to write to
     * @param talk
     *            talk under review
     * @param locale
     */
    @Async
    public void sendNewCommentToSpeaker(User speaker, TalkAdmin talk, Locale locale) {
        log.debug("Sending new comment email to speaker '{}' for talk '{}'", speaker.getEmail(), talk.getName());

        List<String> bcc = users.findAdminsEmail(Event.current());

        HashMap<String, String> map = new HashMap<>();
        map.put("name", speaker.getFirstname());
        map.put("talk", talk.getName());
        map.put("hostname", globalSettings.getHostname());
        map.put("id", String.valueOf(talk.getId()));

        String templatePath = emailingSettings.getTemplatePath(EmailType.NEW_COMMENT_TO_SPEAKER, locale);

        String content = processContent(templatePath, map);
        String subject = emailingSettings.getSubject(EmailType.NEW_COMMENT_TO_SPEAKER, locale, talk.getName());

        sendEmail(speaker.getEmail(), subject, content, bcc, null);
    }

    /**
     * Send an email to administrators to notify them that a speaker wrote a
     * new comment on his talk.
     *
     * @param speaker
     *            the speaker writing this message
     * @param talk
     *            talk under review
     * @param locale
     */
    @Async
    public void sendNewCommentToAdmins(User speaker, TalkUser talk, Locale locale) {
        log.debug("Sending new comment email to admins for talk '{}'", talk.getName());

        List<String> bcc = users.findAdminsEmail(Event.current());

        HashMap<String, String> map = new HashMap<>();
        map.put("name", speaker.getFirstname());
        map.put("talk", talk.getName());
        map.put("hostname", globalSettings.getHostname());
        map.put("id", String.valueOf(talk.getId()));

        String templatePath = emailingSettings.getTemplatePath(EmailType.NEW_COMMENT_TO_ADMIN, locale);

        String content = processContent(templatePath, map);
        String subject = emailingSettings.getSubject(EmailType.NEW_COMMENT_TO_ADMIN, locale, speaker.getFirstname() + " " + speaker.getLastname(),
                talk.getName());

        // To address must not be null => from = to
        sendEmail(emailingSettings.getEmailSender(), subject, content, null, bcc);
    }

    /**
     * Send Confirmation of selection.
     *
     * @param talk
     * @param locale
     */
    @Async
    public void sendNotSelectionned(TalkUser talk, Locale locale) {
        UserProfil user = talk.getSpeaker();

        log.debug("Sending not selectionned e-mail to '{}'", user.getEmail());

        List<String> cc = new ArrayList<>();
        if (talk.getCospeakers() != null) {
            for (CospeakerProfil cospeakerProfil : talk.getCospeakers()) {
                cc.add(cospeakerProfil.getEmail());
            }
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("name", user.getFirstname());
        map.put("talk", talk.getName());
        map.put("hostname", globalSettings.getHostname());
        map.put("community", applicationConfigService.getAppConfig().getCommunity());
        map.put("event", applicationConfigService.getAppConfig().getEventName());

        String templatePath = emailingSettings.getTemplatePath(EmailType.NOT_SELECTIONNED, locale);

        String content = processContent(templatePath, map);
        String subject = emailingSettings.getSubject(EmailType.NOT_SELECTIONNED, locale);

        sendEmail(user.getEmail(), subject, content, cc, null);
    }

    @Async
    public void sendPending(TalkUser talk, Locale locale) {
        UserProfil user = talk.getSpeaker();

        log.debug("Sending pending e-mail to '{}'", user.getEmail());

        List<String> cc = new ArrayList<>();
        if (talk.getCospeakers() != null) {
            for (CospeakerProfil cospeakerProfil : talk.getCospeakers()) {
                cc.add(cospeakerProfil.getEmail());
            }
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("name", user.getFirstname());
        map.put("talk", talk.getName());
        map.put("hostname", globalSettings.getHostname());
        map.put("event", applicationConfigService.getAppConfig().getEventName());
        map.put("date", applicationConfigService.getAppConfig().getDate());

        String templatePath = emailingSettings.getTemplatePath(EmailType.PENDING, locale);

        String content = processContent(templatePath, map);
        String subject = emailingSettings.getSubject(EmailType.PENDING, locale);

        sendEmail(user.getEmail(), subject, content, cc, null);
    }

    @Async
    public void sendSelectionned(TalkUser talk, Locale locale) {
        UserProfil user = talk.getSpeaker();

        log.debug("Sending selectionned e-mail to '{}'", user.getEmail());

        List<String> cc = new ArrayList<>();
        if (talk.getCospeakers() != null) {
            for (CospeakerProfil cospeakerProfil : talk.getCospeakers()) {
                cc.add(cospeakerProfil.getEmail());
            }
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("name", user.getFirstname());
        map.put("talk", talk.getName());
        map.put("hostname", globalSettings.getHostname());
        map.put("event", applicationConfigService.getAppConfig().getEventName());
        map.put("date", applicationConfigService.getAppConfig().getDate());
        map.put("releaseDate", applicationConfigService.getAppConfig().getReleaseDate());

        String templatePath = emailingSettings.getTemplatePath(EmailType.SELECTIONNED, locale);

        String content = processContent(templatePath, map);
        String subject = emailingSettings.getSubject(EmailType.SELECTIONNED, locale);

        sendEmail(user.getEmail(), subject, content, cc, null);
    }

    /**
     * Send email validation.
     *
     * @param user
     * @param locale
     */
    @Async
    public void sendEmailValidation(User user, Locale locale) {
        log.debug("Sending email validation e-mail to '{}'", user.getEmail());

        String link = globalSettings.getHostname() + "/#/verify?id=" + String.valueOf(user.getId()) + "&token=" + user.getVerifyToken();
        log.debug(link);

        HashMap<String, String> map = new HashMap<>();
        map.put("link", link);
        map.put("hostname", globalSettings.getHostname());

        String templatePath = emailingSettings.getTemplatePath(EmailType.VERIFY, locale);

        String content = processContent(templatePath, map);
        String subject = emailingSettings.getSubject(EmailType.VERIFY, locale);

        sendEmail(user.getEmail(), subject, content, null, null);
    }

    public void sendEmail(String to, String subject, String content, List<String> cc, List<String> bcc) {
        if (!emailingSettings.isSend())
            return;

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(emailingSettings.getEmailSender());
            helper.setTo(to);
            if (bcc != null) {
                helper.setBcc(bcc.toArray(new String[bcc.size()]));
            }
            if (cc != null) {
                helper.setCc(cc.toArray(new String[cc.size()]));
            }
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);
            log.debug("Sent e-mail to User '{}'", to);
        } catch (MailSendException | MessagingException e) {
            log.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
        }
    }

    public String processContent(String templatePath, Map<String, String> data) {
        // Init velocityEngine
        VelocityEngine velocityEngine = buildVelocityEngine();
        velocityEngine.init();
        VelocityContext context = new VelocityContext();
        for (Map.Entry<String, String> item : data.entrySet()) {
            context.put(item.getKey(), item.getValue());
        }

        Template template = velocityEngine.getTemplate(templatePath, "UTF-8");

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    private VelocityEngine buildVelocityEngine() {
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return new VelocityEngine(props);
    }
}
