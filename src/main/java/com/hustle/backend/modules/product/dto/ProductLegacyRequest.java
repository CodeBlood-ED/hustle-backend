package com.hustle.backend.modules.product.dto;

import org.springframework.web.multipart.MultipartFile;

public class ProductLegacyRequest {
    private String product_title;
    private String product_category;
    private String product_mrp;
    private String product_netPrice;
    private MultipartFile product_image;

    public ProductLegacyRequest() {
    }

    public ProductLegacyRequest(String product_title, String product_category, String product_mrp, String product_netPrice, MultipartFile product_image) {
        this.product_title = product_title;
        this.product_category = product_category;
        this.product_mrp = product_mrp;
        this.product_netPrice = product_netPrice;
        this.product_image = product_image;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }

    public String getProduct_mrp() {
        return product_mrp;
    }

    public void setProduct_mrp(String product_mrp) {
        this.product_mrp = product_mrp;
    }

    public String getProduct_netPrice() {
        return product_netPrice;
    }

    public void setProduct_netPrice(String product_netPrice) {
        this.product_netPrice = product_netPrice;
    }

    public MultipartFile getProduct_image() {
        return product_image;
    }

    public void setProduct_image(MultipartFile product_image) {
        this.product_image = product_image;
    }
}
