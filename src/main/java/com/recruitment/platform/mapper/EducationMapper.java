package com.recruitment.platform.mapper;

import com.recruitment.platform.common.mapper.BaseMapper;
import com.recruitment.platform.model.document.EducationDocument;
import com.recruitment.platform.model.dto.EducationDTO;
import com.recruitment.platform.model.entity.EducationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EducationMapper extends BaseMapper<EducationEntity, EducationDTO, EducationDocument> {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    EducationEntity toEntity(EducationDTO dto);
}