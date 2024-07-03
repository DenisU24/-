package com.example.demo;

import com.example.demo.entity.Client;
import com.example.demo.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FillTest
{
    @Autowired
    private ClientRepository clientRepository;
    @Test
    void createTwoClients(){
        Client client1 = new Client();
        client1.setAddress("address1");
        client1.setExternalId(1L);
        client1.setFullName("fullName1");
        client1.setPhoneNumber("1234567890");
        clientRepository.save(client1);
        Client client2 = new Client();
        client2.setAddress("address1");
        client2.setExternalId(1L);
        client2.setFullName("fullName1");
        client2.setPhoneNumber("0987654321");
        clientRepository.save(client2);
    }
}



