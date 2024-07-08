package com.example.demo.repository;

import com.example.demo.entity.OrderProduct;
import com.example.demo.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "orderProducts", path = "orderProducts")
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    @Query("SELECT op.product FROM OrderProduct op GROUP BY op.product ORDER BY COUNT(op.product) DESC")
    List<Product> findTopPopularProducts(Pageable pageable);

}
