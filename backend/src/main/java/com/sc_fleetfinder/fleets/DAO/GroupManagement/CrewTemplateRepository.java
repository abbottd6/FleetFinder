package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CrewTemplateRepository extends JpaRepository<CrewTemplate, Long> {

    @Query(value = """
            SELECT * FROM crew_template template
            WHERE template.owner_id IS NULL
                OR template.owner_id = :userId
            """, nativeQuery = true)
    List<CrewTemplate> fetchTemplateSummariesForUser(@Param("userId") Long userId);

    @Query("SELECT t FROM CrewTemplate t WHERE t.templateId = :templateId")
    Optional<CrewTemplate> findTemplateAndChildrenById(@Param("templateId") Long templateId);
}
