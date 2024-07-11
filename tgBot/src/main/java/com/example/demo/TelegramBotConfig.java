package com.example.demo;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfig {

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot("7353596792:AAF1bk923eQYxevfQfM8d1VCKspBzekWvFc");
    }
}

