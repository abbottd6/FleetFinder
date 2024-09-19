package dao;

import entities.GroupListing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupListingRepository extends JpaRepository<GroupListing, Integer> {
}
