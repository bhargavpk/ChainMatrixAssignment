package com.example.backend.cart.api;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.cart.service.CartService;

@RequestMapping("/cart")
@RestController
public class CartController {
	private final CartService cartService;
	@Autowired
	public CartController(CartService cartService) {
		this.cartService = cartService;
	}
	
	@PostMapping(value="/addProduct")
	public Object addToCart(@RequestBody Map<String, Integer> reqBody) {
		try {
			int userId = reqBody.get("userId");
			int productId = reqBody.get("productId");
			cartService.addProduct(userId, productId);
			return 1;
			
		} catch (SQLException e) {
			return (new HashMap<String, String>()).put("message", "Bad input");
		} catch (Exception e) {
			return (new HashMap<String, String>()).put("message", "Internal server error");
		}
	}
	
	@PostMapping(value="/removeProduct")
	public Object removeFromCart(@RequestBody Map<String, Integer> reqBody) {
		try {
			int userId = reqBody.get("userId");
			int productId = reqBody.get("productId");
			cartService.removeProduct(userId, productId);
			return 1;
			
		} catch (SQLException e) {
			return (new HashMap<String, String>()).put("message", "Bad input");
		} catch (Exception e) {
			return (new HashMap<String, String>()).put("message", "Internal server error");
		}
	}
	
	@PostMapping(value="/decrementProduct")
	public @ResponseBody Object decrementFromCart(@RequestBody Map<String, Integer> reqBody) {
		try {
			int userId = reqBody.get("userId");
			int productId = reqBody.get("productId");
			cartService.removeProductQty(userId, productId);
			return 1;
			
		} catch (SQLException e) {
			return (new HashMap<String, String>()).put("message", "Bad input");
		} catch (Exception e) {
			return (new HashMap<String, String>()).put("message", "Internal server error");
		}
	}
	
	@PostMapping(value="/history")
	public @ResponseBody Object getCartHistory(@RequestBody Map<String, Integer> reqBody) {
		try {
			int userId = reqBody.get("userId");
			return cartService.getCartHistory(userId);
		} catch (SQLException e) {
			return (new HashMap<String, String>()).put("message", "Bad input");
		} catch (Exception e) {
			return (new HashMap<String, String>()).put("message", "Internal server error");
		}
	}
	
	

}
