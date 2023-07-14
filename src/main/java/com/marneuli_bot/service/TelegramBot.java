package com.marneuli_bot.service;

import com.marneuli_bot.config.BotConfig;
import com.marneuli_bot.entity.Categories;
import com.marneuli_bot.entity.Order;
import com.marneuli_bot.repository.CategoryRepository;
import com.marneuli_bot.repository.OrderRepository;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderService orderService;



    @Autowired
    private OrderRepository orderRepository;

    private Map<String, Integer> userStates = new HashMap<>();

    private int sos;

    private long tempCatIdSell;
    private long tempCatIdBuy;

    private String name5;
    private String description5;
    private float price5;
    private byte[] image5;
    private long userIdCallback5;


    private long sellerChatId5;
   private Categories categories5;
   private String sellerUserName5;
   private String getSellerUserName5;
   private Order order5;
   private Sostoyanie sostoyanie5;
    private Message topCommand;
    private String topHello;

    final BotConfig config;
    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Sostoyanie sostoyanie = new Sostoyanie();


        Categories categories = new Categories();
        Categories categories2 = new Categories();
        categories.setId(tempCatIdSell);
        categories2.setId(tempCatIdBuy);
        categories5 = categories;
        Order order = new Order();
        startBot(update);

        if (sos == sostoyanie.needNameWrite) {
            order.setCategory(categories);
            if (update.hasMessage() && update.getMessage().hasText()) {
                String chatId = update.getMessage().getChatId().toString();
                String nameText = update.getMessage().getText();
                order.setName(nameText);
                name5 = nameText;

                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Вы выбрали название " + nameText + ". Теперь введите описание товара")
                        .build();

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                sos = sostoyanie.needDescriptionWrite;
            }

        } else if (sos == sostoyanie.needDescriptionWrite) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String chatId = update.getMessage().getChatId().toString();
                String descriptionText = update.getMessage().getText();
                /* Contact contact = update.getMessage().getContact();
                String contactInfo = contact != null ? contact.toString() : "";
                String conPlus = contact != null ? contact.getPhoneNumber() : "";*/
                order.setDescription(descriptionText);

                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Вы добавили описание: " + descriptionText + ". Теперь введите цену в лари")
                        .build();

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                this.description5 = descriptionText;
                //sos = sostoyanie.confirmNewOrder; это переносится в needPrice
                sos = sostoyanie.needPriceWrite;

            //    confirmNewOrderCar(update, sostoyanie); это переносится в needPrice

            }
        } else if (sos == sostoyanie.changeNameString) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String chatId = update.getMessage().getChatId().toString();
                String changedName = update.getMessage().getText();
                order.setName(changedName);

                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Вы поменяли название")
                        .build();

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                this.name5 = changedName;
                sos = sostoyanie.confirmNewOrder;

                confirmNewOrderCar(update, sostoyanie);
            }
        } else if (sos == sostoyanie.changeDescriptionString) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String chatId = update.getMessage().getChatId().toString();
                String changedDescription = update.getMessage().getText();
                order.setDescription(changedDescription);

                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Вы поменяли описание")
                        .build();

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                this.description5 = changedDescription;
                sos = sostoyanie.confirmNewOrder;

                confirmNewOrderCar(update, sostoyanie);
            }
        }
        else if (sos == sostoyanie.needPriceWrite) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                 String chatId = update.getMessage().getChatId().toString();
                 String priceText = update.getMessage().getText();
                 this.price5 = parseFloatPrice(priceText);


                 order.setPrice(price5);

                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Вы добавили цену: " + priceText + " лари")
                        .build();

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }


                sos = sostoyanie.confirmNewOrder;

                confirmNewOrderCar(update, sostoyanie);
            }

        }
        else if (sos == sostoyanie.changePrice) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String chatId = update.getMessage().getChatId().toString();
                String priceText = update.getMessage().getText();
                this.price5 = parseFloatPrice(priceText);

                order.setPrice(price5);


                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Вы изменили цену: " + priceText + "лари ")
                        .build();

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }


                sos = sostoyanie.confirmNewOrder;

                confirmNewOrderCar(update, sostoyanie);
            }

        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            if (messageText.equals("Подтвердить")) {
              //  this.userId5 = update.getMessage().getFrom().getId();
               // this.userId5 = update.getCallbackQuery().getMessage().getFrom().getId();   - return null (оно и понятно)
                this.sellerUserName5 = update.getMessage().getChat().getUserName();
                this.sellerChatId5 = update.getMessage().getChatId();
              //  order.setUserId(userId5);
                order.setUserId(userIdCallback5);

                writeOrderInDb(order);



                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Вы подтвердили введенные данные. Ваш товар/услуга скоро будет опубликован и будет видим другим пользователям. Бот вернется к меню выбора")
                        .replyMarkup(new ReplyKeyboardRemove(true)) // чтобы пользователь не мог 2 раза опублиовать один ордер клава убирается
                        .build();






                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                startAnswer(topCommand, topHello);

            } else if (messageText.equals("Редактировать название")) {
                sos = sostoyanie.changeNameString;
                //  changeCarName(update, sos, order);

            } else if (messageText.equals("Редактировать описание")) {
                sos = sostoyanie.changeDescriptionString;
                //    changeCarDescription();

            } else if (messageText.equals("Поменять фото")) {

                //   String chatId = update.getMessage().getChatId().toString();
                sos = sostoyanie.changePhoto;
                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Замените фотографию товара/услуги")
                        .build();

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            else if (messageText.equals("Изменить цену")) {
                sos = sostoyanie.changePrice;
            }
        }

        if (sos == sostoyanie.needPhotoUpload) {

            if (update.hasMessage() && update.getMessage().hasPhoto()) {
                String chatId = update.getMessage().getChatId().toString();
                List<PhotoSize> photos = update.getMessage().getPhoto();

                String token = config.getToken();
                String f_id = photos.stream()
                        .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                        .findFirst()
                        .orElse(null).getFileId();


                String photoUrl = "https://api.telegram.org/bot" + token + "/getFile?file_id=" + f_id;
                GetFile getFile = new GetFile(f_id);

                // Создание HTTP-клиента
                CloseableHttpClient httpClient = HttpClients.createDefault();

                // Создание HTTP-запроса для получения информации о файле
                HttpGet request = new HttpGet(photoUrl);

                try {
                    // Выполнение запроса и получение ответа
                    HttpResponse response = httpClient.execute(request);

                    // Получение содержимого ответа
                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();

                    // Чтение содержимого ответа и сохранение в массив байтов
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }
                    byte[] imageBytes = byteArrayOutputStream.toByteArray();

                    image5 = imageBytes;

                    // Теперь у вас есть массив байтов изображения, который можно использовать по вашему усмотрению
                    // ...

                } catch (IOException e) {
                    // Обработка ошибок HTTP-запроса
                    e.printStackTrace();
                } finally {
                    // Закрытие ресурсов
                    try {
                        httpClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Know photo size
                int f_size = photos.stream()
                        .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                        .findFirst()
                        .orElse(null).getFileSize();
                // Know photo height

                // Set photo caption
                String caption = "file_id: " + f_id + "\nsize: " + Integer.toString(f_size);
                InputFile inputFile = new InputFile(f_id);
                SendPhoto photo = SendPhoto.builder()
                        .chatId(chatId)
                        .photo(inputFile)
                        .caption(caption)
                        .build();

                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Вы добавили фотографию, напишите название товара/услуги")
                        .build();

                try {
                    execute(photo);
                    execute(message);
                    sos = sostoyanie.needNameWrite;
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (sos == sostoyanie.changePhoto) {
            if (update.hasMessage() && update.getMessage().hasPhoto()) {
                String chatId = update.getMessage().getChatId().toString();
                List<PhotoSize> photos = update.getMessage().getPhoto();

                String token = config.getToken();
                String f_id = photos.stream()
                        .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                        .findFirst()
                        .orElse(null).getFileId();


                String photoUrl = "https://api.telegram.org/bot" + token + "/getFile?file_id=" + f_id;
                GetFile getFile = new GetFile(f_id);

                // Создание HTTP-клиента
                CloseableHttpClient httpClient = HttpClients.createDefault();

                // Создание HTTP-запроса для получения информации о файле
                HttpGet request = new HttpGet(photoUrl);

                try {
                    // Выполнение запроса и получение ответа
                    HttpResponse response = httpClient.execute(request);

                    // Получение содержимого ответа
                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();

                    // Чтение содержимого ответа и сохранение в массив байтов
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }
                    byte[] imageBytes = byteArrayOutputStream.toByteArray();

                    image5 = imageBytes;

                } catch (IOException e) {

                    e.printStackTrace();
                } finally {
                    // Закрытие ресурсов
                    try {
                        httpClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // Know photo size
                int f_size = photos.stream()
                        .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                        .findFirst()
                        .orElse(null).getFileSize();

                String caption = "file_id: " + f_id + "\nsize: " + Integer.toString(f_size);
                InputFile inputFile = new InputFile(f_id);
                SendPhoto photo = SendPhoto.builder()
                        .chatId(chatId)
                        .photo(inputFile)
                        .caption(caption)
                        .build();

                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Вы поменяли фотографию")
                        .build();

                try {
                    execute(photo);
                    execute(message);

                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                sos = sostoyanie.confirmNewOrder;
                confirmNewOrderCar(update, sostoyanie);

            }
        }

        if (update.hasCallbackQuery()) {

            if (update.getCallbackQuery().getData().equals("sell")) {
                this.userIdCallback5 = update.getCallbackQuery().getMessage().getFrom().getId();

                try {

                    String chatId0 = update.getCallbackQuery().getMessage().getChatId().toString();

                    sos = sostoyanie.checkCategoryForSell;
                    List<Categories> categoryList = checkCategorySeller(chatId0);

                    InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

                    for (Categories category : categoryList) {
                        InlineKeyboardButton button = new InlineKeyboardButton();
                        List<InlineKeyboardButton> row = new ArrayList<>();
                        button.setText(category.getName());
                        button.setCallbackData("category:" + category.getId());

                        row.add(button);
                        rowList.add(row);
                    }

                    keyboard.setKeyboard(rowList);

                    SendMessage message = SendMessage.builder()
                            .chatId(chatId0)
                            .text("Выберите категорию к которой относится Ваш товар/услуга:")
                            .replyMarkup(keyboard)
                            .build();

                    try {
                        // Отправка сообщения
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if (update.getCallbackQuery().getData().equals("buy_order")) {
                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
                sos = sostoyanie.buyOrder;
                String buyerUserName = update.getCallbackQuery().getMessage().getChat().getUserName();
             //   String phoneNumber = update.getCallbackQuery().getMessage().getContact().getPhoneNumber();
                String buyerId = update.getCallbackQuery().getMessage().getFrom().getId().toString();
             tellSellerAboutBuyer(buyerUserName, order5);

                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Свяжитесь с продавцом, написав ему в Telegram: @" + getSellerUserName5)
                        .build();

                SendMessage messageToSeller = SendMessage.builder()
                        .chatId(buyerId)
                        .text("Напишите покупателю @"+ buyerId)
                        .build();

                try {
                    execute(message);
                    //    execute(messageToSeller); выйдет ошибка, что нельзя боту писать другм ботам
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                startBot(update);
            }

            if (update.getCallbackQuery().getData().equals("buy")) {

                try {
                    sos = sostoyanie.buttonBuy;
                    String chatId0 = update.getCallbackQuery().getMessage().getChatId().toString();


                    List<Categories> categoryList = checkCategoryBuyer(chatId0);
                    InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

                    for (Categories category : categoryList) {
                        InlineKeyboardButton button = new InlineKeyboardButton();
                        List<InlineKeyboardButton> row = new ArrayList<>();
                        button.setText(category.getName());
                        button.setCallbackData("category_buy:" + category.getId());


                        row.add(button);
                        rowList.add(row);
                    }

                    keyboard.setKeyboard(rowList);

                    SendMessage message = SendMessage.builder()
                            .chatId(chatId0)
                            .text("Выберите категорию товара, который хотите купить:")
                            .replyMarkup(keyboard)
                            .build();
                     sos = sostoyanie.checkCategoryForBuy;

                    try {
                        // Отправка сообщения
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);

                }
            }

            if (update.getCallbackQuery().getData().equals("back_start")) {
                sos = sostoyanie.backStart;
                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

                startAnswer(topCommand, topHello);
            }

            if (update.getCallbackQuery().getData().equals("my_purchases")) {
                sos = sostoyanie.myPurchases;

                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Ваши покупки:")
                        .build();

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            if (update.getCallbackQuery().getData().equals("my_goods")) {
                sos = sostoyanie.myGoods;

                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

                 long userId = update.getCallbackQuery().getMessage().getFrom().getId(); // попытка вывести чат айди по другому
            //    userId5 = userId;


          //      List<Order> userOrders = orderRepository.findBySellerChatId(userId);
                  List<Order> userOrders = orderRepository.findByUserId(userId);

                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Ваш userID - " + userId + ", Ваш chatID - "+ chatId + " Размещенные Вами товары:")
                        .build();

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                sendUserGoods(chatId, userOrders, userId);
            }

            else if (update.hasCallbackQuery() && sos == sostoyanie.checkCategoryForSell) {
                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

                // Создание списка объектов с информацией о категориях и текстах сообщений
                List<CategoryInfoSell> categoryInfoList = new ArrayList<>();

                categoryInfoList.add(new CategoryInfoSell("category:1", "Добавьте одно фото автомобиля", 1));
                categoryInfoList.add(new CategoryInfoSell("category:2", "Приложите фотографию автозапчасти", 2));
                categoryInfoList.add(new CategoryInfoSell("category:3", "Приложите фотографию (пример работы или прайс).", 3));
                categoryInfoList.add(new CategoryInfoSell("category:4", "Приложите фотографию телефона", 4));
                categoryInfoList.add(new CategoryInfoSell("category:5", "Приложите фотографию услуги или прайс ремонта телефона", 5));
                categoryInfoList.add(new CategoryInfoSell("category:6", "Добавьте фотографию компьютера", 6));
                categoryInfoList.add(new CategoryInfoSell("category:7", "Приложите фотографию ремонта компьютера/ прайс", 7));
                // Поиск выбранной категории в списке и отправка соответствующего сообщения
                for (CategoryInfoSell categoryInfo : categoryInfoList) {

                    if (update.getCallbackQuery().getData().equals(categoryInfo.getCategory())) {
                        tempCatIdSell = categoryInfo.getSostId();
                        SendMessage message = SendMessage.builder()
                                .chatId(chatId)
                                .text(categoryInfo.getMessage())
                                .build();

                        try {
                            // Отправка сообщения
                            execute(message);

                            sos = sostoyanie.needPhotoUpload;

                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            else if (update.hasCallbackQuery()) {
                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

                // Создание списка объектов с информацией о категориях и текстах сообщений
                List<CategoryInfoBuy> categoryInfoList = new ArrayList<>();

                categoryInfoList.add(new CategoryInfoBuy("category_buy:1", "Список автомобилей:", 1));
                categoryInfoList.add(new CategoryInfoBuy("category_buy:2", "Список автозапчастей:", 2));
                categoryInfoList.add(new CategoryInfoBuy("category_buy:3", "Услуги парикмахеров:", 3));
                categoryInfoList.add(new CategoryInfoBuy("category_buy:4", "Список смартфонов:", 4));
                categoryInfoList.add(new CategoryInfoBuy("category_buy:5", "Ремонт телефонов:", 5));
                categoryInfoList.add(new CategoryInfoBuy("category_buy:6", "Список компьютеров:", 6));
                categoryInfoList.add(new CategoryInfoBuy("category_buy:7", "Ремонт компьютеров:", 7));

                for (CategoryInfoBuy categoryInfo : categoryInfoList) {
                    if (update.getCallbackQuery().getData().equals(categoryInfo.getCategory())) {
                        tempCatIdBuy = categoryInfo.getSostId();

                        SendMessage message = SendMessage.builder()
                                .chatId(chatId)
                                .text(categoryInfo.getMessage())
                                .build();

                        try {
                            // Отправка сообщения
                            execute(message);
                            sos = sostoyanie.checkCategoryForBuy;
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        List<Order> orders = orderRepository.findByCategoryId(tempCatIdBuy);

                        sendOrdersToChat(chatId, orders);
                    }
                }

            }

        }
    }

    private void tellSellerAboutBuyer(String buyerUserName, Order order) {
        String sellerChatId = String.valueOf(sellerChatId5);
       // пытался испаользовать phone number но это бессмысленно( может быть не указан)


         SendMessage message = SendMessage.builder()
                 .chatId(sellerChatId)
                 .text("с Вами хочет связаться покупатель @" + buyerUserName + " Телефон - " + " ID " + sellerChatId)
                 .build();

         SendMessage message2 = SendMessage.builder()
                 .chatId(sellerChatId)
                 .text(order.getName() + " - Ваш товар/услуга, заинтересовавший покупателя")
                 .build();

        try {
            execute(message);
            execute(message2);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private float parseFloatPrice(String priceText) {
        // Заменяем запятую на точку (если присутствует)
        String normalizedInput = priceText.replace(",", ".");

        // Удаляем все нецифровые символы из строки
        String digitsOnly = normalizedInput.replaceAll("[^0-9.]", "");

        // Преобразуем строку в float
        return Float.parseFloat(digitsOnly);
    }

    private void sendOrdersToChat(String chatId, List<Order> orders) {

        for (Order order : orders) {
            InlineKeyboardMarkup keyboardBuyOrder = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            InlineKeyboardButton buy = new InlineKeyboardButton();
            InlineKeyboardButton backInStart = new InlineKeyboardButton();

            List<InlineKeyboardButton> row = new ArrayList<>();
            buy.setText("Купить (продавец свяжется с Вами");
            buy.setCallbackData("buy_order");
            backInStart.setText("Вернуться в начало");
            backInStart.setCallbackData("back_start");

            getSellerUserName5 = order.getSellerUserName();

                    row.add(buy);
            row.add(backInStart);

            rowList.add(row);
            keyboardBuyOrder.setKeyboard(rowList);


            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text("Order: " + order.getName() + ", Description: " + order.getDescription() + ", Price: " + order.getPrice())
                    .build();
            this.order5 = order;

           // sellerUserName5 = order.getSellerUserName();

            String fileId = getOrderPhotoBy(order);
            InputFile inputFile = new InputFile(fileId);

            SendPhoto photoMessage = SendPhoto.builder()
                    .chatId(chatId)
                    .photo(inputFile)
                    .replyMarkup(keyboardBuyOrder)
                    .build();

            try {
                execute(message);
                execute(photoMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendUserGoods(String chatId, List<Order> userOrders, long userId) {


        for (Order order : userOrders) {
            InlineKeyboardMarkup keyboardBuyOrder = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            InlineKeyboardButton deleteButton = new InlineKeyboardButton();
            InlineKeyboardButton backInStartButton = new InlineKeyboardButton();

            List<InlineKeyboardButton> row = new ArrayList<>();
            deleteButton.setText("Удалить");
            deleteButton.setCallbackData("delete_order_" + order.getId());
            backInStartButton.setText("Вернуться в начало");
            backInStartButton.setCallbackData("back_start");

            row.add(deleteButton);
            row.add(backInStartButton);

            rowList.add(row);
            keyboardBuyOrder.setKeyboard(rowList);

            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text("Order: " + order.getName() + ", Description: " + order.getDescription() + ", Price: " + order.getPrice())
                    .replyMarkup(keyboardBuyOrder)
                    .build();

            String fileId = getOrderPhotoBy(order);
            InputFile inputFile = new InputFile(fileId);

            SendPhoto photoMessage = SendPhoto.builder()
                    .chatId(chatId)
                    .photo(inputFile)
                    .replyMarkup(keyboardBuyOrder)
                    .build();

            try {
                execute(message);
                execute(photoMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public String getOrderPhotoBy(Order order) {
        String photoData = new String(order.getPhoto());
        String startDelimiter = "\"file_id\":\"";
        String endDelimiter = "\"";

        int startIndex = photoData.indexOf(startDelimiter);
        if (startIndex != -1) {
            startIndex += startDelimiter.length();
            int endIndex = photoData.indexOf(endDelimiter, startIndex);
            if (endIndex != -1) {
                return photoData.substring(startIndex, endIndex);
            }
        }

        return null;
    }

    public void startBot(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            String messageText = update.getMessage().getText();
            Message command = update.getMessage();
            topCommand = command;

            String hello = "Привет, " + update.getMessage().getChat().getUserName();

            topHello = hello;

            switch (messageText) {
                case "/start":
                    startAnswer(command, hello);
                    break;

                default:
            }
        }
    }

    public void startAnswer(Message message, String hello) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();


        InlineKeyboardButton sell = new InlineKeyboardButton();
        sell.setText("Продать");
        sell.setCallbackData("sell");

        InlineKeyboardButton buy = new InlineKeyboardButton();
        buy.setText("Купить");
        buy.setCallbackData("buy");

        InlineKeyboardButton myPurchases = new InlineKeyboardButton();
        myPurchases.setText("Мои покупки");
        myPurchases.setCallbackData("my_purchases");

        InlineKeyboardButton myGoods = new InlineKeyboardButton();
        myGoods.setText("Мои товары/услуги");
        myGoods.setCallbackData("my_goods");

        InlineKeyboardButton botRules = new InlineKeyboardButton();
        botRules.setText("Правила");
        botRules.setCallbackData("rules");

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(sell);
        keyboardButtonsRow.add(buy);

        List<InlineKeyboardButton> keyboardButtons5Lists = new ArrayList<>();
        keyboardButtons5Lists.add(myPurchases);
        keyboardButtons5Lists.add(myGoods);

        List<InlineKeyboardButton> rulesList = new ArrayList<>();
        rulesList.add(botRules);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);
        rowList.add(keyboardButtons5Lists);
        rowList.add(rulesList);
        keyboard.setKeyboard(rowList);

        try {
            execute(
                    SendMessage.builder()
                            .chatId(String.valueOf(message.getChatId()))
                            .parseMode("Markdown")
                            .text(hello + "! Здесь вы можете купить или продать товары и услуги.")
                            .replyMarkup(keyboard)
                            .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void confirmNewOrderCar(Update update, Sostoyanie sostoyanie) {
        String chatId = String.valueOf(update.getMessage().getChatId());

        if (sos == sostoyanie.confirmNewOrder) {
            ReplyKeyboardMarkup keyboardMarkup555 = new ReplyKeyboardMarkup();
            keyboardMarkup555.setOneTimeKeyboard(true);
            keyboardMarkup555.setResizeKeyboard(true);

            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow accept = new KeyboardRow();
            KeyboardRow changeName = new KeyboardRow();
            KeyboardRow changeDescription = new KeyboardRow();
            KeyboardRow changePhoto = new KeyboardRow();
            KeyboardRow changePrice = new KeyboardRow();
            accept.add(new KeyboardButton("Подтвердить"));
            changeName.add(new KeyboardButton("Редактировать название"));
            changeDescription.add(new KeyboardButton("Редактировать описание"));
            changePhoto.add(new KeyboardButton("Поменять фото"));
            changePrice.add(new KeyboardButton("Изменить цену"));
            keyboard.add(accept);
            keyboard.add(changeName);
            keyboard.add(changeDescription);
            keyboard.add(changePhoto);
            keyboard.add(changePrice);


            keyboardMarkup555.setKeyboard(keyboard);


            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text("Подтвердите, если все введено правильно, или отредактируйте нужную информацию")
                    .replyMarkup(keyboardMarkup555)  // Устанавливаем клавиатуру в сообщение
                    .build();

            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void writeOrderInDb(Order order) {
        order.setId(userIdCallback5);
        order.setCategory(categories5);
        order.setName(name5);
        order.setPhoto(image5);
        order.setDescription(description5);
        order.setPrice(price5);
        order.setTimePublication(LocalDateTime.now());
        order.setSellerUserName(sellerUserName5);
        order.setSellerChatId(sellerChatId5);
        orderService.saveOrder(order.getUserId(), order.getName(),order.getDescription(), order.getPrice(), order.getPhoto(),order.getCategory(), order.getTimePublication(), order.getSellerUserName(), order.getSellerChatId());

    }


    private List<Categories> checkCategorySeller(String chatId) {

        return categoryRepository.findAll();

    }

    private List<Categories> checkCategoryBuyer(String chatId) {

        return categoryRepository.findAll();
    }

    private byte[] downloadPhoto(String fileId) {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);

        try {
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
            String filePath = file.getFilePath();
            // Вы можете использовать filePath для получения фотографии
            // Например, скачать фотографию по ссылке: https://api.telegram.org/file/bot<token>/<file_path>
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    /*private void downloadAndSavePhoto(String fileId) {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);

        try {
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
            String filePath = file.getFilePath();
            byte[] photoData = downloadPhoto(filePath);

            if (photoData != null && photoData.length > 0) {
                if (photoData.length <= 1024 * 1024) {
                    // savePhotoToDatabase(photoData);
                } else {
                    System.out.println("Фотография превышает размер 1 мегабайта. Уменьшение размера...");

                    // Создание BufferedImage из исходных данных фотографии
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(photoData);
                    BufferedImage originalImage = ImageIO.read(inputStream);

                    // Определение новых размеров для уменьшенной фотографии
                    int targetWidth = 800; // Здесь укажите требуемую ширину
                    int targetHeight = 800; // Здесь укажите требуемую высоту

                    // Масштабирование исходной фотографии до новых размеров
                    Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
                    BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
                    resizedImage.getGraphics().drawImage(scaledImage, 0, 0, null);

                    // Сохранение уменьшенной фотографии в ByteArrayOutputStream
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ImageIO.write(resizedImage, "jpg", outputStream);

                    // Получение уменьшенных данных фотографии
                    byte[] resizedPhotoData = outputStream.toByteArray();

                    // Проверка размера уменьшенной фотографии
                    if (resizedPhotoData.length <= 950 * 1024) {
                        //Сюда надо вставить метод, который будет сохранять в БД картинку
                        System.out.println("Фотография уменьшена и сохранена.");
                    } else {
                        System.out.println("Фотография все еще превышает размер 950 килобайт. Не удалось сохранить.");
                    }
                }
            }
        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }
    }*/

    public class CategoriesSostoyanie{
        int category1 = 1;
        int category2 = 2;
        int category3 = 3;
    }

    public class Sostoyanie {

        private int sss;
        final int start = 0;
        final int sell = 1;
        private final int sellProcess = 9;
        private final int buy = 2;


        private final int mySells = 3;
        private final int MyBuys = 4;

        private final int deleteMyGood = 5;
        private final int needPhotoUpload = 10;
        private final int needNameWrite = 11;
        private final int needDescriptionWrite = 12;
        private final int confirmNewOrder = 13;
        private final int sendOrderToDb = 14;
        private final int changeNameString = 15;
        private final int changeDescriptionString = 16;
        private final int changePhoto = 17;
        private final int needPriceWrite = 20;
        private final int changePrice = 21;
        private final int photoUploaded = 6;
        final int checkCategoryForSell = 7;
        private final int needPhotoAutoPartUpload = 18;
        private final int buttonBuy = 8;
        private final int checkCategoryForBuy = 19;
        private final int buyOrder = 22;
        private final int myPurchases = 23;
        private final int myGoods = 24;
        private final int backStart = 25;
    }
}









