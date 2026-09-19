package com.jsy.platform.concept.application;

import com.jsy.platform.concept.api.ConceptDto;
import com.jsy.platform.concept.domain.Concept;
import com.jsy.platform.concept.infrastructure.ConceptRepository;
import com.jsy.platform.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ConceptService {

    private final ConceptRepository conceptRepository;

    public ConceptService(ConceptRepository conceptRepository) {
        this.conceptRepository = conceptRepository;
    }

    public ConceptDto getByCode(String code) {
        Concept c = conceptRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Kavram bulunamadı: " + code));
        return new ConceptDto(c.getId(), c.getCode(), c.getTitle(),
                c.getRealLifeAnalogy(), c.getWhyExplanation(),
                c.getWrongExample(), c.getWrongExampleExplanation(),
                c.getRightExample(), c.getRightExampleExplanation());
    }
}
