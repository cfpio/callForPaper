package io.cfp.api;

import io.cfp.mapper.EventMapper;
import io.cfp.mapper.UserMapper;
import io.cfp.model.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Date;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ApplicationController.class)
public class ApplicationControllerTest {

    @MockBean
    private EventMapper eventMapper;

    @MockBean
    private UserMapper userMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_return_event_settings() throws Exception {

        Event event = new Event();
        event.setName("EVENT_ID");
        event.setDate(new Date());
        event.setReleaseDate(new Date());
        event.setDecisionDate(new Date());


        when(eventMapper.findOne(anyString())).thenReturn(event);

        mockMvc.perform(get("/api/application")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
         .andDo(MockMvcResultHandlers.print())
         .andExpect(status().isOk())
         .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
         .andExpect(jsonPath("$.eventName").value("EVENT_ID"))
        ;

    }

    @Test
    public void should_return_not_found_id_no_event() throws Exception {

        when(eventMapper.findOne(anyString())).thenReturn(null);

        mockMvc.perform(get("/api/application")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

    }

}
