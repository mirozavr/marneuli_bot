package com.marneuli_bot.entity.brands;

import com.marneuli_bot.entity.Order;
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
@Table(name = "car_sell")
public class CarSelling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "year_created")
    private int yearOfIssue;

    @Column(name = "country_of_origin")
    private String countryOfOrigin;


    @Column(name = "car_model_id")
    private long carModels;

    @ManyToOne
    @JoinColumn(name = "car_brand_id")
    private CarBrands carBrands;


    @Column(name = "order_id")
    private long orderId;
}
