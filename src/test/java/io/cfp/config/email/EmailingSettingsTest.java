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

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.util.Assert;

import io.cfp.config.email.EmailingSettings.EmailType;

@RunWith(BlockJUnit4ClassRunner.class)
public class EmailingSettingsTest {
  private final static String TALK = "Talk12345";

  private final static String SPEAKER = "Speaker12345";

  EmailingSettings emailingSettings = new EmailingSettings();

  @Test
  public void whenPostingNewCommentToSpeakerSubjectIsValid() {
    // Given
    final EmailType emailType = EmailType.NEW_COMMENT_TO_SPEAKER;

    // When
    final String subject = emailingSettings.getSubject(emailType, Locale.FRENCH, TALK);

    // Then
    Assert.isTrue(subject.contains(TALK));
  }

  @Test
  public void whenPostingNewCommentToAdminsSubjectIsValid() {
    // Given
    final EmailType emailType = EmailType.NEW_COMMENT_TO_ADMIN;

    // When
    final String subject = emailingSettings.getSubject(emailType, Locale.FRENCH, SPEAKER, TALK);

    // Then
    Assert.isTrue(subject.contains(TALK));
    Assert.isTrue(subject.contains(SPEAKER));
  }
}
