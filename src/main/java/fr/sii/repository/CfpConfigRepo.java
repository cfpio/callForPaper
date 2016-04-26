package fr.sii.repository;

import fr.sii.entity.CfpConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by lhuet on 21/11/15.
 */
public interface CfpConfigRepo extends JpaRepository<CfpConfig, Integer>{

    @Query("SELECT c.value FROM CfpConfig c where c.key = :cKey and c.event.id = :eventId")
    String findValueByKey(@Param("cKey") String key, @Param("eventId") String eventId);

    CfpConfig findByKeyAndEventId(String key, String eventId);


}
