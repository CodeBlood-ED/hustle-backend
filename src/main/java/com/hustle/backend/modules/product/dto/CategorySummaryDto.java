package com.hustle.backend.modules.product.dto;

public class CategorySummaryDto {
    private String model;
    private String label;
    private String subtitle;
    private String accent;
    private long count;

    public CategorySummaryDto() {
    }

    public CategorySummaryDto(String model, String label, String subtitle, String accent, long count) {
        this.model = model;
        this.label = label;
        this.subtitle = subtitle;
        this.accent = accent;
        this.count = count;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getAccent() {
        return accent;
    }

    public void setAccent(String accent) {
        this.accent = accent;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
