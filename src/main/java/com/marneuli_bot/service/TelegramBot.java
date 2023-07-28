package com.marneuli_bot.service.bot;

import com.marneuli_bot.config.BotConfig;
import com.marneuli_bot.entity.Categories;
import com.marneuli_bot.entity.Order;
import com.marneuli_bot.entity.auto_entities.CarBrands;
import com.marneuli_bot.entity.auto_entities.CarModels;
import com.marneuli_bot.entity.auto_entities.CarSelling;
import com.marneuli_bot.entity.mobile_entities.MobileBrands;
import com.marneuli_bot.entity.mobile_entities.MobileModels;
import com.marneuli_bot.entity.mobile_entities.MobileOrder;
import com.marneuli_bot.repository.*;
import com.marneuli_bot.service.CarService;
import com.marneuli_bot.service.OrderService;
import com.marneuli_bot.service.brands.*;
import com.marneuli_bot.service.helpers.CarWithOrderInfo;
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
    private CarService carService;

    CarSelling carSelling5;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CarBrandRepository carBrandRepository;
    @Autowired
    private MobileBrandsRepository mobileBrandsRepository;
    @Autowired
    private MobileModelsRepository mobileModelsRepository;
    @Autowired
    private CarModelRepository carModelRepository;


    private Map<String, Integer> userStates = new HashMap<>();

    private int sos;
    private long tempCatIdSell;
    private long tempCatIdBuy;

    private String name5;
    private String description5;
    private float price5;
    private byte[] image5;
    private long botIdCallback5;
    //////////////////////////// CAR LOGIC
    /// Car Brand
    private long tempCarBrandIdSell5;
    private long tempCarBrandIdBuy5;
    private String tempCarBrandNameSell5;
    private String tempCarBrandNameBuy5;


    /// Car Model
    private boolean carInDb;
    private long tempCarModelIdSell5;
    private long tempCarModelIdBuy5;
    private CarBrands carBrands5;
    private CarModels carModels5;
    private String tempCarModelNameBuy5;

    private String tempCarModelNameSell5;
    private CarBrands tempCarModelBrandIdBuy5;
    private String tempCarModelBodyStyleBuy5;
    /// Car Sell
    private int tempYearOfIssue5;
    //////////////////////////// MOBILE LOGIC
    boolean mobileInDb;
    private long tempMobileBrandIdSell5;
    private String tempMobileBrandNameSell5;
    private long tempMobileModelIdSell5;
    private String tempMobileModelNameSell5;
    private String tempColorOfMobile5;
    private MobileBrands mobileBrands5;
    private MobileModels mobileModels5;
    private MobileOrder mobileOrder5;


    ////////////////////
    private long sellerChatId5;
   private Categories categories5;
   private String sellerUserName5;
   private String getSellerUserName5;
   private Order order5;
   private Sostoyanie sostoyanie5;
    private Message topCommand;
    private String topHello;

    final BotConfig config;
    private final CarSellingRepository carSellingRepository;


    public TelegramBot(BotConfig config,
                       CarSellingRepository carSellingRepository) {
        this.config = config;
        this.carSellingRepository = carSellingRepository;
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

        Categories categories7 = new Categories(); // sell
        Categories categories2 = new Categories(); // buy
        // CARS
        CarBrands carBrands7 = new CarBrands();
        CarModels carModels7 = new CarModels();
        CarSelling carSelling7 = new CarSelling();

      // carModels7.setModel();
        carSelling7.setYearOfIssue(tempYearOfIssue5);
        categories7.setId(tempCatIdSell);
        carBrands7.setId(tempCarBrandIdSell5);
        categories2.setId(tempCatIdBuy);
        categories5 = categories7;
        carBrands5 = carBrands7;
        carModels5 = carModels7;
        carSelling5 = carSelling7;

        // MOBILE
        MobileBrands mobileBrands7 = new MobileBrands();
        MobileModels mobileModels7 = new MobileModels();
        MobileOrder mobileOrder7 = new MobileOrder();

        mobileBrands5 = mobileBrands7;
        mobileModels5 = mobileModels7;
        mobileOrder5 = mobileOrder7;


        Order order = new Order();
        startBot(update);

        if (sos == sostoyanie.needNameWrite) {
            order.setCategory(categories7);
            if (update.hasMessage() && update.getMessage().hasText()) {
                String chatId = update.getMessage().getChatId().toString();
                String nameText = update.getMessage().getText();
                order.setName(nameText);
                name5 = nameText;

                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Вы выбрали название " + nameText + ". Теперь введите контакт для связи с Вами, город и описание товара")
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

            //    confirmNewOrder(update, sostoyanie); это переносится в needPrice

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

                confirmNewOrder(update, sostoyanie);
            }
        } else if (sos == sostoyanie.changeDescriptionString) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String chatId = update.getMessage().getChatId().toString();
                String changedDescription = update.getMessage().getText();
                order.setDescription(changedDescription);

                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Вы поменяли данные в описании")
                        .build();

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                this.description5 = changedDescription;
                sos = sostoyanie.confirmNewOrder;

                confirmNewOrder(update, sostoyanie);
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

                confirmNewOrder(update, sostoyanie);
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

                confirmNewOrder(update, sostoyanie);
            }
        }

        if (sos == sostoyanie.writeYearOfIssue) {
            String chatId = update.getMessage().getChatId().toString();
            if (update.hasMessage() && update.getMessage().hasText()) {
                String yearOfIssue = update.getMessage().getText();
                tempYearOfIssue5 = Integer.parseInt(yearOfIssue);
                try {
                    int yearOfIssueInt = Integer.parseInt(yearOfIssue);
                   carSelling7.setYearOfIssue(yearOfIssueInt);

                    SendMessage message = SendMessage.builder()
                            .chatId(chatId)
                            .text("Вы указали год: " + yearOfIssueInt)
                            .build();

                    SendMessage message2 = SendMessage.builder()
                            .chatId(chatId)
                            .text("Добавьте фотографию авто")
                            .build();
                    carInDb = true;

                    try {
                        execute(message);
                        execute(message2);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                } catch (NumberFormatException e) {
                    SendMessage message = SendMessage.builder()
                            .chatId(chatId)
                            .text("Пожалуйста, укажите год цифрами, например 2017")
                            .build();

                    try {
                        execute(message);
                    } catch (TelegramApiException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                sos = sostoyanie.needPhotoUpload;
            }
        }
        if (sos == sostoyanie.writeColorOfMobile) {
            String chatId = update.getMessage().getChatId().toString();
            if (update.hasMessage() && update.getMessage().hasText()) {
                String colorOfMobile = update.getMessage().getText();
                tempColorOfMobile5 = colorOfMobile;

                mobileOrder7.setColor(tempColorOfMobile5);

                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Вы указали цвет: " + colorOfMobile)
                        .build();

                SendMessage message2 = SendMessage.builder()
                        .chatId(chatId)
                        .text("Добавьте фотографию телефона U+1F517")
                        .build();
                mobileInDb = true;

                try {
                    execute(message);
                    execute(message2);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                sos = sostoyanie.needPhotoUpload;

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

                try {
                    writeOrderInDb(order);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }








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
                confirmNewOrder(update, sostoyanie);

            }
        }

        if (update.hasCallbackQuery()) {

            if (update.getCallbackQuery().getData().equals("sell")) {
                // this.userIdCallback5 = update.getCallbackQuery().getMessage().getFrom().getId(); - возвращает БОТ id! понадобится если нужно разостать всем участниам какое то сообщение!

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
                long chatId1 = update.getCallbackQuery().getMessage().getChatId();  // методу нужен id не стринговый

                 long userId = update.getCallbackQuery().getMessage().getFrom().getId(); // попытка вывести чат айди по другому
            //    userId5 = userId;


                List<Order> userOrders = orderRepository.findBySellerChatId(chatId1);
           //       List<Order> userOrders = orderRepository.findByUserId(userId);

                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text("ID бота - " + userId + ", Ваш id - "+ chatId + " Размещенные Вами товары:")
                        .build();

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                sendUserGoods(chatId, userOrders, chatId1);
            }

            else if (update.hasCallbackQuery() && sos == sostoyanie.checkCategoryForSell) {
                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

                // Создание списка объектов с информацией о категориях и текстах сообщений
                List<CategoryInfoSell> categoryInfoList = new ArrayList<>();

                categoryInfoList.add(new CategoryInfoSell("category:1", "Автомобили", 1));
                categoryInfoList.add(new CategoryInfoSell("category:2", "Приложите фотографию автозапчасти", 2));
                categoryInfoList.add(new CategoryInfoSell("category:3", "Приложите фотографию (пример работы или прайс).", 3));
                categoryInfoList.add(new CategoryInfoSell("category:4", "Смартфоны", 4));
                categoryInfoList.add(new CategoryInfoSell("category:5", "Приложите фотографию услуги или прайс ремонта телефона", 5));
                categoryInfoList.add(new CategoryInfoSell("category:6", "Добавьте фотографию компьютера", 6));
                categoryInfoList.add(new CategoryInfoSell("category:7", "Приложите фотографию ремонта компьютера/прайс", 7));
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

                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }

                        // Auto Sell logic
                        if (tempCatIdSell == 1) {
                            System.out.println("вы хотите ПРОДАТЬ авто");
                          //  tempCatIdSell = 0;
                            sos = sostoyanie.chooseCarBrandForSell;

                            List<CarBrands> carBrandsList = checkCarBrandsSeller(chatId);

                            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

                            for (CarBrands carBrands1 : carBrandsList) {
                                InlineKeyboardButton button = new InlineKeyboardButton();
                                List<InlineKeyboardButton> row = new ArrayList<>();
                                button.setText(carBrands1.getName());
                                button.setCallbackData("car_brand:" + carBrands1.getId());
                                tempCarBrandIdSell5 = carBrands1.getId();

                                row.add(button);
                                rowList.add(row);
                            }

                            keyboard.setKeyboard(rowList);
                            SendMessage message1 = SendMessage.builder()
                                    .chatId(chatId)
                                    .text("Выберите марку продаваемого автомобиля:")
                                    .replyMarkup(keyboard)
                                    .build();

                            try {
                                execute(message1);
                                sos = sostoyanie.chooseCarModelForSell;
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        // Phone Sell logic
                        else if (tempCatIdSell == 4) {
                            System.out.println("смартфоны продажа");

                            sos = sostoyanie.chooseMobileBrandForSell;

                            List<MobileBrands> mobileBrandsList = checkMobileBrandsSeller(chatId);

                            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

                            for (MobileBrands mobileBrands1 : mobileBrandsList) {
                                InlineKeyboardButton button = new InlineKeyboardButton();
                                List<InlineKeyboardButton> row = new ArrayList<>();
                                button.setText(mobileBrands1.getName());
                                button.setCallbackData("mobile_brand:" + mobileBrands1.getId());
                                tempMobileBrandIdSell5 = mobileBrands1.getId();

                                row.add(button);
                                rowList.add(row);
                            }

                            keyboard.setKeyboard(rowList);
                            SendMessage message1 = SendMessage.builder()
                                    .chatId(chatId)
                                    .text("Выберите марку продаваемого телефона:")
                                    .replyMarkup(keyboard)
                                    .build();

                            try {
                                execute(message1);
                                sos = sostoyanie.chooseMobileModelForSell;
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }



               // временная заглушка для остальных категорий
                                   else sos = sostoyanie.needPhotoUpload; // временная заглушка

                    }
                }
            }

            else if (update.hasCallbackQuery() && sos == sostoyanie.chooseCarModelForSell) {

                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

                CarBrandInfoSell carBrandInfoSell1 = new CarBrandInfoSell();
                List<CarBrandInfoSell> carBrandsList = carBrandInfoSell1.createCarBrandList();
                // тут список марок автомобилей для продажи

                for (CarBrandInfoSell carBrandInfoSell : carBrandsList) {
                    if (update.getCallbackQuery().getData().equals(carBrandInfoSell.getBrand())) {
                        tempCarBrandIdSell5 = carBrandInfoSell.getSostId();
                        tempCarBrandNameSell5 = carBrandInfoSell.getBrand();

                        carBrands7.setId(tempCarBrandIdSell5);
                        carModels7.setCarBrands(carBrands7);
                        carSelling7.setCarBrands(carBrands7);

                        List<CarModels> carModelsList = checkCarModelsSeller(chatId);

                        InlineKeyboardMarkup keyboardModels = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> rowListModels = new ArrayList<>();

                        for (CarModels carModels : carModelsList) {
                            if (carModels.getCarBrands().getId() == tempCarBrandIdSell5) {
                                InlineKeyboardButton button = new InlineKeyboardButton();
                                List<InlineKeyboardButton> row = new ArrayList<>();
                                button.setText(carModels.getModel());
                                button.setCallbackData("car_model:" + carModels.getId()); // ПРОДАЖА!!! не менять на car_model_ buy!!!

                                row.add(button);
                                rowListModels.add(row);
                            }
                        }

                        keyboardModels.setKeyboard(rowListModels);
                        SendMessage message2 = SendMessage.builder()
                                .chatId(chatId)
                                .text("Выберите модель: " + carBrandInfoSell.getMessage())
                                .replyMarkup(keyboardModels)
                                .build();

                        System.out.println(carBrands7.getId());

                        try {
                            execute(message2);
                            sos = sostoyanie.setCarCreateYear;
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            else if (update.hasCallbackQuery() && sos == sostoyanie.chooseMobileModelForSell) {

                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

                MobileBrandInfoSell mobileBrandInfoSell1 = new MobileBrandInfoSell();
                List<MobileBrandInfoSell> mobileBrandList = mobileBrandInfoSell1.createMobileBrandList();
                // тут список марок телефонов для продажи

                for (MobileBrandInfoSell mobileBrandInfoSell : mobileBrandList) {
                    if (update.getCallbackQuery().getData().equals(mobileBrandInfoSell.getBrand())) {
                        tempMobileBrandIdSell5 = mobileBrandInfoSell.getSostId();
                        tempMobileBrandNameSell5 = mobileBrandInfoSell.getBrand();

                        mobileBrands7.setId(tempMobileBrandIdSell5);
                        mobileModels7.setMobileBrandId(mobileBrands7);
                        mobileOrder7.setMobileBrandId(mobileBrands7);

                        List<MobileModels> mobileModelsList = checkMobileModelsSeller(chatId);

                        InlineKeyboardMarkup keyboardModels = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> rowListModels = new ArrayList<>();

                        for (MobileModels mobileModels : mobileModelsList) {
                            if (mobileModels.getMobileBrandId().getId() == tempMobileBrandIdSell5) {
                                InlineKeyboardButton button = new InlineKeyboardButton();
                                List<InlineKeyboardButton> row = new ArrayList<>();
                                button.setText(mobileModels.getModelName());
                                button.setCallbackData("mobile_model:" + mobileModels.getId());

                                row.add(button);
                                rowListModels.add(row);
                            }
                        }

                        keyboardModels.setKeyboard(rowListModels);
                        SendMessage message2 = SendMessage.builder()
                                .chatId(chatId)
                                .text("Выберите модель: " + mobileBrandInfoSell.getMessage())
                                .replyMarkup(keyboardModels)
                                .build();

                        System.out.println(mobileBrands7.getId());

                        try {
                            execute(message2);
                            sos = sostoyanie.setMobileColor;
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);

                        }
                    }
                }
            }
             // потом поменять название переменной чтобы не вводить в заблуждение!
            //это не setYear а setModel!!! и в телефонах по этому типо было сделано тоже
            else if (update.hasCallbackQuery() && sos == sostoyanie.setCarCreateYear) {
                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

                   CarModelInfoSell carModelInfoSell1 = new CarModelInfoSell(carModelRepository);
                List<CarModelInfoSell> carModelsList = carModelInfoSell1.createCarModelList();

                // тут список моделей автомобилей


                for (CarModelInfoSell carModelInfoSell : carModelsList) {
                    if (update.getCallbackQuery().getData().equals("car_model:" + carModelInfoSell.getId())) {
                    tempCarModelIdSell5 = carModelInfoSell.getId();
                    tempCarModelNameSell5 = carModelInfoSell.getModel();
                    carModels7.setId(tempCarModelIdSell5);

                    carModelInfoSell.setBrandId(carBrands5);
                    carModelInfoSell.setModel(tempCarModelNameSell5); // установил имя модели
                    carSelling7.setCarBrands(carBrands7);
                   // carSelling7.setCarModels(carModels7);
                    carSelling7.setCountryOfOrigin("fff");

                        SendMessage message = SendMessage.builder()
                                .chatId(chatId)
                                .text("Напишите год выпуска автомобиля")
                                .build();

                        sos = sostoyanie.writeYearOfIssue;

                        System.out.println("model number " + carModelInfoSell.getId());
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }// setMobileColor - не совсем корректное название, потом изменить и в авто тоже на setModel
            else if (update.hasCallbackQuery() && sos == sostoyanie.setMobileColor) {

                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

                 MobileModelInfoSell mobileModelInfoSell1 = new MobileModelInfoSell(mobileModelsRepository);
                 List<MobileModelInfoSell> mobileModelList = mobileModelInfoSell1.createMobileModelList();

                 // Тут список моделей телефонов

                for (MobileModelInfoSell mobileModelInfoSell : mobileModelList) {
                    if (update.getCallbackQuery().getData().equals("mobile_model:" + mobileModelInfoSell.getId())) {
                       tempMobileModelIdSell5 = mobileModelInfoSell.getId();
                       tempMobileModelNameSell5 = mobileModelInfoSell.getPhoneModelName();
                       mobileModels7.setId(tempMobileModelIdSell5);

                       mobileModelInfoSell.setPhoneBrandId(mobileBrands5);
                       mobileModelInfoSell.setPhoneModelName(tempMobileModelNameSell5);
                       mobileOrder7.setMobileBrandId(mobileBrands7);

                       SendMessage message = SendMessage.builder()
                               .chatId(chatId)
                               .text("Укажите цвет телефона")
                               .build();

                       sos = sostoyanie.writeColorOfMobile;

                        System.out.println("mobile model number " + mobileModelInfoSell.getId());

                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            /////////////////// Логика покупки

           else if (update.hasCallbackQuery() && sos == sostoyanie.checkCategoryForBuy) {
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
                    //        sos = sostoyanie.checkCategoryForBuy; похоже что не нужно
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }

                        if (tempCatIdBuy == 1) {
                            System.out.println("вы хотите КУПИТЬ авто");
                            sos = sostoyanie.chooseCarBrandForBuy;

                            List<CarBrands> carBrandsList = checkCarBandsBuy(chatId);

                            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

                            for (CarBrands carBrands1 : carBrandsList) {
                                InlineKeyboardButton button = new InlineKeyboardButton();
                                List<InlineKeyboardButton> row = new ArrayList<>();
                                button.setText(carBrands1.getName());
                                button.setCallbackData("car_brand_buy:" + carBrands1.getId());
                                tempCarBrandIdBuy5 = carBrands1.getId();

                                row.add(button);
                                rowList.add(row);
                            }

                            keyboard.setKeyboard(rowList);
                            SendMessage message1 = SendMessage.builder()
                                    .chatId(chatId)
                                    .text("Выберите марку автомобиля, который хотите купить:")
                                    .replyMarkup(keyboard)
                                    .build();

                            try {
                                execute(message1);
                                sos = sostoyanie.chooseCarModelForBuy;
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        // временная заглушка для остальных категорий

                        else {
                        List<Order> orders = orderRepository.findByCategoryId(tempCatIdBuy);
                        sendOrdersToChat(chatId, orders);
                        }
                    }
                }
            }

                    else if (update.hasCallbackQuery() && sos == sostoyanie.chooseCarModelForBuy) {

                        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

                        CarBrandInfoBuy carBrandInfoBuy1 = new CarBrandInfoBuy();
                        List<CarBrandInfoBuy> carBrandList = carBrandInfoBuy1.createCarBrandList();
                        // тут список марок автомобилей для покупки

                for (CarBrandInfoBuy carBrandInfoBuy : carBrandList) {
                    if (update.getCallbackQuery().getData().equals(carBrandInfoBuy.getBrand())) {
                        tempCarBrandIdBuy5 = carBrandInfoBuy.getSostId();
                        tempCarBrandNameBuy5 = carBrandInfoBuy.getBrand();

                        // потом обдумать нужно ли чтото здесь

                        List<CarModels> carModelsList = checkCarModelsBuy(chatId);

                        InlineKeyboardMarkup keyboardModels = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> rowListModels = new ArrayList<>();

                        for (CarModels carModels : carModelsList) {
                            if (carModels.getCarBrands().getId() == tempCarBrandIdBuy5) {
                                InlineKeyboardButton button = new InlineKeyboardButton();
                                List<InlineKeyboardButton> row = new ArrayList<>();
                                button.setText(carModels.getModel());
                                button.setCallbackData("car_model_buy:" + carModels.getId());

                                row.add(button);
                                rowListModels.add(row);
                            }
                            //TODO
                        }
                        keyboardModels.setKeyboard(rowListModels);
                        SendMessage message2 = SendMessage.builder()
                                .chatId(chatId)
                                .text("Выберите модель автомобиля, который хотите купить: " + carBrandInfoBuy.getMessage())
                                .replyMarkup(keyboardModels)
                                .build();


                        try {
                            execute(message2);
                            sos = sostoyanie.seeCarsByModel;
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

                    else if (update.hasCallbackQuery() && sos == sostoyanie.seeCarsByModel) {
                        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

                        CarModelInfoBuy carModelInfoBuy1 = new CarModelInfoBuy(carModelRepository);
                        List<CarModelInfoBuy> carModelList = carModelInfoBuy1.createCarModelList();

                        // тут список моделей автомобилей


                      for (CarModelInfoBuy carModelInfoBuy : carModelList) {
                          if (update.getCallbackQuery().getData().equals("car_model_buy:" + carModelInfoBuy.getId())) {
                              tempCarModelIdBuy5 = carModelInfoBuy.getId();
                              tempCarModelNameBuy5 = carModelInfoBuy.getModel();
                              tempCarModelBrandIdBuy5 = carModelInfoBuy.getBrandId();
                              tempCarModelBodyStyleBuy5 = carModelInfoBuy.getBodyStyle();

                              sendCarsToChatByModels(chatId, tempCarModelNameSell5);

                          }
                      }
            }
        }
    }



    private List<MobileBrands> checkMobileBrandsSeller(String chatId) {

        return mobileBrandsRepository.findAll();
    }
    private List<MobileModels> checkMobileModelsSeller(String chatId) {
        return mobileModelsRepository.findAll();
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
            buy.setText("Купить (продавец свяжется с Вами)");
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

    private void confirmNewOrder(Update update, Sostoyanie sostoyanie) {
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

    private void writeOrderInDb(Order order) throws InterruptedException {

        order.setCategory(categories5);
        order.setName(name5);
        order.setPhoto(image5);
        order.setDescription(description5);
        order.setPrice(price5);
        order.setTimePublication(LocalDateTime.now());
        order.setSellerUserName(sellerUserName5);
        order.setSellerChatId(sellerChatId5);
      //  orderService.saveOrder(order.getName(),order.getDescription(), order.getPrice(), order.getPhoto(),order.getCategory(), order.getTimePublication(), order.getSellerUserName(), order.getSellerChatId());

        orderRepository.saveAndFlush(order);

        Thread.sleep(500);
if (carInDb == true) {
    saveCarInDb(carSelling5, order.getId());
}

else System.out.println("записана категория НЕ авто");
    }
    // Save Car
    private void saveCarInDb(CarSelling carSelling, long orderId) {

        carSelling.setYearOfIssue(tempYearOfIssue5);
        carSelling.setCountryOfOrigin("Страна");
        carSelling.setCarModelId(tempCarModelIdSell5);
        carSelling.setCarBrands(carBrands5);
        carSelling.setOrderId(orderId);
        carSelling.setCarModelName(tempCarModelNameSell5);
        carService.saveCar(tempYearOfIssue5, "неизвестно", tempCarModelIdSell5, carBrands5, orderId, tempCarModelNameSell5);
        }


    private List<Categories> checkCategorySeller(String chatId) {

        return categoryRepository.findAll();

    }
    // Auto Logic
    private List<CarBrands> checkCarBrandsSeller(String chatId) {

        return carBrandRepository.findAll();
    }
    private List<CarModels> checkCarModelsSeller(String chatId) {

        return carModelRepository.findAll();
    }

    private List<CarBrands> checkCarBandsBuy(String chatId) {

        return carBrandRepository.findAll();
    }

    private List<CarModels> checkCarModelsBuy(String chatId) {

        return carModelRepository.findAll();
    }
    /////////

    private List<Categories> checkCategoryBuyer(String chatId) {

        return categoryRepository.findAll();
    }

    private void sendAdvertisementsToChat(String chatId, List<CarSelling> advertisements) {
        // Loop through advertisements and send them to the chat
        for (CarSelling advertisement : advertisements) {
            String messageText = "Advertisement ID: " + advertisement.getId()
                    + "\nCar Brand: " + advertisement.getCarBrands().getName()
                    + "\nYear of Issue: " + advertisement.getYearOfIssue()
                    + "\nCountry of Origin: " + advertisement.getCountryOfOrigin();

            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(messageText)
                    .build();

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    // Метод предназначенный для отображения автомобилей одной марки и з двух таблиц
    private List<CarWithOrderInfo> getCarsWithOrderInfoByModel(String carModelName) {
        List<CarSelling> carSellingsByModel = carSellingRepository.findByCarModelName(carModelName);
        List<CarWithOrderInfo> carsWithOrderInfo = new ArrayList<>();

        for (CarSelling carSelling : carSellingsByModel) {
            Order order = orderRepository.findById(carSelling.getOrderId()).orElse(null);

            if (order != null) {
                CarWithOrderInfo carWithOrderInfo = new CarWithOrderInfo(carSelling, order);
                carsWithOrderInfo.add(carWithOrderInfo);
            }
        }

        return carsWithOrderInfo;
    }

    /**
     * Метод использующий getCarsWithOrderInfoByModel для работы
     */

    private void sendCarsToChatByModels(String chatId, String carModelName) {
        List<CarWithOrderInfo> carsWithOrderInfo = getCarsWithOrderInfoByModel(carModelName);

        for (CarWithOrderInfo carWithOrderInfo : carsWithOrderInfo) {
            CarSelling carSelling = carWithOrderInfo.getCarSelling();
            Order order = carWithOrderInfo.getOrder();




            String messageText = "Car Brand: " + carSelling.getCarBrands().getName()
                    + "\nCar Model: " + carSelling.getCarModelName()
                    + "\nYear of Issue: " + carSelling.getYearOfIssue()
                    + "\nCountry of Origin: " + carSelling.getCountryOfOrigin()
                    + "\nOrder Name: " + order.getName()
                    + "\nOrder Description: " + order.getDescription()
                    + "\nOrder Price: " + order.getPrice();


            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(messageText)
                    .build();

            String photoByOrderId = getOrderPhotoBy(order);
            InputFile inputFile = new InputFile(photoByOrderId);

            SendPhoto photoMessage = SendPhoto.builder()
                    .chatId(chatId)
                    .photo(inputFile)
                    .build();



            try {
                execute(message);
                execute(photoMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
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
        public final int checkCategoryForBuy = 19;
        private final int buyOrder = 22;
        private final int myPurchases = 23;
        private final int myGoods = 24;
        private final int backStart = 25;
        private final int chooseCarBrandForSell = 26;
        private final int chooseCarModelForSell = 27;
        private final int setCarCreateYear = 28;
        private final int writeYearOfIssue = 29;
        private final int chooseCarBrandForBuy = 30;
        private final int chooseCarModelForBuy = 31;
        private final int seeCarsByModel = 32;
        private final int chooseMobileBrandForSell = 33;
        private final int chooseMobileModelForSell = 34;
        private final int setMobileColor = 35;
        private final int writeColorOfMobile = 36;
    }
}










