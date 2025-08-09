package org.hamit.batchdemo.batch.orderProduct.steps;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hamit.batchdemo.batch.base.BaseAbstractStep;
import org.hamit.batchdemo.batch.orderProduct.writers.OrderProductWriter;
import org.hamit.batchdemo.dao.entity.Order;
import org.hamit.batchdemo.dao.entity.Product;
import org.hamit.batchdemo.dao.repository.OrderRepository;
import org.hamit.batchdemo.dao.repository.ProductRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class OrderStep extends BaseAbstractStep<Product, Order> {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public RepositoryItemReader<Product> getReader(Integer PAGE_SIZE) {
        RepositoryItemReader<Product> reader = new RepositoryItemReader<>();
        reader.setRepository(productRepository);
        reader.setSort(Map.of("id", Sort.Direction.ASC));
        reader.setMethodName("findAllByQuantityLessThan");
        reader.setPageSize(PAGE_SIZE);
        reader.setArguments(List.of(100));
        return reader;
    }

    @Override
    public ItemProcessor<Product, Order> getProcessor() {
        return item -> {
            log.info("ITEM QUANTITY BELOW 100, CURRENT QUANTITY: {} , ID : {}", item.getQuantity(), item.getId());
            Order order = new Order();
            order.addProduct(item);
            return order;
        };
    }

    @Override
    public ItemWriter<Order> getWriter() {
        return new OrderProductWriter(productRepository, orderRepository);
    }
}
