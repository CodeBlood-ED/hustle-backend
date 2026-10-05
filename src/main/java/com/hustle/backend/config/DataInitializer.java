package com.hustle.backend.config;

import com.hustle.backend.modules.auth.entity.Role;
import com.hustle.backend.modules.auth.entity.User;
import com.hustle.backend.modules.auth.repository.UserRepository;
import com.hustle.backend.modules.product.entity.Product;
import com.hustle.backend.modules.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           ProductRepository productRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedProducts();
    }

    private void seedUsers() {
        // Admin users can only be signed up by manual script added to Supabase PostgreSQL.
        logger.info("Admin accounts are restricted to manual database provisioning in Supabase PostgreSQL.");
    }

    private void seedProducts() {
        if (productRepository.count() == 0) {
            logger.info("Seeding initial catalog products matching Hustle Web catalog...");

            List<Product> products = new ArrayList<>();

            // iPhone 13
            products.add(createSeedProduct(
                    "Crystal Grip", "iphone-13", "999/-", "499/-",
                    "Soft-touch case with a matte finish and drop-tested corners.",
                    "Popular", "#0d917e",
                    "#0d917e,#d1fae5,#111827",
                    "TPU shell,Shock-absorbing corners",
                    "Raised camera lip,MagSafe ready,Drop-tested"
            ));
            products.add(createSeedProduct(
                    "MagSafe Shield", "iphone-13", "999/-", "499/-",
                    "A magnetic, shock-ready case built for everyday carry.",
                    "Best seller", "#fac5d2",
                    "#fac5d2,#f472b6,#111827",
                    "Polycarbonate,Soft-touch lining",
                    "MagSafe compatible,Precise cutouts,Lightweight"
            ));
            products.add(createSeedProduct(
                    "Leather Luxe", "iphone-13", "999/-", "499/-",
                    "Premium leather finish with a refined luxury profile.",
                    "Luxury", "#7c3aed",
                    "#7c3aed,#d8b4fe,#111827",
                    "Genuine vegan leather,Textured finish",
                    "Premium grip,Classic profile,Scratch-resistant"
            ));
            products.add(createSeedProduct(
                    "Urban Clear", "iphone-13", "999/-", "499/-",
                    "Transparent shell with anti-yellow finish and anti-scratch coating.",
                    "Clear", "#60a5fa",
                    "#60a5fa,#e0f2fe,#f8fafc",
                    "Clear polycarbonate,Anti-yellow coating",
                    "Crystal view,Air-pocket corners,Ultra-clear"
            ));

            // iPhone 14
            products.add(createSeedProduct(
                    "Aero Case", "iphone-14", "999/-", "499/-",
                    "Ultra-light profile with shock absorption and raised edge protection.",
                    "New", "#22c55e",
                    "#22c55e,#bbf7d0,#111827",
                    "Flexible TPU,Impact foam",
                    "Air cushion corners,Raised lip,Slim profile"
            ));
            products.add(createSeedProduct(
                    "Nightfall", "iphone-14", "999/-", "499/-",
                    "Satin finish with a sleek profile for professional style.",
                    "Limited", "#111827",
                    "#111827,#9ca3af,#d1d5db",
                    "Microfiber interior,Satin finish",
                    "Professional finish,Soft grip,Easy install"
            ));
            products.add(createSeedProduct(
                    "Rose Quartz", "iphone-14", "999/-", "499/-",
                    "Soft pastel finish with a premium everyday grip.",
                    "Trending", "#f472b6",
                    "#f472b6,#fbcfe8,#ffffff",
                    "Soft-touch TPU,Dual-layer shell",
                    "Pastel tone,Shock absorbing,Daily comfort"
            ));

            // iPhone 15
            products.add(createSeedProduct(
                    "Titanium Lite", "iphone-15", "999/-", "499/-",
                    "Premium matte finish with scratch resistance and slim curves.",
                    "Pro", "#c084fc",
                    "#c084fc,#f5d0fe,#111827",
                    "Matte shell,Scratch-safe finish",
                    "Slim fit,Premium finish,Scratch resistant"
            ));
            products.add(createSeedProduct(
                    "Cloud Cover", "iphone-15", "999/-", "499/-",
                    "Minimalist design with a microfiber-soft touch finish.",
                    "Soft touch", "#38bdf8",
                    "#38bdf8,#dbeafe,#f8fafc",
                    "Microfiber interior,Soft-touch texture",
                    "Minimal profile,Soft grip,Comfort use"
            ));

            // iPhone 16
            products.add(createSeedProduct(
                    "Carbon Frame", "iphone-16", "999/-", "499/-",
                    "Strong edges and sharp contours for a sleek profile.",
                    "Popular", "#1f2937",
                    "#1f2937,#9ca3af,#d1d5db",
                    "Acrylic shell,Shock core",
                    "Strong edges,Quick grip,Modern body"
            ));
            products.add(createSeedProduct(
                    "Bloom Shell", "iphone-16", "999/-", "499/-",
                    "Soft pastel tones with a premium, contemporary finish.",
                    "Fresh", "#f9a8d4",
                    "#f9a8d4,#fdf2f8,#60a5fa",
                    "Soft-touch polymer,Dual-layer build",
                    "Pastel palette,Daily comfort,Premium detailing"
            ));

            // iPhone 16 Pro
            products.add(createSeedProduct(
                    "Pro Armor", "iphone-16-pro", "999/-", "499/-",
                    "Heavy-duty protection combined with premium materials.",
                    "Pro", "#0f172a",
                    "#0f172a,#94a3b8,#cbd5e1",
                    "Dual-layer shell,Shock-absorbing corners",
                    "Pro-grade,Corner protection,Secure fit"
            ));

            // iPhone 16 Pro Max
            products.add(createSeedProduct(
                    "Max Shield", "iphone-16-pro-max", "999/-", "499/-",
                    "Heavy-duty rugged case built for larger-screen protection.",
                    "Heavy duty", "#0d917e",
                    "#0d917e,#a7f3d0,#111827",
                    "Shock core,Rugged shell",
                    "Max protection,Bulk-safe,Strong grip"
            ));

            // iPhone 17
            products.add(createSeedProduct(
                    "Monarch", "iphone-17", "999/-", "499/-",
                    "Contemporary case with bold color accents and a slim build.",
                    "Featured", "#f43f5e",
                    "#f43f5e,#fecdd3,#111827",
                    "Soft-touch shell,Dual-layer build",
                    "Bold finish,Slim profile,Daily grip"
            ));

            // iPhone 17 Pro
            products.add(createSeedProduct(
                    "Pro Apex", "iphone-17-pro", "999/-", "499/-",
                    "Luxury protection crafted for premium performance devices.",
                    "Top pick", "#7c3aed",
                    "#7c3aed,#ddd6fe,#111827",
                    "Premium shell,Shock guard",
                    "Luxury protection,Pro-grade fit,High performance"
            ));

            // iPhone 18
            products.add(createSeedProduct(
                    "Beam Edge", "iphone-18", "999/-", "499/-",
                    "A polished, premium case built for refined everyday use.",
                    "New", "#22d3ee",
                    "#22d3ee,#cffafe,#111827",
                    "Clear shell,Textured rim",
                    "Premium finish,Refined profile,Strong corners"
            ));

            // iPhone 18 Pro
            products.add(createSeedProduct(
                    "Pro Fusion", "iphone-18-pro", "999/-", "499/-",
                    "Premium build with comfortable grip and luxury appeal.",
                    "Pro", "#0d917e",
                    "#0d917e,#a7f3d0,#111827",
                    "Luxury shell,Impact cushion",
                    "Luxury finish,Comfort grip,Premium protection"
            ));

            productRepository.saveAll(products);
            logger.info("Catalog products seeded successfully. Total: {}", products.size());
        }
    }

    private Product createSeedProduct(String title, String category, String mrp, String netPrice,
                                      String description, String tag, String accent,
                                      String colors, String materials, String features) {
        Product product = new Product();
        product.setTitle(title);
        product.setCategory(category);
        product.setMrp(mrp);
        product.setNetPrice(netPrice);
        product.setDescription(description);
        product.setTag(tag);
        product.setAccent(accent);
        product.setColors(colors);
        product.setMaterials(materials);
        product.setFeatures(features);
        product.setActive(true);
        return product;
    }
}
