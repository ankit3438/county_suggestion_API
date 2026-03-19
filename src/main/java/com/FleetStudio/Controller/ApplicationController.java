package com.FleetStudio.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.FleetStudio.Dto.CountySuggestion;
import com.FleetStudio.Service.CountyService;

import lombok.RequiredArgsConstructor;

@RestController 
@RequiredArgsConstructor
public class ApplicationController {

    //application controller will expose the endpints as requested.

    private final CountyService service;

    @GetMapping("/suggest")
    public ResponseEntity<List<CountySuggestion>> suggest(@RequestParam String q) {
        return ResponseEntity.ok(service.searchCounties(q));
    }

}
