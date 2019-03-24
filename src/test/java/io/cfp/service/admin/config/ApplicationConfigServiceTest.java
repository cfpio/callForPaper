package io.cfp.service.admin.config;


import io.cfp.entity.Event;
import io.cfp.mapper.EventMapper;
import io.cfp.repository.EventRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationConfigServiceTest {


    @InjectMocks
    private ApplicationConfigService applicationConfigService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Before
    @Deprecated
    public void setUp() {
        Event.setCurrent("EVENT_ID");
    }

    @After
    @Deprecated
    public void tearDown() {
        Event.unsetCurrent();
    }

    @Test
    @Deprecated
    @Ignore
    public void should_return_ApplicationSettings() {

        Event event = new Event();
        event.setDate(new Date());
        event.setReleaseDate(new Date());
        event.setDecisionDate(new Date());

        when(eventRepository.findOne("EVENT_ID")).thenReturn(event);
// FIXME deprecated
//        ApplicationSettings appConfig = applicationConfigService.getAppConfig();
//
//        assertThat(appConfig).isNotNull();
    }

    @Test
    @Deprecated
    public void should_close_CFP_old() {

        Event event = new Event();
        event.setOpen(true);
        event.setDate(new Date());
        event.setReleaseDate(new Date());
        event.setDecisionDate(new Date());

        when(eventRepository.findOne("EVENT_ID")).thenReturn(event);

        applicationConfigService.closeCfp();

        verify(eventRepository).save(eq(event));

        assertThat(event.isOpen()).isFalse();
    }

    @Test
    public void should_close_CFP() {

        io.cfp.model.Event event = new io.cfp.model.Event();
        event.setOpen(true);
        event.setDate(new Date());
        event.setReleaseDate(new Date());
        event.setDecisionDate(new Date());

        when(eventMapper.findOne("EVENT_ID")).thenReturn(event);

        applicationConfigService.closeCfp("EVENT_ID");

        verify(eventMapper).update(eq(event));

        assertThat(event.isOpen()).isFalse();
    }

    @Test
    @Deprecated
    public void should_open_CFP_old() {

        Event event = new Event();
        event.setOpen(false);
        event.setDate(new Date());
        event.setReleaseDate(new Date());
        event.setDecisionDate(new Date());

        when(eventRepository.findOne("EVENT_ID")).thenReturn(event);

        applicationConfigService.openCfp();

        verify(eventRepository).save(eq(event));

        assertThat(event.isOpen()).isTrue();
    }

    @Test
    public void should_open_CFP() {

        io.cfp.model.Event event = new io.cfp.model.Event();
        event.setOpen(true);
        event.setDate(new Date());
        event.setReleaseDate(new Date());
        event.setDecisionDate(new Date());

        when(eventMapper.findOne("EVENT_ID")).thenReturn(event);

        applicationConfigService.openCfp("EVENT_ID");

        verify(eventMapper).update(eq(event));

        assertThat(event.isOpen()).isTrue();
    }

    @Test
    public void should_return_that_CFP_is_open() {

        Event event = new Event();
        event.setOpen(true);
        event.setDate(new Date());
        event.setReleaseDate(new Date());
        event.setDecisionDate(new Date());

        when(eventRepository.findOne("EVENT_ID")).thenReturn(event);

        assertThat(applicationConfigService.isCfpOpen()).isTrue();
    }
}
