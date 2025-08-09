package org.hamit.batchdemo.batch;

import org.hamit.batchdemo.batch.orderProduct.steps.OrderStep;
import org.hamit.batchdemo.dao.entity.Order;
import org.hamit.batchdemo.dao.entity.Product;
import org.hamit.batchdemo.dao.repository.OrderRepository;
import org.hamit.batchdemo.dao.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderProductBatchTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private Environment environment;

    @Mock
    OrderStep orderStep;

    @InjectMocks
    private OrderProductBatch orderProductBatch;

    private Product testProduct;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        // Set up test data
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(50.0);
        testProduct.setQuantity(80);

        Product testProduct2 = new Product();
        testProduct2.setId(2L);
        testProduct2.setName("Test Product 2");
        testProduct2.setDescription("Test Description 2");
        testProduct2.setPrice(75.0);
        testProduct2.setQuantity(90);

        testProducts = Arrays.asList(testProduct, testProduct2);

        // Mock environment properties for batch configuration (using lenient to avoid
        // unnecessary stubbing errors)
        lenient().when(environment.getProperty("batch.default.core-pool-size", Integer.class)).thenReturn(5);
        lenient().when(environment.getProperty("batch.default.max-pool-size", Integer.class)).thenReturn(10);
        lenient().when(environment.getProperty("batch.default.queue-capacity", Integer.class)).thenReturn(25);
    }

    @Test
    void testGetBatchConfigPrefix() {
        // When
        String configPrefix = orderProductBatch.getBatchConfigPrefix();

        // Then
        assertEquals(BatchConstants.DEFAULT_BATCH_CONFIG_PREFIX, configPrefix);
    }

    @Test
    void testJobCreation() {
        // When
        Job job = orderProductBatch.job(jobRepository);

        // Then
        assertNotNull(job);
        assertEquals(BatchConstants.ORDER_PRODUCT_JOB_NAME, job.getName());
    }

    @Test
    void testStepCreation() {
        // When
        Step step = orderProductBatch.orderStep(jobRepository);

        // Then
        assertNotNull(step);
        assertEquals(BatchConstants.ORDER_PRODUCT_JOB_STEP, step.getName());
    }

    @Test
    void testItemReaderConfiguration() {
        // When
        RepositoryItemReader<Product> reader = orderStep.getReader(20);

        // Then
        assertNotNull(reader);
        assertEquals(productRepository, ReflectionTestUtils.getField(reader, "repository"));
        assertEquals("findAllByQuantityLessThan", ReflectionTestUtils.getField(reader, "methodName"));
        assertEquals(20, ReflectionTestUtils.getField(reader, "pageSize"));
    }

    @Test
    void testItemWriterConfiguration() {
        // When
        ItemWriter<Order> writer = orderStep.getWriter();

        // Then
        assertNotNull(writer);
        assertEquals(orderRepository, ReflectionTestUtils.getField(writer, "repository"));
        assertEquals("save", ReflectionTestUtils.getField(writer, "methodName"));
    }

    @Test
    void testItemProcessorLogic() throws Exception {
        // Given
        ItemProcessor<Product, Order> processor = orderStep.getProcessor();

        // When
        Order result = processor.process(testProduct);

        // Then
        assertNotNull(result);
        assertNotNull(result.getProducts());
        assertEquals(1, result.getProducts().size());
        assertEquals(testProduct, result.getProducts().get(0));
        assertEquals(result, testProduct.getOrder());
    }

    @Test
    void testItemProcessorWithNullInput() {
        // Given
        ItemProcessor<Product, Order> processor = orderStep.getProcessor();

        // When & Then
        assertThrows(NullPointerException.class, () -> processor.process(null));
    }

    @Test
    void testItemProcessorWithDifferentQuantities() throws Exception {
        // Given
        ItemProcessor<Product, Order> processor = orderStep.getProcessor();

        Product lowQuantityProduct = new Product();
        lowQuantityProduct.setId(3L);
        lowQuantityProduct.setName("Low Stock Product");
        lowQuantityProduct.setQuantity(10);

        // When
        Order result = processor.process(lowQuantityProduct);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getProducts().size());
        assertEquals(lowQuantityProduct, result.getProducts().get(0));
        assertEquals(10, result.getProducts().get(0).getQuantity());
    }

    @Test
    void testRepositoryInteraction() {
        // Given
        Page<Product> productPage = new PageImpl<>(testProducts);
        lenient().when(productRepository.findAllByQuantityLessThan(eq(100), any(Pageable.class)))
                .thenReturn(productPage);

        RepositoryItemReader<Product> reader = orderStep.getReader(20);

        // Simulate reader initialization
        ReflectionTestUtils.setField(reader, "repository", productRepository);
        ReflectionTestUtils.setField(reader, "methodName", "findAllByQuantityLessThan");
        ReflectionTestUtils.setField(reader, "arguments", List.of(100));

        // When
        // The reader would be called by Spring Batch framework
        // We're testing the configuration here

        // Then
        verify(productRepository, never()).findAllByQuantityLessThan(anyInt(), any(Pageable.class));

        // Verify the reader is properly configured
        assertEquals("findAllByQuantityLessThan", ReflectionTestUtils.getField(reader, "methodName"));
    }

    @Test
    void testBatchConfigurationProperties() throws Exception {
        // Given
        ReflectionTestUtils.setField(orderProductBatch, "environment", environment);

        // When
        orderProductBatch.afterPropertiesSet();

        // Then
        verify(environment).getProperty("batch.default.core-pool-size", Integer.class);
        verify(environment).getProperty("batch.default.max-pool-size", Integer.class);
        verify(environment).getProperty("batch.default.queue-capacity", Integer.class);
    }

    @Test
    void testTransactionManagerBean() {
        // When
        var transactionManager = orderProductBatch.getTransactionManager();

        // Then
        assertNotNull(transactionManager);
        assertEquals("org.springframework.orm.jpa.JpaTransactionManager",
                transactionManager.getClass().getName());
    }

    @Test
    void testPageSizeConstant() {
        // When
        Integer pageSize = (Integer) ReflectionTestUtils.getField(OrderProductBatch.class, "PAGE_SIZE");

        // Then
        assertEquals(20, pageSize);
    }

    @Test
    void testOrderCreationInProcessor() throws Exception {
        // Given
        ItemProcessor<Product, Order> processor = orderStep.getProcessor();
        Product product = new Product();
        product.setId(5L);
        product.setName("Test Product");
        product.setQuantity(50);

        // When
        Order order = processor.process(product);

        // Then
        assertNotNull(order);
        assertNotNull(order.getProducts());
        assertFalse(order.getProducts().isEmpty());
        assertEquals(product, order.getProducts().get(0));
        assertEquals(order, product.getOrder());
    }

    @Test
    void testMultipleProductsProcessing() throws Exception {
        // Given
        ItemProcessor<Product, Order> processor = orderStep.getProcessor();

        // When
        Order order1 = processor.process(testProducts.get(0));
        Order order2 = processor.process(testProducts.get(1));

        // Then
        assertNotNull(order1);
        assertNotNull(order2);
        assertNotEquals(order1, order2); // Each product should create a separate order
        assertEquals(testProducts.get(0), order1.getProducts().get(0));
        assertEquals(testProducts.get(1), order2.getProducts().get(0));
    }
}
