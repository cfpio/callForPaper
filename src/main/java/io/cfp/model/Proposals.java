package io.cfp.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class Proposals {

    private List<Proposal> proposals;
    private Integer totalElements;
    private Integer page;
}
