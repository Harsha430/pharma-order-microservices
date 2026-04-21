package com.pharmaorder.inventoryservice.config;

import com.pharmaorder.inventoryservice.model.entity.Inventory;
import com.pharmaorder.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class DataSeeder implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;
    private final Random random = new Random();

    public DataSeeder(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void run(String... args) {
        if (inventoryRepository.count() == 0) {
            seedInventory();
        }
    }

    private void seedInventory() {
        List<Inventory> inventoryList = new ArrayList<>();
        // Seed stock for Product IDs 1 to 60 (covering all medicines in product-service seeder)
        for (long i = 1; i <= 60; i++) {
            int total = 100 + random.nextInt(400); // 100 to 500
            int reserved = random.nextInt(20);
            
            inventoryList.add(Inventory.builder()
                    .productId(i)
                    .totalQuantity(total)
                    .availableQuantity(total - reserved)
                    .reservedQuantity(reserved)
                    .lowStockThreshold(20)
                    .build());
        }
        inventoryRepository.saveAll(inventoryList);
        System.out.println("Inventory seeded for 60 products.");
    }
}
