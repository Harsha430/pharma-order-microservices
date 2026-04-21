# Loyalty Service - Points & Offers Management

Manages loyalty points, discount codes, seasonal offers, and health packages for customer rewards.

## 📋 Overview

Handles the loyalty program including points earning, redemption, discount code validation, and promotional offers management.

**Port**: 8088

## 🎯 Key Features

- Loyalty points earning (1 point per ₹10 spent)
- Points redemption at checkout
- Discount code generation and validation
- Seasonal offers management
- Health package bundles
- Points transaction history
- Offer expiry tracking

## 🗄️ Database Schema

```sql
CREATE TABLE loyalty_accounts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    points_balance INTEGER DEFAULT 0,
    lifetime_points INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE points_ledger (
    id UUID PRIMARY KEY,
    account_id UUID REFERENCES loyalty_accounts(id),
    transaction_type VARCHAR(30) NOT NULL,
    points INTEGER NOT NULL,
    order_id UUID,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE discount_codes (
    id UUID PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    discount_type VARCHAR(20) NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    min_order_amount DECIMAL(10,2),
    max_discount_amount DECIMAL(10,2),
    usage_limit INTEGER,
    usage_count INTEGER DEFAULT 0,
    valid_from TIMESTAMP,
    valid_until TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE seasonal_offers (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    discount_percent DECIMAL(5,2),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE health_packages (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    original_price DECIMAL(10,2) NOT NULL,
    package_price DECIMAL(10,2) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE health_package_items (
    id UUID PRIMARY KEY,
    package_id UUID REFERENCES health_packages(id),
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL
);
```

## 📡 REST API Endpoints

### Loyalty Points

```http
# Get my points balance
GET /api/v1/loyalty/me/points
Authorization: Bearer {token}

Response: 200 OK
{
  "balance": 250,
  "lifetimePoints": 1500
}

# Get points history
GET /api/v1/loyalty/me/history?page=0&size=20
Authorization: Bearer {token}

Response: 200 OK
{
  "content": [
    {
      "transactionType": "EARNED",
      "points": 50,
      "orderId": "uuid",
      "description": "Points earned from order #12345",
      "createdAt": "2024-01-15T10:30:00Z"
    }
  ]
}

# Redeem points (internal - called by Order Service)
POST /api/v1/loyalty/redeem
{
  "userId": "uuid",
  "points": 100,
  "orderId": "uuid"
}
```

### Discount Codes

```http
# Validate discount code
POST /api/v1/discount-codes/validate
{
  "code": "SAVE10",
  "orderAmount": 500.00
}

Response: 200 OK
{
  "valid": true,
  "discountAmount": 50.00,
  "code": "SAVE10"
}

# Create discount code (ADMIN)
POST /api/v1/discount-codes
Authorization: Bearer {adminToken}
{
  "code": "SAVE10",
  "discountType": "PERCENTAGE",
  "discountValue": 10.00,
  "minOrderAmount": 200.00,
  "maxDiscountAmount": 100.00,
  "usageLimit": 1000,
  "validFrom": "2024-01-01T00:00:00Z",
  "validUntil": "2024-12-31T23:59:59Z"
}

# Get all discount codes (ADMIN)
GET /api/v1/discount-codes
```

### Offers

```http
# Get active offers
GET /api/v1/offers/active

Response: 200 OK
[
  {
    "id": "uuid",
    "title": "New Year Sale",
    "description": "Get 20% off on all medicines",
    "discountPercent": 20.00,
    "startDate": "2024-01-01T00:00:00Z",
    "endDate": "2024-01-31T23:59:59Z"
  }
]

# Create offer (ADMIN)
POST /api/v1/offers
Authorization: Bearer {adminToken}
{
  "title": "New Year Sale",
  "description": "Get 20% off on all medicines",
  "discountPercent": 20.00,
  "startDate": "2024-01-01T00:00:00Z",
  "endDate": "2024-01-31T23:59:59Z"
}
```

### Health Packages

```http
# Get all packages
GET /api/v1/packages

Response: 200 OK
[
  {
    "id": "uuid",
    "name": "Diabetes Care Package",
    "description": "Complete diabetes management kit",
    "originalPrice": 1500.00,
    "packagePrice": 1200.00,
    "savings": 300.00,
    "items": [...]
  }
]

# Create package (ADMIN)
POST /api/v1/packages
Authorization: Bearer {adminToken}
{
  "name": "Diabetes Care Package",
  "description": "Complete diabetes management kit",
  "originalPrice": 1500.00,
  "packagePrice": 1200.00,
  "items": [
    {
      "productId": "uuid",
      "quantity": 2
    }
  ]
}
```

## 💰 Points Calculation

```java
@Service
public class LoyaltyPointsService {
    
    private static final BigDecimal POINTS_PER_RUPEE = BigDecimal.valueOf(0.1); // 1 point per ₹10
    private static final BigDecimal RUPEES_PER_POINT = BigDecimal.valueOf(0.5); // 1 point = ₹0.50
    
    public int calculatePointsEarned(BigDecimal orderAmount) {
        return orderAmount
            .multiply(POINTS_PER_RUPEE)
            .setScale(0, RoundingMode.DOWN)
            .intValue();
    }
    
    public BigDecimal calculateRedemptionValue(int points) {
        return BigDecimal.valueOf(points)
            .multiply(RUPEES_PER_POINT);
    }
    
    @Transactional
    public void creditPoints(UUID userId, int points, UUID orderId, String description) {
        LoyaltyAccount account = getOrCreateAccount(userId);
        
        account.setPointsBalance(account.getPointsBalance() + points);
        account.setLifetimePoints(account.getLifetimePoints() + points);
        loyaltyAccountRepository.save(account);
        
        // Record transaction
        PointsLedger ledger = new PointsLedger();
        ledger.setAccountId(account.getId());
        ledger.setTransactionType(TransactionType.EARNED);
        ledger.setPoints(points);
        ledger.setOrderId(orderId);
        ledger.setDescription(description);
        pointsLedgerRepository.save(ledger);
    }
    
    @Transactional
    public void redeemPoints(UUID userId, int points, UUID orderId) {
        LoyaltyAccount account = loyaltyAccountRepository.findByUserId(userId)
            .orElseThrow(() -> new LoyaltyAccountNotFoundException(userId));
        
        if (account.getPointsBalance() < points) {
            throw new InsufficientPointsException(userId, points);
        }
        
        account.setPointsBalance(account.getPointsBalance() - points);
        loyaltyAccountRepository.save(account);
        
        // Record transaction
        PointsLedger ledger = new PointsLedger();
        ledger.setAccountId(account.getId());
        ledger.setTransactionType(TransactionType.REDEEMED);
        ledger.setPoints(-points);
        ledger.setOrderId(orderId);
        ledger.setDescription("Points redeemed for order #" + orderId);
        pointsLedgerRepository.save(ledger);
    }
}
```

## 🎟️ Discount Code Validation

```java
@Service
public class DiscountCodeService {
    
    @Transactional
    public DiscountValidationResponse validateCode(String code, BigDecimal orderAmount) {
        DiscountCode discountCode = discountCodeRepository.findByCode(code)
            .orElseThrow(() -> new InvalidDiscountCodeException(code));
        
        // Check if active
        if (!discountCode.isActive()) {
            throw new DiscountCodeInactiveException(code);
        }
        
        // Check validity period
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(discountCode.getValidFrom()) || now.isAfter(discountCode.getValidUntil())) {
            throw new DiscountCodeExpiredException(code);
        }
        
        // Check usage limit
        if (discountCode.getUsageLimit() != null && 
            discountCode.getUsageCount() >= discountCode.getUsageLimit()) {
            throw new DiscountCodeUsageLimitExceededException(code);
        }
        
        // Check minimum order amount
        if (discountCode.getMinOrderAmount() != null && 
            orderAmount.compareTo(discountCode.getMinOrderAmount()) < 0) {
            throw new MinimumOrderAmountNotMetException(
                discountCode.getMinOrderAmount()
            );
        }
        
        // Calculate discount
        BigDecimal discountAmount = calculateDiscount(discountCode, orderAmount);
        
        return new DiscountValidationResponse(true, discountAmount, code);
    }
    
    private BigDecimal calculateDiscount(DiscountCode code, BigDecimal orderAmount) {
        BigDecimal discount;
        
        if (code.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = orderAmount
                .multiply(code.getDiscountValue())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            discount = code.getDiscountValue();
        }
        
        // Apply max discount cap
        if (code.getMaxDiscountAmount() != null && 
            discount.compareTo(code.getMaxDiscountAmount()) > 0) {
            discount = code.getMaxDiscountAmount();
        }
        
        return discount;
    }
}
```

## 📨 Event Handling

```java
@Component
public class LoyaltyEventListener {
    
    @Autowired
    private LoyaltyPointsService loyaltyPointsService;
    
    @RabbitListener(queues = "order.delivered.queue")
    public void handleOrderDelivered(OrderDeliveredEvent event) {
        int points = loyaltyPointsService.calculatePointsEarned(event.getOrderAmount());
        
        loyaltyPointsService.creditPoints(
            event.getUserId(),
            points,
            event.getOrderId(),
            "Points earned from order #" + event.getOrderId()
        );
    }
}
```

## 🚀 Running the Service

```bash
mvn spring-boot:run
```

## 📄 License

MIT License - Part of PharmaOrder Platform
