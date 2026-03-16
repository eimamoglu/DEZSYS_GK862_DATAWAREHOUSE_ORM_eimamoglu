package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path="/warehouse")
public class WarehouseController {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @GetMapping("/purchases/all")
    public Iterable<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    // Neues Warehouse speichern
    @PostMapping(path="/add")
    public String addWarehouse(@RequestBody Warehouse warehouse) {
        warehouseRepository.save(warehouse);
        return "Warehouse Saved";
    }

    // Alle anzeigen
    @GetMapping(path="/all")
    public Iterable<Warehouse> getAll() {
        return warehouseRepository.findAll();
    }

    // Update über die warehouseID
    @PutMapping(path="/update/{warehouseID}")
    public String updateWarehouse(@PathVariable String warehouseID, @RequestBody Warehouse newData) {
        return warehouseRepository.findByWarehouseID(warehouseID).map(w -> {
            w.setWarehouseName(newData.getWarehouseName());
            w.setWarehouseAddress(newData.getWarehouseAddress());
            warehouseRepository.save(w);
            return "Updated";
        }).orElse("Not Found");
    }
    // Löschen über die warehouseID
    @DeleteMapping(path="/delete/{id}")
    public String deleteWarehouse(@PathVariable Long id) {
        warehouseRepository.deleteById(id);
        return "Warehouse und zugehörige Produkte gelöscht!";
    }

    // 1. Alle Daten eines spezifischen Warenhouses (via datawarehouseID)
    @GetMapping("/{warehouseID}")
    public Optional<Warehouse> getWarehouseById(@PathVariable String warehouseID) {
        return warehouseRepository.findByWarehouseID(warehouseID);
    }

    // 2. Ein einzelnes Produkt eines Warenhouses (via warehouseID und productID)
    @GetMapping("/{warehouseID}/product/{productID}")
    public Object getProductFromWarehouse(@PathVariable String warehouseID, @PathVariable String productID) {
        Warehouse w = warehouseRepository.findByWarehouseID(warehouseID).orElse(null);
        if (w != null) {
            return w.getProductData().stream()
                    .filter(p -> p.getProductID().equals(productID))
                    .findFirst()
                    .orElse(null);
        }
        return "Warehouse not found";
    }
}