package com.recruitment.platform.model.document;

import com.recruitment.platform.common.document.BaseDocument;
import com.recruitment.platform.model.enums.ApplicantStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Set;

/**
 * Elasticsearch document — read-optimised projection of ApplicantEntity.
 * Source of truth is PostgreSQL. This is rebuilt from DB on INDEX_FAILED recovery.
 * Used for: findById (fast path) and keyword search.
 *
 * Nested document classes (WorkExperienceDocument, SkillDocument, etc.) are used
 * instead of DTOs — ES documents are a separate concern from API data transfer.
 */
@Document(indexName = "applicants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ApplicantDocument extends BaseDocument {

    @Field(type = FieldType.Text)
    private String fullName;

    @Field(type = FieldType.Keyword)
    private String email;

    @Field(type = FieldType.Keyword)
    private String phone;

    @Field(type = FieldType.Text)
    private String nationality;

    @Field(type = FieldType.Text)
    private String address;

    @Field(type = FieldType.Keyword)
    private String linkedInUrl;

    @Field(type = FieldType.Keyword)
    private String portfolioUrl;

    @Field(type = FieldType.Text)
    private String summary;

    @Field(type = FieldType.Keyword)
    private ApplicantStatus status;

    @Field(type = FieldType.Nested)
    private CvFileDocument cvFile;

    @Field(type = FieldType.Nested)
    private Set<WorkExperienceDocument> workExperiences;

    @Field(type = FieldType.Nested)
    private Set<SkillDocument> skills;

    @Field(type = FieldType.Nested)
    private Set<CertificateDocument> certificates;

    @Field(type = FieldType.Nested)
    private Set<EducationDocument> educations;

    @Field(type = FieldType.Nested)
    private Set<LanguageDocument> languages;
}