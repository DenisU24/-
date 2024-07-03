package com.example.demo.repository;

import com.example.demo.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;


@RepositoryRestResource(collectionResourceRel =
        "clients", path = "clients")

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByExternalId(Long externalId);
}
