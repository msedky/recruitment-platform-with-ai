package com.recruitment.platform.service;

import com.recruitment.platform.model.enums.VacancyStatus;
import com.recruitment.platform.model.payload.request.JobVacancyRequest;
import com.recruitment.platform.model.dto.JobVacancyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface JobVacancyService {
    JobVacancyDTO create(JobVacancyRequest request);

    JobVacancyDTO update(UUID id, JobVacancyRequest request);

    JobVacancyDTO getById(UUID id);

    Page<JobVacancyDTO> getAll(VacancyStatus status, Pageable pageable);

    JobVacancyDTO openVacancy(UUID id);

    JobVacancyDTO closeVacancy(UUID id);

    void delete(UUID id);
}
