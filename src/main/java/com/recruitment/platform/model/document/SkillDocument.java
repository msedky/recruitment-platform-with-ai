package com.recruitment.platform.model.document;

import com.recruitment.platform.common.document.BaseDocument;
import com.recruitment.platform.model.enums.SkillLevel;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SkillDocument extends BaseDocument {

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Keyword)
    private SkillLevel level;
}