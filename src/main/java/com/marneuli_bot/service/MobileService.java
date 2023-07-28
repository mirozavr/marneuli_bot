package com.marneuli_bot.service;

import com.marneuli_bot.entity.Categories;
import com.marneuli_bot.entity.mobile_entities.MobileBrands;
import com.marneuli_bot.entity.mobile_entities.MobileOrder;
import com.marneuli_bot.repository.MobileOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MobileService {

    private final MobileOrderRepository mobileOrderRepository;

    @Autowired
    public MobileService(MobileOrderRepository mobileOrderRepository) {
        this.mobileOrderRepository = mobileOrderRepository;
    }

    public void saveMobileOrder(String description,
                                float price,
                                byte[] photoBytes,
                                Categories categories,
                                String phoneModelName,
                                MobileBrands phoneBrandId,
                                String phoneBrandName,
                                LocalDateTime timePublication,
                                String sellerUserName,
                                Long sellerChatId
                                ) {

        MobileOrder mobileOrder = MobileOrder.builder()
                .description(description)
                .price(price)
                .photo(photoBytes)
                .category(categories)
                .phoneModelName(phoneModelName)
                .mobileBrandId(phoneBrandId)
                .phoneBrandName(phoneBrandName)
                .timePublication(timePublication)
                .sellerUserName(sellerUserName)
                .sellerChatId(sellerChatId)
                .build();
    }

}







