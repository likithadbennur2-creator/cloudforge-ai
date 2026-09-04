package com.cloudforge.repository;

import com.cloudforge.model.AIAnalysis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AIAnalysisRepository extends JpaRepository<AIAnalysis, Long> {
}