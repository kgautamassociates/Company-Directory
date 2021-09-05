package com.kga.cd.repository;

import com.kga.cd.domain.Director;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Director entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {}
