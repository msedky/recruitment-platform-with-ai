package com.recruitment.platform.mapper;

import com.recruitment.platform.common.mapper.BaseMapper;
import com.recruitment.platform.model.dto.CertificateDTO;
import com.recruitment.platform.model.entity.CertificateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CertificateMapper extends BaseMapper<CertificateEntity, CertificateDTO> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    CertificateEntity toEntity(CertificateDTO dto);
}