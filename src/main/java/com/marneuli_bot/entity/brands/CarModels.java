package com.marneuli_bot.entity.brands;

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
@Table(name = "car_models")
public class CarModels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "model")
    private String model;

    @Column(name = "body_style") // тип кузова (Седан, Хэтчбек, Универсал, Лифтбэк, Купе, Кабриолет, Родстер, Стретч, Тарга, Внедорожник, Кроссовер, Пикап, Фургон, Минивэн)
    private String bodyStyle;

    @ManyToOne
    @JoinColumn(name = "car_brand_id")
    private CarBrands carBrands;
}
