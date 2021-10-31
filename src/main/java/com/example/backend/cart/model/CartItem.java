package com.example.backend.cart.model;

import java.util.List;

class ProductInfo {
	private final int productId, quantity;
	
	ProductInfo(int productId, int quantity) {
		this.productId = productId;
		this.quantity = quantity;
	}
	
}

public class CartItem {
	private final int userId;
	private final List<ProductInfo> products;
	
	CartItem(int userId, List<ProductInfo> products) {
		this.userId = userId;
		this.products = products;
	}
	
	// Expose methods for members
	public int getUserId() {
		return userId;
	}
	public List<ProductInfo> getProducts() {
		return products;
	}
}
