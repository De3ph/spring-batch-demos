package org.hamit.batchdemo.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ORDERS")
public class Order {

    @SequenceGenerator(name = "orders_seq", sequenceName = "orders_seq", allocationSize = 1)
    @GeneratedValue(generator = "orders_seq")
    @Id
    private Long id;

    @OneToMany(mappedBy = "orderId")
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product){
        products.add(product);
        product.setOrderId(this);
    }

}
