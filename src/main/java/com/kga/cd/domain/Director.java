package com.kga.cd.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Director.
 */
@Entity
@Table(name = "director")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "director")
public class Director implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "din")
    private String din;

    @Column(name = "name")
    private String name;

    @Column(name = "begin_date")
    private LocalDate beginDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne
    @JsonIgnoreProperties(value = { "directors" }, allowSetters = true)
    private Company company;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Director id(Long id) {
        this.id = id;
        return this;
    }

    public String getDin() {
        return this.din;
    }

    public Director din(String din) {
        this.din = din;
        return this;
    }

    public void setDin(String din) {
        this.din = din;
    }

    public String getName() {
        return this.name;
    }

    public Director name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBeginDate() {
        return this.beginDate;
    }

    public Director beginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
        return this;
    }

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public Director endDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Company getCompany() {
        return this.company;
    }

    public Director company(Company company) {
        this.setCompany(company);
        return this;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Director)) {
            return false;
        }
        return id != null && id.equals(((Director) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Director{" +
            "id=" + getId() +
            ", din='" + getDin() + "'" +
            ", name='" + getName() + "'" +
            ", beginDate='" + getBeginDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }
}
