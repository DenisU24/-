package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
public class ServiceTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ClientOrderRepository clientOrderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Test
    public void setup() {
        // Создаем тестовые данные
        for (int i = 0; i < 5; i++) {
            Category category = new Category();
            category.setName("TestCategory" + i);
            category = categoryRepository.save(category);

            Product product = new Product();
            product.setName("TestProduct" + i);
            product.setDescription("TestDescription" + i);
            product.setPrice(BigDecimal.valueOf(100 + i));
            product.setCategory(category);
            product = productRepository.save(product);

            Client client = new Client();
            client.setFullName("TestClient" + i);
            client.setExternalId(1L + i);
            client.setPhoneNumber("123456789" + i);
            client.setAddress("TestAddress" + i);
            client = clientRepository.save(client);

            ClientOrder clientOrder = new ClientOrder();
            clientOrder.setClient(client);
            clientOrder.setStatus(1);
            clientOrder.setTotal(BigDecimal.valueOf(100 + i));
            clientOrder = clientOrderRepository.save(clientOrder);

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setClientOrder(clientOrder);
            orderProduct.setProduct(product);
            orderProduct.setCountProduct(5 - i);
            orderProductRepository.save(orderProduct);
        }
    }
}
