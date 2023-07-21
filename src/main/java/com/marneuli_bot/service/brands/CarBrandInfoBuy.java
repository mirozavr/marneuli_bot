package com.marneuli_bot.service.brands;

import java.util.ArrayList;
import java.util.List;

public class CarBrandInfoBuy {

    private String brand;

    private String message;

    private int sostId;

    public CarBrandInfoBuy(String brand, String message, int sostId) {
        this.brand = brand;
        this.message = message;
        this.sostId = sostId;
    }

    public CarBrandInfoBuy() {

    }

    public String getBrand() {
        return brand;
    }

    public String getMessage() {
        return message;
    }

    public int getSostId() {
        return sostId;
    }

    public List<CarBrandInfoBuy> createCarBrandList() {
        List<CarBrandInfoBuy> carBrandsList = new ArrayList<>();
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:1", "Audi", 1));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:2", "BMW", 2));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:3", "Mercedes-Benz", 3));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:4", "Honda", 4));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:5", "Ford", 5));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:6", "Huyndai", 6));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:7", "Kia", 7));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:8", "Lexus", 8));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:9", "Mazda", 9));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:10", "Nissan", 10));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:11", "Porsche", 11));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:12", "Subaru", 12));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:13", "Suzuki", 13));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:14", "Tesla", 14));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:15", "Toyota", 15));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:16", "Volkswagen", 6));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:17", "Volvo", 17));
        carBrandsList.add(new CarBrandInfoBuy("car_brand_buy:18", "Lada", 18));

        return carBrandsList;
    }
}
