package com.recruitment.platform.repository;

import com.recruitment.platform.model.document.ApplicantDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicantSearchRepository
        extends ElasticsearchRepository<ApplicantDocument, String> {

    /**
     * Full-text search across name, summary, skills, job titles, companies.
     * Spring Data ES generates the query from the method name.
     */
    List<ApplicantDocument> findByFullNameContainingOrSummaryContainingOrSkillsContaining(
            String fullName, String summary, String skills);
}