package com.marneuli_bot.service;

import com.marneuli_bot.entity.Categories;
import com.marneuli_bot.entity.Order;
import com.marneuli_bot.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void saveOrder(String nameText, String descriptionText, float priceF, byte[] photoBytes, Categories category, LocalDateTime timePublication, long sellerId, String sellerUserName, long sellerChatId) {

        Order order = Order.builder()
                .name(nameText)
                .description(descriptionText)
                .price(priceF)
                .photo(photoBytes)
                .category(category)
                .timePublication(timePublication)
                .sellerId(sellerId)
                .sellerUserName(sellerUserName)
                .sellerChatId(sellerChatId)
                .build();

        orderRepository.save(order);
    }
}
