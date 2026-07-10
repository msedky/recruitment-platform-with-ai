package com.recruitment.platform.common.mapper;

import com.recruitment.platform.common.dto.BaseDTO;
import com.recruitment.platform.common.entity.BaseEntity;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public interface BaseMapper<E extends BaseEntity, DTO extends BaseDTO> {
    @Mapping(target = "createdAt", expression = "java(instantToOffsetDateTime(entity.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(instantToOffsetDateTime(entity.getUpdatedAt()))")
    DTO toDTO(E entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    E toEntity(DTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(DTO dto, @MappingTarget E entity);

    default OffsetDateTime instantToOffsetDateTime(Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }
}