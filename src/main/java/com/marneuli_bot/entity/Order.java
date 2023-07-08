package com.marneuli_bot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;

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

    @Column(name = "price", columnDefinition = "numeric(10,2)")
    private float price;

    @Column(name = "photo")
    private byte[] photo;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories category;

    @Column(name = "time_publication")
    private LocalDateTime timePublication;

    @Column(name = "seller_id")
    private long sellerId;

    @Column(name = "seller_username")
    private String sellerUserName;

    @Column(name = "seller_chat_id")
    private long sellerChatId;

    public void setTimePublication(LocalDateTime timePublication) {
        this.timePublication = LocalDateTime.now();
    }

}
