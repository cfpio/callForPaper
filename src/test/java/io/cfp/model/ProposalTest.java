package io.cfp.model;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProposalTest {

    @Test
    public void should_return_a_list_of_speaker_and_cospeakers() {
        User speaker = new User();
        speaker.setId(1);
        speaker.setEmail("EMAIL_SPEAKER");
        speaker.addRole(Role.AUTHENTICATED);

        User cospeaker = new User();
        cospeaker.setId(2);
        cospeaker.setEmail("EMAIL_COSPEAKER");
        cospeaker.addRole(Role.AUTHENTICATED);

        Proposal proposal = new Proposal();
        proposal.setSpeaker(speaker);
        proposal.getCospeakers().add(cospeaker);

        assertThat(proposal.getSpeakersIds()).hasSize(2).contains(1,2);
    }
}
