package com.marneuli_bot.service;

import com.marneuli_bot.entity.Categories;
import com.marneuli_bot.entity.Order;
import com.marneuli_bot.entity.brands.CarBrands;
import com.marneuli_bot.entity.brands.CarModels;
import com.marneuli_bot.entity.brands.CarSelling;
import com.marneuli_bot.repository.CarBrandRepository;
import com.marneuli_bot.repository.CarModelRepository;
import com.marneuli_bot.repository.CarSellingRepository;
import com.marneuli_bot.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarService {


    private final CarSellingRepository carSellingRepository;

    @Autowired
    public CarService(CarSellingRepository carSellingRepository) {

        this.carSellingRepository = carSellingRepository;
    }

    public void saveCar(int yearOfIssue, String countryOfOrigin, long carModels, CarBrands carBrands, long orderId) {

        CarSelling carSelling = CarSelling.builder()
                .yearOfIssue(yearOfIssue)
                .countryOfOrigin(countryOfOrigin)
                .carModels(carModels)
                .carBrands(carBrands)
                .orderId(orderId)
                .build();
        carSellingRepository.save(carSelling);
    }
}
