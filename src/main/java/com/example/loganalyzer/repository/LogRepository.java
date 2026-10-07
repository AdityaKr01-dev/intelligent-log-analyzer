package com.example.loganalyzer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.loganalyzer.entity.LogEntry;

public interface LogRepository extends JpaRepository<LogEntry, Long> {
    List<LogEntry> findBySeverityIgnoreCase(String severity);
    List<LogEntry> findByServiceIgnoreCase(String service);
    List<LogEntry> findByMessageContainingIgnoreCase(String keyword);
}