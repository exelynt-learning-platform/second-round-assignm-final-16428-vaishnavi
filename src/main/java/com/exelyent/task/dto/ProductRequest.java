package com.exelyent.task.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ProductRequest {

    public static class Create {

        @NotBlank(message = "Product name is required")
        @Size(min = 2, max = 200)
        private String name;

        @Size(max = 2000)
        private String description;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Invalid price format")
        private BigDecimal price;

        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity cannot be negative")
        private Integer stockQuantity;

        @Size(max = 500)
        private String imageUrl;

        @Size(max = 100)
        private String category;

        @Size(max = 100)
        private String brand;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
    }

    public static class Update {

        @Size(min = 2, max = 200)
        private String name;

        @Size(max = 2000)
        private String description;

        @DecimalMin(value = "0.01")
        @Digits(integer = 10, fraction = 2)
        private BigDecimal price;

        @Min(value = 0)
        private Integer stockQuantity;

        @Size(max = 500)
        private String imageUrl;

        @Size(max = 100)
        private String category;

        @Size(max = 100)
        private String brand;

        private Boolean active;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }

        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }
    }
}