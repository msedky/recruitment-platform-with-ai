package com.recruitment.platform.model.payload.response;

import com.recruitment.platform.model.dto.ShortlistResultDTO;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortlistResponse {
    UUID jobVacancyId;
    int totalApplicants;
    List<ShortlistResultDTO> shortlisted;
    OffsetDateTime evaluatedA;
}
