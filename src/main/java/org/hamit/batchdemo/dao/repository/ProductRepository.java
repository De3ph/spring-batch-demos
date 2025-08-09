package org.hamit.batchdemo.dao.repository;

import org.hamit.batchdemo.dao.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByQuantityLessThan(Integer quantityIsLessThan, Pageable pageable);
}
