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
        // Force reseed for verification
        seedInventory();
    }

    private void seedInventory() {
        List<Inventory> inventoryList = new ArrayList<>();
        // Seed stock for Product IDs 1 to 100
        for (long i = 1; i <= 100; i++) {
            final long pid = i;
            int total = 500 + random.nextInt(500); // 500 to 1000
            int reserved = random.nextInt(10);
            
            Inventory inv = inventoryRepository.findByProductId(pid)
                    .orElseGet(() -> {
                        Inventory newInv = new Inventory();
                        newInv.setProductId(pid);
                        return newInv;
                    });
            
            inv.setTotalQuantity(total);
            inv.setAvailableQuantity(total - reserved);
            inv.setReservedQuantity(reserved);
            inv.setLowStockThreshold(20);
            
            inventoryList.add(inv);
        }
        inventoryRepository.saveAll(inventoryList);
        System.out.println("Inventory synchronized for 100 products with high stock.");
    }
}
