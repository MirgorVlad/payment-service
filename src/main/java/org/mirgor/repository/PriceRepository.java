package org.mirgor.repository;

import org.mirgor.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    boolean existsById(Long id);
}
