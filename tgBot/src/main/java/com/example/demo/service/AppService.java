package com.example.demo.service;


import com.example.demo.entity.Client;
import com.example.demo.entity.ClientOrder;
import com.example.demo.entity.Product;

import java.util.List;

import com.example.demo.entity.*;

import java.util.List;
import java.util.Optional;


/**
 * Сервис для работы с сущностями телеграмм-бота
 */
public interface AppService
{
    /**
     * Получить список товаров в категории
     * @param id идентификатор категории
     */
    List<Product> getProductsByCategoryId(Long id);
    /**
     * Получить список заказов клиента
     * @param id идентификатор клиента
     */
    List<ClientOrder> getClientOrders(Long id);
    /**
     * Получить список всех товаров во всех заказах клиента
     * @param id идентификатор клиента
     */
    List<Product> getClientProducts(Long id);
    /**
     * Получить указанное кол-во самых популярных (наибольшее
     * количество штук в заказах) товаров среди клиентов
     * @param limit максимальное кол-во товаров
     */
    List<Product> getTopPopularProducts(Integer limit);
    /**
     * Найти всех клиентов по подстроке имени
     * @param name подстрока имени клиента
     */
    default List<Client> searchClientsByName(String name) {
        throw new UnsupportedOperationException("Доп. задание");
    }
    /**
     * Найти все продукты по подстроке названия
     * @param name подстрока названия продукта
     */
    default List<Product> searchProductsByName(String name) {
        throw new UnsupportedOperationException("Доп. задание");
    }

    Optional<Client> findClientByExternalId(Long externalId);

    Client saveNewClient(Long chatId, String fullName);

    Client updateExistingClient(Client client);

    ClientOrder saveClientOrder(ClientOrder clientOrder);

    List<Category> getMainCategories();

    List<Category> getSubCategories(Long parentId);

    Optional<Category> findCategoryByName(String name);

    List<OrderProduct> getProductsByClientOrder(Long orderId);

}
