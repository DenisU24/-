package com.example.demo.rest;

import com.example.demo.entity.ClientOrder;
import com.example.demo.entity.Product;
import com.example.demo.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApplicationRestController {
    private final AppService appService;

    @Autowired
    public ApplicationRestController(AppService appService) {
        this.appService = appService;
    }

    @GetMapping("/rest/products/search")
    public List<Product> getProductsByCategoryId(@RequestParam Long categoryId) {
        return appService.getProductsByCategoryId(categoryId);
    }

    @GetMapping("/rest/clients/{id}/orders")
    public List<ClientOrder> getClientOrders(@PathVariable Long id) {
        return appService.getClientOrders(id);
    }

    @GetMapping("/rest/clients/{id}/products")
    public List<Product> getClientProducts(@PathVariable Long id) {
        return appService.getClientProducts(id);
    }

    @GetMapping("/rest/products/popular")
    public List<Product> getTopPopularProducts(@RequestParam Integer limit) {
        return appService.getTopPopularProducts(limit);
    }
}
