package com.marneuli_bot.repository;

import com.marneuli_bot.entity.mobile_entities.MobileBrands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileBrandsRepository extends JpaRepository<MobileBrands, Long> {


}
