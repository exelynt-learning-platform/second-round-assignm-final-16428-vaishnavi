package com.exelyent.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OrderRequest {

    @NotBlank(message = "Shipping address line 1 is required")
    @Size(max = 200)
    private String shippingAddressLine1;

    @Size(max = 200)
    private String shippingAddressLine2;

    @NotBlank(message = "Shipping city is required")
    @Size(max = 100)
    private String shippingCity;

    @NotBlank(message = "Shipping state is required")
    @Size(max = 100)
    private String shippingState;

    @NotBlank(message = "Shipping postal code is required")
    @Size(max = 20)
    private String shippingPostalCode;

    @NotBlank(message = "Shipping country is required")
    @Size(max = 100)
    private String shippingCountry;

    public String getShippingAddressLine1() { return shippingAddressLine1; }
    public void setShippingAddressLine1(String shippingAddressLine1) { this.shippingAddressLine1 = shippingAddressLine1; }

    public String getShippingAddressLine2() { return shippingAddressLine2; }
    public void setShippingAddressLine2(String shippingAddressLine2) { this.shippingAddressLine2 = shippingAddressLine2; }

    public String getShippingCity() { return shippingCity; }
    public void setShippingCity(String shippingCity) { this.shippingCity = shippingCity; }

    public String getShippingState() { return shippingState; }
    public void setShippingState(String shippingState) { this.shippingState = shippingState; }

    public String getShippingPostalCode() { return shippingPostalCode; }
    public void setShippingPostalCode(String shippingPostalCode) { this.shippingPostalCode = shippingPostalCode; }

    public String getShippingCountry() { return shippingCountry; }
    public void setShippingCountry(String shippingCountry) { this.shippingCountry = shippingCountry; }
}