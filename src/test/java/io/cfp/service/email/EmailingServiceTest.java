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
import freemarker.template.TemplateExceptionHandler;
import io.cfp.config.MailConfig;
import io.cfp.dto.TalkAdmin;
import io.cfp.dto.TalkUser;
import io.cfp.dto.user.UserProfil;
import io.cfp.entity.Event;
import io.cfp.entity.Talk;
import io.cfp.entity.User;
import io.cfp.mapper.EventMapper;
import io.cfp.mapper.UserMapper;
import io.cfp.model.Proposal;
import io.cfp.service.admin.config.ApplicationConfigService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EmailingServiceTest.Config.class)
public class EmailingServiceTest {

    private static final String CONTACT_MAIL = "contact@maconf.fr";
    private static final String JOHN_DOE_EMAIL = "john.doe@gmail.com";

    @Spy
    private EmailingService emailingService;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private freemarker.template.Configuration freemarkerCfg;

    private GreenMail testSmtp;

    private String emailSender;

    private User user;

    private io.cfp.model.User newUser;

    private Talk talk;

    private TalkAdmin talkAdmin;

    private TalkUser talkUser;

    private Event event;
    private io.cfp.model.Event newEvent;

    @Before
    public void setup() throws IOException {
        emailingService = new EmailingService();
        emailingService.loadSubjects();
        emailSender = "sender@cfp.io";

        user = new User();
        user.setId(1);
        user.setEmail(JOHN_DOE_EMAIL);
        user.setFirstname("john");

        newUser = new io.cfp.model.User();
        newUser.setId(1);
        newUser.setEmail(JOHN_DOE_EMAIL);
        newUser.setFirstname("john");


        talkUser = new TalkUser();
        talkUser.setId(1);
        talkUser.setName("My amazing user talk 1");

        UserProfil speaker = new UserProfil(0, "john", "Doe", "john@doe.net");
        speaker.setEmail(JOHN_DOE_EMAIL);
        talkUser.setSpeaker(speaker);

        talkAdmin = new TalkAdmin();
        talkAdmin.setId(2);
        talkAdmin.setName("My amazing user talk 2");

        event = Event.builder()
            .id("test")
            .name("test")
            .date(new Date())
            .releaseDate(new Date())
            .logoUrl("http://localhost/logo.png")
            .contactMail(CONTACT_MAIL)
            .build();

        newEvent = new io.cfp.model.Event()
            .setId("test")
            .setName("test")
            .setDate(new Date())
            .setReleaseDate(new Date())
            .setLogoUrl("http://localhost/logo.png")
            .setContactMail(CONTACT_MAIL);

        talk = new Talk();
        talk.user(user).name("Awesome talk").event(event);


        Event.setCurrent("test");

        when(eventMapper.findOne("test")).thenReturn(newEvent);

        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(emailingService, "userMapper", userMapper);
        ReflectionTestUtils.setField(emailingService, "eventMapper", eventMapper);
        ReflectionTestUtils.setField(emailingService, "freemarker", freemarkerCfg);
        ReflectionTestUtils.setField(emailingService, "emailSender", emailSender);
        ReflectionTestUtils.setField(emailingService, "hostname", "demo.cfp.io");

        testSmtp = new GreenMail(ServerSetupTest.SMTP);
        testSmtp.start();

    }

    @Test
    public void sendSessionConfirmation() {
        // Given
        String templatePath = emailingService.getTemplatePath("confirmed.html", Locale.FRENCH);

        // When
        emailingService.sendConfirmed(newUser, talkUser, Locale.FRENCH);

        // Then
        verify(emailingService).processTemplate(eq(templatePath), anyMap(), anyString(), anyString());
        verify(emailingService).sendEmail(eq(CONTACT_MAIL), eq(JOHN_DOE_EMAIL), anyString(), anyString(), isNull(List.class), isNull(List.class));
    }

    @Test
    public void sendNotSelectionned() {
        // Given
    	String templatePath = emailingService.getTemplatePath("notSelectionned.html", Locale.FRENCH);

        // When
        emailingService.sendNotSelectionned(talk, Locale.FRENCH);

        // Then
        verify(emailingService).processTemplate(eq(templatePath), anyMap(), anyString(), anyString());
        verify(emailingService).sendEmail(eq(CONTACT_MAIL), eq(JOHN_DOE_EMAIL), anyString(), anyString(), notNull(List.class), isNull(List.class));
    }

    @Test
    public void sendPending() {
        // Given
    	String templatePath = emailingService.getTemplatePath("pending.html", Locale.FRENCH);

        // When
        emailingService.sendPending(talkUser, Locale.FRENCH);

        // Then
        verify(emailingService).processTemplate(eq(templatePath), anyMap(), anyString(), anyString());
        verify(emailingService).sendEmail(eq(CONTACT_MAIL), eq(JOHN_DOE_EMAIL), anyString(), anyString(), notNull(List.class), isNull(List.class));
    }

    @Test
    public void sendSelectionned() {
        // Given
    	String templatePath = emailingService.getTemplatePath("selectionned.html", Locale.FRENCH);

        // When
        emailingService.sendSelectionned(talk, Locale.FRENCH);

        // Then
        verify(emailingService).processTemplate(eq(templatePath), anyMap(), anyString(), anyString());
        verify(emailingService).sendEmail(eq(CONTACT_MAIL), eq(JOHN_DOE_EMAIL), anyString(), anyString(), notNull(List.class), isNull(List.class));
    }

    @Test
    public void processContentTest() {
        // Given
    	String templatePath = emailingService.getTemplatePath("test.html", Locale.FRENCH);

        Map<String, Object> map = new HashMap<>();
        map.put("var1", "test1");
        map.put("var2", "test2");

        String eventId = "test";

        // When
        String content = emailingService.processTemplate(templatePath, map, eventId, "");

        // Then
        assertEquals(false, content.contains("$"));
    }

    @Test
    public void processContentConfirmed() {
        // Given
    	String templatePath = emailingService.getTemplatePath("confirmed.html", Locale.FRENCH);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Thomas");
        map.put("talk", new Proposal().setName("Google App Engine pour les nuls"));
        map.put("id", "123");

        String eventId = "test";

        // When
        String content = emailingService.processTemplate(templatePath, map, eventId, "");

        // Then
        assertEquals(false, content.contains("$"));
    }

    @Test
    public void processContentNotSelectionned() {
        // Given
    	String templatePath = emailingService.getTemplatePath("notSelectionned.html", Locale.FRENCH);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Thomas");
        map.put("talk", new Proposal().setName("Google App Engine pour les nuls"));
        map.put("id", "123");

        String eventId = "test";

        // When
        String content = emailingService.processTemplate(templatePath, map, eventId, "");

        // Then
        assertEquals(false, content.contains("$"));
    }

    @Test
    public void processContentPending() {
        // Given
    	String templatePath = emailingService.getTemplatePath("pending.html", Locale.FRENCH);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");

        String eventId = "test";

        // When
        String content = emailingService.processTemplate(templatePath, map, eventId, "");

        // Then
        assertEquals(false, content.contains("$"));
    }

    @Test
    public void processContentSelectionned() {
        // Given
    	String templatePath = emailingService.getTemplatePath("selectionned.html", Locale.FRENCH);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");
        map.put("id", "1");

        String eventId = "test";

        // When
        String content = emailingService.processTemplate(templatePath, map, eventId, "");

        // Then
        assertEquals(false, content.contains("$"));
    }

    @Test
    public void processContentNewMessage() {
        // Given
    	String templatePath = emailingService.getTemplatePath("newMessage.html", Locale.FRENCH);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");
        map.put("id", "123");

        String eventId = "test";

        // When
        String content = emailingService.processTemplate(templatePath, map, eventId, "");

        // Then
        assertEquals(false, content.contains("$"));
    }

    @Test
    public void processContentNewMessageAdmin() {
        // Given
    	String templatePath = emailingService.getTemplatePath("newMessageAdmin.html", Locale.FRENCH);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Thomas");
        map.put("talk", "Google App Engine pour les nuls");
        map.put("id", "123");

        String eventId = "test";

        // When
        String content = emailingService.processTemplate(templatePath, map, eventId, "");

        // Then
        assertEquals(false, content.contains("$"));
    }

    @Test
    public void processContentSessionConfirmation() {
        // Given
    	String templatePath = emailingService.getTemplatePath("confirmed.html", Locale.FRENCH);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Thomas");
        map.put("talk", new Proposal().setName("Google App Engine pour les nuls"));
        map.put("id", "123");


        String eventId = "test";

        // When
        String content = emailingService.processTemplate(templatePath, map, eventId, "");

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

        @Bean // field injection of EmailingService
        public JavaMailSenderImpl javaMailSender() {
            return mock(JavaMailSenderImpl.class);
        }

        @Bean
        public EventMapper eventMapper() {
            return mock(EventMapper.class);
        }

        @Bean
        public UserMapper userMapper() {
            return mock(UserMapper.class);
        }

        @Bean
        public freemarker.template.Configuration freemarkerCfg() {
            freemarker.template.Configuration config = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_21);
            config.setClassForTemplateLoading(MailConfig.class, "/mails/");
            config.setDefaultEncoding("UTF-8");
            config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            return config;
        }

    }
}
