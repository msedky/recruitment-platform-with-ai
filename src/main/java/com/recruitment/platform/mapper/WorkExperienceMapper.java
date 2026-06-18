package com.recruitment.platform.mapper;

import com.recruitment.platform.common.mapper.BaseMapper;
import com.recruitment.platform.model.document.WorkExperienceDocument;
import com.recruitment.platform.model.dto.WorkExperienceDTO;
import com.recruitment.platform.model.entity.WorkExperienceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkExperienceMapper extends BaseMapper<WorkExperienceEntity, WorkExperienceDTO, WorkExperienceDocument> {

    @Override
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    WorkExperienceEntity toEntity(WorkExperienceDTO dto);
}