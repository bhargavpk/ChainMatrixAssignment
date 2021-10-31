package com.example.backend.payment.api;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.payment.service.PaymentService;

@RequestMapping("/payment")
@RestController
public class PaymentController {
	private final PaymentService paymentService;
	@Autowired
	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}
	
	@PostMapping("/balance")
	public int getBalance(@RequestBody Map<String, String> requestBody) {
		try {
			int userId = Integer.valueOf(requestBody.get("userId"));
			String paymentType = requestBody.get("paymentType");
			String companyName = requestBody.get("companyName");
			return paymentService.getBalance(userId, paymentType, companyName);
			
		} catch(SQLException e) {
			return -2;
		} catch(Exception e) {
			return -1;
		}
	}
	
	@PostMapping("/product")
	public @ResponseBody Map<String, String> checkoutProduct(@RequestBody Map<String, String> requestBody) {
		int userId = Integer.valueOf(requestBody.get("userId"));
		int productId = Integer.valueOf(requestBody.get("productId"));
		String paymentType = requestBody.get("paymentType");
		String companyName = requestBody.get("companyName");
		int quantity = Integer.valueOf(requestBody.get("quantity"));
		Map<String, String> responseBody = new HashMap<String, String>();
		responseBody.put("message", paymentService.checkoutProduct(userId, productId, paymentType, companyName, quantity));
		return responseBody;
	}
	
	@PostMapping("/cart")
	// TODO: User also needs to provide wallet company or online payment company
	public @ResponseBody Map<String, String> checkoutCart(@RequestBody Map<String, String> requestBody) {
		int userId = Integer.valueOf(requestBody.get("userId"));
		String paymentType = requestBody.get("paymentType");
		String companyName = requestBody.get("companyName");
		Map<String, String> responseBody = new HashMap<String, String>();
		responseBody.put("message", paymentService.checkoutCart(userId, paymentType, companyName));
		return responseBody;
	}
}
