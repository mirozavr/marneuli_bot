package com.marneuli_bot.service;

public class CategoryInfoBuy {

    private String category;
    private String message;
    private int sostId;

    public CategoryInfoBuy(String category, String message, int sostId) {
        this.category = category;
        this.message = message;
        this.sostId = sostId;
    }

    public String getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }

    public int getSostId() {
        return sostId;
    }
}
