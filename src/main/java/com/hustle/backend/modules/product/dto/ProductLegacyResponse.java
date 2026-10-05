package com.hustle.backend.modules.product.dto;

public class ProductLegacyResponse {
    private Long product_id;
    private String product_title;
    private String product_category;
    private String product_mrp;
    private String product_net_price;
    private String product_image;

    public ProductLegacyResponse() {
    }

    public ProductLegacyResponse(Long product_id, String product_title, String product_category, String product_mrp, String product_net_price, String product_image) {
        this.product_id = product_id;
        this.product_title = product_title;
        this.product_category = product_category;
        this.product_mrp = product_mrp;
        this.product_net_price = product_net_price;
        this.product_image = product_image;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
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

    public String getProduct_net_price() {
        return product_net_price;
    }

    public void setProduct_net_price(String product_net_price) {
        this.product_net_price = product_net_price;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }
}
