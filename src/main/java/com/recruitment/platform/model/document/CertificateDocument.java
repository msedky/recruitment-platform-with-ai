package com.recruitment.platform.model.document;

import com.recruitment.platform.common.document.BaseDocument;
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
public class CertificateDocument extends BaseDocument {

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String issuingOrganization;

    @Field(type = FieldType.Date)
    private LocalDate issueDate;

    @Field(type = FieldType.Date)
    private LocalDate expiryDate;
}