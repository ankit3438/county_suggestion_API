package com.FleetStudio.Controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.FleetStudio.Dto.CountySuggestion;
import com.FleetStudio.Service.CountyService;

@WebMvcTest(ApplicationController.class)
public class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CountyService countyService;

    private List<CountySuggestion> mockCounties;

    @BeforeEach
    public void setUp() {
        mockCounties = Arrays.asList(
                CountySuggestion.builder().fips("53015").state("WA").name("Cowlitz").build(),
                CountySuggestion.builder().fips("53001").state("WA").name("Adams").build()
        );
    }

    @Test
    public void testSuggestWithValidQuery() throws Exception {
      
        String query = "cowlitz, wa";
        when(countyService.searchCounties(query)).thenReturn(mockCounties);

     
        mockMvc.perform(get("/suggest")
                .param("q", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fips").value("53015"))
                .andExpect(jsonPath("$[0].state").value("WA"))
                .andExpect(jsonPath("$[0].name").value("Cowlitz"));

        verify(countyService, times(1)).searchCounties(query);
    }

    @Test
    public void testSuggestWithStateOnlyQuery() throws Exception {
        
        String query = "wa";
        when(countyService.searchCounties(query)).thenReturn(mockCounties);

        mockMvc.perform(get("/suggest")
                .param("q", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(countyService, times(1)).searchCounties(query);
    }

    @Test
    public void testSuggestWithNameOnlyQuery() throws Exception {
       
        String query = "cowl";
        List<CountySuggestion> singleCounty = Arrays.asList(
                CountySuggestion.builder().fips("53015").state("WA").name("Cowlitz").build()
        );
        when(countyService.searchCounties(query)).thenReturn(singleCounty);

      
        mockMvc.perform(get("/suggest")
                .param("q", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(countyService, times(1)).searchCounties(query);
    }

    @Test
    public void testSuggestWithNoResults() throws Exception {
        
        String query = "nonexistent";
        when(countyService.searchCounties(query)).thenReturn(Arrays.asList());

     
        mockMvc.perform(get("/suggest")
                .param("q", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(countyService, times(1)).searchCounties(query);
    }

    @Test
    public void testSuggestWithMissingQueryParameter() throws Exception {
        
        mockMvc.perform(get("/suggest"))
                .andExpect(status().isBadRequest());

        verify(countyService, never()).searchCounties(anyString());
    }



}
