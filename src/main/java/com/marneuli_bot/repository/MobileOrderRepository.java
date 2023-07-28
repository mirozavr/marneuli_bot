package com.marneuli_bot.repository;

import com.marneuli_bot.entity.mobile_entities.MobileBrands;
import com.marneuli_bot.entity.mobile_entities.MobileModels;
import com.marneuli_bot.entity.mobile_entities.MobileOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MobileOrderRepository extends JpaRepository<MobileOrder, Long> {

    List<MobileOrder> findByPhoneModelName(MobileModels models);
    List<MobileOrder> findByPhoneBrandName(MobileBrands brands);
    List<MobileOrder> findByCategoryId(long categoryId);

    List<MobileOrder  > findBySellerChatId(long sellerId);


}
