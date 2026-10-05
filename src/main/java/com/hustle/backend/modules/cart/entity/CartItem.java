package com.hustle.backend.modules.cart.entity;

import com.hustle.backend.modules.product.entity.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private int quantity = 1;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    public CartItem() {
    }

    public CartItem(Cart cart, Product product, String model, int quantity, Double unitPrice) {
        this.cart = cart;
        this.product = product;
        this.model = model;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Double getSubtotal() {
        return (unitPrice != null ? unitPrice : 0.0) * quantity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
