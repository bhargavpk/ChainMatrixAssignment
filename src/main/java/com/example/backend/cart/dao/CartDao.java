package com.example.backend.cart.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository("realCartDao")
public class CartDao {
	private Connection conn;
	private int PORT = 3306;
	public CartDao() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		String dbUrl = "jdbc:mysql://localhost:3306/ecomm";
		String username = "root";
		String password = "password";
		conn = DriverManager.getConnection(dbUrl, username, password);
	}
	
	public boolean checkCart(int userId, int productId) throws SQLException {
		// Check if cart is present for user
		// If not present, create it
		String query = "SELECT * FROM cart WHERE checkout_status = 'U' AND user_id = " + userId + " AND product_id = " + productId;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		return rs.next();
	}
	
	public int generateCartId() throws SQLException {
		String query = "SELECT COUNT(*), MAX(id) FROM cart WHERE checkout_status = 'U'";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		if(rs.getInt(1) == 0)
		{
			query = "SELECT MAX(id) FROM cart";
			rs = stmt.executeQuery(query);
			rs.next();
			return rs.getInt(1)+1;
		}
		return rs.getInt(2);
	}
	
	public int initializeCart(int userId, int productId, int quantity) throws SQLException {
		int cartId = generateCartId();
		String queryParams = cartId + ", " +  userId + ", " + productId + ", " + quantity;
		String query = "INSERT INTO cart(id, user_id, product_id, quantity) VALUES (" + queryParams + ")";
		Statement stmt = conn.createStatement();
		stmt.execute(query);
		return 1;
	}
	
	public int addProduct(int userId, int productId) throws SQLException {	
		if(!checkCart(userId, productId))
		{
			initializeCart(userId, productId, 1);
			return 1;
		}
		String query = "UPDATE cart"
						+ " SET quantity = quantity + 1"
						+ " WHERE checkout_status = 'U' AND user_id = " + userId + " AND product_id = " + productId;
		Statement stmt = conn.createStatement();
		stmt.execute(query);
		return 1;
	}
	
	public int removeProductQty(int userId, int productId) throws SQLException {
		
		if(!checkCart(userId, productId))
			return 1;
		String query = "UPDATE cart SET quantity = quantity - 1"
					   + " WHERE checkout_status = 'U' AND user_id = " + userId + " AND product_id = " + productId;
		// TODO: When qty is 0, write a trigger removing product or delete it here 
		Statement stmt = conn.createStatement();
		int updateStatus = stmt.executeUpdate(query);
		if(updateStatus > 0) {
			ResultSet rs = stmt.executeQuery("SELECT quantity FROM cart WHERE"
												  + " checkout_status = 'U' AND user_id = "+ userId + " AND product_id = " + productId);
			if(!rs.next())
				return 0;
			int cartQuantity = rs.getInt(1);
			if(cartQuantity == 0) {
				return stmt.executeUpdate("DELETE FROM cart WHERE"
									+ " checkout_status = 'U' AND user_id = " + userId +" AND product_id = " + productId);
			}
			return updateStatus;
		}
		return 0;
	}
	
	public int removeProduct(int userId, int productId) throws SQLException {
		Statement stmt = conn.createStatement();
		return stmt.executeUpdate("DELETE FROM cart WHERE"
							+ " user_id = " + userId +" AND product_id = " + productId);
	}
	
	public List<Map<String, String>> getCartHistory(int userId) throws SQLException {
		Statement stmt = conn.createStatement();
		String query = "SELECT cart.id, name, quantity, checkout_time FROM cart, product WHERE user_id = "+userId+" AND product_id=product.id";
		ResultSet rs = stmt.executeQuery(query);
		List<Map<String, String>> cartItems = new ArrayList<Map<String, String>>();
		while(rs.next())
		{
			Map<String, String> item = new HashMap<String, String>();
			item.put("id", String.valueOf(rs.getInt(1)));
			item.put("product", rs.getString(2));
			item.put("quantity", String.valueOf(rs.getInt(3)));
			item.put("checkoutTime", rs.getString(4));
			cartItems.add(item);
		}
		return cartItems;
	}
}
