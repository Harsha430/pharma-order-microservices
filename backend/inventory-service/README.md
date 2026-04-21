# Inventory Service - Stock Management

Manages product inventory, stock reservations, batch tracking, and automatic stock deduction for orders.

## 📋 Overview

Tracks stock levels with batch management, handles inventory reservations to prevent overselling, and provides low stock alerts.

**Port**: 8083

## 🎯 Key Features

- Real-time stock tracking
- Inventory reservation (prevent overselling)
- Batch management (expiry tracking)
- Automatic stock deduction on order confirmation
- Low stock alerts
- Inventory audit log
- Optimistic locking for concurrency

## 🗄️ Database Schema

```sql
CREATE TABLE inventory (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL UNIQUE,
    total_qty INTEGER NOT NULL DEFAULT 0,
    reserved_qty INTEGER NOT NULL DEFAULT 0,
    available_qty INTEGER NOT NULL DEFAULT 0,
    low_stock_threshold INTEGER DEFAULT 10,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inventory_batches (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    batch_no VARCHAR(50) NOT NULL,
    mfg_date DATE NOT NULL,
    exp_date DATE NOT NULL,
    quantity INTEGER NOT NULL,
    location VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inventory_reservations (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    order_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    reserved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP
);

CREATE TABLE inventory_audit_log (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    change_type VARCHAR(50) NOT NULL,
    qty_before INTEGER,
    qty_after INTEGER,
    changed_by VARCHAR(100),
    reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 📡 REST API Endpoints

```http
# Get inventory for product
GET /api/v1/inventory/{productId}

# Get low stock items (ADMIN)
GET /api/v1/inventory/low-stock

# Reserve stock (internal - called by Order Service)
POST /api/v1/inventory/reserve
{
  "productId": "uuid",
  "orderId": "uuid",
  "quantity": 2
}

# Confirm reservation (deduct stock permanently)
POST /api/v1/inventory/confirm
{
  "orderId": "uuid"
}

# Release reservation (cancel order)
POST /api/v1/inventory/release
{
  "orderId": "uuid"
}

# Manual stock adjustment (ADMIN)
POST /api/v1/inventory/adjust
{
  "productId": "uuid",
  "quantity": 100,
  "reason": "Stock replenishment"
}

# Get audit log (ADMIN)
GET /api/v1/inventory/audit-log?productId=uuid
```

## 🔒 Concurrency Control

```java
@Entity
@Table(name = "inventory")
public class Inventory {
    
    @Id
    private UUID id;
    
    @Version
    private Long version; // Optimistic locking
    
    private UUID productId;
    private Integer totalQty;
    private Integer reservedQty;
    private Integer availableQty;
    
    // Getters and setters
}

@Service
public class InventoryService {
    
    @Transactional
    @Retryable(value = OptimisticLockException.class, maxAttempts = 3)
    public void reserveStock(UUID productId, UUID orderId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new InventoryNotFoundException(productId));
        
        if (inventory.getAvailableQty() < quantity) {
            throw new InsufficientStockException(productId, quantity);
        }
        
        inventory.setReservedQty(inventory.getReservedQty() + quantity);
        inventory.setAvailableQty(inventory.getAvailableQty() - quantity);
        
        inventoryRepository.save(inventory);
        
        // Create reservation record
        InventoryReservation reservation = new InventoryReservation();
        reservation.setProductId(productId);
        reservation.setOrderId(orderId);
        reservation.setQuantity(quantity);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservationRepository.save(reservation);
        
        // Audit log
        logInventoryChange(productId, "RESERVE", quantity, "Order: " + orderId);
    }
}
```

## 📨 Event Handling

```java
@Component
public class OrderEventListener {
    
    @Autowired
    private InventoryService inventoryService;
    
    @RabbitListener(queues = "order.confirmed.queue")
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        inventoryService.confirmReservation(event.getOrderId());
    }
    
    @RabbitListener(queues = "order.cancelled.queue")
    public void handleOrderCancelled(OrderCancelledEvent event) {
        inventoryService.releaseReservation(event.getOrderId());
    }
}
```

## 🚨 Low Stock Alerts

```java
@Scheduled(cron = "0 0 9 * * *") // Daily at 9 AM
public void checkLowStock() {
    List<Inventory> lowStockItems = inventoryRepository
        .findByAvailableQtyLessThanLowStockThreshold();
    
    if (!lowStockItems.isEmpty()) {
        LowStockEvent event = new LowStockEvent(lowStockItems);
        rabbitTemplate.convertAndSend("inventory.exchange", "inventory.stock.low", event);
    }
}
```

## 🚀 Running the Service

```bash
mvn spring-boot:run
```

## 📄 License

MIT License - Part of PharmaOrder Platform
