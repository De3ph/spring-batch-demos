package org.hamit.batchdemo.dao.repository;

import org.hamit.batchdemo.dao.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
}
