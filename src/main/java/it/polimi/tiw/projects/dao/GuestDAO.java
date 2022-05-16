package it.polimi.tiw.projects.dao;


import java.sql.*;

public class GuestDAO {
	private Connection connection;

	public GuestDAO(Connection connection) {
		this.connection = connection;
	}

	public void registerGuests(String[] guests, int conferenceId) throws SQLException{

		for(int i=0; i<guests.length; i++){
			String query = "INSERT INTO guests (idconference, idguest) VALUES (?, ?)";
			try (PreparedStatement pstatement = connection.prepareStatement(query);) {
				pstatement.setInt(1, conferenceId);
				pstatement.setInt(2, Integer.parseInt(guests[i]));

				pstatement.executeUpdate();
			}
		}
	}
}
