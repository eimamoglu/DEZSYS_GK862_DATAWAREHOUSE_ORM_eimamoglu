package org.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("Application started!");
    }

    @Bean
    public CommandLineRunner demo(PurchaseRepository purchaseRepository,
                                  ProductRepository productRepository,
                                  WarehouseRepository warehouseRepository) {
        return (args) -> {
            purchaseRepository.deleteAll();
            productRepository.deleteAll();
            warehouseRepository.deleteAll();

            // 1. Warehouses erstellen
            Warehouse w1 = new Warehouse();
            w1.setWarehouseID("001");
            w1.setWarehouseName("Linz Bahnhof");
            w1.setProductData(new ArrayList<>()); // Liste initialisieren

            Warehouse w2 = new Warehouse();
            w2.setWarehouseID("002");
            w2.setWarehouseName("Wien Hauptbahnhof");
            w2.setProductData(new ArrayList<>());

            // 2. Produkte erstellen & ZUWEISEN
            List<Product> allProducts = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                Product p = new Product();
                p.setProductID("P-ID-" + i);
                p.setProductName("Produkt " + i);
                p.setProductCategory(i % 2 == 0 ? "Hardware" : "Software");
                p.setProductQuantity(100 + i);
                p.setProductUnit("Stück");

                productRepository.save(p);
                allProducts.add(p);

                // WICHTIG: Hier die Verknüpfung machen!
                if (i <= 5) {
                    w1.getProductData().add(p); // Erste 5 zu Linz
                } else {
                    w2.getProductData().add(p); // Rest zu Wien
                }
            }

            // JETZT die Warehouses mit den verknüpften Produkten speichern
            warehouseRepository.save(w1);
            warehouseRepository.save(w2);

            // 3. Käufe (Dein Code bleibt hier gleich...)
            for (int i = 0; i < 50; i++) {
                Purchase pur = new Purchase();
                pur.setAmount((int) (Math.random() * 5) + 1);
                pur.setDateTime("2026-03-" + ((i % 28) + 1) + " 14:00:00");
                pur.setWarehouseID(i % 2 == 0 ? "001" : "002");
                pur.setProduct(allProducts.get(i % allProducts.size()));
                purchaseRepository.save(pur);
            }

            System.out.println("✅ Setup abgeschlossen!");
        };
    }
}