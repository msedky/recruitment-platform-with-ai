package com.recruitment.platform.mapper;

import com.recruitment.platform.common.mapper.BaseMapper;
import com.recruitment.platform.model.document.LanguageDocument;
import com.recruitment.platform.model.dto.LanguageDTO;
import com.recruitment.platform.model.entity.LanguageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LanguageMapper extends BaseMapper<LanguageEntity, LanguageDTO, LanguageDocument> {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    LanguageEntity toEntity(LanguageDTO dto);
}