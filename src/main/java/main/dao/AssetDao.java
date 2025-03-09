package main.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import main.dto.AssetPercentage;
import main.dto.AssetValue;
import main.util.DBConnector;

@Component
public class AssetDao {
	Connection conn = null;
	PreparedStatement preparedStatement = null;
	ResultSet result = null;
	
	public AssetValue getCurrentAssetValueByAccountId(int accountId){
		AssetValue assetValue = new AssetValue();	
		String sqlQuery = 
				"SELECT\r\n"
				+ "	DATE_FORMAT(time, '%Y-%m-%d %H:%i:00') AS time,\r\n"
				+ "	SUM(`value (in USD)`) as value\r\n"
				+ "FROM balance_history b JOIN portfolio p\r\n"
				+ "ON b.portfolio_id = p.portfolio_id\r\n"
				+ "WHERE p.account_id = ?\r\n"
				+ "AND time = (SELECT MAX(time) FROM balance_history)\r\n"
				+ "GROUP BY time;";
		
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(sqlQuery);
			preparedStatement.setInt(1, accountId);
			
			result = preparedStatement.executeQuery();
			
			if (result.next()) {
				assetValue.setTime(result.getTimestamp("time"));
				assetValue.setValue(result.getDouble("value"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement, result);
		}
		return assetValue;
	}
	
	public AssetValue getFirstAssetValueByAccountId(int accountId){
		AssetValue assetValue = new AssetValue();	
		String sqlQuery = 
				"SELECT\r\n"
				+ "	DATE_FORMAT(time, '%Y-%m-%d %H:%i:00') AS time,\r\n"
				+ "	SUM(`value (in USD)`) as value\r\n"
				+ "FROM balance_history b JOIN portfolio p\r\n"
				+ "ON b.portfolio_id = p.portfolio_id\r\n"
				+ "WHERE p.account_id = ?\r\n"
				+ "AND time = (SELECT MIN(time) FROM balance_history)\r\n"
				+ "GROUP BY time;";
		
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(sqlQuery);
			preparedStatement.setInt(1, accountId);
			
			result = preparedStatement.executeQuery();
			
			if (result.next()) {
				assetValue.setTime(result.getTimestamp("time"));
				assetValue.setValue(result.getDouble("value"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement, result);
		}
		return assetValue;
	}
	
	public List<AssetPercentage> getAssetPercentageByAccountId(int accountId){
		List<AssetPercentage> assetPercentages = new ArrayList<AssetPercentage>();
		String sqlQuery = 
				"WITH asset_value AS( "
				+ "	SELECT  "
				+ "		category, "
				+ "		SUM(`value (in USD)`) as value "
				+ "	FROM balance_history b "
				+ "	JOIN portfolio p ON b.portfolio_id = p.portfolio_id "
				+ "	JOIN security s ON b.security_id = s.security_id "
				+ "	WHERE p.account_id = ? "
				+ "	AND time = (SELECT MAX(time) FROM balance_history) "
				+ "	GROUP BY s.category "
				+ "	) "
				+ "SELECT "
				+ "	category, "
				+ "    value/(SELECT SUM(value) FROM asset_value)*100 as percentage "
				+ "FROM asset_value;";
		
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(sqlQuery);
			preparedStatement.setInt(1, accountId);
			
			result = preparedStatement.executeQuery();
			
			while (result.next()) {
				AssetPercentage assetPercentage = new AssetPercentage();
				assetPercentage.setCategory(result.getString("category"));
				assetPercentage.setPercentage(result.getDouble("percentage"));
				
				assetPercentages.add(assetPercentage);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement, result);
		}
		return assetPercentages;
	}
	public static void main(String[] args) {
		AssetDao assetDao = new AssetDao();

		AssetValue assetValue = assetDao.getFirstAssetValueByAccountId(1);
		System.out.println(assetValue);
		
//		List<AssetPercentage> assetPercentages = assetDao.getAssetPercentageByAccountId(1);
//		System.out.println(assetPercentages);

	}
}
