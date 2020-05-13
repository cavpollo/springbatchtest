package com.demo.springbatch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "warehouse_orders")
public class WarehouseOrder {
    @Id
    private Long id;

    private String customerOrderReference;

    private String transporterCode;

    private Integer quantity;

    private LocalDateTime deliveredAt;

    private LocalDateTime createdAt;
}
