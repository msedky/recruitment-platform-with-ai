package com.recruitment.platform.mapper;

import com.recruitment.platform.common.mapper.BaseMapper;
import com.recruitment.platform.model.document.CvFileDocument;
import com.recruitment.platform.model.dto.CvFileDTO;
import com.recruitment.platform.model.entity.CvFileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CvFileMapper extends BaseMapper<CvFileEntity, CvFileDTO, CvFileDocument> {

    @Override
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    CvFileEntity toEntity(CvFileDTO dto);

}