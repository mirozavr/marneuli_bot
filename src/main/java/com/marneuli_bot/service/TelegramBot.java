package com.marneuli_bot.service;

import com.marneuli_bot.config.BotConfig;
import com.marneuli_bot.entity.Categories;
import com.marneuli_bot.repository.CategoryRepository;
import com.marneuli_bot.repository.OrderRepository;
import com.marneuli_bot.user.UserState;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.*;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Map<String, Integer> userStates = new HashMap<>();

    private int sos;

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


        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Message command = update.getMessage();


            String hello = "Привет " + update.getMessage().getChat().getFirstName();

            switch (messageText) {
                case "/start":
                    startAnswer(command, hello);
                    break;

                default:

            }
        }    if (sos == sostoyanie.needPhotoUpload && isWaitingPhotoUpload(String.valueOf(update.getMessage().getChatId()))) {
            if (update.hasMessage() && update.getMessage().hasPhoto()) {
                String chatId = update.getMessage().getChatId().toString();
                List<PhotoSize> photos = update.getMessage().getPhoto();


                String f_id = photos.stream()
                        .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                        .findFirst()
                        .orElse(null).getFileId();
                // Know photo width
                int f_width = photos.stream()
                        .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                        .findFirst()
                        .orElse(null).getWidth();
                // Know photo height
                int f_height = photos.stream()
                        .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                        .findFirst()
                        .orElse(null).getHeight();
                // Set photo caption
                String caption = "file_id: " + f_id + "\nwidth: " + Integer.toString(f_width) + "\nheight: " + Integer.toString(f_height);
                InputFile inputFile = new InputFile(f_id);
                SendPhoto photo = SendPhoto.builder()
                        .chatId(chatId)
                        .photo(inputFile)
                        .caption(caption)
                        .build();

                try {
                    execute(photo);
                    clearNextStep(chatId);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }}
            }

        if (update.hasCallbackQuery()) {

            Message m = update.getMessage();
            Categories categories = null;
            UserState userState = null;
            if (update.getCallbackQuery().getData().equals("sell")) {
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
                        categories = category;
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

            else if (update.hasCallbackQuery()) {
                if (update.getCallbackQuery().getData().equals("category:1")) {
                    // Получение chatId
                    String chatId0 = update.getCallbackQuery().getMessage().getChatId().toString();
                    Categories selectedCategory = new Categories();
                    // Отправка сообщения с просьбой добавить фото
                    SendMessage photoMessage = SendMessage.builder()
                            .chatId(chatId0)
                            .text("Добавьте одно фото автомобиля")
                            .build();

                    try {
                        // Отправка сообщения с просьбой добавить фото
                        execute(photoMessage);
                        clearNextStep(chatId0);
                        setNextStep(chatId0);
                        sos = sostoyanie.needPhotoUpload;
                    } catch (TelegramApiException e) {
                        e.printStackTrace();

                    }

                    clearNextStep(chatId0);



                }

                if (update.getCallbackQuery().getData().equals("category:2")) {
                    String chatId2 = update.getCallbackQuery().getMessage().getChatId().toString();
                    SendMessage message = SendMessage.builder()
                            .chatId(chatId2.toString())
                            .text("Приложите фотографию автозапчасти")
                            .build();

                    try {
                        // Отправка сообщения
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else if (update.getCallbackQuery().getData().equals("category:3")) {
                    String chatId3 = update.getCallbackQuery().getMessage().getChatId().toString();
                    SendMessage message = SendMessage.builder()
                            .chatId(chatId3.toString())
                            .text("Приложите фотографию (пример работы или прайс).")
                            .build();

                    try {
                        // Отправка сообщения
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private boolean isWaitingPhotoUpload(String chatId) {
        userStates.put(chatId, 1);
        return userStates.containsKey(chatId);
    }
    private boolean isWaitingWriteName(String chatId) {
        userStates.put(chatId, 2);
        return userStates.containsKey(chatId);
    }
    private boolean isWaitingWriteDescription(String chatId) {
        userStates.put(chatId, 3);
        return userStates.containsKey(chatId);
    }
    private void clearNextStep(String chatId) {
        userStates.remove(chatId, 1);
    }
    private void setNextStep(String chatId) {
        userStates.put(chatId, +1);
    }


    private List<Categories> checkCategorySeller(String chatId) {

        return categoryRepository.findAll();

    }


    private void startAnswer(Message message, String hello) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();


        InlineKeyboardButton sell = new InlineKeyboardButton();
        sell.setText("Продать");
        sell.setCallbackData("sell");

        InlineKeyboardButton buy = new InlineKeyboardButton();
        buy.setText("Купить");
        buy.setCallbackData("start1");

        InlineKeyboardButton myPurchases = new InlineKeyboardButton();
        myPurchases.setText("Мои покупки");
        myPurchases.setCallbackData("start2");

        InlineKeyboardButton myGoods = new InlineKeyboardButton();
        myGoods.setText("Мои товары/услуги");
        myGoods.setCallbackData("start3");

        InlineKeyboardButton botRules = new InlineKeyboardButton();
        botRules.setText("Правила");
        botRules.setCallbackData("start4");

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

    private void downloadAndSavePhoto(String fileId) {
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
    }



    public class Sostoyanie {
        private int sss;

        final int sell = 1;
        private final int sellProcess = 9;
        private final int buy = 2;


        private final int mySells = 3;
        private final int MyBuys = 4;

        private final int deleteMyGood = 5;
        private final int needPhotoUpload = 10;
        private final int photoUploaded = 6;
        final int checkCategoryForSell = 7;
        private final int getCheckCategoryForBuy = 8;
    }
}









