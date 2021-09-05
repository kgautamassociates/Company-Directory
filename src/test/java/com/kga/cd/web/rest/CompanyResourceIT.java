package com.kga.cd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kga.cd.IntegrationTest;
import com.kga.cd.domain.Company;
import com.kga.cd.repository.CompanyRepository;
import com.kga.cd.repository.search.CompanySearchRepository;
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
 * Integration tests for the {@link CompanyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CompanyResourceIT {

    private static final String DEFAULT_CIN = "AAAAAAAAAA";
    private static final String UPDATED_CIN = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_REGISTERED_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_REGISTERED_ADDRESS = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_OF_INCORPORATION = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_INCORPORATION = LocalDate.now(ZoneId.systemDefault());

    private static final Long DEFAULT_AUTHORISED_CAPITAL = 1L;
    private static final Long UPDATED_AUTHORISED_CAPITAL = 2L;

    private static final Long DEFAULT_PAID_UP_CAPITAL = 1L;
    private static final Long UPDATED_PAID_UP_CAPITAL = 2L;

    private static final String DEFAULT_EMAIL_ID = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL_ID = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_OF_LAST_AGM = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_LAST_AGM = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATE_OF_BALANCE_SHEET = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_BALANCE_SHEET = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_COMPANY_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_ROC_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ROC_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/companies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/companies";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CompanyRepository companyRepository;

    /**
     * This repository is mocked in the com.kga.cd.repository.search test package.
     *
     * @see com.kga.cd.repository.search.CompanySearchRepositoryMockConfiguration
     */
    @Autowired
    private CompanySearchRepository mockCompanySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCompanyMockMvc;

    private Company company;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Company createEntity(EntityManager em) {
        Company company = new Company()
            .cin(DEFAULT_CIN)
            .name(DEFAULT_NAME)
            .registeredAddress(DEFAULT_REGISTERED_ADDRESS)
            .dateOfIncorporation(DEFAULT_DATE_OF_INCORPORATION)
            .authorisedCapital(DEFAULT_AUTHORISED_CAPITAL)
            .paidUpCapital(DEFAULT_PAID_UP_CAPITAL)
            .emailId(DEFAULT_EMAIL_ID)
            .dateOfLastAGM(DEFAULT_DATE_OF_LAST_AGM)
            .dateOfBalanceSheet(DEFAULT_DATE_OF_BALANCE_SHEET)
            .companyStatus(DEFAULT_COMPANY_STATUS)
            .rocCode(DEFAULT_ROC_CODE);
        return company;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Company createUpdatedEntity(EntityManager em) {
        Company company = new Company()
            .cin(UPDATED_CIN)
            .name(UPDATED_NAME)
            .registeredAddress(UPDATED_REGISTERED_ADDRESS)
            .dateOfIncorporation(UPDATED_DATE_OF_INCORPORATION)
            .authorisedCapital(UPDATED_AUTHORISED_CAPITAL)
            .paidUpCapital(UPDATED_PAID_UP_CAPITAL)
            .emailId(UPDATED_EMAIL_ID)
            .dateOfLastAGM(UPDATED_DATE_OF_LAST_AGM)
            .dateOfBalanceSheet(UPDATED_DATE_OF_BALANCE_SHEET)
            .companyStatus(UPDATED_COMPANY_STATUS)
            .rocCode(UPDATED_ROC_CODE);
        return company;
    }

    @BeforeEach
    public void initTest() {
        company = createEntity(em);
    }

    @Test
    @Transactional
    void createCompany() throws Exception {
        int databaseSizeBeforeCreate = companyRepository.findAll().size();
        // Create the Company
        restCompanyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(company)))
            .andExpect(status().isCreated());

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(databaseSizeBeforeCreate + 1);
        Company testCompany = companyList.get(companyList.size() - 1);
        assertThat(testCompany.getCin()).isEqualTo(DEFAULT_CIN);
        assertThat(testCompany.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCompany.getRegisteredAddress()).isEqualTo(DEFAULT_REGISTERED_ADDRESS);
        assertThat(testCompany.getDateOfIncorporation()).isEqualTo(DEFAULT_DATE_OF_INCORPORATION);
        assertThat(testCompany.getAuthorisedCapital()).isEqualTo(DEFAULT_AUTHORISED_CAPITAL);
        assertThat(testCompany.getPaidUpCapital()).isEqualTo(DEFAULT_PAID_UP_CAPITAL);
        assertThat(testCompany.getEmailId()).isEqualTo(DEFAULT_EMAIL_ID);
        assertThat(testCompany.getDateOfLastAGM()).isEqualTo(DEFAULT_DATE_OF_LAST_AGM);
        assertThat(testCompany.getDateOfBalanceSheet()).isEqualTo(DEFAULT_DATE_OF_BALANCE_SHEET);
        assertThat(testCompany.getCompanyStatus()).isEqualTo(DEFAULT_COMPANY_STATUS);
        assertThat(testCompany.getRocCode()).isEqualTo(DEFAULT_ROC_CODE);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(1)).save(testCompany);
    }

    @Test
    @Transactional
    void createCompanyWithExistingId() throws Exception {
        // Create the Company with an existing ID
        company.setId(1L);

        int databaseSizeBeforeCreate = companyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompanyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(company)))
            .andExpect(status().isBadRequest());

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(databaseSizeBeforeCreate);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(company);
    }

    @Test
    @Transactional
    void checkCinIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRepository.findAll().size();
        // set the field null
        company.setCin(null);

        // Create the Company, which fails.

        restCompanyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(company)))
            .andExpect(status().isBadRequest());

        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCompanies() throws Exception {
        // Initialize the database
        companyRepository.saveAndFlush(company);

        // Get all the companyList
        restCompanyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(company.getId().intValue())))
            .andExpect(jsonPath("$.[*].cin").value(hasItem(DEFAULT_CIN)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].registeredAddress").value(hasItem(DEFAULT_REGISTERED_ADDRESS)))
            .andExpect(jsonPath("$.[*].dateOfIncorporation").value(hasItem(DEFAULT_DATE_OF_INCORPORATION.toString())))
            .andExpect(jsonPath("$.[*].authorisedCapital").value(hasItem(DEFAULT_AUTHORISED_CAPITAL.intValue())))
            .andExpect(jsonPath("$.[*].paidUpCapital").value(hasItem(DEFAULT_PAID_UP_CAPITAL.intValue())))
            .andExpect(jsonPath("$.[*].emailId").value(hasItem(DEFAULT_EMAIL_ID)))
            .andExpect(jsonPath("$.[*].dateOfLastAGM").value(hasItem(DEFAULT_DATE_OF_LAST_AGM.toString())))
            .andExpect(jsonPath("$.[*].dateOfBalanceSheet").value(hasItem(DEFAULT_DATE_OF_BALANCE_SHEET.toString())))
            .andExpect(jsonPath("$.[*].companyStatus").value(hasItem(DEFAULT_COMPANY_STATUS)))
            .andExpect(jsonPath("$.[*].rocCode").value(hasItem(DEFAULT_ROC_CODE)));
    }

    @Test
    @Transactional
    void getCompany() throws Exception {
        // Initialize the database
        companyRepository.saveAndFlush(company);

        // Get the company
        restCompanyMockMvc
            .perform(get(ENTITY_API_URL_ID, company.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(company.getId().intValue()))
            .andExpect(jsonPath("$.cin").value(DEFAULT_CIN))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.registeredAddress").value(DEFAULT_REGISTERED_ADDRESS))
            .andExpect(jsonPath("$.dateOfIncorporation").value(DEFAULT_DATE_OF_INCORPORATION.toString()))
            .andExpect(jsonPath("$.authorisedCapital").value(DEFAULT_AUTHORISED_CAPITAL.intValue()))
            .andExpect(jsonPath("$.paidUpCapital").value(DEFAULT_PAID_UP_CAPITAL.intValue()))
            .andExpect(jsonPath("$.emailId").value(DEFAULT_EMAIL_ID))
            .andExpect(jsonPath("$.dateOfLastAGM").value(DEFAULT_DATE_OF_LAST_AGM.toString()))
            .andExpect(jsonPath("$.dateOfBalanceSheet").value(DEFAULT_DATE_OF_BALANCE_SHEET.toString()))
            .andExpect(jsonPath("$.companyStatus").value(DEFAULT_COMPANY_STATUS))
            .andExpect(jsonPath("$.rocCode").value(DEFAULT_ROC_CODE));
    }

    @Test
    @Transactional
    void getNonExistingCompany() throws Exception {
        // Get the company
        restCompanyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCompany() throws Exception {
        // Initialize the database
        companyRepository.saveAndFlush(company);

        int databaseSizeBeforeUpdate = companyRepository.findAll().size();

        // Update the company
        Company updatedCompany = companyRepository.findById(company.getId()).get();
        // Disconnect from session so that the updates on updatedCompany are not directly saved in db
        em.detach(updatedCompany);
        updatedCompany
            .cin(UPDATED_CIN)
            .name(UPDATED_NAME)
            .registeredAddress(UPDATED_REGISTERED_ADDRESS)
            .dateOfIncorporation(UPDATED_DATE_OF_INCORPORATION)
            .authorisedCapital(UPDATED_AUTHORISED_CAPITAL)
            .paidUpCapital(UPDATED_PAID_UP_CAPITAL)
            .emailId(UPDATED_EMAIL_ID)
            .dateOfLastAGM(UPDATED_DATE_OF_LAST_AGM)
            .dateOfBalanceSheet(UPDATED_DATE_OF_BALANCE_SHEET)
            .companyStatus(UPDATED_COMPANY_STATUS)
            .rocCode(UPDATED_ROC_CODE);

        restCompanyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCompany.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCompany))
            )
            .andExpect(status().isOk());

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);
        Company testCompany = companyList.get(companyList.size() - 1);
        assertThat(testCompany.getCin()).isEqualTo(UPDATED_CIN);
        assertThat(testCompany.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCompany.getRegisteredAddress()).isEqualTo(UPDATED_REGISTERED_ADDRESS);
        assertThat(testCompany.getDateOfIncorporation()).isEqualTo(UPDATED_DATE_OF_INCORPORATION);
        assertThat(testCompany.getAuthorisedCapital()).isEqualTo(UPDATED_AUTHORISED_CAPITAL);
        assertThat(testCompany.getPaidUpCapital()).isEqualTo(UPDATED_PAID_UP_CAPITAL);
        assertThat(testCompany.getEmailId()).isEqualTo(UPDATED_EMAIL_ID);
        assertThat(testCompany.getDateOfLastAGM()).isEqualTo(UPDATED_DATE_OF_LAST_AGM);
        assertThat(testCompany.getDateOfBalanceSheet()).isEqualTo(UPDATED_DATE_OF_BALANCE_SHEET);
        assertThat(testCompany.getCompanyStatus()).isEqualTo(UPDATED_COMPANY_STATUS);
        assertThat(testCompany.getRocCode()).isEqualTo(UPDATED_ROC_CODE);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository).save(testCompany);
    }

    @Test
    @Transactional
    void putNonExistingCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().size();
        company.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompanyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, company.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(company))
            )
            .andExpect(status().isBadRequest());

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(company);
    }

    @Test
    @Transactional
    void putWithIdMismatchCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().size();
        company.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(company))
            )
            .andExpect(status().isBadRequest());

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(company);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().size();
        company.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(company)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(company);
    }

    @Test
    @Transactional
    void partialUpdateCompanyWithPatch() throws Exception {
        // Initialize the database
        companyRepository.saveAndFlush(company);

        int databaseSizeBeforeUpdate = companyRepository.findAll().size();

        // Update the company using partial update
        Company partialUpdatedCompany = new Company();
        partialUpdatedCompany.setId(company.getId());

        partialUpdatedCompany
            .name(UPDATED_NAME)
            .registeredAddress(UPDATED_REGISTERED_ADDRESS)
            .authorisedCapital(UPDATED_AUTHORISED_CAPITAL)
            .emailId(UPDATED_EMAIL_ID)
            .companyStatus(UPDATED_COMPANY_STATUS)
            .rocCode(UPDATED_ROC_CODE);

        restCompanyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompany.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCompany))
            )
            .andExpect(status().isOk());

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);
        Company testCompany = companyList.get(companyList.size() - 1);
        assertThat(testCompany.getCin()).isEqualTo(DEFAULT_CIN);
        assertThat(testCompany.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCompany.getRegisteredAddress()).isEqualTo(UPDATED_REGISTERED_ADDRESS);
        assertThat(testCompany.getDateOfIncorporation()).isEqualTo(DEFAULT_DATE_OF_INCORPORATION);
        assertThat(testCompany.getAuthorisedCapital()).isEqualTo(UPDATED_AUTHORISED_CAPITAL);
        assertThat(testCompany.getPaidUpCapital()).isEqualTo(DEFAULT_PAID_UP_CAPITAL);
        assertThat(testCompany.getEmailId()).isEqualTo(UPDATED_EMAIL_ID);
        assertThat(testCompany.getDateOfLastAGM()).isEqualTo(DEFAULT_DATE_OF_LAST_AGM);
        assertThat(testCompany.getDateOfBalanceSheet()).isEqualTo(DEFAULT_DATE_OF_BALANCE_SHEET);
        assertThat(testCompany.getCompanyStatus()).isEqualTo(UPDATED_COMPANY_STATUS);
        assertThat(testCompany.getRocCode()).isEqualTo(UPDATED_ROC_CODE);
    }

    @Test
    @Transactional
    void fullUpdateCompanyWithPatch() throws Exception {
        // Initialize the database
        companyRepository.saveAndFlush(company);

        int databaseSizeBeforeUpdate = companyRepository.findAll().size();

        // Update the company using partial update
        Company partialUpdatedCompany = new Company();
        partialUpdatedCompany.setId(company.getId());

        partialUpdatedCompany
            .cin(UPDATED_CIN)
            .name(UPDATED_NAME)
            .registeredAddress(UPDATED_REGISTERED_ADDRESS)
            .dateOfIncorporation(UPDATED_DATE_OF_INCORPORATION)
            .authorisedCapital(UPDATED_AUTHORISED_CAPITAL)
            .paidUpCapital(UPDATED_PAID_UP_CAPITAL)
            .emailId(UPDATED_EMAIL_ID)
            .dateOfLastAGM(UPDATED_DATE_OF_LAST_AGM)
            .dateOfBalanceSheet(UPDATED_DATE_OF_BALANCE_SHEET)
            .companyStatus(UPDATED_COMPANY_STATUS)
            .rocCode(UPDATED_ROC_CODE);

        restCompanyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompany.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCompany))
            )
            .andExpect(status().isOk());

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);
        Company testCompany = companyList.get(companyList.size() - 1);
        assertThat(testCompany.getCin()).isEqualTo(UPDATED_CIN);
        assertThat(testCompany.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCompany.getRegisteredAddress()).isEqualTo(UPDATED_REGISTERED_ADDRESS);
        assertThat(testCompany.getDateOfIncorporation()).isEqualTo(UPDATED_DATE_OF_INCORPORATION);
        assertThat(testCompany.getAuthorisedCapital()).isEqualTo(UPDATED_AUTHORISED_CAPITAL);
        assertThat(testCompany.getPaidUpCapital()).isEqualTo(UPDATED_PAID_UP_CAPITAL);
        assertThat(testCompany.getEmailId()).isEqualTo(UPDATED_EMAIL_ID);
        assertThat(testCompany.getDateOfLastAGM()).isEqualTo(UPDATED_DATE_OF_LAST_AGM);
        assertThat(testCompany.getDateOfBalanceSheet()).isEqualTo(UPDATED_DATE_OF_BALANCE_SHEET);
        assertThat(testCompany.getCompanyStatus()).isEqualTo(UPDATED_COMPANY_STATUS);
        assertThat(testCompany.getRocCode()).isEqualTo(UPDATED_ROC_CODE);
    }

    @Test
    @Transactional
    void patchNonExistingCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().size();
        company.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompanyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, company.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(company))
            )
            .andExpect(status().isBadRequest());

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(company);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().size();
        company.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(company))
            )
            .andExpect(status().isBadRequest());

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(company);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().size();
        company.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(company)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(company);
    }

    @Test
    @Transactional
    void deleteCompany() throws Exception {
        // Initialize the database
        companyRepository.saveAndFlush(company);

        int databaseSizeBeforeDelete = companyRepository.findAll().size();

        // Delete the company
        restCompanyMockMvc
            .perform(delete(ENTITY_API_URL_ID, company.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(1)).deleteById(company.getId());
    }

    @Test
    @Transactional
    void searchCompany() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        companyRepository.saveAndFlush(company);
        when(mockCompanySearchRepository.search(queryStringQuery("id:" + company.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(company), PageRequest.of(0, 1), 1));

        // Search the company
        restCompanyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + company.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(company.getId().intValue())))
            .andExpect(jsonPath("$.[*].cin").value(hasItem(DEFAULT_CIN)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].registeredAddress").value(hasItem(DEFAULT_REGISTERED_ADDRESS)))
            .andExpect(jsonPath("$.[*].dateOfIncorporation").value(hasItem(DEFAULT_DATE_OF_INCORPORATION.toString())))
            .andExpect(jsonPath("$.[*].authorisedCapital").value(hasItem(DEFAULT_AUTHORISED_CAPITAL.intValue())))
            .andExpect(jsonPath("$.[*].paidUpCapital").value(hasItem(DEFAULT_PAID_UP_CAPITAL.intValue())))
            .andExpect(jsonPath("$.[*].emailId").value(hasItem(DEFAULT_EMAIL_ID)))
            .andExpect(jsonPath("$.[*].dateOfLastAGM").value(hasItem(DEFAULT_DATE_OF_LAST_AGM.toString())))
            .andExpect(jsonPath("$.[*].dateOfBalanceSheet").value(hasItem(DEFAULT_DATE_OF_BALANCE_SHEET.toString())))
            .andExpect(jsonPath("$.[*].companyStatus").value(hasItem(DEFAULT_COMPANY_STATUS)))
            .andExpect(jsonPath("$.[*].rocCode").value(hasItem(DEFAULT_ROC_CODE)));
    }
}
