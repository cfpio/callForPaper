package io.cfp.api;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;

public class IndexControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexControllerTest.class);

    @Test
    public void should() {
        Link link = linkTo(ProposalsController.class).withRel("proposals");
        assertThat(link.getRel()).isEqualTo("proposals");
        assertThat(link.getHref()).endsWith("/proposals");
    }
}
