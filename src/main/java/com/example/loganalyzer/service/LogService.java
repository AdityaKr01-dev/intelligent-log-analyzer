package com.example.loganalyzer.service;

import com.example.loganalyzer.entity.LogEntry;
import com.example.loganalyzer.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LogService {

    private final LogRepository logRepository;
    private final OpenAIService openAIService;

    public LogService(LogRepository logRepository, OpenAIService openAIService) {
        this.logRepository = logRepository;
        this.openAIService = openAIService;
    }

    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\s+\\[(\\w+)\\]\\s+\\[([\\w-]+)\\]\\s+(.*)$"
    );
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public int processLogFile(MultipartFile file) throws Exception {
        List<LogEntry> entries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                Matcher matcher = LOG_PATTERN.matcher(line);
                if (matcher.matches()) {
                    LocalDateTime timestamp = LocalDateTime.parse(matcher.group(1), FORMATTER);
                    String severity = matcher.group(2).toUpperCase();
                    String service = matcher.group(3);
                    String message = matcher.group(4);

                    String aiSuggestion = null;
                    if ("ERROR".equals(severity)) {
                        aiSuggestion = openAIService.getFixSuggestion(message);
                    }

                    entries.add(new LogEntry(timestamp, severity, service, message, aiSuggestion));
                }
            }
        }

        logRepository.saveAll(entries);
        return entries.size();
    }

    public List<LogEntry> getAllLogs() {
        return logRepository.findAll();
    }

    public List<LogEntry> getLogsBySeverity(String severity) {
        return logRepository.findBySeverityIgnoreCase(severity);
    }

    public List<LogEntry> getLogsByService(String service) {
        return logRepository.findByServiceIgnoreCase(service);
    }

    public List<LogEntry> searchLogs(String keyword) {
        return logRepository.findByMessageContainingIgnoreCase(keyword);
    }
}