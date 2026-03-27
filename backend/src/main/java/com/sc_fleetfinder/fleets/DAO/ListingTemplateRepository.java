package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.ListingTemplate;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ListingTemplateRepository extends JpaRepository<ListingTemplate, Long> {

    @Query("SELECT lt FROM ListingTemplate lt WHERE lt.user = :user")
    Page<ListingTemplate> getListingTemplatesByUser(@Param("user") Users user, Pageable pageable);

    @Modifying
    @Query("DELETE FROM ListingTemplate lt WHERE lt.user = :user and lt.templateId = :id")
    int deleteByUserAndId(@Param("user") Users user, @Param("id") Long templateId);

    @Modifying
    @Query("DELETE FROM ListingTemplate lt WHERE lt.user.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
