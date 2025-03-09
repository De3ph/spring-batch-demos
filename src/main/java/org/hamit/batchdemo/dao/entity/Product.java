package org.hamit.batchdemo.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PRODUCTS")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "NAME",length = 100)
    private String name;
    @Column(name = "DESCRIPTION",length = 100)
    private String description;
    @Column(name = "PRICE", scale = 2)
    private Double price;
    @Column(name = "QUANTITY")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private Order orderId;
}