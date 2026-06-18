package com.recruitment.platform.model.document;

import com.recruitment.platform.common.document.BaseDocument;
import com.recruitment.platform.model.enums.RoleType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WorkExperienceDocument extends BaseDocument {

    @Field(type = FieldType.Text)
    private String companyName;

    @Field(type = FieldType.Text)
    private String jobTitle;

    @Field(type = FieldType.Keyword)
    private RoleType roleType;

    @Field(type = FieldType.Date)
    private LocalDate startDate;

    @Field(type = FieldType.Date)
    private LocalDate endDate;

    @Field(type = FieldType.Boolean)
    private Boolean isCurrent;

    @Field(type = FieldType.Text)
    private String description;
}