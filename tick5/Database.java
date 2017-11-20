package tick5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;

import tick2.RelayMessage;

public class Database {

		private Connection connection;
		
		public Database(String databasePath) throws SQLException, ClassNotFoundException {
			Class.forName("org.hsqldb.jdbcDriver");
			try{
			connection = DriverManager.getConnection("jdbc:hsqldb:file:"+databasePath,"SA","");
			}
			catch(SQLException e){
				System.out.println("Check argument <database name> is correct");
				return;
			}
			Statement delayStmt = connection.createStatement();
			try {
				//Always update data on disk
				delayStmt.execute("SET WRITE_DELAY FALSE");
			}
			finally {
				delayStmt.close();
			}
			connection.setAutoCommit(false);
			Statement sqlStatement = connection.createStatement();
			try{
				sqlStatement.execute("CREATE TABLE messages(nick VARCHAR(255) NOT NULL,"+
						"message VARCHAR(4096) NOT NULL,timeposted BIGINT NOT NULL)");
				sqlStatement.execute("CREATE TABLE statistics(key VARCHAR(255),value INT)");
			}
			catch(SQLException se){
				System.out.println("\"messages\" SQLException: error code: " + Integer.toString(se.getErrorCode()));
			}
			try{
				sqlStatement.execute("CREATE TABLE statistics(key VARCHAR(255),value INT)");
				sqlStatement.execute("INSERT INTO statistics(key,value) VALUES ('Total Messages',0)");
				sqlStatement.execute("INSERT INTO statistics(key,value) VALUES ('Total Logins',0)");
			}
			catch(SQLException se){
				System.out.println("\"statistics\" SQLException: error code: " + Integer.toString(se.getErrorCode()));
			}
			finally{
				sqlStatement.close();
			}
			connection.commit();
		}
		
		public void close() throws SQLException {
			connection.commit();
			connection.close();
		}
		
		public void incrementLogins() throws SQLException {
			Statement sqlStatement = connection.createStatement();
			try{
				sqlStatement.execute("UPDATE statistics SET value = value + 1 WHERE key='Total Logins'");
				connection.commit();
			}
			finally{
				sqlStatement.close();
			}
		}
		
		public void addMessage(RelayMessage m) throws SQLException {
			Statement sqlStatement = connection.createStatement();
			String pStatement = "INSERT INTO messages (nick,message,timeposted) VALUES (?,?,?)";
			PreparedStatement insertMessage = connection.prepareStatement(pStatement);
			try{
				insertMessage.setString(1, m.getFrom());
				insertMessage.setString(2, m.getMessage());
				insertMessage.setLong(3, m.getCreationMillis());
				insertMessage.executeUpdate();
				sqlStatement.execute("UPDATE statistics SET value = value + 1 WHERE key='Total Messages'");
			}
			finally{
				insertMessage.close();
				sqlStatement.close();
			}
			connection.commit();
		}
		
		public LinkedList<RelayMessage> getRecent() throws SQLException {
			LinkedList<RelayMessage> recentMessages = new LinkedList<RelayMessage>();
			String pStatement = "SELECT nick,message,timeposted FROM messages ORDER BY timeposted DESC LIMIT 10";
			PreparedStatement takeMessages = connection.prepareStatement(pStatement);
			try{
				ResultSet rs = takeMessages.executeQuery();
				try{
					while(rs.next()){
						recentMessages.add(new RelayMessage("****"+rs.getString(1), rs.getString(2), new Date(rs.getLong(3))));
					}
				}
				finally{
					rs.close();
				}
			}
			finally{
				takeMessages.close();
			}
			return recentMessages;
		}
		
		
	
	
	
	

	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		
		String databasePath;
		try{
			databasePath = args[0];
		}
		catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Usage: Database <database name>");
			return;
			}
		
		Class.forName("org.hsqldb.jdbcDriver");
		Connection connection;
		try{
		connection = DriverManager.getConnection("jdbc:hsqldb:file:"+databasePath,"SA","");
		}
		catch(SQLException e){
			System.out.println("Check argument <database name> is correct");
			return;
		}
		Statement delayStmt = connection.createStatement();
		try {
			//Always update data on disk
			delayStmt.execute("SET WRITE_DELAY FALSE");
		}
		finally {
			delayStmt.close();
		}
		connection.setAutoCommit(false);
		Statement sqlStatement = connection.createStatement();
		try{
			sqlStatement.execute("CREATE TABLE messages(nick VARCHAR(255) NOT NULL,"+
					"message VARCHAR(4096) NOT NULL,timeposted BIGINT NOT NULL)");
		}
		catch(SQLException se){
			System.out.println("Error code from SQLException: " + Integer.toString(se.getErrorCode()));
			//System.out.println("Warning: Database table \"messages\" already exists.");
		}
		finally{
			sqlStatement.close();
		}
		String statement = "INSERT INTO messages (nick,message,timeposted) VALUES (?,?,?)";
		PreparedStatement insertMessage = connection.prepareStatement(statement);
		try{
			insertMessage.setString(1, "Alastair");
			insertMessage.setString(2,  "Hello, Andy");
			insertMessage.setLong(3,  System.currentTimeMillis());
			insertMessage.executeUpdate();
		}
		finally{
			insertMessage.close();
		}
		connection.commit();
		statement = "SELECT nick,message,timeposted FROM messages ORDER BY timeposted DESC LIMIT 10";
		PreparedStatement recentMessages = connection.prepareStatement(statement);
		try{
			ResultSet resultSet = recentMessages.executeQuery();
			try{
				while(resultSet.next()){
					System.out.println(resultSet.getString(1)+": "+resultSet.getString(2)+" ["+resultSet.getLong(3)+"]");
				}
			}
			finally{
				resultSet.close();
			}
		}
		finally{
			recentMessages.close();
		}
		connection.close();
		
	}
		
}
	
	

