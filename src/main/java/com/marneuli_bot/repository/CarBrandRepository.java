package com.marneuli_bot.repository;

import com.marneuli_bot.entity.brands.CarBrands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarBrandRepository extends JpaRepository<CarBrands, Long> {


}
