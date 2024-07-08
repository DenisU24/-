package com.example.demo;

import com.example.demo.entity.Category;
import com.example.demo.entity.Client;
import com.example.demo.entity.ClientOrder;
import com.example.demo.entity.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ClientOrderRepository;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.EntitiesService;
import com.example.demo.service.ServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class ServiceTest {

    @Autowired
    private EntitiesService entitiesService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientOrderRepository clientOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    Client client1 = new Client();
    Client client2 = new Client();
    Category category1 = new Category();
    Category category2 = new Category();
    Product product1 = new Product();
    Product product2 = new Product();
    ClientOrder order1 = new ClientOrder();
    ClientOrder order2 = new ClientOrder();
    @Test
    public void setUp() {

        //Создание клиентов

        client1.setFullName("Иван Иванов");
        client1.setExternalId(1L);
        client1.setPhoneNumber("1234567890");
        client1.setAddress("Адрес 1");

        client2.setFullName("Петр Петров");
        client2.setExternalId(1L);
        client2.setPhoneNumber("0987654321");
        client2.setAddress("Адрес 2");

        // Сохранение клиентов
        clientRepository.save(client1);
        clientRepository.save(client2);

        category1.setName("Пицца");

        categoryRepository.save(category1);

        // Создание продуктов
        product1.setCategory(category1);
        product1.setName("Продукт 1");
        product1.setDescription("Описание продукта 1");
        product1.setPrice(new BigDecimal("100.00"));

        category2.setName("Роллы");

        categoryRepository.save(category2);

        product2.setCategory(category2);
        product2.setName("Продукт 2");
        product2.setDescription("Описание продукта 2");
        product2.setPrice(new BigDecimal("200.00"));

        // Сохранение продуктов
        productRepository.save(product1);
        productRepository.save(product2);

        // Создание заказов
        order1.setClient(client1);
        order1.setStatus(1);
        order1.setTotal(new BigDecimal("300.00"));


        order2.setClient(client2);
        order2.setStatus(2);
        order2.setTotal(new BigDecimal("400.00"));

        // Сохранение заказов
        clientOrderRepository.save(order1);
        clientOrderRepository.save(order2);
    }
}
