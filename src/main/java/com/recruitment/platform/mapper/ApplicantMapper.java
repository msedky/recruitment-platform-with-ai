package com.recruitment.platform.mapper;

import com.recruitment.platform.common.mapper.BaseMapper;
import com.recruitment.platform.model.document.ApplicantDocument;
import com.recruitment.platform.model.dto.ApplicantDTO;
import com.recruitment.platform.model.entity.ApplicantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {
        CvFileMapper.class,
        WorkExperienceMapper.class,
        SkillMapper.class,
        EducationMapper.class,
        CertificateMapper.class,
        LanguageMapper.class
})
public interface ApplicantMapper extends BaseMapper<ApplicantEntity, ApplicantDTO, ApplicantDocument> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "cvFile", ignore = true)
    @Mapping(target = "workExperiences", ignore = true)
    @Mapping(target = "educations", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "certificates", ignore = true)
    @Mapping(target = "languages", ignore = true)
    void updateEntityFromDTO(ApplicantDTO dto, @MappingTarget ApplicantEntity entity);

    @Override
    @Mapping(target = "id", expression = "java(java.util.UUID.fromString(document.getId()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "cvFile", ignore = true)
    @Mapping(target = "workExperiences", ignore = true)
    @Mapping(target = "educations", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "certificates", ignore = true)
    @Mapping(target = "languages", ignore = true)
    ApplicantDTO toDTO(ApplicantDocument document);
}