package com.example.backend.payment.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.backend.payment.dataAccess.PaymentDao;

@Service
public class PaymentService {
	private final PaymentDao paymentDao;
	@Autowired
	public PaymentService(@Qualifier("paymentDao") PaymentDao paymentDao) {
		this.paymentDao = paymentDao;
	}
	
	public int getBalance(int userId, String paymentType, String companyName) throws SQLException {
		return paymentDao.getBalance(userId, paymentType, companyName);
	}
	
	public String checkoutProduct(int userId, int productId, String paymentType, String companyName, int quantity) {
		return paymentDao.checkoutProduct(userId, productId, paymentType, companyName, quantity);
	}
	
	public String checkoutCart(int userId, String paymentType, String companyName) {
		return paymentDao.checkoutCart(userId, paymentType, companyName);
	}
}
