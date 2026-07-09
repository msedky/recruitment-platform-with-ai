package com.recruitment.platform.mapper;

import com.recruitment.platform.model.dto.JobVacancyDTO;
import com.recruitment.platform.model.entity.JobVacancyEntity;
import com.recruitment.platform.model.payload.request.JobVacancyRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface JobVacancyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "requiredSkills", ignore = true)
    @Mapping(target = "applications", ignore = true)
    JobVacancyEntity toEntity(JobVacancyRequest request);

    @Mapping(target = "createdAt", expression = "java(instantToOffsetDateTime(entity.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(instantToOffsetDateTime(entity.getUpdatedAt()))")
    JobVacancyDTO toResponseDTO(JobVacancyEntity entity);

    default OffsetDateTime instantToOffsetDateTime(Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }
}