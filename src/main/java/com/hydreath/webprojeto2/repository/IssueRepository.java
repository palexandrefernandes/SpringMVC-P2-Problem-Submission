package com.hydreath.webprojeto2.repository;

import com.hydreath.webprojeto2.models.Issue;
import com.hydreath.webprojeto2.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Integer> {
    Optional<List<Issue>> findByUser(User user);

}
