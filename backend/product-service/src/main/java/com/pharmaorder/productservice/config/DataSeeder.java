package com.pharmaorder.productservice.config;

import com.pharmaorder.productservice.model.entity.Category;
import com.pharmaorder.productservice.model.entity.Product;
import com.pharmaorder.productservice.repository.CategoryRepository;
import com.pharmaorder.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public DataSeeder(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            seedCategories();
        }
        if (productRepository.count() == 0) {
            seedProducts();
        }
    }

    private void seedCategories() {
        Category[] categories = {
            Category.builder().name("Antibiotics").slug("antibiotics").build(),
            Category.builder().name("Vitamins & Supplements").slug("vitamins").build(),
            Category.builder().name("Skincare").slug("skincare").build(),
            Category.builder().name("Diabetes Care").slug("diabetes").build(),
            Category.builder().name("Cardiac Care").slug("cardiac").build(),
            Category.builder().name("Immunity").slug("immunity").build(),
            Category.builder().name("Pain Relief").slug("pain-relief").build(),
            Category.builder().name("Ayurvedic").slug("ayurvedic").build(),
            Category.builder().name("Medicines").slug("medicines").build(),
            Category.builder().name("Supplements").slug("supplements").build(),
            Category.builder().name("Devices").slug("devices").build(),
            Category.builder().name("Health Packages").slug("health-packages").build()
        };
        categoryRepository.saveAll(Arrays.asList(categories));
    }

    private void seedProducts() {
        Map<String, Category> categoryMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getSlug, c -> c));

        Product[] products = {
            // Antibiotics
            createProduct("Amoxicillin 500mg", "Broad-spectrum penicillin antibiotic", "48.50", categoryMap.get("antibiotics"), true, "500mg", "Strip of 10"),
            createProduct("Azithromycin 500mg", "Macrolide antibiotic for respiratory infections", "89.00", categoryMap.get("antibiotics"), true, "500mg", "Strip of 3"),
            createProduct("Ciprofloxacin 500mg", "Fluoroquinolone antibiotic for various infections", "65.20", categoryMap.get("antibiotics"), true, "500mg", "Strip of 10"),
            createProduct("Augmentin 625 Duo", "Amoxicillin and Potassium Clavulanate combination", "201.00", categoryMap.get("antibiotics"), true, "625mg", "Strip of 10"),
            createProduct("Cefixime 200mg", "Cephalosporin antibiotic for bacterial infections", "95.50", categoryMap.get("antibiotics"), true, "200mg", "Strip of 10"),

            // Pain Relief
            createProduct("Dolo 650 Tablets", "Paracetamol for pain and fever relief", "30.00", categoryMap.get("pain-relief"), false, "650mg", "Strip of 15"),
            createProduct("Saridon", "Triple action headache relief formula", "25.00", categoryMap.get("pain-relief"), false, "Standard", "Strip of 10"),

            // Cardiac Care
            createProduct("Atorvastatin 10mg", "Lipid-lowering medication for cholesterol", "78.00", categoryMap.get("cardiac"), true, "10mg", "Strip of 15"),
            createProduct("Ecosprin 75", "Aspirin for heart attack and stroke prevention", "5.45", categoryMap.get("cardiac"), true, "75mg", "Strip of 14"),

            // Diabetes Care
            createProduct("Metformin 500mg", "First-line oral medication for Type 2 Diabetes", "22.80", categoryMap.get("diabetes"), true, "500mg", "Strip of 20"),

            // Vitamins & Supplements
            createProduct("Shelcal 500", "Calcium and Vitamin D3 supplement", "118.00", categoryMap.get("vitamins"), false, "500mg", "Strip of 15"),
            createProduct("Zincovit", "Multivitamin with Zinc and minerals", "105.00", categoryMap.get("vitamins"), false, "Standard", "Strip of 15"),
            createProduct("Evion 400", "Vitamin E supplement for skin and muscle health", "35.00", categoryMap.get("vitamins"), false, "400mg", "Strip of 10"),
            createProduct("Multivitamins", "Complete daily nutrition support", "250.00", categoryMap.get("vitamins"), false, "Standard", "Bottle of 30"),
            createProduct("Vitamin C 1000mg", "High potency immunity booster", "150.00", categoryMap.get("vitamins"), false, "1000mg", "Strip of 15"),
            createProduct("Fish Oil Capsules", "Omega-3 rich heart support", "450.00", categoryMap.get("vitamins"), false, "1000mg", "Bottle of 60"),

            // Ayurvedic & Immunity
            createProduct("Chyawanprash 500g", "Ayurvedic immunity booster paste", "195.00", categoryMap.get("ayurvedic"), false, "500g", "Jar"),
            createProduct("Himalaya Ashvagandha", "Adaptogen for stress and general wellness", "165.00", categoryMap.get("ayurvedic"), false, "250mg", "Bottle of 60"),
            createProduct("Tulsi Drops", "Natural herbal extract for wellness", "210.00", categoryMap.get("ayurvedic"), false, "30ml", "Bottle"),
            createProduct("ORS Powder", "Electrolyte replacement for hydration", "20.00", categoryMap.get("immunity"), false, "Standard", "Sachet"),

            // Skincare & General
            createProduct("Betadine Ointment", "Antiseptic for minor cuts and burns", "112.00", categoryMap.get("skincare"), false, "5% w/w", "Tube of 20g"),

            // Devices
            createProduct("BP Monitor", "Digital automatic blood pressure monitor", "1499.00", categoryMap.get("devices"), false, "Digital", "Box")
        };
        productRepository.saveAll(Arrays.asList(products));
        
        Category healthPackages = categoryMap.get("health-packages");
        Category immunity = categoryMap.get("immunity");
        Category vitamins = categoryMap.get("vitamins");

        // HEALTH PACKAGES (Bundles)
        productRepository.save(Product.builder()
                .name("Post-Fever Recovery Pack")
                .description("Curated bundle for post-fever weakness and recovery.")
                .price(new BigDecimal("299.00"))
                .originalPrice(new BigDecimal("450.00"))
                .category(healthPackages)
                .isBundle(true)
                .bundleItems("Dolo 650, Zincovit, ORS Powder, Multivitamins")
                .status("ACTIVE")
                .build());

        productRepository.save(Product.builder()
                .name("Immunity Booster Bundle")
                .description("Daily defense kit for your entire family.")
                .price(new BigDecimal("599.00"))
                .originalPrice(new BigDecimal("850.00"))
                .category(healthPackages)
                .isBundle(true)
                .bundleItems("Chyawanprash, Vitamin C, Ashvagandha, Tulsi Drops")
                .onSeasonalOffer(true)
                .seasonalDiscount(new BigDecimal("50.00"))
                .status("ACTIVE")
                .build());

        productRepository.save(Product.builder()
                .name("Cardiac Essentials Kit")
                .description("Monitor and manage heart health with precision.")
                .price(new BigDecimal("1299.00"))
                .originalPrice(new BigDecimal("1800.00"))
                .category(healthPackages)
                .isBundle(true)
                .bundleItems("BP Monitor, Ecosprin 75, Fish Oil Capsules")
                .status("ACTIVE")
                .build());

        // SEASONAL OFFERS
        productRepository.save(Product.builder()
                .name("Summer Skincare Glow Pack")
                .description("Beat the heat with modern sunblocks and moisturizers.")
                .price(new BigDecimal("890.00"))
                .originalPrice(new BigDecimal("1250.00"))
                .category(categoryMap.get("skincare"))
                .onSeasonalOffer(true)
                .seasonalDiscount(new BigDecimal("100.00"))
                .status("ACTIVE")
                .build());

        productRepository.save(Product.builder()
                .name("Monsoon Immunity Shield")
                .description("Stay protected during the rains with nature's best.")
                .price(new BigDecimal("345.00"))
                .originalPrice(new BigDecimal("500.00"))
                .category(immunity)
                .onSeasonalOffer(true)
                .seasonalDiscount(new BigDecimal("30.00"))
                .status("ACTIVE")
                .build());
    }

    private Product createProduct(String name, String desc, String price, Category cat, boolean rx, String dosage, String packaging) {
        return Product.builder()
                .name(name)
                .description(desc)
                .price(new BigDecimal(price))
                .category(cat)
                .prescriptionRequired(rx)
                .dosage(dosage)
                .packaging(packaging)
                .status("ACTIVE")
                .build();
    }
}
