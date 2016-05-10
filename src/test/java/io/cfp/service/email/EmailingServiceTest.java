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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import io.cfp.dto.ApplicationSettings;
import io.cfp.repository.CfpConfigRepo;
import io.cfp.repository.EventRepository;
import io.cfp.repository.UserRepo;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

import io.cfp.ApplicationJUnit;
import io.cfp.config.email.EmailingSettings;
import io.cfp.config.global.GlobalSettings;
import io.cfp.dto.TalkAdmin;
import io.cfp.dto.TalkUser;
import io.cfp.dto.user.UserProfil;
import io.cfp.entity.User;
import io.cfp.service.admin.config.ApplicationConfigService;
import io.cfp.service.admin.user.AdminUserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EmailingServiceTest.Config.class)
public class EmailingServiceTest {

    private static final String JOHN_DOE_EMAIL = "john.doe@gmail.com";

    @Spy
    private EmailingService emailingService;

    private GreenMail testSmtp;

    @Autowired
    ApplicationConfigService applicationConfigService;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private GlobalSettings globalSettings;

    @Autowired
    private EmailingSettings emailingSettings;

    @Autowired
    private UserRepo users;

    private User user;

    private TalkAdmin talkAdmin;

    private TalkUser talkUser;

    @Before
    public void setup() {
        emailingService = new EmailingService(applicationConfigService,
                                                globalSettings,
                                                emailingSettings,
                                                users,
                                                javaMailSender);

        user = new User();
        user.setId(1);
        user.setEmail(JOHN_DOE_EMAIL);
        user.setFirstname("john");

        talkUser = new TalkUser();
        talkUser.setId(1);
        talkUser.setName("My amazing user talk 1");

        UserProfil speaker = new UserProfil("john", "Doe");
        speaker.setEmail(JOHN_DOE_EMAIL);
        talkUser.setSpeaker(speaker);

        talkAdmin = new TalkAdmin();
        talkAdmin.setId(2);
        talkAdmin.setName("My amazing user talk 2");

        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(emailingService, "globalSettings", globalSettings);
        ReflectionTestUtils.setField(emailingService, "emailingSettings", emailingSettings);
        ReflectionTestUtils.setField(emailingService, "users", users);
        ReflectionTestUtils.setField(emailingService, "javaMailSender", javaMailSender);
        ReflectionTestUtils.setField(emailingService, "applicationConfigService", applicationConfigService);

        testSmtp = new GreenMail(ServerSetupTest.SMTP);
        testSmtp.start();

        // Don't forget to set the test port!
        javaMailSender.setPort(3025);
        javaMailSender.setHost("localhost");

        when(applicationConfigService.getAppConfig()).thenReturn(new ApplicationSettings());

    }

    @Test
    public void sendSessionConfirmation() {
        // Given
        String templatePath = emailingSettings.getTemplatePath(EmailingSettings.EmailType.CONFIRMED, Locale.FRENCH);
        String subject = emailingSettings.getSubject(EmailingSettings.EmailType.CONFIRMED, Locale.FRENCH);

        // When
        emailingService.sendConfirmed(user, talkUser, Locale.FRENCH);

        // Then
        verify(emailingService).processContent(eq(templatePath), anyMap());
        verify(emailingService).sendEmail(eq(JOHN_DOE_EMAIL), eq(subject), anyString(), isNull(List.class), isNull(List.class));
    }

    @Test
    public void sendNewCommentToSpeaker() {
        // Given
        String templatePath = emailingSettings.getTemplatePath(EmailingSettings.EmailType.NEW_COMMENT_TO_SPEAKER, Locale.FRENCH);
        String subject = emailingSettings.getSubject(EmailingSettings.EmailType.NEW_COMMENT_TO_SPEAKER, Locale.FRENCH, talkAdmin.getName());

        // When
        emailingService.sendNewCommentToSpeaker(user, talkAdmin, Locale.FRENCH);

        // Then
        verify(emailingService).processContent(eq(templatePath), anyMap());
        verify(emailingService).sendEmail(eq(JOHN_DOE_EMAIL), eq(subject), anyString(), notNull(List.class), isNull(List.class));
    }

    @Test
    public void sendNewMessageToAdmins() {
        // Given
        String templatePath = emailingSettings.getTemplatePath(EmailingSettings.EmailType.NEW_COMMENT_TO_ADMIN, Locale.FRENCH);
        String subject = emailingSettings.getSubject(EmailingSettings.EmailType.NEW_COMMENT_TO_ADMIN, Locale.FRENCH,
                user.getFirstname() + " " + user.getLastname(), talkUser.getName());

        // When
        emailingService.sendNewCommentToAdmins(user, talkUser, Locale.FRENCH);

        // Then
        verify(emailingService).processContent(eq(templatePath), anyMap());
        verify(emailingService).sendEmail(eq(emailingSettings.getEmailSender()), eq(subject), anyString(), isNull(List.class), notNull(List.class));
    }

    @Test
    public void sendNotSelectionned() {
        // Given
        String templatePath = emailingSettings.getTemplatePath(EmailingSettings.EmailType.NOT_SELECTIONNED, Locale.FRENCH);
        String subject = emailingSettings.getSubject(EmailingSettings.EmailType.NOT_SELECTIONNED, Locale.FRENCH);

        // When
        emailingService.sendNotSelectionned(talkUser, Locale.FRENCH);

        // Then
        verify(emailingService).processContent(eq(templatePath), anyMap());
        verify(emailingService).sendEmail(eq(JOHN_DOE_EMAIL), eq(subject), anyString(), notNull(List.class), isNull(List.class));
    }

    @Test
    public void sendPending() {
        // Given
        String templatePath = emailingSettings.getTemplatePath(EmailingSettings.EmailType.PENDING, Locale.FRENCH);
        String subject = emailingSettings.getSubject(EmailingSettings.EmailType.PENDING, Locale.FRENCH);

        // When
        emailingService.sendPending(talkUser, Locale.FRENCH);

        // Then
        verify(emailingService).processContent(eq(templatePath), anyMap());
        verify(emailingService).sendEmail(eq(JOHN_DOE_EMAIL), eq(subject), anyString(), notNull(List.class), isNull(List.class));
    }

    @Test
    public void sendSelectionned() {
        // Given
        String templatePath = emailingSettings.getTemplatePath(EmailingSettings.EmailType.SELECTIONNED, Locale.FRENCH);
        String subject = emailingSettings.getSubject(EmailingSettings.EmailType.SELECTIONNED, Locale.FRENCH);

        // When
        emailingService.sendSelectionned(talkUser, Locale.FRENCH);

        // Then
        verify(emailingService).processContent(eq(templatePath), anyMap());
        verify(emailingService).sendEmail(eq(JOHN_DOE_EMAIL), eq(subject), anyString(), notNull(List.class), isNull(List.class));
    }

    @Test
    public void processContentTest() {
        // Given
        String templatePath = emailingSettings.getTemplatePath(EmailingSettings.EmailType.TEST, Locale.FRENCH);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("var1", "test1");
        map.put("var2", "test2");

        // When
        String content = emailingService.processContent(templatePath, map);

        // Then
        assertEquals(false, content.contains("$"));
    }

    @Test
    public void processContentConfirmed() {
        // Given
        String templatePath = emailingSettings.getTemplatePath(EmailingSettings.EmailType.CONFIRMED, Locale.FRENCH);

        HashMap<String, String> map = new HashMap<>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");
        map.put("hostname", "http://yourappid.appspot.com/");
        map.put("id", "123");

        // When
        String content = emailingService.processContent(templatePath, map);

        // Then
        assertEquals(false, content.contains("$"));
    }

    @Test
    public void processContentNotSelectionned() {
        // Given
        String templatePath = emailingSettings.getTemplatePath(EmailingSettings.EmailType.NOT_SELECTIONNED, Locale.FRENCH);

        HashMap<String, String> map = new HashMap<>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");
        map.put("community", "GDG Nantes");
        map.put("event", "DevFest 2015");
        map.put("hostname", "http://yourappid.appspot.com/");

        // When
        String content = emailingService.processContent(templatePath, map);

        // Then
        assertEquals(false, content.contains("$"));
    }

    @Test
    public void processContentPending() {
        // Given
        String templatePath = emailingSettings.getTemplatePath(EmailingSettings.EmailType.PENDING, Locale.FRENCH);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");
        map.put("event", "DevFest 2015");
        map.put("date", "13/11/1992");
        map.put("hostname", "http://yourappid.appspot.com/");

        // When
        String content = emailingService.processContent(templatePath, map);

        // Then
        assertEquals(false, content.contains("$"));
    }

    @Test
    public void processContentSelectionned() {
        // Given
        String templatePath = emailingSettings.getTemplatePath(EmailingSettings.EmailType.SELECTIONNED, Locale.FRENCH);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");
        map.put("event", "DevFest 2015");
        map.put("date", "13/11/1992");
        map.put("releaseDate", "12/11/1992");
        map.put("hostname", "http://yourappid.appspot.com/");

        // When
        String content = emailingService.processContent(templatePath, map);

        // Then
        assertEquals(false, content.contains("$"));
    }

    @Test
    public void processContentNewMessage() {
        // Given
        String templatePath = emailingSettings.getTemplatePath(EmailingSettings.EmailType.NEW_COMMENT_TO_SPEAKER, Locale.FRENCH);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");
        map.put("hostname", "http://yourappid.appspot.com/");
        map.put("id", "123");

        // When
        String content = emailingService.processContent(templatePath, map);

        // Then
        assertEquals(false, content.contains("$"));
    }

    @Test
    public void processContentNewMessageAdmin() {
        // Given
        String templatePath = emailingSettings.getTemplatePath(EmailingSettings.EmailType.NEW_COMMENT_TO_ADMIN, Locale.FRENCH);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");
        map.put("hostname", "http://yourappid.appspot.com/");
        map.put("id", "123");

        // When
        String content = emailingService.processContent(templatePath, map);

        // Then
        assertEquals(false, content.contains("$"));
    }

    @Test
    public void processContentSessionConfirmation() {
        // Given
        String templatePath = emailingSettings.getTemplatePath(EmailingSettings.EmailType.CONFIRMED, Locale.FRENCH);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");
        map.put("hostname", "http://yourappid.appspot.com/");
        map.put("id", "123");

        // When
        String content = emailingService.processContent(templatePath, map);

        // Then
        assertEquals(false, content.contains("$"));
    }

    @After
    public void cleanup() {
        testSmtp.stop();
    }

    @Configuration
    static class Config {

        @Bean
        public static PropertySourcesPlaceholderConfigurer properties() {
            final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
            final Properties properties = new Properties();
            properties.setProperty("cfp.app.hostname", "");
            properties.setProperty("cfp.database.loaded", "");
            properties.setProperty("cfp.email.emailsender", "");
            properties.setProperty("cfp.email.send", "false");
            properties.setProperty("authServer", "http://locahost");
            propertySourcesPlaceholderConfigurer.setProperties(properties);
            return propertySourcesPlaceholderConfigurer;
        }

        @Bean // field injection of EmailingService
        public ApplicationConfigService applicationConfigService() {
            return mock(ApplicationConfigService.class);
        }

        @Bean // field injection of ApplicationConfigService
        public CfpConfigRepo cfpConfigRepo() {
            return mock(CfpConfigRepo.class);
        }

        @Bean // field injection of EmailingService
        public GlobalSettings globalSettings() {
            return mock(GlobalSettings.class);
        }

        @Bean // field injection of EmailingService
        public EmailingSettings emailingSettings() {
            return new EmailingSettings();
        }

        @Bean // field injection of EmailingService
        public JavaMailSenderImpl javaMailSender() {
            return mock(JavaMailSenderImpl.class);
        }

        @Bean
        public UserRepo userRepo() {
            return mock(UserRepo.class);
        }

        @Bean // field injection of ApplicationConfigService
        public EventRepository eventRepo() {
            return mock(EventRepository.class);
        }


    }
}
