package com.hustle.backend.modules.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddToCartRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Model is required")
    private String model;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity = 1;

    public AddToCartRequest() {
    }

    public AddToCartRequest(Long productId, String model, int quantity) {
        this.productId = productId;
        this.model = model;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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
}
