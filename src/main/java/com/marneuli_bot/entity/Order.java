package com.marneuli_bot.entity;

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
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    private String description;

    @Lob
    @Column(name = "photo", columnDefinition = "bytea")
    private byte[] photo;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories category;

}
