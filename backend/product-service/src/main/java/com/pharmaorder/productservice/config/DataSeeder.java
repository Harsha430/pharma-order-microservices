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
            createProduct("Combiflam Tablets", "Ibuprofen and Paracetamol combination", "42.00", categoryMap.get("pain-relief"), false, "400mg + 325mg", "Strip of 20"),
            createProduct("Voveran SR 100", "Diclofenac for persistent pain relief", "155.00", categoryMap.get("pain-relief"), true, "100mg", "Strip of 10"),
            createProduct("Zerodol P", "Aceclofenac and Paracetamol for inflammation", "108.00", categoryMap.get("pain-relief"), true, "100mg + 325mg", "Strip of 10"),
            createProduct("Saridon", "Triple action headache relief formula", "25.00", categoryMap.get("pain-relief"), false, "Standard", "Strip of 10"),

            // Cardiac Care
            createProduct("Atorvastatin 10mg", "Lipid-lowering medication for cholesterol", "78.00", categoryMap.get("cardiac"), true, "10mg", "Strip of 15"),
            createProduct("Amlodipine 5mg", "Calcium channel blocker for hypertension", "18.50", categoryMap.get("cardiac"), true, "5mg", "Strip of 15"),
            createProduct("Telmisartan 40mg", "Angiotensin II receptor antagonist", "92.00", categoryMap.get("cardiac"), true, "40mg", "Strip of 30"),
            createProduct("Ecosprin 75", "Aspirin for heart attack and stroke prevention", "5.45", categoryMap.get("cardiac"), true, "75mg", "Strip of 14"),
            createProduct("Metoprolol 25mg", "Beta-blocker for heart rhythm and pressure", "45.00", categoryMap.get("cardiac"), true, "25mg", "Strip of 10"),
            createProduct("Rosuvastatin 20mg", "High-intensity statin for intensive lipid control", "210.00", categoryMap.get("cardiac"), true, "20mg", "Strip of 15"),

            // Diabetes Care
            createProduct("Metformin 500mg", "First-line oral medication for Type 2 Diabetes", "22.80", categoryMap.get("diabetes"), true, "500mg", "Strip of 20"),
            createProduct("Glimepiride 2mg", "Sulfonylurea to lower blood sugar", "54.00", categoryMap.get("diabetes"), true, "2mg", "Strip of 15"),
            createProduct("Jardiance 10mg", "SGLT2 inhibitor for blood glucose management", "840.00", categoryMap.get("diabetes"), true, "10mg", "Strip of 10"),
            createProduct("Januvia 100mg", "DPP-4 inhibitor for adults with type 2 diabetes", "1150.00", categoryMap.get("diabetes"), true, "100mg", "Strip of 7"),
            createProduct("Glycomet GP 1", "Metformin and Glimepiride dual-action therapy", "125.00", categoryMap.get("diabetes"), true, "500mg + 1mg", "Strip of 15"),

            // Vitamins & Supplements
            createProduct("Shelcal 500", "Calcium and Vitamin D3 supplement", "118.00", categoryMap.get("vitamins"), false, "500mg", "Strip of 15"),
            createProduct("Neurobion Forte", "Vitamin B-complex for nerve health", "34.50", categoryMap.get("vitamins"), false, "Standard", "Strip of 30"),
            createProduct("Zincovit", "Multivitamin with Zinc and minerals", "105.00", categoryMap.get("vitamins"), false, "Standard", "Strip of 15"),
            createProduct("Becosules Capsules", "High potency B-complex vitamins with C", "45.00", categoryMap.get("vitamins"), false, "Standard", "Strip of 20"),
            createProduct("Evion 400", "Vitamin E supplement for skin and muscle health", "35.00", categoryMap.get("vitamins"), false, "400mg", "Strip of 10"),
            createProduct("Revital H", "Daily health supplement for energy and immunity", "310.00", categoryMap.get("vitamins"), false, "Standard", "Box of 30"),

            // Ayurvedic & Immunity
            createProduct("Chyawanprash 500g", "Ayurvedic immunity booster paste", "195.00", categoryMap.get("ayurvedic"), false, "500g", "Jar"),
            createProduct("Himalaya Ashvagandha", "Adaptogen for stress and general wellness", "165.00", categoryMap.get("ayurvedic"), false, "250mg", "Bottle of 60"),
            createProduct("Tulsi Tablets", "Herbal support for respiratory health", "145.00", categoryMap.get("ayurvedic"), false, "Standard", "Bottle of 60"),
            createProduct("Liv.52 DS", "Liver health and protection supplement", "180.00", categoryMap.get("ayurvedic"), false, "Double Strength", "Bottle of 60"),
            createProduct("Septilin", "Immunity supporting herbal formula", "140.00", categoryMap.get("ayurvedic"), false, "Standard", "Bottle of 60"),

            // Skincare & General
            createProduct("Beclomethasone Cream", "Topical steroid for skin inflammation", "55.00", categoryMap.get("skincare"), true, "0.025% w/w", "Tube of 20g"),
            createProduct("Ketoconazole Shampoo", "Anti-fungal treatment for dandruff", "245.00", categoryMap.get("skincare"), false, "2% w/v", "Bottle of 60ml"),
            createProduct("Eucerin Moisturizer", "Dermatological lotion for sensitive skin", "890.00", categoryMap.get("skincare"), false, "250ml", "Bottle"),
            createProduct("Betadine Ointment", "Antiseptic for minor cuts and burns", "112.00", categoryMap.get("skincare"), false, "5% w/w", "Tube of 20g"),

            // Respiratory & Others
            createProduct("Cetirizine 10mg", "Antihistamine for allergy relief", "12.00", categoryMap.get("immunity"), false, "10mg", "Strip of 10"),
            createProduct("Montelukast 10mg", "Leukotriene receptor antagonist for asthma", "145.00", categoryMap.get("immunity"), true, "10mg", "Strip of 10"),
            createProduct("Allegra 120mg", "Fexofenadine for seasonal allergies", "215.00", categoryMap.get("immunity"), false, "120mg", "Strip of 10"),
            createProduct("Levocetirizine 5mg", "Second-generation antihistamine", "48.00", categoryMap.get("immunity"), false, "5mg", "Strip of 10"),
            createProduct("Ascoril LS Syrup", "Expectorant for cough and congestion", "118.00", categoryMap.get("immunity"), true, "Expectorant", "Bottle of 100ml")
        };
        productRepository.saveAll(Arrays.asList(products));
        Category medicines = categoryRepository.findByName("Medicines").orElseThrow();
        Category supplements = categoryRepository.findByName("Supplements").orElseThrow();
        Category devices = categoryRepository.findByName("Devices").orElseThrow();
        Category healthPackages = categoryRepository.findByName("Health Packages").orElseThrow();

        // Products with Offers
        productRepository.save(Product.builder()
                .name("Paracetamol 500mg")
                .description("Relief from pain and fever")
                .price(new BigDecimal("4.50"))
                .originalPrice(new BigDecimal("6.00")) // Offer
                .category(medicines)
                .prescriptionRequired(false)
                .status("ACTIVE")
                .build());

        productRepository.save(Product.builder()
                .name("Amoxicillin 250mg")
                .description("Antibiotic for bacterial infections")
                .price(new BigDecimal("12.99"))
                .category(medicines)
                .prescriptionRequired(true)
                .status("ACTIVE")
                .build());

        productRepository.save(Product.builder()
                .name("Amoxicillin 500mg")
                .description("High strength antibiotic")
                .price(new BigDecimal("18.50"))
                .category(medicines)
                .prescriptionRequired(true)
                .status("ACTIVE")
                .build());

        productRepository.save(Product.builder()
                .name("Vitamin C 1000mg")
                .description("Immunity booster and antioxidant")
                .price(new BigDecimal("9.99"))
                .originalPrice(new BigDecimal("14.50")) // Offer
                .category(supplements)
                .prescriptionRequired(false)
                .status("ACTIVE")
                .build());

        productRepository.save(Product.builder()
                .name("Omega-3 Triple Strength")
                .description("Heart and joint health support")
                .price(new BigDecimal("24.90"))
                .category(supplements)
                .prescriptionRequired(false)
                .status("ACTIVE")
                .build());

        productRepository.save(Product.builder()
                .name("Glucosamine Complex")
                .description("Joint health formula")
                .price(new BigDecimal("19.99"))
                .category(supplements)
                .prescriptionRequired(false)
                .status("ACTIVE")
                .build());

        productRepository.save(Product.builder()
                .name("Immunity Booster Bundle (5-in-1)")
                .description("Complete protection kit: Vit C, Vit D3, Zinc, Multivitamins, and Hand Sanitizer")
                .price(new BigDecimal("45.00"))
                .originalPrice(new BigDecimal("65.00")) // Bundle Offer
                .category(healthPackages)
                .prescriptionRequired(false)
                .status("ACTIVE")
                .build());

        productRepository.save(Product.builder()
                .name("Seasonal Allergy Kit")
                .description("Complete relief: Antihistamines, Nasal Spray, and Eye Drops")
                .price(new BigDecimal("29.99"))
                .originalPrice(new BigDecimal("39.99"))
                .category(healthPackages)
                .prescriptionRequired(false) // Assuming OTC for kit components
                .status("ACTIVE")
                .build());

        productRepository.save(Product.builder()
                .name("Blood Pressure Monitor")
                .description("Digital upper arm BP monitor with memory")
                .price(new BigDecimal("49.90"))
                .category(devices)
                .prescriptionRequired(false)
                .status("ACTIVE")
                .build());

        productRepository.save(Product.builder()
                .name("Digital Thermometer")
                .description("Fast and accurate fever reading")
                .price(new BigDecimal("7.50"))
                .originalPrice(new BigDecimal("12.00"))
                .category(devices)
                .prescriptionRequired(false)
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
