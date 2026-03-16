package org.example;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dateTime; // Zeit des Kaufs
    private Integer amount;   // Menge
    private String warehouseID; // Wo wurde gekauft?

    @ManyToOne
    private Product product; // Welches Produkt wurde gekauft?

    // Getter und Setter (Wichtig für Spring!)
    public Long getId() { return id; }
    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public String getWarehouseID() { return warehouseID; }
    public void setWarehouseID(String warehouseID) { this.warehouseID = warehouseID; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}