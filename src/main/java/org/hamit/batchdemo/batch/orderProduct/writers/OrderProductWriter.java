package org.hamit.batchdemo.batch.orderProduct.writers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hamit.batchdemo.dao.entity.Order;
import org.hamit.batchdemo.dao.repository.OrderRepository;
import org.hamit.batchdemo.dao.repository.ProductRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class OrderProductWriter implements ItemWriter<Order> {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void write(@NonNull Chunk<? extends Order> chunk) {
        List<? extends Order> persistedOrders = orderRepository.saveAll(chunk.getItems());
        for (Order order : persistedOrders) {
            // 2) Re-link all products to the managed order and persist them
            if (order.getProducts() != null) {
                // keep both sides in sync
                order.getProducts().forEach(product -> product.setOrder(order));
                // Explicitly save products so the FK ORDER_ID is updated
            }
        }
        productRepository.saveAll(persistedOrders.stream().flatMap(order -> order.getProducts().stream()).toList());
        // Let the transaction flush at the end of the chunk
    }
}
