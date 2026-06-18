package com.recruitment.platform.repository;

import com.recruitment.platform.model.entity.ApplicantEntity;
import com.recruitment.platform.model.enums.ApplicantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicantRepository extends JpaRepository<ApplicantEntity, UUID> {

    @Query("""
                SELECT a FROM ApplicantEntity a
                LEFT JOIN FETCH a.cvFile
                LEFT JOIN FETCH a.skills
                LEFT JOIN FETCH a.workExperiences
                LEFT JOIN FETCH a.educations
                LEFT JOIN FETCH a.certificates
                LEFT JOIN FETCH a.languages
                WHERE a.id = :id
            """)
    Optional<ApplicantEntity> findByIdWithChildren(@Param("id") UUID id);

    List<ApplicantEntity> findByStatus(ApplicantStatus status);

    Optional<ApplicantEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}