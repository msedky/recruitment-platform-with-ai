package com.recruitment.platform.mapper;

import com.recruitment.platform.common.mapper.BaseMapper;
import com.recruitment.platform.model.dto.SkillDTO;
import com.recruitment.platform.model.entity.SkillEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SkillMapper extends BaseMapper<SkillEntity, SkillDTO> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    SkillEntity toEntity(SkillDTO dto);

}