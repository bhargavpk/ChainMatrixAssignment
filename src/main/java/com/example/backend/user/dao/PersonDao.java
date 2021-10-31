package com.example.backend.user.dao;

import java.sql.*;

import org.springframework.stereotype.Repository;

@Repository("realPersonDao")
public class PersonDao {
	private Connection conn;
	public PersonDao() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		String dbUrl = "jdbc:mysql://localhost:3306/ecomm";
		String username = "root";
		String password = "password";
		conn = DriverManager.getConnection(dbUrl, username, password);
	}
	
	public int addWalletInformation(int userId, int walletId, String walletCompany) throws SQLException {
		
		String query;
		// Check validity of wallet id
		query = "SELECT id FROM wallet_info WHERE id = "+walletId;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		if(!rs.next()) {
			// Invalid wallet id
			return 0;
		}
		String insertionParams = "'"+walletCompany+"'" + ", " + userId + ", " + walletId;
		query = "INSERT INTO wallet_user VALUES (" + insertionParams + ")";
		stmt.execute(query);
		
		return 1;
		
	}
	
	public int addOnlinePaymentInformation(int userId, int paymentId, String paymentCompany) throws SQLException {
		
		String query;
		// Check validity of wallet id
		query = "SELECT id FROM online_payment_info WHERE id = "+paymentId;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		if(!rs.next()) {
			// Invalid wallet id
			return 0;
		}
		String insertionParams = "'"+paymentCompany+"'" + ", " + userId + ", " + paymentId;
		query = "INSERT INTO online_payment_user VALUES (" + insertionParams + ")";
		stmt.execute(query);
		
		return 1;
		
	}
	
	public int addShoppingInformation(int userId, String type, String company, int id, String address) throws SQLException {
		
		if(type.equals("wallet"))
			addWalletInformation(userId, id, company);
		else if(type.equals("online-payment"))
			addOnlinePaymentInformation(userId, id, company);
		else
			return 0;
		String params = userId + ", " + "'" + address + "'";
		String query = "INSERT INTO payment_info VALUES (" + params + ")"
						+ " ON DUPLICATE KEY UPDATE address = " + "'" + address + "'";
		Statement stmt = conn.createStatement();
		stmt.execute(query);
		
		return 1;
	}
}
