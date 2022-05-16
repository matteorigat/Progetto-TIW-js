package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.projects.beans.UserBean;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	public UserBean checkCredentials(String usrn, String pwd) throws SQLException {
		String query = "SELECT id, username, name, surname FROM users WHERE username = ? AND password = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					UserBean user = new UserBean();
					user.setId(result.getInt("id"));
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					return user;
				}
			}
		}
	}

	public Boolean checkUsername(String usrn) throws SQLException { // passo l'username e controllo se è l'unico nel database
		String query = "SELECT * FROM users WHERE username = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return false;
				else {
					result.next();
					return true;
				}
			}
		}
	}

	public String registerUser(UserBean userBean) {
		String name = userBean.getName();
		String surname = userBean.getSurname();
		String email = userBean.getEmail();
		String username = userBean.getUsername();
		String password = userBean.getPassword();

		String query = "INSERT INTO users(username,password,name,surname,email) VALUES (?,?,?,?,?)"; //Insert user details into the table 'USERS'
		try(PreparedStatement pstatement = con.prepareStatement(query);){
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			pstatement.setString(3, name);
			pstatement.setString(4, surname);
			pstatement.setString(5, email);

			int i= pstatement.executeUpdate();

			if (i!=0)  //Just to ensure data has been inserted into the database
				return "SUCCESS";
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return "Oops.. Something went wrong there..!";  // On failure, send a message from here.
	}


	public ArrayList<UserBean> getUsers(int userId) throws SQLException {
		ArrayList<UserBean> users = new ArrayList<>();

		String query = "SELECT * FROM users WHERE id <> ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					UserBean user = new UserBean();
					user.setId(result.getInt("id"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					users.add(user);
				}
			}
		}
		return users;
	}
}
