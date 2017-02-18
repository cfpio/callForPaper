package io.cfp.dto;

import io.cfp.config.mapping.Mapping;
import io.cfp.entity.Rate;
import ma.glasnost.orika.MapperFactory;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Component
public class DtoMapping implements Mapping {

    @Override
    public void mapClasses(MapperFactory mapperFactory) {
        mapperFactory.classMap(Rate.class, RateAdmin.class)
            .field("adminUser", "user")
            .byDefault();
    }
}
