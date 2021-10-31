package com.example.backend.payment.dataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.stereotype.Repository;

@Repository("paymentDao")
public class PaymentDao {
	
	private Connection conn;
	public PaymentDao() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		String dbUrl = "jdbc:mysql://localhost:3306/ecomm";
		String username = "root";
		String password = "password";
		conn = DriverManager.getConnection(dbUrl, username, password);
	}
	
	public int getBalance(int userId, String paymentType, String companyName) throws SQLException {
		Statement stmt = conn.createStatement();
		String referredTableName = paymentType.equals("wallet")?"wallet_info":"online_payment_info";
		String refereeTableName = paymentType.equals("wallet")?"wallet_user":"online_payment_user";
		// Get bank balance from user account
		// TODO: Separate functionalities for wallet and online payment
		String query = "SELECT amount FROM bank_account NATURAL JOIN " + referredTableName + " AS account_info, "
						+ refereeTableName + " WHERE account_info.id = " + refereeTableName + ".client_id AND " + refereeTableName + ".user_id = "+userId + " AND "
								+ "account_info.company_name = '"+companyName+"'";
		ResultSet rs = stmt.executeQuery(query);
		if(!rs.next())
			return -1;
		return rs.getInt(1);
	}
	
	public String getAccountNo(int userId, String paymentType, String companyName) throws SQLException {
		String referredTableName = paymentType.equals("wallet")?"wallet_info":"online_payment_info";
		String refereeTableName = paymentType.equals("wallet")?"wallet_user":"online_payment_user";
		Statement stmt = conn.createStatement();
		String query = "SELECT account_no FROM bank_account NATURAL JOIN " + referredTableName + " AS account_info, "
						+ refereeTableName + " WHERE account_info.id = " + refereeTableName + ".client_id AND "
						+ refereeTableName + ".user_id = " + userId + " AND account_info.company_name = '"+companyName+"'";
		ResultSet rs = stmt.executeQuery(query);
		if(!rs.next())
			return "";
		return rs.getString(1);
	}
	
	public boolean decrementBalance(String accountNo, int amount) throws SQLException {
			Statement stmt = conn.createStatement();
			String query = "UPDATE bank_account SET amount = amount - " + amount + " WHERE account_no = '" + accountNo + "'";
			return stmt.executeUpdate(query) > 0;
	}
	
	public boolean validatePayment(int userId) throws SQLException {
		// Check whether address is present
		String query = "SELECT * FROM payment_info WHERE user_id = " + userId;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		return rs.next();
	}
	
	public String checkoutProduct(int userId, int productId, String paymentType, String companyName, int quantity) {
		try {
			// Check payment validity of user
			if(!validatePayment(userId))
				return "Payment information not added";
			
			String accountNo = getAccountNo(userId, paymentType, companyName);
			if(accountNo.equals(""))
			{
				// Unavailable user
				return "Bad request";
			}
			Statement stmt = conn.createStatement();
			String priceQueryString = "SELECT price FROM product WHERE id = " + productId;
			ResultSet rs = stmt.executeQuery(priceQueryString);
			if(!rs.next())
				return "Bad request";
			int productPrice = rs.getInt(1);
			if(!decrementBalance(accountNo, quantity*productPrice)) {
				// Insufficient balance
				return "Insufficient balance";
			}
			// Try decreasing available stock of products
			String qtyQuery = "UPDATE product SET available_quantity = available_quantity-" + quantity + " WHERE id = " + productId;
			stmt.executeUpdate(qtyQuery);
			// Insert into checkout_history
			String insertionParams = userId + ", " + productId + ", " + quantity;
			String checkoutInsertQuery = "INSERT INTO checkout_history(user_id, product_id, quantity) VALUES (" + insertionParams + ")";
			stmt.execute(checkoutInsertQuery);
			return "Checkout successful";
			
		} catch (SQLException e) {
			switch(e.getSQLState()) {
			case "45000":
				return "Insufficient balance";
			case "45001":
				// Product unavailable
				// Refund amount
				// Recalculating product price as variables in scope of try block is unavailable
				Statement stmt;
				try {
					stmt = conn.createStatement();
					String query = "SELECT price FROM product WHERE id = " + productId;
					ResultSet rs = stmt.executeQuery(query);
					rs.next();
					int productPrice = rs.getInt(1);
					decrementBalance(getAccountNo(userId, paymentType, companyName), -quantity*productPrice);
					return "Product(s) unavailable";
					
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					return "Bad request";
				}
			}
			return "Bad request";
			
		} catch (Exception e) {
			return "Server error";
		}
		
	}
	
	public String checkoutCart(int userId, String paymentType, String companyName) {
		try {
			// Check payment validity of user
			if(!validatePayment(userId))
				return "Payment information not added";
			String accountNo = getAccountNo(userId, paymentType, companyName);
			if(accountNo.equals(""))
			{
				// Unavailable user
				System.out.println("Account unavailable!");
				return "Bad request";
			}
			Statement stmt = conn.createStatement();
			String priceQuery = "SELECT SUM(quantity*price) FROM cart, product WHERE product.id = cart.product_id AND user_id = " + userId + " AND checkout_status = 'U'";
			ResultSet rs = stmt.executeQuery(priceQuery);
			// Invalid product
			if(!rs.next())
				return "Bad request";
			int totalPrice = rs.getInt(1);
			if(!decrementBalance(accountNo, totalPrice)) {
				// Insufficient balance
				return "Insufficient balance";
			}
			// Try decreasing available stock of products in cart
			String productStockQuery = "SELECT quantity FROM cart WHERE product.id = product_id AND user_id = "+userId+" AND checkout_status='U'";
			String stockUpdateQuery = "UPDATE product SET available_quantity = available_quantity - (" + productStockQuery + ")"
										+ " WHERE id IN (SELECT product_id FROM cart WHERE user_id = "+userId+" AND checkout_status='U')";
			stmt.executeUpdate(stockUpdateQuery);
			
			// Update cart
			String cartUpdateQuery = "UPDATE cart SET checkout_status = 'C', checkout_time = CURRENT_TIMESTAMP() WHERE user_id = " + userId + " AND checkout_status = 'U'";
			stmt.executeUpdate(cartUpdateQuery);
			return "Success";
			
		} catch (SQLException e) {
			e.printStackTrace();
			switch(e.getSQLState()) {
			case "45000":
				System.out.println("Insufficient balance");
				return "Insufficient balance";
			case "45001":
				// Product unavailable
				System.out.println("Product(s) unavailable");
				// Refund amount
				// Recalculating product price as variables in scope of try block is unavailable
				Statement stmt;
				try {
					stmt = conn.createStatement();
					String priceQuery = "SELECT SUM(quantity*price) FROM cart, product WHERE product.id = cart.product_id AND user_id = "+userId;
					ResultSet rs = stmt.executeQuery(priceQuery);
					rs.next();
					int totalPrice = rs.getInt(1);
					decrementBalance(getAccountNo(userId, paymentType, companyName), -totalPrice);
					return "Product(s) unavailable";
					
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					return "Internal server failure";
				}
			}
			return "Failure";
		}
	}
	
	
	
}
