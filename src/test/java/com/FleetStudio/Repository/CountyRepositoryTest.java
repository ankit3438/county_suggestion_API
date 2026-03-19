package com.FleetStudio.Repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.FleetStudio.Entity.County;

@DataJpaTest
public class CountyRepositoryTest {

    @Autowired
    private CountyRepository countyRepository;

    @BeforeEach
    public void setUp() {
       
        countyRepository.save(County.builder()
                .fips("53015")
                .state("WA")
                .name("Cowlitz")
                .build());

        countyRepository.save(County.builder()
                .fips("53001")
                .state("WA")
                .name("Adams")
                .build());

        countyRepository.save(County.builder()
                .fips("20035")
                .state("KS")
                .name("Cowley")
                .build());
    }

    @Test
    public void testFindByNameContainingIgnoreCase() {
      
        String searchName = "cowl";

      
        List<County> results = countyRepository.findByNameContainingIgnoreCase(searchName);

     
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream()
                .allMatch(c -> c.getName().toLowerCase().contains(searchName.toLowerCase())));
    }

    @Test
    public void testFindByStateIgnoreCase() {
        
        String searchState = "wa";

        
        List<County> results = countyRepository.findByStateIgnoreCase(searchState);

        
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream()
                .allMatch(c -> c.getState().equalsIgnoreCase(searchState)));
    }

    @Test
    public void testFindByNameContainingIgnoreCaseAndStateIgnoreCase() {
       
        String searchName = "cowl";
        String searchState = "wa";

    
        List<County> results = countyRepository
                .findByNameContainingIgnoreCaseAndStateIgnoreCase(searchName, searchState);

        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Cowlitz", results.get(0).getName());
        assertEquals("WA", results.get(0).getState());
    }


}
