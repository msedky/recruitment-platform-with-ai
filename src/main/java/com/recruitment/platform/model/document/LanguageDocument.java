package com.recruitment.platform.model.document;

import com.recruitment.platform.common.document.BaseDocument;
import com.recruitment.platform.model.enums.LanguageProficiency;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LanguageDocument extends BaseDocument {

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Keyword)
    private LanguageProficiency proficiency;
}