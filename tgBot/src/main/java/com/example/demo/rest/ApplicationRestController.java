package com.example.demo.rest;

import com.example.demo.entity.ClientOrder;
import com.example.demo.entity.Product;
import com.example.demo.service.EntitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApplicationRestController {
    private final EntitiesService entitiesService;

    @Autowired
    public ApplicationRestController(EntitiesService entitiesService) {
        this.entitiesService = entitiesService;
    }

    @GetMapping("/rest/products/search")
    public List<Product> getProductsByCategoryId(@RequestParam Long categoryId) {
        return entitiesService.getProductsByCategoryId(categoryId);
    }

    @GetMapping("/rest/clients/{id}/orders")
    public List<ClientOrder> getClientOrders(@PathVariable Long id) {
        return entitiesService.getClientOrders(id);
    }

    @GetMapping("/rest/clients/{id}/products")
    public List<Product> getClientProducts(@PathVariable Long id) {
        return entitiesService.getClientProducts(id);
    }

    @GetMapping("/rest/products/popular")
    public List<Product> getTopPopularProducts(@RequestParam Integer limit) {
        return entitiesService.getTopPopularProducts(limit);
    }

}
