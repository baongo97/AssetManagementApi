package main.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import main.dto.PortfolioValue;
import main.util.DBConnector;

@Component
public class PortfolioDao {
	Connection conn = null;
	PreparedStatement preparedStatement = null;
	ResultSet result = null;
	
	public PortfolioValue getCurrentPortfolioValueById(int portfolioId){
		PortfolioValue portfolioValue = new PortfolioValue();	
		String sqlQuery = 
				"SELECT\r\n"
				+ "	DATE_FORMAT(time, '%Y-%m-%d %H:%i:00') AS time,\r\n"
				+ "	SUM(`value (in USD)`) as value\r\n"
				+ "FROM balance_history\r\n"
				+ "WHERE portfolio_id = ?\r\n"
				+ "AND time = (SELECT MAX(time) FROM balance_history WHERE portfolio_id =?)\r\n"
				+ "GROUP BY time;";
		
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(sqlQuery);
			preparedStatement.setInt(1, portfolioId);
			preparedStatement.setInt(2, portfolioId);
			
			result = preparedStatement.executeQuery();
			
			if (result.next()) {
				portfolioValue.setTime(result.getTimestamp("time"));
				portfolioValue.setValue(result.getDouble("value"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement, result);
		}
		return portfolioValue;
	}
	
	public List<PortfolioValue> getPortfolioValueOverTime(int id, String interval){
		List<PortfolioValue> portfolioValues = new ArrayList<>();
		String sqlQuery = 
				"WITH grouped AS(\r\n"
				+ "	SELECT \r\n"
				+ "		time, \r\n"
				+ "		SUM(`value (in USD)`) as value, \r\n"
				+ "		ROW_NUMBER() OVER (\r\n"
				+ "			PARTITION BY UNIX_TIMESTAMP(time) DIV (? * 60)\r\n"
				+ "		)AS rn\r\n"
				+ "	FROM balance_history\r\n"
				+ "    WHERE portfolio_id = ?\r\n"
				+ "	GROUP BY time\r\n"
				+ ")\r\n"
				+ "SELECT \r\n"
				+ "    DATE_FORMAT(time, '%Y-%m-%d %H:%i:00') AS time,\r\n"
				+ "    value\r\n"
				+ "FROM grouped\r\n"
				+ "WHERE rn = 1;";
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(sqlQuery);
			preparedStatement.setInt(2, id);

			switch (interval) {
			case "15m": {
				preparedStatement.setInt(1, 15);
				break;
			}
			case "30m": {
				preparedStatement.setInt(1, 30);
				break;
			}
			case "1H": {
				preparedStatement.setInt(1, 60);
				break;
			}
			case "4H": {
				preparedStatement.setInt(1, 240);
				break;
			}
			default:
				preparedStatement.setInt(1, 5);
			}
			
			result = preparedStatement.executeQuery();
			
			while (result.next()) {
				Timestamp time = result.getTimestamp(1);
				double value = result.getDouble(2);
				portfolioValues.add(new PortfolioValue(time, value));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement, result);
		}
		return portfolioValues;
	}
	
	public PortfolioValue getCurrentPortfolioValueByAccountId(int accountId){
		PortfolioValue portfolioValue = new PortfolioValue();	
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
				portfolioValue.setTime(result.getTimestamp("time"));
				portfolioValue.setValue(result.getDouble("value"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement, result);
		}
		return portfolioValue;
	}
	
	public static void main(String[] args) {
		PortfolioDao portfolioDao = new PortfolioDao();
		
//		PortfolioValue portfolioValue = portfolioDao.getCurrentPortfolioValueById(2);
//		System.out.println(portfolioValue);
		
		PortfolioValue portfolioValue = portfolioDao.getCurrentPortfolioValueByAccountId(1);
		System.out.println(portfolioValue);
		
		
//		List<PortfolioValue> p = portfolioDao.getPortfolioValueOverTime(2, "1");
//		System.out.println(p);
	}
}
