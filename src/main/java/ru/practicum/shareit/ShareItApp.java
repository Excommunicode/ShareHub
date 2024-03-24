package ru.practicum.shareit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItApp {


    public static void main(String[] args) {
        SpringApplication.run(ShareItApp.class, args);
        System.out.println("Java shareit running on the port 8080");
    }

}
