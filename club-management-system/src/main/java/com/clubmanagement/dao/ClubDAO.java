package com.clubmanagement.dao;

import com.clubmanagement.database.DatabaseManager;
import com.clubmanagement.models.Club;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClubDAO {

    public List<Club> getAllClubs() throws SQLException {
        String query = "SELECT * FROM clubs ORDER BY name";
        List<Club> clubs = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                clubs.add(new Club(
                    rs.getInt("id"),
                    rs.getString("name")
                ));
            }
        }
        return clubs;
    }

    public Club getClubById(int id) throws SQLException {
        String query = "SELECT * FROM clubs WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Club(
                        rs.getInt("id"),
                        rs.getString("name")
                    );
                }
            }
        }
        return null;
    }

    public boolean insertClub(Club club) throws SQLException {
        String query = "INSERT INTO clubs (name) VALUES (?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, club.getName());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateClub(Club club) throws SQLException {
        String query = "UPDATE clubs SET name = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, club.getName());
            pstmt.setInt(2, club.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteClub(int id) throws SQLException {
        String query = "DELETE FROM clubs WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        }
    }
}