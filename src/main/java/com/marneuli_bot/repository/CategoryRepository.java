package com.marneuli_bot.repository;

import com.marneuli_bot.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends JpaRepository<Categories, Long> {


}
