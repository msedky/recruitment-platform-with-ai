package com.recruitment.platform.model.document;

import com.recruitment.platform.common.document.BaseDocument;
import com.recruitment.platform.model.enums.DegreeType;
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
public class EducationDocument extends BaseDocument {

    @Field(type = FieldType.Text)
    private String institution;

    @Field(type = FieldType.Keyword)
    private DegreeType degreeType;

    @Field(type = FieldType.Text)
    private String fieldOfStudy;

    @Field(type = FieldType.Date)
    private LocalDate startDate;

    @Field(type = FieldType.Date)
    private LocalDate endDate;

    @Field(type = FieldType.Boolean)
    private Boolean isCurrent;

    @Field(type = FieldType.Keyword)
    private String grade;
}