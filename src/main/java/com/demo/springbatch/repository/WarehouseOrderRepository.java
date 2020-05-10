package com.demo.springbatch.repository;

import com.demo.springbatch.model.WarehouseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseOrderRepository extends JpaRepository<WarehouseOrder, Long> {
}
