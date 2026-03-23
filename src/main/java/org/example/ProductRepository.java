package org.example;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    // Hier erbt Spring automatisch Methoden wie save(), deleteAll() und count()
}