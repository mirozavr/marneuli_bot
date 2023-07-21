package com.marneuli_bot.service.brands;

import java.util.ArrayList;
import java.util.List;

public class CarBrandInfoSell {
    private String brand;

    private String message;

    private int sostId;

    public CarBrandInfoSell(String brand, String message, int sostId) {
        this.brand = brand;
        this.message = message;
        this.sostId = sostId;
    }

    public CarBrandInfoSell() {

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

    public List<CarBrandInfoSell> createCarBrandList() {
        List<CarBrandInfoSell> carBrandsList = new ArrayList<>();
        carBrandsList.add(new CarBrandInfoSell("car_brand:1", "Audi", 1));
        carBrandsList.add(new CarBrandInfoSell("car_brand:2", "BMW", 2));
        carBrandsList.add(new CarBrandInfoSell("car_brand:3", "Mercedes-Benz", 3));
        carBrandsList.add(new CarBrandInfoSell("car_brand:4", "Honda", 4));
        carBrandsList.add(new CarBrandInfoSell("car_brand:5", "Ford", 5));
        carBrandsList.add(new CarBrandInfoSell("car_brand:6", "Huyndai", 6));
        carBrandsList.add(new CarBrandInfoSell("car_brand:7", "Kia", 7));
        carBrandsList.add(new CarBrandInfoSell("car_brand:8", "Lexus", 8));
        carBrandsList.add(new CarBrandInfoSell("car_brand:9", "Mazda", 9));
        carBrandsList.add(new CarBrandInfoSell("car_brand:10", "Nissan", 10));
        carBrandsList.add(new CarBrandInfoSell("car_brand:11", "Porsche", 11));
        carBrandsList.add(new CarBrandInfoSell("car_brand:12", "Subaru", 12));
        carBrandsList.add(new CarBrandInfoSell("car_brand:13", "Suzuki", 13));
        carBrandsList.add(new CarBrandInfoSell("car_brand:14", "Tesla", 14));
        carBrandsList.add(new CarBrandInfoSell("car_brand:15", "Toyota", 15));
        carBrandsList.add(new CarBrandInfoSell("car_brand:16", "Volkswagen", 6));
        carBrandsList.add(new CarBrandInfoSell("car_brand:17", "Volvo", 17));
        carBrandsList.add(new CarBrandInfoSell("car_brand:18", "Lada", 18));

        return carBrandsList;
    }

}
