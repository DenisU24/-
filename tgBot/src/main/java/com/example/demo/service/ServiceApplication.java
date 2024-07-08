package com.example.demo.service;

import com.example.demo.entity.Client;
import com.example.demo.entity.ClientOrder;
import com.example.demo.entity.Product;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ServiceApplication implements EntitiesService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ClientOrderRepository clientOrderRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

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
    List<Object[]> results = productRepository.findTopPopularProducts(pageable);
    List<Product> products = new ArrayList<>();
    for (Object[] result : results) {
        products.add((Product) result[0]);
    }
    return products;
}


    @Override
    public List<Client> searchClientsByName(String name) {
        return clientRepository.findByFullNameContaining(name);
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContaining(name);
    }

}
