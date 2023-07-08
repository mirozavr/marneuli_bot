package com.marneuli_bot.controller;

import com.marneuli_bot.entity.Order;
import com.marneuli_bot.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PhotoController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/photos/{orderId}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long orderId) {
        // Получите фото из базы данных по orderId
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || order.getPhoto() == null) {
            // Если фото не найдено, верните соответствующий HTTP-ответ, например, 404 Not Found
            return ResponseEntity.notFound().build();
        }

        // Получите массив байт фото
        byte[] photoBytes = order.getPhoto();

        // Верните массив байт фото в теле HTTP-ответа с соответствующими заголовками
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(photoBytes);
    }
}
