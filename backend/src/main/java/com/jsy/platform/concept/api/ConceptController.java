package com.jsy.platform.concept.api;

import com.jsy.platform.concept.application.ConceptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/concepts")
public class ConceptController {

    private final ConceptService conceptService;

    public ConceptController(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    @GetMapping("/{code}")
    public ResponseEntity<ConceptDto> getConcept(@PathVariable String code) {
        return ResponseEntity.ok(conceptService.getByCode(code));
    }
}
