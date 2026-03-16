package org.example;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface WarehouseRepository extends CrudRepository<Warehouse, Long> {
    // Erforderlich für: Collect data specified by datawarehouseID
    Optional<Warehouse> findByWarehouseID(String warehouseID);
}