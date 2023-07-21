package com.marneuli_bot.commands;

import com.marneuli_bot.service.bot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class TopCommands {
    @Autowired
    TelegramBot telegramBot;
    public Message topCommand;
    public String topHello;




    }
