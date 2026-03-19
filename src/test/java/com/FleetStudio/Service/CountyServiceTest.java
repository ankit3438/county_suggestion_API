package com.FleetStudio.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.FleetStudio.Dto.CountySuggestion;
import com.FleetStudio.Entity.County;
import com.FleetStudio.Repository.CountyRepository;

@ExtendWith(MockitoExtension.class)
public class CountyServiceTest {

    @Mock
    private CountyRepository countyRepository;

    @InjectMocks
    private CountyService countyService;

    private List<County> mockCounties;

    @BeforeEach
    public void setUp() {
        mockCounties = Arrays.asList(
                County.builder().fips("53015").state("WA").name("Cowlitz").build(),
                County.builder().fips("53001").state("WA").name("Adams").build(),
                County.builder().fips("53003").state("WA").name("Asotin").build()
        );
    }

    // Test: Search with name and state (comma-separated)
    @Test
    public void testSearchCountiesWithNameAndState() {
        // Arrange
        String query = "cowlitz, wa";
        when(countyRepository.findByNameContainingIgnoreCaseAndStateIgnoreCase("cowlitz", "wa"))
                .thenReturn(mockCounties);

        // Act
        List<CountySuggestion> result = countyService.searchCounties(query);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Cowlitz", result.get(0).getName());
        assertEquals("WA", result.get(0).getState());
        assertEquals("53015", result.get(0).getFips());

        verify(countyRepository, times(1))
                .findByNameContainingIgnoreCaseAndStateIgnoreCase("cowlitz", "wa");
    }

    // Test: Search with state only (no comma)
    @Test
    public void testSearchCountiesWithStateOnly() {
        // Arrange
        String query = "wa";
        when(countyRepository.findByStateIgnoreCase("wa"))
                .thenReturn(mockCounties);

        // Act
        List<CountySuggestion> result = countyService.searchCounties(query);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(c -> c.getState().equals("WA")));

        verify(countyRepository, times(1)).findByStateIgnoreCase("wa");
    }

    // Test: Search with name only (no comma, no state match)
    @Test
    public void testSearchCountiesWithNameOnly() {
        // Arrange
        String query = "cowl";
        when(countyRepository.findByStateIgnoreCase("cowl"))
                .thenReturn(Collections.emptyList());
        when(countyRepository.findByNameContainingIgnoreCase("cowl"))
                .thenReturn(Arrays.asList(
                        County.builder().fips("53015").state("WA").name("Cowlitz").build()
                ));

        // Act
        List<CountySuggestion> result = countyService.searchCounties(query);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cowlitz", result.get(0).getName());

        verify(countyRepository, times(1)).findByStateIgnoreCase("cowl");
        verify(countyRepository, times(1)).findByNameContainingIgnoreCase("cowl");
    }

    // Test: No results found
    @Test
    public void testSearchCountiesWithNoResults() {
        // Arrange
        String query = "nonexistent";
        when(countyRepository.findByStateIgnoreCase("nonexistent"))
                .thenReturn(Collections.emptyList());
        when(countyRepository.findByNameContainingIgnoreCase("nonexistent"))
                .thenReturn(Collections.emptyList());

        // Act
        List<CountySuggestion> result = countyService.searchCounties(query);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(countyRepository, times(1)).findByStateIgnoreCase("nonexistent");
        verify(countyRepository, times(1)).findByNameContainingIgnoreCase("nonexistent");
    }

    // Test: Results are limited to 5
    @Test
    public void testSearchCountiesResultsLimitedToFive() {
        // Arrange
        String query = "wa";
        List<County> tenCounties = Arrays.asList(
                County.builder().fips("53001").state("WA").name("Adams").build(),
                County.builder().fips("53003").state("WA").name("Asotin").build(),
                County.builder().fips("53005").state("WA").name("Benton").build(),
                County.builder().fips("53007").state("WA").name("Chelan").build(),
                County.builder().fips("53009").state("WA").name("Clallam").build(),
                County.builder().fips("53011").state("WA").name("Clark").build(),
                County.builder().fips("53013").state("WA").name("Columbia").build(),
                County.builder().fips("53015").state("WA").name("Cowlitz").build(),
                County.builder().fips("53017").state("WA").name("Douglas").build(),
                County.builder().fips("53019").state("WA").name("Ferry").build()
        );
        when(countyRepository.findByStateIgnoreCase("wa"))
                .thenReturn(tenCounties);

        // Act
        List<CountySuggestion> result = countyService.searchCounties(query);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals("Adams", result.get(0).getName());
        assertEquals("Clallam", result.get(4).getName());

        verify(countyRepository, times(1)).findByStateIgnoreCase("wa");
    }

    // Test: Whitespace trimming
    @Test
    public void testSearchCountiesWithWhitespace() {
        // Arrange
        String query = "  cowlitz  ,  wa  ";
        when(countyRepository.findByNameContainingIgnoreCaseAndStateIgnoreCase("cowlitz", "wa"))
                .thenReturn(mockCounties);

        // Act
        List<CountySuggestion> result = countyService.searchCounties(query);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        verify(countyRepository, times(1))
                .findByNameContainingIgnoreCaseAndStateIgnoreCase("cowlitz", "wa");
    }

    // Test: Single result mapping
    @Test
    public void testSearchCountiesOutputMapping() {
        // Arrange
        String query = "adamS";
        List<County> singleCounty = Arrays.asList(
                County.builder().fips("53001").state("WA").name("Adams").build()
        );
        when(countyRepository.findByStateIgnoreCase("adamS"))
                .thenReturn(Collections.emptyList());
        when(countyRepository.findByNameContainingIgnoreCase("adamS"))
                .thenReturn(singleCounty);

        // Act
        List<CountySuggestion> result = countyService.searchCounties(query);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        CountySuggestion suggestion = result.get(0);
        assertEquals("53001", suggestion.getFips());
        assertEquals("WA", suggestion.getState());
        assertEquals("Adams", suggestion.getName());

        verify(countyRepository, times(1)).findByStateIgnoreCase("adamS");
        verify(countyRepository, times(1)).findByNameContainingIgnoreCase("adamS");
    }

}
