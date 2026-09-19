package com.jsy.platform.concept.infrastructure;

import com.jsy.platform.concept.domain.Concept;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConceptRepository extends JpaRepository<Concept, Long> {
    Optional<Concept> findByCode(String code);
}
