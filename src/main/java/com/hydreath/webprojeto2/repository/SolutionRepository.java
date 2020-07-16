package com.hydreath.webprojeto2.repository;

import com.hydreath.webprojeto2.models.Solution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolutionRepository extends JpaRepository<Solution, Integer> {
    List<Solution> findByClosedAtIsNull();
    Optional<Solution> findByIdAndPassword(int id, String password);
}
