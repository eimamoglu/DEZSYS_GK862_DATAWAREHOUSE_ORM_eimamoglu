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
    public CommandLineRunner demo(PurchaseRepository purchaseRepository, ProductRepository productRepository) {
        return (args) -> {
            // 1. Tabellen leeren, damit wir keine ID-Konflikte bekommen
            purchaseRepository.deleteAll();
            productRepository.deleteAll();

            System.out.println("Erstelle Stammdaten für Produkte...");

            // 2. Produkte erstellen (Passend zu deiner Product-Klasse)
            Product p1 = new Product();
            p1.setProductID("P-100");
            p1.setProductName("Gaming Laptop");
            p1.setProductCategory("Hardware");
            p1.setProductQuantity(15); // Lagerbestand
            p1.setProductUnit("Stück");

            Product p2 = new Product();
            p2.setProductID("P-200");
            p2.setProductName("Ergonomische Maus");
            p2.setProductCategory("Zubehör");
            p2.setProductQuantity(50);
            p2.setProductUnit("Stück");

            Product p3 = new Product();
            p3.setProductID("P-300");
            p3.setProductName("Monitor 27 Zoll");
            p3.setProductCategory("Hardware");
            p3.setProductQuantity(10);
            p3.setProductUnit("Stück");

            productRepository.save(p1);
            productRepository.save(p2);
            productRepository.save(p3);

            Product[] myProducts = {p1, p2, p3};

            // 3. 50 Käufe (Purchases) generieren
            System.out.println("Generiere 50 verknüpfte Transaktionen...");
            String[] locations = {"Zentrallager Berlin", "Lager Hamburg", "Express München"};

            for (int i = 0; i < 50; i++) {
                Purchase purchase = new Purchase();

                // Datum generieren (März 2026)
                int day = (i % 15) + 1;
                purchase.setDateTime("2026-03-" + (day < 10 ? "0" + day : day) + " 09:15:00");

                // Menge des Kaufs
                purchase.setAmount((int) (Math.random() * 2) + 1); // Kauft 1-2 Stück

                // Lagerort
                purchase.setWarehouseID(locations[i % locations.length]);

                // Produkt verknüpfen
                purchase.setProduct(myProducts[i % myProducts.length]);

                purchaseRepository.save(purchase);
            }

            System.out.println("Fertig! 50 hochwertige Datensätze gespeichert.");
            System.out.println("Status: " + purchaseRepository.count() + " Käufe in der DB.");
        };
    }
}