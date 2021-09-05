package com.kga.cd.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.kga.cd.domain.Director;
import com.kga.cd.repository.DirectorRepository;
import com.kga.cd.repository.search.DirectorSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Director}.
 */
@Service
@Transactional
public class DirectorService {

    private final Logger log = LoggerFactory.getLogger(DirectorService.class);

    private final DirectorRepository directorRepository;

    private final DirectorSearchRepository directorSearchRepository;

    public DirectorService(DirectorRepository directorRepository, DirectorSearchRepository directorSearchRepository) {
        this.directorRepository = directorRepository;
        this.directorSearchRepository = directorSearchRepository;
    }

    /**
     * Save a director.
     *
     * @param director the entity to save.
     * @return the persisted entity.
     */
    public Director save(Director director) {
        log.debug("Request to save Director : {}", director);
        Director result = directorRepository.save(director);
        directorSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a director.
     *
     * @param director the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Director> partialUpdate(Director director) {
        log.debug("Request to partially update Director : {}", director);

        return directorRepository
            .findById(director.getId())
            .map(
                existingDirector -> {
                    if (director.getDin() != null) {
                        existingDirector.setDin(director.getDin());
                    }
                    if (director.getName() != null) {
                        existingDirector.setName(director.getName());
                    }
                    if (director.getBeginDate() != null) {
                        existingDirector.setBeginDate(director.getBeginDate());
                    }
                    if (director.getEndDate() != null) {
                        existingDirector.setEndDate(director.getEndDate());
                    }

                    return existingDirector;
                }
            )
            .map(directorRepository::save)
            .map(
                savedDirector -> {
                    directorSearchRepository.save(savedDirector);

                    return savedDirector;
                }
            );
    }

    /**
     * Get all the directors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Director> findAll(Pageable pageable) {
        log.debug("Request to get all Directors");
        return directorRepository.findAll(pageable);
    }

    /**
     * Get one director by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Director> findOne(Long id) {
        log.debug("Request to get Director : {}", id);
        return directorRepository.findById(id);
    }

    /**
     * Delete the director by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Director : {}", id);
        directorRepository.deleteById(id);
        directorSearchRepository.deleteById(id);
    }

    /**
     * Search for the director corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Director> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Directors for query {}", query);
        return directorSearchRepository.search(queryStringQuery(query), pageable);
    }
}
