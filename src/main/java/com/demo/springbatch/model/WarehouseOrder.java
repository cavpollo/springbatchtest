package com.demo.springbatch.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "warehouse_orders")
public class WarehouseOrder {
    @Id
    private Long id;

    private String customerOrderReference;

    private String transporterName;

    private ZonedDateTime deliveredAt;

    private ZonedDateTime createdAt;
}
