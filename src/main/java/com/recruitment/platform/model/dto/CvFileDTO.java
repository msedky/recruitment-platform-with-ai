package com.recruitment.platform.model.dto;

import com.recruitment.platform.common.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CvFileDTO extends BaseDTO {

    private String originalFileName;
    private String storedFileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
}