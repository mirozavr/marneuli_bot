package com.marneuli_bot.repository;

import com.marneuli_bot.entity.Order;
import com.marneuli_bot.entity.brands.CarModels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarModelRepository extends JpaRepository<CarModels, Long> {

    List<CarModels> findByCarBrandsId(long carBrandId);



}
