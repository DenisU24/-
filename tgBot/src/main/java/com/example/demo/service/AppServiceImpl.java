package com.example.demo.service;

import com.example.demo.entity.Client;
import com.example.demo.entity.ClientOrder;
import com.example.demo.entity.Product;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class AppServiceImpl implements AppService {

    private final CategoryRepository categoryRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    @Autowired
    public AppServiceImpl(CategoryRepository categoryRepository,

                              ClientOrderRepository clientOrderRepository,
                              ClientRepository clientRepository,
                              ProductRepository productRepository,
                              OrderProductRepository orderProductRepository)
                           {
        this.categoryRepository = categoryRepository;
        this.clientOrderRepository = clientOrderRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
    }

    @Override
    public List<Product> getProductsByCategoryId(Long id) {
        return productRepository.findByCategoryId(id);
    }

    @Override
    public List<ClientOrder> getClientOrders(Long id) {
        return clientOrderRepository.findByClientId(id);
    }

    @Override
    public List<Product> getClientProducts(Long id) {
        return productRepository.findProductsByClientId(id);
    }

    @Override
    public List<Product> getTopPopularProducts(Integer limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findTopPopularProducts(pageable);
    }

    @Override
    public Optional<Client> findClientByExternalId(Long externalId) {
        return clientRepository.findByExternalId(externalId);
    }

    @Override
    public Client saveNewClient(Long chatId, String fullName) {
        Client client = new Client();
        client.setExternalId(chatId);
        client.setFullName(fullName);
        client.setPhoneNumber("Не указано");
        client.setAddress("Не указано");

        return saveClient(client); // Сохраняем нового клиента
    }

    @Override
    public Client updateExistingClient(Client client) {
        // Обновляем существующего клиента
        return saveClient(client);
    }

    // Метод для сохранения клиента (нового или обновленного)
    private Client saveClient(Client client) {
        // Логика сохранения клиента в базе данных
        // Например, с использованием репозитория:
        return clientRepository.save(client);
    }

    @Override
    public ClientOrder saveClientOrder(ClientOrder clientOrder) {
        return clientOrderRepository.save(clientOrder);
    }

    @Override
    public List<Category> getMainCategories() {
        return categoryRepository.findByParentId(0L);
    }

    @Override
    public List<Category> getSubCategories(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    @Override
    public Optional<Category> findCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public List<OrderProduct> getProductsByClientOrder(Long orderId) {
        return orderProductRepository.findByClientOrderId(orderId);
    }
}
