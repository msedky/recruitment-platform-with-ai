package com.recruitment.platform.repository;

import com.recruitment.platform.model.entity.OutboxEntity;
import com.recruitment.platform.model.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEntity, UUID> {

    List<OutboxEntity> findByStatus(OutboxStatus status);
}