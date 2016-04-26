package fr.sii.repository;

import fr.sii.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RateRepo extends JpaRepository<Rate, Integer> {

    List<Rate> findByEventIdAndTalkUserId(String eventId, int userId);

    List<Rate> findByEventIdAndTalkId(String eventId, int talkId);

    Rate findByEventIdAndTalkIdAndAdminUserId(String eventId, int talkId, int adminId);

    @Query("SELECT r FROM Rate r JOIN FETCH r.adminUser WHERE r.event.id = :eventId")
    List<Rate> findAllFetchAdmin(@Param("eventId") String eventId);

    Rate findByIdAndEventId(int integer, String eventId);

    List<Rate> findByEventId(String eventId);

    void deleteByEventId(String eventId);
}
