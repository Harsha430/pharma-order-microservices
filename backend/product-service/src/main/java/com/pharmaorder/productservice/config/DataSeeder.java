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
            Category.builder().name("Ayurvedic").slug("ayurvedic").build()
        };
        categoryRepository.saveAll(Arrays.asList(categories));
    }

    private void seedProducts() {
        Map<String, Category> categoryMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getSlug, c -> c));

        Product[] products = {
            // Antibiotics
            createProduct("Amoxicillin 500mg", "Broad-spectrum penicillin antibiotic", "48.50", categoryMap.get("antibiotics"), true),
            createProduct("Azithromycin 500mg", "Macrolide antibiotic for respiratory infections", "89.00", categoryMap.get("antibiotics"), true),
            createProduct("Ciprofloxacin 500mg", "Fluoroquinolone antibiotic for various infections", "65.20", categoryMap.get("antibiotics"), true),
            createProduct("Augmentin 625 Duo", "Amoxicillin and Potassium Clavulanate combination", "201.00", categoryMap.get("antibiotics"), true),
            createProduct("Cefixime 200mg", "Cephalosporin antibiotic for bacterial infections", "95.50", categoryMap.get("antibiotics"), true),

            // Pain Relief
            createProduct("Dolo 650 Tablets", "Paracetamol for pain and fever relief", "30.00", categoryMap.get("pain-relief"), false),
            createProduct("Combiflam Tablets", "Ibuprofen and Paracetamol combination", "42.00", categoryMap.get("pain-relief"), false),
            createProduct("Voveran SR 100", "Diclofenac for persistent pain relief", "155.00", categoryMap.get("pain-relief"), true),
            createProduct("Zerodol P", "Aceclofenac and Paracetamol for inflammation", "108.00", categoryMap.get("pain-relief"), true),
            createProduct("Saridon", "Triple action headache relief formula", "25.00", categoryMap.get("pain-relief"), false),

            // Cardiac Care
            createProduct("Atorvastatin 10mg", "Lipid-lowering medication for cholesterol", "78.00", categoryMap.get("cardiac"), true),
            createProduct("Amlodipine 5mg", "Calcium channel blocker for hypertension", "18.50", categoryMap.get("cardiac"), true),
            createProduct("Telmisartan 40mg", "Angiotensin II receptor antagonist", "92.00", categoryMap.get("cardiac"), true),
            createProduct("Ecosprin 75", "Aspirin for heart attack and stroke prevention", "5.45", categoryMap.get("cardiac"), true),
            createProduct("Metoprolol 25mg", "Beta-blocker for heart rhythm and pressure", "45.00", categoryMap.get("cardiac"), true),
            createProduct("Rosuvastatin 20mg", "High-intensity statin for intensive lipid control", "210.00", categoryMap.get("cardiac"), true),

            // Diabetes Care
            createProduct("Metformin 500mg", "First-line oral medication for Type 2 Diabetes", "22.80", categoryMap.get("diabetes"), true),
            createProduct("Glimepiride 2mg", "Sulfonylurea to lower blood sugar", "54.00", categoryMap.get("diabetes"), true),
            createProduct("Jardiance 10mg", "SGLT2 inhibitor for blood glucose management", "840.00", categoryMap.get("diabetes"), true),
            createProduct("Januvia 100mg", "DPP-4 inhibitor for adults with type 2 diabetes", "1150.00", categoryMap.get("diabetes"), true),
            createProduct("Glycomet GP 1", "Metformin and Glimepiride dual-action therapy", "125.00", categoryMap.get("diabetes"), true),

            // Vitamins & Supplements
            createProduct("Shelcal 500", "Calcium and Vitamin D3 supplement", "118.00", categoryMap.get("vitamins"), false),
            createProduct("Neurobion Forte", "Vitamin B-complex for nerve health", "34.50", categoryMap.get("vitamins"), false),
            createProduct("Zincovit", "Multivitamin with Zinc and minerals", "105.00", categoryMap.get("vitamins"), false),
            createProduct("Becosules Capsules", "High potency B-complex vitamins with C", "45.00", categoryMap.get("vitamins"), false),
            createProduct("Evion 400", "Vitamin E supplement for skin and muscle health", "35.00", categoryMap.get("vitamins"), false),
            createProduct("Revital H", "Daily health supplement for energy and immunity", "310.00", categoryMap.get("vitamins"), false),

            // Ayurvedic & Immunity
            createProduct("Chyawanprash 500g", "Ayurvedic immunity booster paste", "195.00", categoryMap.get("ayurvedic"), false),
            createProduct("Himalaya Ashvagandha", "Adaptogen for stress and general wellness", "165.00", categoryMap.get("ayurvedic"), false),
            createProduct("Tulsi Tablets", "Herbal support for respiratory health", "145.00", categoryMap.get("ayurvedic"), false),
            createProduct("Liv.52 DS", "Liver health and protection supplement", "180.00", categoryMap.get("ayurvedic"), false),
            createProduct("Septilin", "Immunity supporting herbal formula", "140.00", categoryMap.get("ayurvedic"), false),

            // Skincare & General
            createProduct("Beclomethasone Cream", "Topical steroid for skin inflammation", "55.00", categoryMap.get("skincare"), true),
            createProduct("Ketoconazole Shampoo", "Anti-fungal treatment for dandruff", "245.00", categoryMap.get("skincare"), false),
            createProduct("Eucerin Moisturizer", "Dermatological lotion for sensitive skin", "890.00", categoryMap.get("skincare"), false),
            createProduct("Betadine Ointment", "Antiseptic for minor cuts and burns", "112.00", categoryMap.get("skincare"), false),

            // Respiratory & Others
            createProduct("Cetirizine 10mg", "Antihistamine for allergy relief", "12.00", categoryMap.get("immunity"), false),
            createProduct("Montelukast 10mg", "Leukotriene receptor antagonist for asthma", "145.00", categoryMap.get("immunity"), true),
            createProduct("Allegra 120mg", "Fexofenadine for seasonal allergies", "215.00", categoryMap.get("immunity"), false),
            createProduct("Levocetirizine 5mg", "Second-generation antihistamine", "48.00", categoryMap.get("immunity"), false),
            createProduct("Ascoril LS Syrup", "Expectorant for cough and congestion", "118.00", categoryMap.get("immunity"), true)
        };
        productRepository.saveAll(Arrays.asList(products));
    }

    private Product createProduct(String name, String desc, String price, Category cat, boolean rx) {
        return Product.builder()
                .name(name)
                .description(desc)
                .price(new BigDecimal(price))
                .category(cat)
                .prescriptionRequired(rx)
                .status("ACTIVE")
                .build();
    }
}
