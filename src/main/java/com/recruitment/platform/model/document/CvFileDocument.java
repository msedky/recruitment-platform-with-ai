package com.recruitment.platform.model.document;

import com.recruitment.platform.common.document.BaseDocument;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CvFileDocument extends BaseDocument {

    @Field(type = FieldType.Keyword)
    private String originalFileName;

    @Field(type = FieldType.Keyword)
    private String storedFileName;

    @Field(type = FieldType.Keyword)
    private String filePath;

    @Field(type = FieldType.Long)
    private Long fileSize;

    @Field(type = FieldType.Keyword)
    private String fileType;
}