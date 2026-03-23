package org.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
public class PredictionController {

    private final PurchaseRepository purchaseRepository;

    public PredictionController(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @GetMapping("/warehouse/ai/predict")
    public String predict() {
        try {
            // Wir holen die Daten
            var sales = purchaseRepository.findAll();

            // WICHTIG: Wir putzen den Text, damit das JSON nicht kaputt geht
            String salesData = sales.toString().replace("\"", "\\\"").replace("\n", " ");

            String prompt = "Analysiere diese Verkaufsdaten und erstelle eine Prognose für die nächsten Monate: " + salesData;

            // JSON für Ollama
            String jsonBody = "{" +
                    "\"model\": \"phi3\"," + // Hier stand vorher llama3
                    "\"prompt\": \"" + prompt + "\"," +
                    "\"stream\": false" +
                    "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Jetzt kriegst du eine saubere Antwort von Ollama zurück
            return response.body();

        } catch (Exception e) {
            return "Ollama Fehler: " + e.getMessage() + ". Hast du 'ollama run llama3' im Terminal ausgeführt?";
        }
    }
}