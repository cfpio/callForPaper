package io.cfp.mapper;

import io.cfp.model.Rate;
import io.cfp.model.Stat;
import io.cfp.model.queries.RateQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface RateMapper {


    List<Rate> findAll(RateQuery rateQuery);
    Rate findMyRate(@Param("proposalId") int proposalId, @Param("user") int userId, @Param("eventId") String eventId);
    int insert(Rate rate);
    int update(Rate rate);
    int deleteForEvent(@Param("id") int id, @Param("eventId") String eventId);
    int deleteAllForEvent(@Param("eventId") String eventId);


    List<Stat> getRateByEmailUsers(String eventId);
}
