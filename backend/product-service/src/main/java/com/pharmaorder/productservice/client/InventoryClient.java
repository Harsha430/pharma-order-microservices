package com.pharmaorder.productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "inventory-service", path = "/api/v1/inventory")
public interface InventoryClient {

    @GetMapping("/{productId}")
    Object getStock(@PathVariable("productId") Long productId);

    @PostMapping("/bulk")
    List<Object> getBulkStock(@RequestBody List<Long> productIds);
}
