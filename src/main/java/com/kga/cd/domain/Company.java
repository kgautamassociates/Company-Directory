package com.kga.cd.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Company details
 */
@ApiModel(description = "Company details")
@Entity
@Table(name = "company")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "company")
public class Company implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "cin", nullable = false)
    private String cin;

    @Column(name = "name")
    private String name;

    @Column(name = "registered_address")
    private String registeredAddress;

    @Column(name = "date_of_incorporation")
    private LocalDate dateOfIncorporation;

    @Column(name = "authorised_capital")
    private Long authorisedCapital;

    @Column(name = "paid_up_capital")
    private Long paidUpCapital;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "date_of_last_agm")
    private LocalDate dateOfLastAGM;

    @Column(name = "date_of_balance_sheet")
    private LocalDate dateOfBalanceSheet;

    @Column(name = "company_status")
    private String companyStatus;

    @Column(name = "roc_code")
    private String rocCode;

    @OneToMany(mappedBy = "company")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Director> directors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Company id(Long id) {
        this.id = id;
        return this;
    }

    public String getCin() {
        return this.cin;
    }

    public Company cin(String cin) {
        this.cin = cin;
        return this;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getName() {
        return this.name;
    }

    public Company name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegisteredAddress() {
        return this.registeredAddress;
    }

    public Company registeredAddress(String registeredAddress) {
        this.registeredAddress = registeredAddress;
        return this;
    }

    public void setRegisteredAddress(String registeredAddress) {
        this.registeredAddress = registeredAddress;
    }

    public LocalDate getDateOfIncorporation() {
        return this.dateOfIncorporation;
    }

    public Company dateOfIncorporation(LocalDate dateOfIncorporation) {
        this.dateOfIncorporation = dateOfIncorporation;
        return this;
    }

    public void setDateOfIncorporation(LocalDate dateOfIncorporation) {
        this.dateOfIncorporation = dateOfIncorporation;
    }

    public Long getAuthorisedCapital() {
        return this.authorisedCapital;
    }

    public Company authorisedCapital(Long authorisedCapital) {
        this.authorisedCapital = authorisedCapital;
        return this;
    }

    public void setAuthorisedCapital(Long authorisedCapital) {
        this.authorisedCapital = authorisedCapital;
    }

    public Long getPaidUpCapital() {
        return this.paidUpCapital;
    }

    public Company paidUpCapital(Long paidUpCapital) {
        this.paidUpCapital = paidUpCapital;
        return this;
    }

    public void setPaidUpCapital(Long paidUpCapital) {
        this.paidUpCapital = paidUpCapital;
    }

    public String getEmailId() {
        return this.emailId;
    }

    public Company emailId(String emailId) {
        this.emailId = emailId;
        return this;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public LocalDate getDateOfLastAGM() {
        return this.dateOfLastAGM;
    }

    public Company dateOfLastAGM(LocalDate dateOfLastAGM) {
        this.dateOfLastAGM = dateOfLastAGM;
        return this;
    }

    public void setDateOfLastAGM(LocalDate dateOfLastAGM) {
        this.dateOfLastAGM = dateOfLastAGM;
    }

    public LocalDate getDateOfBalanceSheet() {
        return this.dateOfBalanceSheet;
    }

    public Company dateOfBalanceSheet(LocalDate dateOfBalanceSheet) {
        this.dateOfBalanceSheet = dateOfBalanceSheet;
        return this;
    }

    public void setDateOfBalanceSheet(LocalDate dateOfBalanceSheet) {
        this.dateOfBalanceSheet = dateOfBalanceSheet;
    }

    public String getCompanyStatus() {
        return this.companyStatus;
    }

    public Company companyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
        return this;
    }

    public void setCompanyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
    }

    public String getRocCode() {
        return this.rocCode;
    }

    public Company rocCode(String rocCode) {
        this.rocCode = rocCode;
        return this;
    }

    public void setRocCode(String rocCode) {
        this.rocCode = rocCode;
    }

    public Set<Director> getDirectors() {
        return this.directors;
    }

    public Company directors(Set<Director> directors) {
        this.setDirectors(directors);
        return this;
    }

    public Company addDirectors(Director director) {
        this.directors.add(director);
        director.setCompany(this);
        return this;
    }

    public Company removeDirectors(Director director) {
        this.directors.remove(director);
        director.setCompany(null);
        return this;
    }

    public void setDirectors(Set<Director> directors) {
        if (this.directors != null) {
            this.directors.forEach(i -> i.setCompany(null));
        }
        if (directors != null) {
            directors.forEach(i -> i.setCompany(this));
        }
        this.directors = directors;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Company)) {
            return false;
        }
        return id != null && id.equals(((Company) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Company{" +
            "id=" + getId() +
            ", cin='" + getCin() + "'" +
            ", name='" + getName() + "'" +
            ", registeredAddress='" + getRegisteredAddress() + "'" +
            ", dateOfIncorporation='" + getDateOfIncorporation() + "'" +
            ", authorisedCapital=" + getAuthorisedCapital() +
            ", paidUpCapital=" + getPaidUpCapital() +
            ", emailId='" + getEmailId() + "'" +
            ", dateOfLastAGM='" + getDateOfLastAGM() + "'" +
            ", dateOfBalanceSheet='" + getDateOfBalanceSheet() + "'" +
            ", companyStatus='" + getCompanyStatus() + "'" +
            ", rocCode='" + getRocCode() + "'" +
            ", directors='" + getDirectors() + "'" +
            "}";
    }
}
