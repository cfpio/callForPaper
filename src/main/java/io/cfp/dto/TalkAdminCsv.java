package io.cfp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.cfp.dto.user.CospeakerProfil;
import io.cfp.dto.user.UserProfil;

import java.util.Set;

/**
 * Allow CSV export to unwrap speaker
 */
public interface TalkAdminCsv {

    @JsonUnwrapped
    UserProfil getSpeaker();

    @JsonIgnore
    Set<CospeakerProfil> getCospeakers();

}
