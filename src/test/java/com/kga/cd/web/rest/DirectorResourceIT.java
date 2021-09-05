package com.kga.cd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kga.cd.IntegrationTest;
import com.kga.cd.domain.Director;
import com.kga.cd.repository.DirectorRepository;
import com.kga.cd.repository.search.DirectorSearchRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DirectorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DirectorResourceIT {

    private static final String DEFAULT_DIN = "AAAAAAAAAA";
    private static final String UPDATED_DIN = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BEGIN_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BEGIN_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/directors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/directors";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DirectorRepository directorRepository;

    /**
     * This repository is mocked in the com.kga.cd.repository.search test package.
     *
     * @see com.kga.cd.repository.search.DirectorSearchRepositoryMockConfiguration
     */
    @Autowired
    private DirectorSearchRepository mockDirectorSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDirectorMockMvc;

    private Director director;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Director createEntity(EntityManager em) {
        Director director = new Director().din(DEFAULT_DIN).name(DEFAULT_NAME).beginDate(DEFAULT_BEGIN_DATE).endDate(DEFAULT_END_DATE);
        return director;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Director createUpdatedEntity(EntityManager em) {
        Director director = new Director().din(UPDATED_DIN).name(UPDATED_NAME).beginDate(UPDATED_BEGIN_DATE).endDate(UPDATED_END_DATE);
        return director;
    }

    @BeforeEach
    public void initTest() {
        director = createEntity(em);
    }

    @Test
    @Transactional
    void createDirector() throws Exception {
        int databaseSizeBeforeCreate = directorRepository.findAll().size();
        // Create the Director
        restDirectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(director)))
            .andExpect(status().isCreated());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeCreate + 1);
        Director testDirector = directorList.get(directorList.size() - 1);
        assertThat(testDirector.getDin()).isEqualTo(DEFAULT_DIN);
        assertThat(testDirector.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDirector.getBeginDate()).isEqualTo(DEFAULT_BEGIN_DATE);
        assertThat(testDirector.getEndDate()).isEqualTo(DEFAULT_END_DATE);

        // Validate the Director in Elasticsearch
        verify(mockDirectorSearchRepository, times(1)).save(testDirector);
    }

    @Test
    @Transactional
    void createDirectorWithExistingId() throws Exception {
        // Create the Director with an existing ID
        director.setId(1L);

        int databaseSizeBeforeCreate = directorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDirectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(director)))
            .andExpect(status().isBadRequest());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeCreate);

        // Validate the Director in Elasticsearch
        verify(mockDirectorSearchRepository, times(0)).save(director);
    }

    @Test
    @Transactional
    void getAllDirectors() throws Exception {
        // Initialize the database
        directorRepository.saveAndFlush(director);

        // Get all the directorList
        restDirectorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(director.getId().intValue())))
            .andExpect(jsonPath("$.[*].din").value(hasItem(DEFAULT_DIN)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].beginDate").value(hasItem(DEFAULT_BEGIN_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }

    @Test
    @Transactional
    void getDirector() throws Exception {
        // Initialize the database
        directorRepository.saveAndFlush(director);

        // Get the director
        restDirectorMockMvc
            .perform(get(ENTITY_API_URL_ID, director.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(director.getId().intValue()))
            .andExpect(jsonPath("$.din").value(DEFAULT_DIN))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.beginDate").value(DEFAULT_BEGIN_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingDirector() throws Exception {
        // Get the director
        restDirectorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDirector() throws Exception {
        // Initialize the database
        directorRepository.saveAndFlush(director);

        int databaseSizeBeforeUpdate = directorRepository.findAll().size();

        // Update the director
        Director updatedDirector = directorRepository.findById(director.getId()).get();
        // Disconnect from session so that the updates on updatedDirector are not directly saved in db
        em.detach(updatedDirector);
        updatedDirector.din(UPDATED_DIN).name(UPDATED_NAME).beginDate(UPDATED_BEGIN_DATE).endDate(UPDATED_END_DATE);

        restDirectorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDirector.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDirector))
            )
            .andExpect(status().isOk());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
        Director testDirector = directorList.get(directorList.size() - 1);
        assertThat(testDirector.getDin()).isEqualTo(UPDATED_DIN);
        assertThat(testDirector.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDirector.getBeginDate()).isEqualTo(UPDATED_BEGIN_DATE);
        assertThat(testDirector.getEndDate()).isEqualTo(UPDATED_END_DATE);

        // Validate the Director in Elasticsearch
        verify(mockDirectorSearchRepository).save(testDirector);
    }

    @Test
    @Transactional
    void putNonExistingDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().size();
        director.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDirectorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, director.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(director))
            )
            .andExpect(status().isBadRequest());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Director in Elasticsearch
        verify(mockDirectorSearchRepository, times(0)).save(director);
    }

    @Test
    @Transactional
    void putWithIdMismatchDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().size();
        director.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(director))
            )
            .andExpect(status().isBadRequest());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Director in Elasticsearch
        verify(mockDirectorSearchRepository, times(0)).save(director);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().size();
        director.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(director)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Director in Elasticsearch
        verify(mockDirectorSearchRepository, times(0)).save(director);
    }

    @Test
    @Transactional
    void partialUpdateDirectorWithPatch() throws Exception {
        // Initialize the database
        directorRepository.saveAndFlush(director);

        int databaseSizeBeforeUpdate = directorRepository.findAll().size();

        // Update the director using partial update
        Director partialUpdatedDirector = new Director();
        partialUpdatedDirector.setId(director.getId());

        partialUpdatedDirector.din(UPDATED_DIN).name(UPDATED_NAME).beginDate(UPDATED_BEGIN_DATE);

        restDirectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDirector.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDirector))
            )
            .andExpect(status().isOk());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
        Director testDirector = directorList.get(directorList.size() - 1);
        assertThat(testDirector.getDin()).isEqualTo(UPDATED_DIN);
        assertThat(testDirector.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDirector.getBeginDate()).isEqualTo(UPDATED_BEGIN_DATE);
        assertThat(testDirector.getEndDate()).isEqualTo(DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void fullUpdateDirectorWithPatch() throws Exception {
        // Initialize the database
        directorRepository.saveAndFlush(director);

        int databaseSizeBeforeUpdate = directorRepository.findAll().size();

        // Update the director using partial update
        Director partialUpdatedDirector = new Director();
        partialUpdatedDirector.setId(director.getId());

        partialUpdatedDirector.din(UPDATED_DIN).name(UPDATED_NAME).beginDate(UPDATED_BEGIN_DATE).endDate(UPDATED_END_DATE);

        restDirectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDirector.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDirector))
            )
            .andExpect(status().isOk());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);
        Director testDirector = directorList.get(directorList.size() - 1);
        assertThat(testDirector.getDin()).isEqualTo(UPDATED_DIN);
        assertThat(testDirector.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDirector.getBeginDate()).isEqualTo(UPDATED_BEGIN_DATE);
        assertThat(testDirector.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().size();
        director.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDirectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, director.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(director))
            )
            .andExpect(status().isBadRequest());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Director in Elasticsearch
        verify(mockDirectorSearchRepository, times(0)).save(director);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().size();
        director.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(director))
            )
            .andExpect(status().isBadRequest());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Director in Elasticsearch
        verify(mockDirectorSearchRepository, times(0)).save(director);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDirector() throws Exception {
        int databaseSizeBeforeUpdate = directorRepository.findAll().size();
        director.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(director)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Director in the database
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Director in Elasticsearch
        verify(mockDirectorSearchRepository, times(0)).save(director);
    }

    @Test
    @Transactional
    void deleteDirector() throws Exception {
        // Initialize the database
        directorRepository.saveAndFlush(director);

        int databaseSizeBeforeDelete = directorRepository.findAll().size();

        // Delete the director
        restDirectorMockMvc
            .perform(delete(ENTITY_API_URL_ID, director.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Director> directorList = directorRepository.findAll();
        assertThat(directorList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Director in Elasticsearch
        verify(mockDirectorSearchRepository, times(1)).deleteById(director.getId());
    }

    @Test
    @Transactional
    void searchDirector() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        directorRepository.saveAndFlush(director);
        when(mockDirectorSearchRepository.search(queryStringQuery("id:" + director.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(director), PageRequest.of(0, 1), 1));

        // Search the director
        restDirectorMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + director.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(director.getId().intValue())))
            .andExpect(jsonPath("$.[*].din").value(hasItem(DEFAULT_DIN)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].beginDate").value(hasItem(DEFAULT_BEGIN_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }
}
