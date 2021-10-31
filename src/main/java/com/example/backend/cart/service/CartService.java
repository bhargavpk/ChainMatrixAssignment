package com.example.backend.cart.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.backend.cart.dao.CartDao;

@Service
public class CartService {
	private final CartDao cartDao;
	@Autowired
	public CartService(@Qualifier("realCartDao") CartDao cartDao) {
		this.cartDao = cartDao;
	}
	
	public int addProduct(int userId, int productId) throws SQLException {
		return cartDao.addProduct(userId, productId);
	}
	
	public int removeProduct(int userId, int productId) throws SQLException {
		return cartDao.removeProduct(userId, productId);
	}
	
	public int removeProductQty(int userId, int productId) throws SQLException {
		return cartDao.removeProductQty(userId, productId);
	}
	
	public List<Map<String, String>> getCartHistory(int userId) throws SQLException {
		return cartDao.getCartHistory(userId);
	}
}
