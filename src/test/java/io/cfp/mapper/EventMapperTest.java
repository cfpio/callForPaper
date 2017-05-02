package io.cfp.mapper;

import io.cfp.model.Event;
import io.cfp.model.queries.EventQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@MybatisTest
public class EventMapperTest {

    @Autowired
    private EventMapper eventMapper;

    @Test
    public void should_find_all_open_Event() {
        List<Event> openEvents = eventMapper.findOpen();
        assertThat(openEvents).isNotEmpty();
    }

    @Test
    public void should_find_all_open_Event_by_query() {
        EventQuery eventQuery = new EventQuery().setOpen(true);
        List<Event> openEvents = eventMapper.findAll(eventQuery);
        assertThat(openEvents).isNotEmpty();
    }

    @Test
    public void should_find_all_passed_Event() {
        List<Event> openEvents = eventMapper.findOpen();
        assertThat(openEvents).isNotEmpty();
    }

    @Test
    public void should_find_all_passed_Event_by_query() {
        EventQuery eventQuery = new EventQuery().setPassed(true);
        List<Event> openEvents = eventMapper.findAll(eventQuery);
        assertThat(openEvents).isNotEmpty();
    }

    @Test
    public void should_find_all_Events_by_user() {
        List<Event> eventsOfUser = eventMapper.findByUser(10);
        assertThat(eventsOfUser).isNotEmpty();
    }

    @Test
    public void should_find_all_Events_by_user_by_query() {
        EventQuery eventQuery = new EventQuery().setUser(10);
        List<Event> eventsOfUser = eventMapper.findAll(eventQuery);
        assertThat(eventsOfUser).isNotEmpty();
    }

    @Test
    public void should_create_an_Event() {
        Event event = new Event().setId("EVENT_TEST");
        int createdLines = eventMapper.insert(event);

        assertThat(createdLines).isEqualTo(1);
    }


    @Test
    public void should_return_an_existing_Event() {
        assertThat(eventMapper.exists("EVENT_ID")).isTrue();
    }



}
