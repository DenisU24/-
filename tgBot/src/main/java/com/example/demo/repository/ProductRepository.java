package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "products", path = "products")
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long id);

    @Query("select op.product from OrderProduct op " +
            "join op.clientOrder co " +
            "where co.client.id = :clientId")
    List<Product> findProductsByClientId(@Param("clientId") Long clientId);

    @Query("SELECT op.product FROM OrderProduct op " +
            "GROUP BY op.product " +
            "ORDER BY SUM(op.countProduct) DESC")
    List<Product> findTopPopularProducts(Pageable pageable);
}
