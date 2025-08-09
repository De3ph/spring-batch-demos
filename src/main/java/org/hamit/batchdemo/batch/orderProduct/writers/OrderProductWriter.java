package org.hamit.batchdemo.batch.orderProduct.writers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hamit.batchdemo.dao.entity.Order;
import org.hamit.batchdemo.dao.entity.Product;
import org.hamit.batchdemo.dao.repository.OrderRepository;
import org.hamit.batchdemo.dao.repository.ProductRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class OrderProductWriter implements ItemWriter<Order> {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void write(@NonNull Chunk<? extends Order> chunk) {
        for (Order order : chunk.getItems()) {
            // 1) Persist the Order first
            Order managedOrder = orderRepository.save(order);

            // 2) Re-link all products to the managed order and persist them
            if (order.getProducts() != null) {
                for (Product product : order.getProducts()) {
                    // keep both sides in sync
                    product.setOrder(managedOrder);
                }
                // Explicitly save products so the FK ORDER_ID is updated
                productRepository.saveAll(order.getProducts());
            }
        }
        // Let the transaction flush at the end of the chunk
    }
}
