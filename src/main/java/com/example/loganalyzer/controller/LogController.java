package com.example.loganalyzer.controller;

import com.example.loganalyzer.entity.LogEntry;
import com.example.loganalyzer.service.LogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadLogFile(@RequestParam("file") MultipartFile file) {
        try {
            int count = logService.processLogFile(file);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Processed " + count + " log entries."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<List<LogEntry>> getAllLogs() {
        return ResponseEntity.ok(logService.getAllLogs());
    }

    @GetMapping("/errors")
    public ResponseEntity<List<LogEntry>> getErrorLogs() {
        return ResponseEntity.ok(logService.getLogsBySeverity("ERROR"));
    }

    @GetMapping("/search")
    public ResponseEntity<List<LogEntry>> searchLogs(
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String service,
            @RequestParam(required = false) String query) {

        if (severity != null) {
            return ResponseEntity.ok(logService.getLogsBySeverity(severity));
        }
        if (service != null) {
            return ResponseEntity.ok(logService.getLogsByService(service));
        }
        if (query != null) {
            return ResponseEntity.ok(logService.searchLogs(query));
        }
        return ResponseEntity.ok(logService.getAllLogs());
    }
}