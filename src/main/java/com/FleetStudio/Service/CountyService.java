package com.FleetStudio.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.FleetStudio.Dto.CountySuggestion;
import com.FleetStudio.Entity.County;
import com.FleetStudio.Repository.CountyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor //for constructor injection of the repository
public class CountyService {

    private final CountyRepository countyRepository;

    public List<CountySuggestion> searchCounties(String q) {
        if (q.contains(",")) {
            String[] parts = q.split(",");
            String name = parts[0].trim();
            String state = parts[1].trim();

            return mapAndLimit(countyRepository
                .findByNameContainingIgnoreCaseAndStateIgnoreCase(name, state));
        }

        List<County> stateMatches = countyRepository.findByStateIgnoreCase(q.trim());

        if (!stateMatches.isEmpty()) {
            return mapAndLimit(stateMatches);
        }

        return mapAndLimit(countyRepository.findByNameContainingIgnoreCase(q.trim()));
    }

    
    private List<CountySuggestion> mapAndLimit(List<County> counties) {
        return counties.stream()
                .limit(5)
                .map(c -> new CountySuggestion(
                        c.getFips(),
                        c.getState(),
                        c.getName()))
                .toList();
    }

}
