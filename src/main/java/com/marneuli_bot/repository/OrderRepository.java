package com.marneuli_bot.repository;

import com.marneuli_bot.entity.Categories;
import com.marneuli_bot.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {



    List<Order> findByCategory(Categories category);

    List<Order> findByCategoryAndCategoryId(Categories category, long categoryId);

    List<Order> findByCategoryId(long categoryId);

    List<Order> findBySellerChatId(long sellerId);




}
