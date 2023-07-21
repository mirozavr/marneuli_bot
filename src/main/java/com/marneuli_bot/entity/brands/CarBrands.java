package com.marneuli_bot.entity.brands;

import com.marneuli_bot.entity.Categories;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "car_brands")
public class CarBrands {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "brand")
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories category;

}
