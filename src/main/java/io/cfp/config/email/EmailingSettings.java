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

package io.cfp.config.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by tmaugin on 02/04/2015.
 */

/**
 * Email settings
 */
@Component
public class EmailingSettings {

  public enum EmailType {

    SELECTIONNED("selectionned.html"), PENDING("pending.html"), NOT_SELECTIONNED("notSelectionned.html"), CONFIRMED("confirmed.html"), VERIFY("verify.html"), NEW_COMMENT_TO_ADMIN(
        "newMessageAdmin.html"), NEW_COMMENT_TO_SPEAKER("newMessage.html"), TEST("test.html");

    public String getTemplate() {
      return template;
    }

    private final String template;

    EmailType(final String template) {
      this.template = template;
    }
  }

  private final static Map<EmailType, String> SUBJECTS_FR = new HashMap<>();

  private final static Map<EmailType, String> SUBJECTS_EN = new HashMap<>();

  static {
    SUBJECTS_FR.put(EmailType.SELECTIONNED, "Votre proposition a été acceptée");
    SUBJECTS_FR.put(EmailType.PENDING, "Confirmation de votre session");
    SUBJECTS_FR.put(EmailType.NOT_SELECTIONNED, "Votre proposition a été refusée");
    SUBJECTS_FR.put(EmailType.CONFIRMED, "Confirmation de votre session");
    SUBJECTS_FR.put(EmailType.NEW_COMMENT_TO_SPEAKER, "Nouveau commentaire sur le talk %s");
    SUBJECTS_FR.put(EmailType.NEW_COMMENT_TO_ADMIN, "Speaker %s a posté un commentaire pour le talk %s");
    SUBJECTS_FR.put(EmailType.VERIFY, "Confirmation de votre adresse e-mail");
  }

  static {
    SUBJECTS_EN.put(EmailType.SELECTIONNED, "Your proposal has been accepted");
    SUBJECTS_EN.put(EmailType.PENDING, "Confirmation of your session");
    SUBJECTS_EN.put(EmailType.NOT_SELECTIONNED, "Your proposal has been refused");
    SUBJECTS_EN.put(EmailType.CONFIRMED, "Confirmation of your session");
    SUBJECTS_EN.put(EmailType.NEW_COMMENT_TO_SPEAKER, "New comment about talk %s");
    SUBJECTS_EN.put(EmailType.NEW_COMMENT_TO_ADMIN, "Speaker %s posted a new comment on talk %s");
    SUBJECTS_EN.put(EmailType.VERIFY, "Confirm your email address");
  }

  @Value("${cfp.email.emailsender}")
  private String emailSender;

  @Value("${cfp.email.send}")
  private boolean send;

  public String getSubject(final EmailType emailType, final Locale locale, final String... strings) {
    final Map<EmailType, String> subjects = selectDictionary(locale);
    return String.format(subjects.get(emailType), (Object[]) strings);
  }

  public String getTemplatePath(final EmailType emailType, final Locale locale) {
    return "mails/" + locale.getLanguage() + "/" + emailType.getTemplate();
  }

  public String getEmailSender() {
    return emailSender;
  }

  public boolean isSend() {
    return send;
  }

  private Map<EmailType, String> selectDictionary(final Locale locale) {
    Map<EmailType, String> subjects;

    switch (locale.getLanguage()) {
    case "fr":
      subjects = EmailingSettings.SUBJECTS_FR;
      break;
    case "en":
      subjects = EmailingSettings.SUBJECTS_EN;
      break;
    default:
      subjects = EmailingSettings.SUBJECTS_FR;
      break;
    }
    return subjects;
  }
}
