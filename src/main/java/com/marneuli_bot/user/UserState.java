package com.marneuli_bot.user;

import com.marneuli_bot.entity.Categories;
import lombok.Data;

@Data

public class UserState {
    private Categories selectedCategory;
    private byte[] photo;
    private String name;
    private String description;
    private int sostoyanie;
}
