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

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import io.cfp.dto.ApplicationSettings;
import io.cfp.dto.TalkAdmin;
import io.cfp.dto.TalkUser;
import io.cfp.dto.user.UserProfil;
import io.cfp.entity.Event;
import io.cfp.entity.User;
import io.cfp.repository.CfpConfigRepo;
import io.cfp.repository.EventRepository;
import io.cfp.repository.UserRepo;
import io.cfp.service.admin.config.ApplicationConfigService;
import org.apache.velocity.app.VelocityEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EmailingServiceTest.Config.class)
public class EmailingServiceTest {

    private static final String CONTACT_MAIL = "contact@maconf.fr";
    private static final String JOHN_DOE_EMAIL = "john.doe@gmail.com";

    @Spy
    private EmailingService emailingService;

    private GreenMail testSmtp;

    @Autowired
    private ApplicationConfigService applicationConfigService;

    @Mock
    private UserRepo users;

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private VelocityEngine velocityEngine;

    private String emailSender;

    private User user;

    private TalkAdmin talkAdmin;

    private TalkUser talkUser;

    private Event event;

    @Before
    public void setup() {
        emailingService = new EmailingService();
        emailSender = "sender@cfp.io";

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

        event = new Event();
        event.setId("test");
        event.setName("Test");
        event.setDate(new Date());
        event.setReleaseDate(new Date());
        event.logo("http://localhost/logo.png");
        event.setContactMail(CONTACT_MAIL);
        Event.setCurrent("test");
        when(eventRepo.findOne("test")).thenReturn(event);

        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(emailingService, "users", users);
        ReflectionTestUtils.setField(emailingService, "eventRepo", eventRepo);
        ReflectionTestUtils.setField(emailingService, "applicationConfigService", applicationConfigService);
        ReflectionTestUtils.setField(emailingService, "velocityEngine", velocityEngine);
        ReflectionTestUtils.setField(emailingService, "emailSender", emailSender);
        ReflectionTestUtils.setField(emailingService, "hostname", "demo.cfp.io");

        testSmtp = new GreenMail(ServerSetupTest.SMTP);
        testSmtp.start();

        // Don't forget to set the test port!
        when(applicationConfigService.getAppConfig()).thenReturn(new ApplicationSettings());

    }

    @Test
    public void sendSessionConfirmation() {
        // Given
        String templatePath = emailingService.getTemplatePath("confirmed.html", Locale.FRENCH);

        // When
        emailingService.sendConfirmed(user, talkUser, Locale.FRENCH);

        // Then
        verify(emailingService).processTemplate(eq(templatePath), anyMap());
        verify(emailingService).sendEmail(eq(CONTACT_MAIL), eq(JOHN_DOE_EMAIL), anyString(), anyString(), isNull(List.class), isNull(List.class));
    }

    @Test
    public void sendNewCommentToSpeaker() {
        // Given
    	String templatePath = emailingService.getTemplatePath("newMessage.html", Locale.FRENCH);

        // When
        emailingService.sendNewCommentToSpeaker(user, talkAdmin, Locale.FRENCH);

        // Then
        verify(emailingService).processTemplate(eq(templatePath), anyMap());
        verify(emailingService).sendEmail(eq(CONTACT_MAIL), eq(JOHN_DOE_EMAIL), anyString(), anyString(), notNull(List.class), isNull(List.class));
    }

    @Test
    public void sendNewMessageToAdmins() {
        // Given
    	String templatePath = emailingService.getTemplatePath("newMessageAdmin.html", Locale.FRENCH);

        // When
        emailingService.sendNewCommentToAdmins(user, talkUser, Locale.FRENCH);

        // Then
        verify(emailingService).processTemplate(eq(templatePath), anyMap());
        verify(emailingService).sendEmail(eq(CONTACT_MAIL), eq(emailSender), anyString(), anyString(), isNull(List.class), notNull(List.class));
    }

    @Test
    public void sendNotSelectionned() {
        // Given
    	String templatePath = emailingService.getTemplatePath("notSelectionned.html", Locale.FRENCH);

        // When
        emailingService.sendNotSelectionned(talkUser, Locale.FRENCH);

        // Then
        verify(emailingService).processTemplate(eq(templatePath), anyMap());
        verify(emailingService).sendEmail(eq(CONTACT_MAIL), eq(JOHN_DOE_EMAIL), anyString(), anyString(), notNull(List.class), isNull(List.class));
    }

    @Test
    public void sendPending() {
        // Given
    	String templatePath = emailingService.getTemplatePath("pending.html", Locale.FRENCH);

        // When
        emailingService.sendPending(talkUser, Locale.FRENCH);

        // Then
        verify(emailingService).processTemplate(eq(templatePath), anyMap());
        verify(emailingService).sendEmail(eq(CONTACT_MAIL), eq(JOHN_DOE_EMAIL), anyString(), anyString(), notNull(List.class), isNull(List.class));
    }

    @Test
    public void sendSelectionned() {
        // Given
    	String templatePath = emailingService.getTemplatePath("selectionned.html", Locale.FRENCH);

        // When
        emailingService.sendSelectionned(talkUser, Locale.FRENCH);

        // Then
        verify(emailingService).processTemplate(eq(templatePath), anyMap());
        verify(emailingService).sendEmail(eq(CONTACT_MAIL), eq(JOHN_DOE_EMAIL), anyString(), anyString(), notNull(List.class), isNull(List.class));
    }

    @Test
    public void processContentTest() {
        // Given
    	String templatePath = emailingService.getTemplatePath("test.html", Locale.FRENCH);

        Map<String, Object> map = new HashMap<>();
        map.put("var1", "test1");
        map.put("var2", "test2");

        // When
        String content = emailingService.processTemplate(templatePath, map);

        // Then
        assertEquals(false, content.contains("$"));
    }

    @Test
    public void processContentConfirmed() {
        // Given
    	String templatePath = emailingService.getTemplatePath("confirmed.html", Locale.FRENCH);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");
        map.put("id", "123");

        // When
        String content = emailingService.processTemplate(templatePath, map);

        // Then
        assertEquals(false, content.contains("$"));
        assertNotNull(map.get("subject"));
    }

    @Test
    public void processContentNotSelectionned() {
        // Given
    	String templatePath = emailingService.getTemplatePath("notSelectionned.html", Locale.FRENCH);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");
        map.put("id", "123");

        // When
        String content = emailingService.processTemplate(templatePath, map);

        // Then
        assertEquals(false, content.contains("$"));
        assertNotNull(map.get("subject"));
    }

    @Test
    public void processContentPending() {
        // Given
    	String templatePath = emailingService.getTemplatePath("pending.html", Locale.FRENCH);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");

        // When
        String content = emailingService.processTemplate(templatePath, map);

        // Then
        assertEquals(false, content.contains("$"));
        assertNotNull(map.get("subject"));
    }

    @Test
    public void processContentSelectionned() {
        // Given
    	String templatePath = emailingService.getTemplatePath("selectionned.html", Locale.FRENCH);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");

        // When
        String content = emailingService.processTemplate(templatePath, map);

        // Then
        assertEquals(false, content.contains("$"));
        assertNotNull(map.get("subject"));
    }

    @Test
    public void processContentNewMessage() {
        // Given
    	String templatePath = emailingService.getTemplatePath("newMessage.html", Locale.FRENCH);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");
        map.put("id", "123");

        // When
        String content = emailingService.processTemplate(templatePath, map);

        // Then
        assertEquals(false, content.contains("$"));
        assertNotNull(map.get("subject"));
    }

    @Test
    public void processContentNewMessageAdmin() {
        // Given
    	String templatePath = emailingService.getTemplatePath("newMessageAdmin.html", Locale.FRENCH);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");
        map.put("id", "123");

        // When
        String content = emailingService.processTemplate(templatePath, map);

        // Then
        assertEquals(false, content.contains("$"));
        assertNotNull(map.get("subject"));
    }

    @Test
    public void processContentSessionConfirmation() {
        // Given
    	String templatePath = emailingService.getTemplatePath("confirmed.html", Locale.FRENCH);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");
        map.put("id", "123");

        // When
        String content = emailingService.processTemplate(templatePath, map);

        // Then
        assertEquals(false, content.contains("$"));
        assertNotNull(map.get("subject"));
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
            properties.setProperty("cfp.email.sendgrid.apikey", "");
            properties.setProperty("cfp.email.send", "false");
            properties.setProperty("authServer", "http://localhost");
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

        @Bean
        public VelocityEngine velocityEngine() {
        	Properties props = new Properties();
            props.setProperty("resource.loader", "class");
            props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            return new VelocityEngine(props);
        }

    }
}
