package com.FleetStudio.Utility;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.FleetStudio.Entity.County;
import com.FleetStudio.Repository.CountyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CountyRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {

        InputStream is = new ClassPathResource("data.json").getInputStream();

        List<Map<String, String>> data = mapper.readValue(is, List.class);

        List<County> counties = data.stream()
                .map(d -> County.builder()
                        .fips(d.get("fips"))
                        .name(d.get("name"))
                        .state(d.get("state"))
                        .build())
                .toList();

        repository.saveAll(counties);
    }
}
