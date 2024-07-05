package com.example.demo.repository;

import com.example.demo.entity.ClientOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "orders", path = "orders")
public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {
}
