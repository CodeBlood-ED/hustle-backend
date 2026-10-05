package com.hustle.backend.modules.product.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Column(name = "mrp")
    private String mrp;

    @Column(name = "net_price", nullable = false)
    private String netPrice;

    @Column(name = "numeric_price")
    private Double numericPrice;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(length = 2000)
    private String description;

    @Column
    private String tag;

    @Column
    private String accent;

    @Column(name = "colors", length = 500)
    private String colors;

    @Column(name = "materials", length = 500)
    private String materials;

    @Column(name = "features", length = 1000)
    private String features;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Product() {
    }

    public Product(String title, String category, String mrp, String netPrice, String imageUrl) {
        this.title = title;
        this.category = category;
        this.mrp = mrp;
        this.netPrice = netPrice;
        this.imageUrl = imageUrl;
        this.numericPrice = parsePriceToDouble(netPrice);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.numericPrice == null && this.netPrice != null) {
            this.numericPrice = parsePriceToDouble(this.netPrice);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.netPrice != null) {
            this.numericPrice = parsePriceToDouble(this.netPrice);
        }
    }

    public static Double parsePriceToDouble(String priceStr) {
        if (priceStr == null || priceStr.isBlank()) {
            return 0.0;
        }
        String clean = priceStr.replaceAll("[^\\d.]", "");
        try {
            return clean.isEmpty() ? 0.0 : Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(String netPrice) {
        this.netPrice = netPrice;
        this.numericPrice = parsePriceToDouble(netPrice);
    }

    public Double getNumericPrice() {
        return numericPrice;
    }

    public void setNumericPrice(Double numericPrice) {
        this.numericPrice = numericPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAccent() {
        return accent;
    }

    public void setAccent(String accent) {
        this.accent = accent;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public String getMaterials() {
        return materials;
    }

    public void setMaterials(String materials) {
        this.materials = materials;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
