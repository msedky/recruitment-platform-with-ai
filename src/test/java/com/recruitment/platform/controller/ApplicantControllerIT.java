package com.recruitment.platform.controller;

import com.recruitment.platform.model.dto.ApplicantDTO;
import com.recruitment.platform.model.dto.CvFileDTO;
import com.recruitment.platform.model.dto.WorkExperienceDTO;
import com.recruitment.platform.model.entity.JobRequiredSkillEntity;
import com.recruitment.platform.model.entity.JobVacancyEntity;
import com.recruitment.platform.model.enums.*;
import com.recruitment.platform.repository.ApplicantRepository;
import com.recruitment.platform.repository.JobVacancyRepository;
import com.recruitment.platform.service.AiExtractionService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class ApplicantControllerIT {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));

    private final String BASE_URL = "/api/v1/applicants";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobVacancyRepository jobVacancyRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @MockitoBean
    private AiExtractionService aiExtractionService;

    private JobVacancyEntity vacancy;

    @BeforeEach
    void setUp() {
        vacancy = jobVacancyRepository.save(JobVacancyEntity.builder()
                .title("Backend Engineer")
                .description("Build and maintain backend services.")
                .department("Engineering")
                .location("Some Location")
                .employmentType(EmploymentType.FULL_TIME)
                .workModel(WorkModel.HYBRID)
                .minExperienceYears(2)
                .status(VacancyStatus.OPENED)
                .build());

        vacancy.setRequiredSkills(Set.of(
                JobRequiredSkillEntity.builder().jobVacancy(vacancy).name("Java").minimumLevel(SkillLevel.INTERMEDIATE).build(),
                JobRequiredSkillEntity.builder().jobVacancy(vacancy).name("Spring Boot").minimumLevel(SkillLevel.INTERMEDIATE).build()
        ));

        jobVacancyRepository.save(vacancy);
    }

    @Nested
    @DisplayName("POST /api/v1/applicants/upload/{jobVacancyId}")
    class UploadCV {

        @Test
        void uploadCv_withValidPdf_extractsAndPersistsApplicant() throws Exception {
            MockMultipartFile cv = new MockMultipartFile(
                    "file",
                    "jane-doe-cv.pdf",
                    "application/pdf",
                    buildSamplePdfBytes("Jane Doe\nSenior Backend Engineer\n"
                            + "5 years of experience building distributed systems in Java and Spring Boot.\n"
                            + "jane.doe@example.com | +1-555-0100"));

            ApplicantDTO applicantDTO = ApplicantDTO.builder()
                    .id(UUID.randomUUID())
                    .createdAt(OffsetDateTime.of(2026, 6, 1, 12, 0, 0, 0, OffsetDateTime.now().getOffset()))
                    .fullName("Jane Doe")
                    .email("jane.doe@example.com")
                    .phone("+1-555-0100")
                    .nationality("American")
                    .address("123 Main St, Anytown, USA")
                    .linkedInUrl("https://www.linkedin.com/in/janedoe")
                    .portfolioUrl(null)
                    .summary("Senior Backend Engineer with 5 years of experience building distributed systems in Java and Spring Boot.")
                    .status(ApplicantStatus.PUBLISHED)
                    .cvFile(CvFileDTO.builder()
                            .originalFileName("jane-doe-cv.pdf")
                            .storedFileName("jane-doe-cv.pdf")
                            .filePath("/path/to/storage/jane-doe-cv.pdf")
                            .fileSize(cv.getSize())
                            .fileType("application/pdf")
                            .build())
                    .workExperiences(Set.of(
                            WorkExperienceDTO.builder()
                                    .companyName("Tech Solutions Inc.")
                                    .jobTitle("Senior Backend Engineer")
                                    .roleType(RoleType.INDIVIDUAL_CONTRIBUTOR)
                                    .startDate(LocalDate.of(2020, 10, 1))
                                    .endDate(LocalDate.of(2022, 5, 31))
                                    .isCurrent(false)
                                    .description("Developed and maintained backend services using Java and Spring Boot. Led a team of 3 engineers to implement microservices architecture.")
                                    .build(),
                            WorkExperienceDTO.builder()
                                    .companyName("Innovatech LLC")
                                    .jobTitle("Senior Backend Engineer")
                                    .roleType(RoleType.INDIVIDUAL_CONTRIBUTOR)
                                    .startDate(LocalDate.of(2022, 6, 1))
                                    .endDate(LocalDate.of(2025, 10, 31))
                                    .isCurrent(false)
                                    .description("Designed and implemented scalable backend systems for e-commerce platforms. Optimized database queries and improved API response times by 30%.")
                                    .build()
                    ))
                    .skills(Set.of())
                    .certificates(Set.of())
                    .educations(Set.of())
                    .languages(Set.of())
                    .build();
            when(aiExtractionService.extractApplicantData(anyString())).thenReturn(applicantDTO);

            mockMvc.perform(multipart(BASE_URL + "/upload/{jobVacancyId}", vacancy.getId())
                            .file(cv))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.fullName").value(applicantDTO.getFullName()))
                    .andExpect(jsonPath("$.email").value(applicantDTO.getEmail()))
                    .andExpect(jsonPath("$.phone").value(applicantDTO.getPhone()))
                    .andExpect(jsonPath("$.nationality").value(applicantDTO.getNationality()))
                    .andExpect(jsonPath("$.address").value(applicantDTO.getAddress()))
                    .andExpect(jsonPath("$.linkedInUrl").value(applicantDTO.getLinkedInUrl()))
                    .andExpect(jsonPath("$.portfolioUrl").value(applicantDTO.getPortfolioUrl()))
                    .andExpect(jsonPath("$.summary").value(applicantDTO.getSummary()))
            ;
        }
    }

    private static byte[] buildSamplePdfBytes(String text) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(50, 700);
                for (String line : text.split("\n")) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -15);
                }
                contentStream.endText();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }
}
