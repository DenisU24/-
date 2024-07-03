package com.example.demo;

import com.example.demo.entity.Category;
import com.example.demo.entity.Client;
import com.example.demo.entity.ClientOrder;
import com.example.demo.entity.OrderProduct;
import com.example.demo.entity.Product;
import com.example.demo.service.AppService;
import com.example.demo.repository.OrderProductRepository;
import com.example.demo.repository.ProductRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class TelegramBotService {

    private final AppService appService;
    private final TelegramBot bot;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;
    private final Map<Long, Integer> userStates = new HashMap<>();
    private final Map<Long, Integer> userOrders = new HashMap<>();

    @Autowired
    public TelegramBotService(AppService appService, TelegramBot bot,
                              ProductRepository productRepository, OrderProductRepository orderProductRepository) {
        this.appService = appService;
        this.bot = bot;
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
    }

    @PostConstruct
    public void createConnection() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void processUpdate(Update update) {
        if (update.message() != null) {
            processMessage(update.message());
        } else if (update.callbackQuery() != null) {
            processCallbackQuery(update.callbackQuery());
        }
    }

    private void processMessage(Message message) {
        Long chatId = message.chat().id();
        String text = message.text();

        // Проверка, находится ли пользователь на стадии ввода данных
        if (userStates.containsKey(chatId)) {
            int state = userStates.get(chatId);
            Optional<Client> clientOptional = appService.findClientByExternalId(chatId);
            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                if (state == 0) {
                    client.setFullName(text);
                    bot.execute(new SendMessage(chatId, "Введите ваш номер телефона:"));
                    userStates.put(chatId, 1);
                } else if (state == 1) {
                    client.setPhoneNumber(text);
                    bot.execute(new SendMessage(chatId, "Введите ваш адрес:"));
                    userStates.put(chatId, 2);
                } else if (state == 2) {
                    client.setAddress(text);
                    appService.saveClient(client);
                    bot.execute(new SendMessage(chatId, "Спасибо! Ваши данные сохранены."));
                    sendMainMenu(chatId);
                    userStates.remove(chatId);
                }
            } else {
                bot.execute(new SendMessage(chatId, "Что-то пошло не так. Пожалуйста, попробуйте еще раз позже."));
            }
        } else {
            // Обработка сообщений
            if (text != null) {
                switch (text) {
                    case "/start":
                        handleStartCommand(chatId, message);
                        break;
                    case "Оформить заказ":
                        handlePlaceOrder(chatId);
                        break;
                    case "В основное меню":
                        sendMainMenu(chatId);
                        break;
                    default:
                        handleCategorySelection(chatId, text);
                        break;
                }
            }
        }
    }

    private void handleStartCommand(Long chatId, Message message) {
        String fullName = message.from().firstName();

        // Проверка, существует ли клиент
        Optional<Client> clientOptional = appService.findClientByExternalId(chatId);
        Client client;
        if (clientOptional.isEmpty()) {
            // Создание нового клиента
            client = new Client();
            client.setExternalId(chatId);
            client.setFullName(fullName);
            client.setPhoneNumber("Не указано");
            client.setAddress("Не указано");

            client = appService.saveClient(client);

            // Приветственное сообщение
            bot.execute(new SendMessage(chatId, "Добро пожаловать, " + fullName + "! Пожалуйста, введите свои данные для создания заказа."));
            bot.execute(new SendMessage(chatId, "Введите ваш полное имя:"));

            userStates.put(chatId, 0);
        } else {
            client = clientOptional.get();
            sendMainMenu(chatId);
        }
    }

    private void handlePlaceOrder(Long chatId) {
        Optional<Client> clientOptional = appService.findClientByExternalId(chatId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            List<OrderProduct> orderProducts = getOrderProductsForActiveOrder(client);

            if (!orderProducts.isEmpty()) {
                BigDecimal totalAmount = calculateTotalAmount(orderProducts);
                closeActiveOrder(client);

                String orderSummary = getOrderSummaryMessage(orderProducts, totalAmount);

                int orderNumber = userOrders.getOrDefault(chatId, 1);

                userOrders.put(chatId, orderNumber + 1);

                String confirmationMessage = "Заказ №" + orderNumber + " подтвержден. Курьер уже едет к Вам по адресу: " + client.getAddress() + ". Приблизительное время доставки 90 мин.";
                bot.execute(new SendMessage(chatId, confirmationMessage + "\n" + orderSummary));
            } else {
                bot.execute(new SendMessage(chatId, "Ваш заказ пуст."));
            }

            createNewActiveOrder(client);
        } else {
            bot.execute(new SendMessage(chatId, "Что-то пошло не так. Пожалуйста, попробуйте еще раз позже."));
        }
    }

    private List<OrderProduct> getOrderProductsForActiveOrder(Client client) {
        List<ClientOrder> orders = appService.getClientOrders(client.getId());
        return orders.stream()
                .filter(order -> order.getStatus() == 1) // Фильтрация активных заказов
                .findFirst()
                .map(order -> appService.getProductsByClientOrder(order.getId()))
                .orElse(new ArrayList<>());
    }

    private BigDecimal calculateTotalAmount(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .map(orderProduct -> orderProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(orderProduct.getCountProduct())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void closeActiveOrder(Client client) {
        List<ClientOrder> orders = appService.getClientOrders(client.getId());
        orders.stream()
                .filter(order -> order.getStatus() == 1)
                .findFirst()
                .ifPresent(order -> {
                    order.setStatus(2); // Статус "Закрыт"
                    appService.saveClientOrder(order);
                });
    }

    private String getOrderSummaryMessage(List<OrderProduct> orderProducts, BigDecimal totalAmount) {
        StringBuilder sb = new StringBuilder();
        sb.append("Товары в заказе:\n");

        // Группировка продуктов по имени и суммирирование их количества
        Map<String, Integer> productCounts = new HashMap<>();
        for (OrderProduct orderProduct : orderProducts) {
            String productName = orderProduct.getProduct().getName();
            int count = productCounts.getOrDefault(productName, 0);
            productCounts.put(productName, count + orderProduct.getCountProduct());
        }

        for (Map.Entry<String, Integer> entry : productCounts.entrySet()) {
            sb.append(entry.getKey())
                    .append(" - ")
                    .append(entry.getValue())
                    .append(" шт.\n");
        }

        sb.append("Итого: ")
                .append(totalAmount)
                .append(" руб.");
        return sb.toString();
    }

    private ClientOrder createNewActiveOrder(Client client) {
        ClientOrder newOrder = new ClientOrder();
        newOrder.setClient(client);
        newOrder.setStatus(1); // Статус "Создан"
        newOrder.setTotal(BigDecimal.ZERO);

        return appService.saveClientOrder(newOrder);
    }

    private ReplyKeyboardMarkup createCategoryKeyboard(List<Category> categories) {
        String[][] buttons = new String[categories.size() + 1][1];
        for (int i = 0; i < categories.size(); i++) {
            buttons[i][0] = categories.get(i).getName();
        }
        buttons[categories.size()][0] = "В основное меню";
        return new ReplyKeyboardMarkup(buttons);
    }

    private void sendMainMenu(Long chatId) {
        List<Category> mainCategories = appService.getMainCategories();
        ReplyKeyboardMarkup keyboard = createMainMenuKeyboard();
        SendMessage message = new SendMessage(chatId, "Выберите категорию:").replyMarkup(keyboard);
        bot.execute(message);
    }

    private ReplyKeyboardMarkup createMainMenuKeyboard() {
        List<Category> mainCategories = appService.getMainCategories();
        String[][] buttons = new String[mainCategories.size() + 5][1];

        for (int i = 0; i < mainCategories.size(); i++) {
            buttons[i][0] = mainCategories.get(i).getName();
        }
        buttons[mainCategories.size()][0] = "Пицца";
        buttons[mainCategories.size() + 1][0] = "Роллы";
        buttons[mainCategories.size() + 2][0] = "Бургеры";
        buttons[mainCategories.size() + 3][0] = "Напитки";
        buttons[mainCategories.size() + 4][0] = "Оформить заказ";

        return new ReplyKeyboardMarkup(buttons);
    }

    private void handleUserInput(Long chatId, String text) {
        Optional<Category> selectedCategoryOptional = appService.findCategoryByName(text);
        if (selectedCategoryOptional.isPresent()) {
            Category selectedCategory = selectedCategoryOptional.get();
            List<Category> subCategories = appService.getSubCategories(selectedCategory.getId());
            List<Product> products = appService.getProductsByCategoryId(selectedCategory.getId());

            if (!subCategories.isEmpty()) {
                ReplyKeyboardMarkup keyboard = createSubCategoryKeyboard(subCategories);
                addBackAndOrderButtons(keyboard);
                SendMessage message = new SendMessage(chatId, "Выберите подкатегорию:").replyMarkup(keyboard);
                bot.execute(message);
            } else if (!products.isEmpty()) {
                ReplyKeyboardMarkup keyboard = createProductKeyboard(products);
                addBackAndOrderButtons(keyboard);
                SendMessage message = new SendMessage(chatId, "Выберите товар:").replyMarkup(keyboard);
                bot.execute(message);
            } else {
                bot.execute(new SendMessage(chatId, "В данной категории нет товаров."));
            }
        } else {
            bot.execute(new SendMessage(chatId, "Категория не найдена."));
        }
    }

    private ReplyKeyboardMarkup createSubCategoryKeyboard(List<Category> categories) {
        String[][] buttons = new String[categories.size() + 1][1];
        for (int i = 0; i < categories.size(); i++) {
            buttons[i][0] = categories.get(i).getName();
        }
        buttons[categories.size()][0] = "В основное меню";
        return new ReplyKeyboardMarkup(buttons);
    }

    private ReplyKeyboardMarkup createProductKeyboard(List<Product> products) {
        String[][] buttons = new String[products.size() + 1][1];
        for (int i = 0; i < products.size(); i++) {
            buttons[i][0] = products.get(i).getName();
        }
        buttons[products.size()][0] = "В основное меню";
        return new ReplyKeyboardMarkup(buttons);
    }

    private void handleCategorySelection(Long chatId, String categoryName) {
        Optional<Category> selectedCategoryOptional = appService.findCategoryByName(categoryName);
        if (selectedCategoryOptional.isPresent()) {
            Category selectedCategory = selectedCategoryOptional.get();
            List<Category> subCategories = appService.getSubCategories(selectedCategory.getId());
            List<Product> products = appService.getProductsByCategoryId(selectedCategory.getId());

            if (!subCategories.isEmpty()) {
                ReplyKeyboardMarkup keyboard = createCategoryKeyboard(subCategories);
                addBackAndOrderButtons(keyboard);
                SendMessage message = new SendMessage(chatId, "Выберите подкатегорию:").replyMarkup(keyboard);
                bot.execute(message);
            } else if (!products.isEmpty()) {
                InlineKeyboardMarkup keyboard = createProductInlineKeyboard(products);
                SendMessage message = new SendMessage(chatId, "Выберите товар:").replyMarkup(keyboard);
                bot.execute(message);
            } else {
                bot.execute(new SendMessage(chatId, "В данной категории нет товаров."));
            }
        } else {
            bot.execute(new SendMessage(chatId, "Категория не найдена."));
        }
    }

    private InlineKeyboardMarkup createProductInlineKeyboard(List<Product> products) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Product product : products) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            String buttonText = product.getName() + " - " + product.getPrice() + " руб.";
            row.add(new InlineKeyboardButton(buttonText).callbackData("addProduct_" + product.getId()));
            keyboard.add(row);
        }

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        backRow.add(new InlineKeyboardButton("В основное меню").callbackData("backToMainMenu"));
        keyboard.add(backRow);

        InlineKeyboardButton[][] keyboardArray = new InlineKeyboardButton[keyboard.size()][];
        for (int i = 0; i < keyboard.size(); i++) {
            List<InlineKeyboardButton> row = keyboard.get(i);
            keyboardArray[i] = row.toArray(new InlineKeyboardButton[0]);
        }

        return new InlineKeyboardMarkup(keyboardArray);
    }

    private void addBackAndOrderButtons(ReplyKeyboardMarkup keyboard) {
        keyboard.addRow("Оформить заказ");
    }

    private void processCallbackQuery(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.message().chat().id();
        String callbackData = callbackQuery.data();

        if (callbackData.startsWith("addProduct_")) {
            Long productId = Long.parseLong(callbackData.substring("addProduct_".length()));
            addProductToOrder(chatId, productId);
        } else if (callbackData.equals("backToMainMenu")) {
            sendMainMenu(chatId);
        }

        bot.execute(new AnswerCallbackQuery(callbackQuery.id()));
    }

    private void addProductToOrder(Long chatId, Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            Optional<Client> clientOptional = appService.findClientByExternalId(chatId);
            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                List<ClientOrder> orders = appService.getClientOrders(client.getId());
                ClientOrder activeOrder = orders.stream()
                        .filter(order -> order.getStatus() == 1)
                        .findFirst()
                        .orElseGet(() -> createNewClientOrder(client));

                // Добавление продукта в заказ
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setClientOrder(activeOrder);
                orderProduct.setProduct(product);
                orderProduct.setCountProduct(1); 

                // Сохранение OrderProduct в базе данных
                orderProductRepository.save(orderProduct);

                bot.execute(new SendMessage(chatId, "Товар " + product.getName() + " добавлен в ваш заказ."));
            } else {
                bot.execute(new SendMessage(chatId, "Клиент не найден."));
            }
        } else {
            bot.execute(new SendMessage(chatId, "Продукт не найден."));
        }
    }

    private ClientOrder createNewClientOrder(Client client) {
        ClientOrder clientOrder = new ClientOrder();
        clientOrder.setClient(client);
        clientOrder.setStatus(1); // Статус "Создан"
        clientOrder.setTotal(BigDecimal.ZERO);

        return appService.saveClientOrder(clientOrder);
    }
}
