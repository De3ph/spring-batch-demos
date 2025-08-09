package org.hamit.batchdemo.batch.orderProduct.writers;

import lombok.RequiredArgsConstructor;
import org.hamit.batchdemo.dao.entity.Order;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.core.io.FileSystemResource;

import java.time.LocalDate;

@RequiredArgsConstructor
public class OrderReportWriter {

    public JsonFileItemWriter<Order> writer() {
        final String orderFileName = "orders" + LocalDate.now() + ".json";
        final String path = "D:/Dev/Java/Spring Boot/projects/batch-demo/generatedOrderReports" + orderFileName;
        FileSystemResource fileSystemResource = new FileSystemResource(path);

        return new JsonFileItemWriterBuilder<Order>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(fileSystemResource)
                .name("ordersJsonFileItemWriter")
                .build();
    }
}
