# Order Service - Order Management

Manages the complete order lifecycle from cart to delivery, including prescription validation and inventory coordination.

## 📋 Overview

Handles shopping cart, checkout process, order placement, and order status management with integration to inventory and prescription services.

**Port**: 8085

## 🎯 Key Features

- Shopping cart management
- Prescription validation at checkout
- Inventory reservation coordination
- Order lifecycle management
- Discount and loyalty points application
- Order history and tracking
- Quick reorder functionality
- Admin order management

## 🗄️ Database Schema

```sql
CREATE TABLE carts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart_items (
    id UUID PRIMARY KEY,
    cart_id UUID REFERENCES carts(id),
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    prescription_required BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    cart_id UUID,
    delivery_address_id UUID,
    prescription_id UUID,
    subtotal DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    loyalty_discount DECIMAL(10,2) DEFAULT 0,
    gst_amount DECIMAL(10,2) NOT NULL,
    delivery_charge DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(30) DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    payment_status VARCHAR(30),
    placed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID REFERENCES orders(id),
    product_id UUID NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    gst_rate DECIMAL(5,2),
    batch_no VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_status_history (
    id UUID PRIMARY KEY,
    order_id UUID REFERENCES orders(id),
    status VARCHAR(30) NOT NULL,
    changed_by UUID,
    notes TEXT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 📊 Order Status Lifecycle

```
CART → PENDING → PRESCRIPTION_REQUIRED → PAYMENT_PENDING → 
CONFIRMED → PROCESSING → SHIPPED → DELIVERED

Alternative flows:
→ CANCELLED
→ RETURNED
```

## 📡 REST API Endpoints

### Cart Management

```http
# Add item to cart
POST /api/v1/orders/cart/add
{
  "productId": "uuid",
  "quantity": 2
}

# Update cart item
PUT /api/v1/orders/cart/update/{itemId}
{
  "quantity": 3
}

# Remove from cart
DELETE /api/v1/orders/cart/remove/{itemId}

# Get cart
GET /api/v1/orders/cart

# Clear cart
DELETE /api/v1/orders/cart
```

### Checkout & Orders

```http
# Checkout
POST /api/v1/orders/checkout
{
  "deliveryAddressId": "uuid",
  "prescriptionId": "uuid",
  "paymentMethod": "CARD",
  "discountCode": "SAVE10",
  "loyaltyPointsToRedeem": 100
}

Response: 201 Created
{
  "orderId": "uuid",
  "totalAmount": 450.00,
  "status": "PAYMENT_PENDING",
  "paymentUrl": "https://payment-gateway.com/..."
}

# Get my orders
GET /api/v1/orders/me?page=0&size=20

# Get order details
GET /api/v1/orders/me/{orderId}

# Cancel order
POST /api/v1/orders/me/{orderId}/cancel
{
  "reason": "Changed my mind"
}

# Reorder
POST /api/v1/orders/me/{orderId}/reorder
```

### Admin Endpoints

```http
# Get all orders
GET /api/v1/orders?page=0&size=20&status=CONFIRMED

# Update order status
PUT /api/v1/orders/{orderId}/status
{
  "status": "SHIPPED",
  "notes": "Shipped via FedEx. Tracking: 123456"
}
```

## 🛒 Cart Service

```java
@Service
public class CartService {
    
    @Transactional
    public CartResponse addToCart(UUID userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);
        
        // Check if item already exists
        Optional<CartItem> existingItem = cart.getItems().stream()
            .filter(item -> item.getProductId().equals(request.getProductId()))
            .findFirst();
        
        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            // Get product details
            Product product = productServiceClient.getProduct(request.getProductId());
            
            // Create new cart item
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProductId(product.getId());
            item.setQuantity(request.getQuantity());
            item.setUnitPrice(product.getSellingPrice());
            item.setPrescriptionRequired(product.isPrescriptionRequired());
            
            cart.getItems().add(item);
        }
        
        cart = cartRepository.save(cart);
        return mapToResponse(cart);
    }
}
```

## ✅ Checkout Validation

```java
@Service
public class CheckoutService {
    
    @Transactional
    public OrderResponse checkout(UUID userId, CheckoutRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new CartNotFoundException(userId));
        
        // 1. Validate prescription if required
        validatePrescriptionRequirements(cart, request.getPrescriptionId(), userId);
        
        // 2. Check inventory availability and reserve
        reserveInventory(cart);
        
        // 3. Apply discounts and loyalty points
        OrderCalculation calculation = calculateOrderTotal(cart, request);
        
        // 4. Create order
        Order order = createOrder(userId, cart, request, calculation);
        
        // 5. Clear cart
        cartRepository.delete(cart);
        
        // 6. Publish order placed event
        publishOrderPlacedEvent(order);
        
        return mapToResponse(order);
    }
    
    private void validatePrescriptionRequirements(Cart cart, UUID prescriptionId, UUID userId) {
        boolean hasPrescriptionItems = cart.getItems().stream()
            .anyMatch(CartItem::isPrescriptionRequired);
        
        if (hasPrescriptionItems) {
            if (prescriptionId == null) {
                throw new PrescriptionRequiredException("Cart contains prescription items");
            }
            
            // Validate prescription with Prescription Service
            prescriptionServiceClient.validatePrescription(prescriptionId, userId);
        }
    }
    
    private void reserveInventory(Cart cart) {
        for (CartItem item : cart.getItems()) {
            try {
                inventoryServiceClient.reserveStock(
                    item.getProductId(),
                    item.getQuantity()
                );
            } catch (InsufficientStockException e) {
                throw new CheckoutException("Product " + item.getProductId() + " is out of stock");
            }
        }
    }
}
```

## 💰 Order Calculation

```java
@Service
public class OrderCalculationService {
    
    public OrderCalculation calculateTotal(Cart cart, CheckoutRequest request) {
        BigDecimal subtotal = cart.getItems().stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Apply discount code
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getDiscountCode() != null) {
            discountAmount = loyaltyServiceClient.validateAndCalculateDiscount(
                request.getDiscountCode(),
                subtotal
            );
        }
        
        // Apply loyalty points
        BigDecimal loyaltyDiscount = BigDecimal.ZERO;
        if (request.getLoyaltyPointsToRedeem() != null) {
            loyaltyDiscount = loyaltyServiceClient.calculateLoyaltyDiscount(
                request.getLoyaltyPointsToRedeem(),
                subtotal
            );
        }
        
        // Calculate GST
        BigDecimal gstAmount = subtotal
            .subtract(discountAmount)
            .subtract(loyaltyDiscount)
            .multiply(BigDecimal.valueOf(0.12)); // 12% GST
        
        // Delivery charge
        BigDecimal deliveryCharge = subtotal.compareTo(BigDecimal.valueOf(500)) >= 0
            ? BigDecimal.ZERO
            : BigDecimal.valueOf(50);
        
        // Total
        BigDecimal total = subtotal
            .subtract(discountAmount)
            .subtract(loyaltyDiscount)
            .add(gstAmount)
            .add(deliveryCharge);
        
        return new OrderCalculation(
            subtotal,
            discountAmount,
            loyaltyDiscount,
            gstAmount,
            deliveryCharge,
            total
        );
    }
}
```

## 📨 Event Handling

```java
@Component
public class OrderEventPublisher {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void publishOrderPlaced(Order order) {
        OrderPlacedEvent event = new OrderPlacedEvent(
            order.getId(),
            order.getUserId(),
            order.getTotalAmount()
        );
        rabbitTemplate.convertAndSend("order.exchange", "order.placed", event);
    }
    
    public void publishOrderConfirmed(Order order) {
        OrderConfirmedEvent event = new OrderConfirmedEvent(order.getId());
        rabbitTemplate.convertAndSend("order.exchange", "order.confirmed", event);
    }
    
    public void publishOrderCancelled(Order order) {
        OrderCancelledEvent event = new OrderCancelledEvent(order.getId());
        rabbitTemplate.convertAndSend("order.exchange", "order.cancelled", event);
    }
}
```

## 🚀 Running the Service

```bash
mvn spring-boot:run
```

## 📄 License

MIT License - Part of PharmaOrder Platform
