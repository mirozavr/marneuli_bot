package com.marneuli_bot.service.brands;

import com.marneuli_bot.entity.brands.CarBrands;
import com.marneuli_bot.entity.brands.CarModels;
import com.marneuli_bot.repository.CarModelRepository;

import java.util.ArrayList;
import java.util.List;
// ...

public class CarModelInfoSell {

    private String model;

    String bodyStyle;

    String country; // на будущее если пригодится

    private long id;

    public String getBodyStyle() {
        return bodyStyle;
    }

    public void setBodyStyle(String bodyStyle) {
        this.bodyStyle = bodyStyle;
    }

    private CarBrands brandId;

    private final CarModelRepository carModelRepository;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CarBrands getBrandId() {
        return brandId;
    }

    public void setBrandId(CarBrands brandId) {
        this.brandId = brandId;
    }

    public CarModelInfoSell(CarModelRepository carModelRepository) {

        this.carModelRepository = carModelRepository;
    }


    public List<CarModelInfoSell> createCarModelList() {
        List<CarModelInfoSell> carModelsList = new ArrayList<>();

        // Get car models from the database using CarModelRepository
        List<CarModels> modelsFromDatabase = carModelRepository.findAll();

        // Convert CarModels entities to CarModelInfoSell objects
        for (CarModels carModel : modelsFromDatabase) {
            CarModelInfoSell carModelInfoSell = new CarModelInfoSell(carModelRepository);
            carModelInfoSell.setModel(carModel.getModel());
            carModelInfoSell.setBodyStyle(carModel.getBodyStyle());
            carModelInfoSell.setId(carModel.getId());
            carModelInfoSell.setBrandId(carModel.getCarBrands());
            carModelsList.add(carModelInfoSell);
        }
    return carModelsList;

    }
}












