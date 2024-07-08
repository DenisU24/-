package com.example.demo;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.math.BigDecimal;

@SpringBootTest
public class FillingTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void createCategories() {

        //Создание основных категорий
        Category pizza = new Category();
        pizza.setName("Пицца");
        categoryRepository.save(pizza);
        createProductsForCategory(pizza);

        Category rolls = new Category();
        rolls.setName("Роллы");
        categoryRepository.save(rolls);
        createProductsForCategory(rolls);

        Category burgers = new Category();
        burgers.setName("Бургеры");
        categoryRepository.save(burgers);
        createProductsForCategory(burgers);

        Category drinks = new Category();
        drinks.setName("Напитки");
        categoryRepository.save(drinks);
        createProductsForCategory(drinks);

        //Создание подкатегорий для Роллы
        Category classicRolls = new Category();
        classicRolls.setName("Классические роллы");
        classicRolls.setParent(rolls);
        categoryRepository.save(classicRolls);
        createProductsForCategory(classicRolls);

        Category bakedRolls = new Category();
        bakedRolls.setName("Запеченные роллы");
        bakedRolls.setParent(rolls);
        categoryRepository.save(bakedRolls);
        createProductsForCategory(bakedRolls);

        Category sweetRolls = new Category();
        sweetRolls.setName("Сладкие роллы");
        sweetRolls.setParent(rolls);
        categoryRepository.save(sweetRolls);
        createProductsForCategory(sweetRolls);

        Category sets = new Category();
        sets.setName("Наборы");
        sets.setParent(rolls);
        categoryRepository.save(sets);
        createProductsForCategory(sets);

        //Создание подкатегорий для Бургеров
        Category classicBurgers = new Category();
        classicBurgers.setName("Классические бургеры");
        classicBurgers.setParent(burgers);
        categoryRepository.save(classicBurgers);
        createProductsForCategory(classicBurgers);

        Category spicyBurgers = new Category();
        spicyBurgers.setName("Острые бургеры");
        spicyBurgers.setParent(burgers);
        categoryRepository.save(spicyBurgers);
        createProductsForCategory(spicyBurgers);

        //Создание подкатегорий для Напитков
        Category carbonatedDrinks = new Category();
        carbonatedDrinks.setName("Газированные напитки");
        carbonatedDrinks.setParent(drinks);
        categoryRepository.save(carbonatedDrinks);
        createProductsForCategory(carbonatedDrinks);

        Category energyDrinks = new Category();
        energyDrinks.setName("Энергетические напитки");
        energyDrinks.setParent(drinks);
        categoryRepository.save(energyDrinks);
        createProductsForCategory(energyDrinks);

        Category juices = new Category();
        juices.setName("Соки");
        juices.setParent(drinks);
        categoryRepository.save(juices);
        createProductsForCategory(juices);

        Category other = new Category();
        other.setName("Другие");
        other.setParent(drinks);
        categoryRepository.save(other);
        createProductsForCategory(other);
    }

    void createProductsForCategory(Category category){
        for (int i = 1; i<=3; i++){
            Product product = new Product();
            product.setName(category.getName() + "продукт" + i);
            product.setDescription("Описание для " + product.getName());
            product.setPrice(new BigDecimal(100.0 *i));
            product.setCategory(category);
            productRepository.save(product);
        }
    }
}