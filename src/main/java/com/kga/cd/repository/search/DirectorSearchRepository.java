package com.kga.cd.repository.search;

import com.kga.cd.domain.Director;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Director} entity.
 */
public interface DirectorSearchRepository extends ElasticsearchRepository<Director, Long> {}
