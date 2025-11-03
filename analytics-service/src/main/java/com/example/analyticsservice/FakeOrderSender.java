package com.example.analyticsservice;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class FakeOrderSender {
    private static final String URL = "http://localhost:8001/api/v1/events/order-placed";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Random random = new Random();

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 100; i++) {
            Map<String, Object> event = new HashMap<>();
            event.put("orderId", UUID.randomUUID().toString());
            event.put("customerId", String.valueOf(100 + random.nextInt(900)));
            double totalAmount = 10 + random.nextDouble() * 200;
            event.put("totalAmount", Math.round(totalAmount * 100.0) / 100.0);

            // timestamp: random time in last hour
            long millis = Instant.now().minus(random.nextInt(60), ChronoUnit.MINUTES).toEpochMilli();
            event.put("timestamp", Instant.ofEpochMilli(millis).toString());

            // random products 1-5
            int productCount = 1 + random.nextInt(5);
            List<Map<String, Object>> products = new ArrayList<>();
            for (int j = 0; j < productCount; j++) {
                Map<String, Object> p = new HashMap<>();
                p.put("productId", "PROD-" + (400 + random.nextInt(100)));
                p.put("name", "Product " + (j+1));
                double price = 5 + random.nextDouble() * 50;
                p.put("price", Math.round(price * 100.0) / 100.0);
                p.put("quantity", 1 + random.nextInt(3));
                products.add(p);
            }
            event.put("products", products);

            String json = mapper.writeValueAsString(event);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Sent event " + (i+1));
        }
    }
}
