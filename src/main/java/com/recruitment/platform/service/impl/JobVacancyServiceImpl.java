package com.recruitment.platform.service.impl;

import com.recruitment.platform.mapper.JobRequiredSkillMapper;
import com.recruitment.platform.mapper.JobVacancyMapper;
import com.recruitment.platform.model.dto.JobVacancyDTO;
import com.recruitment.platform.model.entity.JobRequiredSkillEntity;
import com.recruitment.platform.model.entity.JobVacancyEntity;
import com.recruitment.platform.model.enums.VacancyStatus;
import com.recruitment.platform.model.payload.request.JobVacancyRequest;
import com.recruitment.platform.repository.JobVacancyRepository;
import com.recruitment.platform.service.JobVacancyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobVacancyServiceImpl implements JobVacancyService {

    private final JobVacancyRepository jobVacancyRepository;
    private final JobVacancyMapper jobVacancyMapper;
    private final JobRequiredSkillMapper jobRequiredSkillMapper;

    @Override
    @Transactional
    public JobVacancyDTO create(JobVacancyRequest request) {
        JobVacancyEntity entity = jobVacancyMapper.toEntity(request);
        entity.setStatus(VacancyStatus.DRAFT);
        entity.setRequiredSkills(mapRequiredSkills(request, entity));
        return jobVacancyMapper.toResponseDTO(jobVacancyRepository.save(entity));
    }

    @Override
    @Transactional
    public JobVacancyDTO update(UUID id, JobVacancyRequest request) {
        JobVacancyEntity entity = findEntityOrThrow(id);
        if(entity.getStatus() != VacancyStatus.DRAFT) {
            throw new IllegalStateException("Only draft vacancies can be updated.");
        }

        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setDepartment(request.getDepartment());
        entity.setLocation(request.getLocation());
        entity.setEmploymentType(request.getEmploymentType());
        entity.setMinExperienceYears(request.getMinExperienceYears());
        entity.setRequiredDegreeType(request.getRequiredDegreeType());
        entity.setRequiredFieldOfStudy(request.getRequiredFieldOfStudy());
        entity.setPostedDate(request.getPostedDate());
        entity.setClosingDate(request.getClosingDate());

        entity.getRequiredSkills().clear();
        entity.getRequiredSkills().addAll(mapRequiredSkills(request, entity));

        return jobVacancyMapper.toResponseDTO(jobVacancyRepository.save(entity));
    }

    @Override
    public JobVacancyDTO getById(UUID id) {
        return jobVacancyMapper.toResponseDTO(findEntityOrThrow(id));
    }

    @Override
    public Page<JobVacancyDTO> getAll(VacancyStatus status, Pageable pageable) {
        Page<JobVacancyEntity> page = status != null
                ? jobVacancyRepository.findByStatus(status, pageable)
                : jobVacancyRepository.findAll(pageable);
        return page.map(jobVacancyMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public JobVacancyDTO openVacancy(UUID id) {
        JobVacancyEntity entity = findEntityOrThrow(id);
        entity.setStatus(VacancyStatus.OPENED);
        return jobVacancyMapper.toResponseDTO(jobVacancyRepository.save(entity));
    }

    @Override
    @Transactional
    public JobVacancyDTO closeVacancy(UUID id) {
        JobVacancyEntity entity = findEntityOrThrow(id);
        entity.setStatus(VacancyStatus.CLOSED);
        return jobVacancyMapper.toResponseDTO(jobVacancyRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        jobVacancyRepository.delete(findEntityOrThrow(id));
    }

    private JobVacancyEntity findEntityOrThrow(UUID id) {
        return jobVacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job vacancy not found: " + id));
    }

    private Set<JobRequiredSkillEntity> mapRequiredSkills(JobVacancyRequest request, JobVacancyEntity owner) {
        return request.getRequiredSkills().stream()
                .map(dto -> {
                    JobRequiredSkillEntity skill = jobRequiredSkillMapper.toEntity(dto);
                    skill.setJobVacancy(owner);
                    return skill;
                })
                .collect(Collectors.toSet());
    }
}