package com.kga.cd.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.kga.cd.domain.Company;
import com.kga.cd.repository.CompanyRepository;
import com.kga.cd.repository.search.CompanySearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Company}.
 */
@Service
@Transactional
public class CompanyService {

    private final Logger log = LoggerFactory.getLogger(CompanyService.class);

    private final CompanyRepository companyRepository;

    private final CompanySearchRepository companySearchRepository;

    public CompanyService(CompanyRepository companyRepository, CompanySearchRepository companySearchRepository) {
        this.companyRepository = companyRepository;
        this.companySearchRepository = companySearchRepository;
    }

    /**
     * Save a company.
     *
     * @param company the entity to save.
     * @return the persisted entity.
     */
    public Company save(Company company) {
        log.debug("Request to save Company : {}", company);
        Company result = companyRepository.save(company);
        companySearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a company.
     *
     * @param company the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Company> partialUpdate(Company company) {
        log.debug("Request to partially update Company : {}", company);

        return companyRepository
            .findById(company.getId())
            .map(
                existingCompany -> {
                    if (company.getCin() != null) {
                        existingCompany.setCin(company.getCin());
                    }
                    if (company.getName() != null) {
                        existingCompany.setName(company.getName());
                    }
                    if (company.getRegisteredAddress() != null) {
                        existingCompany.setRegisteredAddress(company.getRegisteredAddress());
                    }
                    if (company.getDateOfIncorporation() != null) {
                        existingCompany.setDateOfIncorporation(company.getDateOfIncorporation());
                    }
                    if (company.getAuthorisedCapital() != null) {
                        existingCompany.setAuthorisedCapital(company.getAuthorisedCapital());
                    }
                    if (company.getPaidUpCapital() != null) {
                        existingCompany.setPaidUpCapital(company.getPaidUpCapital());
                    }
                    if (company.getEmailId() != null) {
                        existingCompany.setEmailId(company.getEmailId());
                    }
                    if (company.getDateOfLastAGM() != null) {
                        existingCompany.setDateOfLastAGM(company.getDateOfLastAGM());
                    }
                    if (company.getDateOfBalanceSheet() != null) {
                        existingCompany.setDateOfBalanceSheet(company.getDateOfBalanceSheet());
                    }
                    if (company.getCompanyStatus() != null) {
                        existingCompany.setCompanyStatus(company.getCompanyStatus());
                    }
                    if (company.getRocCode() != null) {
                        existingCompany.setRocCode(company.getRocCode());
                    }

                    return existingCompany;
                }
            )
            .map(companyRepository::save)
            .map(
                savedCompany -> {
                    companySearchRepository.save(savedCompany);

                    return savedCompany;
                }
            );
    }

    /**
     * Get all the companies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Company> findAll(Pageable pageable) {
        log.debug("Request to get all Companies");
        return companyRepository.findAll(pageable);
    }

    /**
     * Get one company by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Company> findOne(Long id) {
        log.debug("Request to get Company : {}", id);
        Optional<Company> byId = companyRepository.findById(id);
        return byId;
    }

    /**
     * Delete the company by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Company : {}", id);
        companyRepository.deleteById(id);
        companySearchRepository.deleteById(id);
    }

    /**
     * Search for the company corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Company> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Companies for query {}", query);
        return companySearchRepository.search(queryStringQuery("*" + query + "*").analyzeWildcard(true), pageable);
    }
}
