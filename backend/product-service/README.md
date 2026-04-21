# Product Service - Medicine Catalogue Management

Manages the complete product catalogue including medicines, categories, brands, dosage forms, and packaging types.

## 📋 Overview

Central repository for all medicine and healthcare product data with advanced search, filtering, and caching capabilities.

**Port**: 8082

## 🎯 Responsibilities

- Product CRUD operations (medicines, healthcare items)
- Category management (hierarchical categories)
- Brand management
- Dosage form management (Tablet, Capsule, Syrup, etc.)
- Packaging type management
- Product search with filters
- Product image management
- Featured products and offers
- Redis caching for performance

## 🛠️ Technology Stack

- Spring Boot 3.2.4
- Spring Data JPA
- PostgreSQL
- Redis (Caching)
- Spring Cache
- Hibernate Search (optional)

## 🗄️ Database Schema

```sql
CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) UNIQUE NOT NULL,
    parent_id UUID REFERENCES categories(id),
    icon_url VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE brands (
    id UUID PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    logo_url VARCHAR(255),
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE dosage_forms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE packaging_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unit_count INTEGER,
    unit_label VARCHAR(50)
);

CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    generic_name VARCHAR(255),
    brand_id UUID REFERENCES brands(id),
    category_id UUID REFERENCES categories(id),
    dosage_form_id BIGINT REFERENCES dosage_forms(id),
    packaging_id BIGINT REFERENCES packaging_types(id),
    mrp DECIMAL(10,2) NOT NULL,
    selling_price DECIMAL(10,2) NOT NULL,
    prescription_required BOOLEAN DEFAULT FALSE,
    description TEXT,
    manufacturer VARCHAR(255),
    hsn_code VARCHAR(20),
    gst_percent DECIMAL(5,2),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product_images (
    id UUID PRIMARY KEY,
    product_id UUID REFERENCES products(id),
    file_key VARCHAR(255) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0
);
```

## 📡 REST API Endpoints

### Products

```http
# Get all products (paginated, filterable)
GET /api/v1/products?page=0&size=20&category=uuid&prescriptionRequired=true&minPrice=10&maxPrice=100

# Get product by ID
GET /api/v1/products/{id}

# Search products
GET /api/v1/products/search?q=paracetamol

# Get featured products
GET /api/v1/products/featured

# Create product (ADMIN)
POST /api/v1/products
Content-Type: application/json
{
  "name": "Paracetamol 500mg",
  "genericName": "Acetaminophen",
  "brandId": "uuid",
  "categoryId": "uuid",
  "dosageFormId": 1,
  "packagingId": 1,
  "mrp": 50.00,
  "sellingPrice": 45.00,
  "prescriptionRequired": false,
  "description": "Pain reliever and fever reducer",
  "manufacturer": "ABC Pharma",
  "hsnCode": "30049099",
  "gstPercent": 12.00
}

# Update product (ADMIN)
PUT /api/v1/products/{id}

# Delete product (ADMIN)
DELETE /api/v1/products/{id}
```

### Categories

```http
# Get all categories
GET /api/v1/categories

# Get category by ID
GET /api/v1/categories/{id}

# Get products by category
GET /api/v1/categories/{id}/products

# Create category (ADMIN)
POST /api/v1/categories
{
  "name": "Antibiotics",
  "slug": "antibiotics",
  "parentId": null,
  "iconUrl": "https://..."
}

# Update category (ADMIN)
PUT /api/v1/categories/{id}

# Delete category (ADMIN)
DELETE /api/v1/categories/{id}
```

### Brands

```http
# Get all brands
GET /api/v1/brands

# Create brand (ADMIN)
POST /api/v1/brands
{
  "name": "ABC Pharma",
  "logoUrl": "https://..."
}
```

## 🔍 Search & Filtering

### Advanced Search

```java
@Service
public class ProductSearchService {
    
    public Page<Product> searchProducts(ProductSearchCriteria criteria, Pageable pageable) {
        Specification<Product> spec = Specification.where(null);
        
        if (criteria.getQuery() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + criteria.getQuery().toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("genericName")), "%" + criteria.getQuery().toLowerCase() + "%")
                )
            );
        }
        
        if (criteria.getCategoryId() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("category").get("id"), criteria.getCategoryId())
            );
        }
        
        if (criteria.getPrescriptionRequired() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("prescriptionRequired"), criteria.getPrescriptionRequired())
            );
        }
        
        if (criteria.getMinPrice() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.greaterThanOrEqualTo(root.get("sellingPrice"), criteria.getMinPrice())
            );
        }
        
        if (criteria.getMaxPrice() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.lessThanOrEqualTo(root.get("sellingPrice"), criteria.getMaxPrice())
            );
        }
        
        return productRepository.findAll(spec, pageable);
    }
}
```

## 💾 Caching Strategy

```java
@Service
public class ProductService {
    
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }
    
    @Cacheable(value = "productList", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    @CacheEvict(value = {"products", "productList"}, allEntries = true)
    public Product updateProduct(UUID id, ProductUpdateRequest request) {
        // Update logic
    }
    
    @CacheEvict(value = {"products", "productList"}, allEntries = true)
    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }
}
```

### Redis Configuration

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 600000 # 10 minutes
  redis:
    host: localhost
    port: 6379
```

## 🚀 Running the Service

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run

# Docker
docker build -t pharmaorder/product-service:latest .
docker run -p 8082:8082 pharmaorder/product-service:latest
```

## 🧪 Testing

```bash
# Get all products
curl http://localhost:8080/api/v1/products

# Search products
curl "http://localhost:8080/api/v1/products/search?q=paracetamol"

# Get product by ID
curl http://localhost:8080/api/v1/products/{id}

# Create product (ADMIN)
curl -X POST http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Product",...}'
```

## 📊 Monitoring

### Key Metrics

- `product.search.total` - Total searches
- `product.cache.hits` - Cache hit rate
- `product.cache.misses` - Cache miss rate
- `product.created.total` - Products created
- `product.updated.total` - Products updated

## 📄 License

MIT License - Part of PharmaOrder Platform
