package com.recruitment.platform.mapper;

import com.recruitment.platform.model.dto.JobRequiredSkillDTO;
import com.recruitment.platform.model.entity.JobRequiredSkillEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobRequiredSkillMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "jobVacancy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    JobRequiredSkillEntity toEntity(JobRequiredSkillDTO dto);
}
