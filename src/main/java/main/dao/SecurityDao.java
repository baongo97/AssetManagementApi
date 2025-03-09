package main.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import main.dto.SecurityValue;
import main.util.DBConnector;

@Component
public class SecurityDao {
	Connection conn = null;
	PreparedStatement preparedStatement = null;
	ResultSet result = null;
	
	public List<SecurityValue> getAllSecurityByAccountId(int accountId){
		List<SecurityValue> securityList = new ArrayList<SecurityValue>();
		String sqlQuery = 
				"SELECT s.*, b.`value (in USD)` AS value\r\n"
				+ "FROM balance_history b JOIN portfolio p\r\n"
				+ "ON b.portfolio_id = p.portfolio_id\r\n"
				+ "JOIN security s\r\n"
				+ "ON b.security_id = s.security_id\r\n"
				+ "WHERE p.account_id = ?\r\n"
				+ "AND time = (SELECT MAX(time) FROM balance_history) "
				+ "ORDER BY value DESC";
		
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(sqlQuery);
			preparedStatement.setInt(1, accountId);
			
			result = preparedStatement.executeQuery();
			
			while (result.next()) {
				SecurityValue securityValue = new SecurityValue();
				securityValue.setSecurityId(result.getInt(1));
				securityValue.setName(result.getString(2));
				securityValue.setSymbol(result.getString(3));
				securityValue.setCategory(result.getString(4));
				securityValue.setCurrency(result.getString(5));
				securityValue.setValue(result.getDouble(6));
				
				securityList.add(securityValue);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement, result);
		}
		return securityList;
	}
	public static void main(String[] args) {
		SecurityDao securityDao = new SecurityDao();
		
		List<SecurityValue> securityList = securityDao.getAllSecurityByAccountId(1);
		System.out.println(securityList);
	}
}
