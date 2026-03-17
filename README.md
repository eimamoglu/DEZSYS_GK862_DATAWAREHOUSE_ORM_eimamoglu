## Protokoll: DEZSYS_GK81_WAREHOUSE_ORM & AI Prediction
    
Name: Elyesa Imamoglu
Klasse: 4CHIT
Datum: 16. März 2026

## Einleitung
   
Dieses Projekt demonstriert die Implementierung eines Data-Warehouse-Systems mit Java Spring Boot. Der Fokus liegt auf Object-Relational Mapping (ORM) mittels JPA/Hibernate, der Verwaltung von Relationen zwischen Entitäten und der Erweiterung des Systems um eine künstliche Intelligenz (LLM) zur Absatzprognose.

## Anforderungen (Requirements)

Java SDK: 18 oder höher 
Build-Tool: Gradle 8+ 
Datenbank: MySQL 
KI-Backend: Ollama mit dem Modell phi3 (Docker Container)

## Wichtige Codebloecke

#### Grundlagen: Das User-Beispiel (Tutorial-Teil)
In diesem Schritt wurde das initiale Tutorial "Accessing data with MySQL" implementiert.

**Entity: User.java**
```java
@Entity 
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String name;
    private String email;
    // Getter und Setter...
}
```

**Controller: MainController.java (User Management)**
Hier wird gezeigt, wie neue User via POST-Request angelegt werden.
```java
@PostMapping(path="/add") 
public @ResponseBody String addNewUser (@RequestParam String name, @RequestParam String email) {
   User n = new User();
   n.setName(name);
   n.setEmail(email);
   userRepository.save(n);
   return "Saved";
}
```

#### Grundlagen: Data Warehouse Modell (Min. 2 Entities, 1 Relation)
Die XML-Struktur wurde in ein relationales Modell übersetzt. Hierbei wurde eine `@OneToMany` Relation zwischen `Warehouse` und `Product` gewählt.

**Entity: Warehouse.java**
```java
@Entity
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String warehouseID; // z.B. "001"
    private String warehouseName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "warehouse_internal_id") 
    private List<Product> productData;
    // Getter und Setter...
}
```

#### Erweiterte Grundlagen: Repository Erweiterungen & CRUD
Hier wurden spezifische Abfragemethoden im `WarehouseRepository` implementiert, um Daten über die fachliche `warehouseID` zu finden.

**Repository: WarehouseRepository.java**
```java
public interface WarehouseRepository extends CrudRepository<Warehouse, Long> {
    // Collect data specified by datawarehouseID
    Optional<Warehouse> findByWarehouseID(String warehouseID);
}
```

**Controller: WarehouseController.java (Update & Filter Logik)**
Diese Methoden erfüllen die Anforderung, einzelne Produkte eines Warenhouses zu finden oder ein Warehouse zu aktualisieren.
```java
// Ein einzelnes Produkt eines Warenhouses via warehouseID und productID finden
@GetMapping("/{warehouseID}/product/{productID}")
public Object getProductFromWarehouse(@PathVariable String warehouseID, @PathVariable String productID) {
    Warehouse w = warehouseRepository.findByWarehouseID(warehouseID).orElse(null);
    if (w != null) {
        return w.getProductData().stream()
                .filter(p -> p.getProductID().equals(productID))
                .findFirst().orElse(null);
    }
    return "Warehouse not found";
}
```

#### Erweiterte Grundlagen: Customer Purchases Modell
Zusätzlich wurde eine `Purchase`-Entity eingeführt, um Verkäufe (Time, Amount, Location) zu dokumentieren.

**Entity: Purchase.java**
```java
@Entity
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String dateTime; 
    private Integer amount;
    private String warehouseID;

    @ManyToOne
    private Product product; // Verknüpfung zum verkauften Produkt
}
```

#### Vertiefung: Data Seeding (50+ Records)
In der `Main.java` wurde ein `CommandLineRunner` implementiert, der bei jedem Start die Datenbank bereinigt und die benötigten Testdaten (Produkte und 50 Käufe) automatisch generiert.

**Main.java (Ausschnitt)**
```java
@Bean
public CommandLineRunner demo(PurchaseRepository purchaseRepository, ProductRepository productRepository) {
    return (args) -> {
        purchaseRepository.deleteAll();
        productRepository.deleteAll();

        Product p1 = new Product(); // Beispiel Produkt Erstellung
        p1.setProductID("P-100");
        p1.setProductName("Gaming Laptop");
        productRepository.save(p1);

        for (int i = 0; i < 50; i++) {
            Purchase purchase = new Purchase();
            purchase.setAmount((int) (Math.random() * 2) + 1);
            purchase.setProduct(p1);
            purchaseRepository.save(purchase);
        }
    };
}
```

#### Vertiefung: LLM Integration (Ollama / Phi-3)
Die Anbindung an das lokale Modell `phi3` erfolgt über einen HTTP-POST Request an die Ollama API.

**PredictionController.java**
Hier werden die Verkaufsdaten aus der Datenbank in einen Text-Prompt umgewandelt und an die KI gesendet.
```java
@GetMapping("/warehouse/ai/predict")
public String predict() {
    var sales = purchaseRepository.findAll();
    String prompt = "Analysiere diese Verkaufsdaten und erstelle eine Prognose: " + sales.toString();

    String jsonBody = "{" +
            "\"model\": \"phi3\"," +
            "\"prompt\": \"" + prompt + "\"," +
            "\"stream\": false" +
            "}";

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:11434/api/generate"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();
    // HTTP Client send & return response...
}
```

### Datenbank-Konfiguration (application.properties)
Ohne diese Zeilen weiß Spring nicht, wohin die Daten sollen.

```properties
spring.application.name=demo
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3308/example
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

---

## Important Commands. 


| Aktion | Befehl                                                     |
| :--- |:-----------------------------------------------------------|
| **App Start** | `./gradlew bootRun` oder durch starten von Main Klasse     |
| **Ollama Modell laden** | `ollama run phi3` nicht wirklich nötig                     |
| **API Test (Linux/Mac)** | `curl http://localhost:8080/ai/prediction` oder im Browser |
---


*   MySQL Shell Commands  
    `USE example;`
`SELECT COUNT(*) FROM purchase;`
`Select * FROM purchase;`

-- 1. Alle Daten löschen (falls noch nicht geschehen)
`SET FOREIGN_KEY_CHECKS = 0;`
`TRUNCATE TABLE product;`
`TRUNCATE TABLE warehouse;`
`SET FOREIGN_KEY_CHECKS = 1;`

-- 2. Den Auto-Increment Zähler manuell auf 1 setzen
`ALTER TABLE warehouse AUTO_INCREMENT = 1;`
`ALTER TABLE product AUTO_INCREMENT = 1;`


## Questions

* What is ORM and how is JPA used?

ORM (Object-Relational Mapping) ist eine Technik, die eine Brücke zwischen der objektorientierten Welt (Java-Klassen) und der relationalen Welt (Datenbanktabellen) schlägt. Anstatt SQL-Queries manuell zu schreiben, erlaubt ORM dem Entwickler, Datenbankinhalte als Java-Objekte zu behandeln.

JPA (Jakarta Persistence API) ist die standardisierte Schnittstelle (Spezifikation) in Java für ORM. JPA selbst führt keinen Code aus, sondern definiert die Regeln und Annotationen. Ein Framework wie Hibernate fungiert dann als die tatsächliche "Engine" (Implementierung), die diese Regeln umsetzt und die SQL-Befehle im Hintergrund generiert.

* What is the application.properties used for and where must it be stored?  

Die Datei `application.properties` wird in Spring Boot verwendet, um Konfigurationseinstellungen für die Anwendung zu speichern. Hier können Datenbankverbindungsdetails, Serverport, Logging-Level und andere wichtige Parameter definiert werden.
Die `application.properties`-Datei muss im `src/main/resources`-Verzeichnis des Projekts gespeichert werden, damit Spring Boot sie automatisch laden und verwenden kann.


* Which annotations are frequently used for entity types? Which key points must be observed?   

Die häufigsten Annotationen für Entity-Typen in JPA sind:
@Entity: Markiert eine Klasse als JPA-Entity, die in der Datenbank persistiert wird.
@Table: Optional, um den Tabellennamen zu spezifizieren (Standard ist der Klassenname).
@Id: Kennzeichnet das Primärschlüsselfeld.
@GeneratedValue: Gibt an, dass der Primärschlüssel automatisch generiert wird (z.B. AUTO, IDENTITY).
@Column: Optional, um Spaltennamen und -eigenschaften zu definieren.
Beobachtungen:
Jede Entity-Klasse muss eine eindeutige ID haben, die mit @Id markiert ist.
Die Klasse muss eine No-Args-Konstruktor haben (entweder explizit oder implizit).
Die Felder sollten private sein und über Getter/Setter zugänglich gemacht werden.
Die Klasse sollte mit @Entity annotiert sein, damit sie von JPA erkannt wird.

* What methods do you need for CRUD operations?  

CRUD steht für Create, Read, Update, Delete. In Spring Data JPA werden diese Operationen durch die JpaRepository-Schnittstelle bereitgestellt. Hier sind die wichtigsten Methoden:
Create: save(S entity) - Speichert ein neues Objekt oder aktualisiert ein bestehendes.
Read: findById(ID id) - Sucht ein Objekt anhand seiner ID.
findAll() - Gibt eine Liste aller Objekte zurück.
Update: save(S entity) - Wie bei Create, aber mit einem bestehenden Objekt (ID muss vorhanden sein).
Delete: deleteById(ID id) - Löscht ein Objekt anhand seiner ID.
delete(S entity) - Löscht ein bestimmtes Objekt.
deleteAll() - Löscht alle Objekte.



## Links & Further Resources

* Object Relational Mapping (ORM) Data Access:   
   https://docs.spring.io/spring-framework/reference/data-access/orm.html
* Accessing data with MySQL.  
   https://spring.io/guides/gs/accessing-data-mysql
* Accessing Data with JPA   
   https://spring.io/guides/gs/accessing-data-jpa
* Difference between Hibernate and Spring Data:  
   https://dzone.com/articles/what-is-the-difference-between-hibernate-and-sprin-1
* Introduction Hibernate:   
   https://vicksheet.medium.com/getting-started-with-hibernate-an-introduction-to-the-orm-framework-for-java-applications-fd97af01b7a6
* Video:   
   https://www.youtube.com/watch?v=NC-1j1grMPI&ab_channel=ManningPublications