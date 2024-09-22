package com.sc_fleetfinder.fleets.entitiesTests;

import com.sc_fleetfinder.fleets.dao.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/* I was having problems with my JPA relationships not populating the sets for OneToMany relationships, so I made this test to check it.
    Running the test produced an Assertion error on GameplayCategory.java:19 and GameplaySubcategory.java:15, which is where my Lombok class
    level annotations were, so I realized that it was a problem with the @Data annotation being in conflict with the @OneToMany and @ManyToOne
    annotations. Swapped it for @Getter and @Setter and the sets are populating correctly now.
 */
@SpringBootTest
public class GameplayCategoryTest {

    @Autowired
    private GameplayCategoryRepository gameplayCategoryRepository;

    @Test
    public void testGameplaySubcategoriesPopulating() {
        int categoryId = 1;
        GameplayCategory fetchedCategory = gameplayCategoryRepository.findById(categoryId).orElse(null);

        assertNotNull(fetchedCategory, "Category should not be null");
        assertNotNull(fetchedCategory.getGameplaySubcategories(), "Subcategories set should not be null");
        assertFalse(fetchedCategory.getGameplaySubcategories().isEmpty(), "Subcategories should not be empty");

        fetchedCategory.getGameplaySubcategories().forEach(sub -> {
            assertNotNull(sub.getSubcategoryName(), "Subcategory name should not be null");
            assertEquals(categoryId, sub.getGameplayCategory().getCategoryId(), "Subcategory should map to the correct category using categoryId");
        });
    }

}




