package io.cfp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Data @NoArgsConstructor @AllArgsConstructor @Accessors(chain = true)
public class Event {

    private String id;

    private String name;

    private String shortDescription;

    private boolean published;

    private String url;

    private String logoUrl;

    private String videosUrl;

    private String contactMail;

    private Date date;

    private int duration;

    private Date releaseDate;

    private Date decisionDate;

    private boolean open = true;
}
