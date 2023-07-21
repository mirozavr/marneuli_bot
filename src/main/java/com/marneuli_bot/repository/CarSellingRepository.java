package com.marneuli_bot.repository;

import com.marneuli_bot.entity.Order;
import com.marneuli_bot.entity.brands.CarBrands;
import com.marneuli_bot.entity.brands.CarSelling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarSellingRepository extends JpaRepository<CarSelling, Long> {

    List<CarSelling> findByCarBrands(CarBrands carBrand);

    // List<CarSelling> findByCarModels(CarModels carModels);
    List<CarSelling> findByOrderId(long order);

}
