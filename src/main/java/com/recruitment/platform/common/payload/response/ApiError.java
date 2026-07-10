package com.recruitment.platform.common.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
public class ApiError {
    private OffsetDateTime timestamp;
    private int status;
    private String error;
}
