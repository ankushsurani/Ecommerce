package com.eworld.dto;

import com.eworld.entities.Product;
import com.eworld.enumstype.DeliveryStatus;

public class AccountOrderDto {
    private Product product;
    private int finalPrice;
    private int quantity;
    private DeliveryStatus deliveryStatus;

    public AccountOrderDto(Product product, int finalPrice, int quantity, DeliveryStatus deliveryStatus) {
        this.product = product;
        this.finalPrice = finalPrice;
        this.quantity = quantity;
        this.deliveryStatus = deliveryStatus;
    }

    public Product getProduct() {
        return product;
    }
    
    public int getFinalPrice() {
		return finalPrice;
	}

	public int getQuantity() {
        return quantity;
    }

	public DeliveryStatus getDeliveryStatus() {
		return deliveryStatus;
	}
	
}

