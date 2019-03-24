package io.cfp.service.admin.config;


import io.cfp.mapper.EventMapper;
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
    private EventMapper eventMapper;

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

}
