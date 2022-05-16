package it.polimi.tiw.projects.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;

public class ConnectionHandler {

	public static Connection getConnection(ServletContext context) throws UnavailableException {
		Connection connection = null;
		try {

			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
		return connection;
	}

	public static void closeConnection(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	public static Connection createConnection()
	{
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/Progetto-Tiw?serverTimezone=UTC"; //MySQL URL followed by the database name
		String username = "root"; //MySQL username
		String password = "password"; //MySQL password
		System.out.println("In DBConnection.java class ");

		try
		{
			try
			{
				Class.forName("com.mysql.cj.jdbc.Driver"); //loading MySQL drivers. This differs for database servers
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			con = DriverManager.getConnection(url, username, password); //attempting to connect to MySQL database
			System.out.println("Printing connection object " + con);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return con;
	}
	
}
