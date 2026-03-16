package org.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("Application started!");
    }
    @Bean
    public CommandLineRunner demo(PurchaseRepository purchaseRepository, WarehouseRepository warehouseRepository) {
        return (args) -> {
            // Wir erstellen einen Test-Verkauf
            Purchase p1 = new Purchase();
            p1.setDateTime("2026-03-16 14:00:00");
            p1.setAmount(5);
            p1.setWarehouseID("001");

            purchaseRepository.save(p1);
            System.out.println("Test-Kauf wurde gespeichert!");
        };
    }
}