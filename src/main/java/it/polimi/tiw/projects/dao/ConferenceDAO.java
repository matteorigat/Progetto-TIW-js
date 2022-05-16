package it.polimi.tiw.projects.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Conference;

public class ConferenceDAO {
    private Connection connection;
    public ConferenceDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Conference> findConferenceByUser(int userId) throws SQLException {
        List<Conference> conferences = new ArrayList<Conference>();

        String query = "SELECT * FROM conferences WHERE host = ? AND date >= CURRENT_TIMESTAMP ORDER BY date";
        try (PreparedStatement pstatement = connection.prepareStatement(query);) {
            pstatement.setInt(1, userId);
            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    Conference conference = new Conference();
                    conference.setId(result.getInt("id"));
                    conference.setTitle(result.getString("title"));
                    conference.setDate(result.getTimestamp("date"));
                    conference.setDuration(result.getTime("duration"));
                    conference.setGuests(result.getInt("guests"));
                    conference.setHostId(userId);
                    conferences.add(conference);
                }
            }
        }
        return conferences;
    }

    public List<Conference> findConference2ByUser(int userId) throws SQLException {
        List<Conference> conferences = new ArrayList<Conference>();

        String query = "SELECT * FROM conferences WHERE id IN (SELECT idconference FROM guests WHERE idguest = ?) AND date >= CURRENT_TIMESTAMP ORDER BY date";
        try (PreparedStatement pstatement = connection.prepareStatement(query);) {
            pstatement.setInt(1, userId);
            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    Conference conference = new Conference();
                    conference.setId(result.getInt("id"));
                    conference.setTitle(result.getString("title"));
                    conference.setDate(result.getTimestamp("date"));
                    conference.setDuration(result.getTime("duration"));
                    conference.setGuests(result.getInt("guests"));
                    conference.setHostId(userId);
                    conferences.add(conference);
                }
            }
        }
        return conferences;
    }

    public int findLastConferenceByUser(int userId) throws SQLException {
        int conferenceId = 0;

        String query = "SELECT * FROM conferences WHERE host = ? ORDER BY id DESC"; // ordina le conference per id decrescente (quindi in ordine di creazione dall'ultima alla prima)
        try (PreparedStatement pstatement = connection.prepareStatement(query);) {
            pstatement.setInt(1, userId);
            try (ResultSet result = pstatement.executeQuery();) {
                if (result.next()) {
                    conferenceId = result.getInt("id");
                }
            }
        }
        return conferenceId;
    }

    public void deleteConferenceById(int conferenceId) throws SQLException {
        String query = "DELETE FROM conferences WHERE id = ?";
        try (PreparedStatement pstatement = connection.prepareStatement(query);) {
            pstatement.setInt(1, conferenceId);
            pstatement.executeUpdate();
        }
    }


    public void createConference(Conference conference)
            throws SQLException {

        String query = "INSERT INTO conferences (host, title, date, duration, guests) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstatement = connection.prepareStatement(query);) {
            pstatement.setInt(1, conference.getHostId());
            pstatement.setString(2, conference.getTitle());
            pstatement.setTimestamp(3, conference.getDate());
            pstatement.setTime(4, conference.getDuration());
            pstatement.setInt(5, conference.getGuests());
            pstatement.executeUpdate();
        }
    }

}