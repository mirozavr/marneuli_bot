package com.marneuli_bot.repository;

import com.marneuli_bot.entity.mobile_entities.MobileBrands;
import com.marneuli_bot.entity.mobile_entities.MobileModels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MobileModelsRepository extends JpaRepository<MobileModels, Long> {

    List<MobileModels> findByModelName (String modelName);

    List<MobileModels> findByMobileBrandId (MobileBrands phoneBrands);

}
