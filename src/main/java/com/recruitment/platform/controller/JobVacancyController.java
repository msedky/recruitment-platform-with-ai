package com.recruitment.platform.controller;

import com.recruitment.platform.model.enums.VacancyStatus;
import com.recruitment.platform.model.payload.request.JobVacancyRequest;
import com.recruitment.platform.model.dto.JobVacancyDTO;
import com.recruitment.platform.service.JobVacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/job-vacancies")
@RequiredArgsConstructor
public class JobVacancyController {
    private final JobVacancyService jobVacancyService;

    @PostMapping
    public ResponseEntity<JobVacancyDTO> create(@Valid @RequestBody JobVacancyRequest request) {
        return ResponseEntity.ok(jobVacancyService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobVacancyDTO> update(
            @PathVariable UUID id, @Valid @RequestBody JobVacancyRequest request) {
        return ResponseEntity.ok(jobVacancyService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobVacancyDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(jobVacancyService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<JobVacancyDTO>> getAll(
            @RequestParam(required = false) VacancyStatus status, Pageable pageable) {
        return ResponseEntity.ok(jobVacancyService.getAll(status, pageable));
    }

    @PatchMapping("/{id}/open")
    public ResponseEntity<JobVacancyDTO> open(@PathVariable UUID id) {
        return ResponseEntity.ok(jobVacancyService.openVacancy(id));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<JobVacancyDTO> close(@PathVariable UUID id) {
        return ResponseEntity.ok(jobVacancyService.closeVacancy(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        jobVacancyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}