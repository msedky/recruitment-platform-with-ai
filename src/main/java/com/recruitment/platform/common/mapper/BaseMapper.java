package com.recruitment.platform.common.mapper;

import com.recruitment.platform.common.document.BaseDocument;
import com.recruitment.platform.common.dto.BaseDTO;
import com.recruitment.platform.common.entity.BaseEntity;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public interface BaseMapper<ENTITY extends BaseEntity, DTO extends BaseDTO, DOCUMENT extends BaseDocument> {
    @Mapping(target = "createdAt", expression = "java(instantToOffsetDateTime(entity.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(instantToOffsetDateTime(entity.getUpdatedAt()))")
    DTO toDTO(ENTITY entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ENTITY toEntity(DTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(DTO dto, @MappingTarget ENTITY entity);

    @Mapping(target = "createdAt", expression = "java(instantToOffsetDateTime(entity.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(instantToOffsetDateTime(entity.getUpdatedAt()))")
    DOCUMENT toDocument(ENTITY entity);


    @Mapping(target = "id", expression = "java(java.util.UUID.fromString(document.getId()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DTO toDTO(DOCUMENT document);

    default OffsetDateTime instantToOffsetDateTime(Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }
}