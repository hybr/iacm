package com.clubmanagement.dao;

import com.clubmanagement.database.DatabaseManager;
import com.clubmanagement.models.ClubAllocation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClubAllocationDAO {

    public boolean allocateStudentToClub(int studentId, int clubId) throws SQLException {
        String query = "INSERT OR REPLACE INTO club_allocation (student_id, club_id) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, clubId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public List<ClubAllocation> getAllAllocations() throws SQLException {
        String query = "SELECT * FROM club_allocation ORDER BY club_id, student_id";
        List<ClubAllocation> allocations = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                allocations.add(new ClubAllocation(
                    rs.getInt("id"),
                    rs.getInt("student_id"),
                    rs.getInt("club_id")
                ));
            }
        }
        return allocations;
    }

    public List<ClubAllocation> getAllocationsByClubId(int clubId) throws SQLException {
        String query = "SELECT * FROM club_allocation WHERE club_id = ?";
        List<ClubAllocation> allocations = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, clubId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    allocations.add(new ClubAllocation(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("club_id")
                    ));
                }
            }
        }
        return allocations;
    }

    public ClubAllocation getAllocationByStudentId(int studentId) throws SQLException {
        String query = "SELECT * FROM club_allocation WHERE student_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ClubAllocation(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("club_id")
                    );
                }
            }
        }
        return null;
    }

    public boolean removeAllocation(int studentId) throws SQLException {
        String query = "DELETE FROM club_allocation WHERE student_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public int getClubMemberCount(int clubId) throws SQLException {
        String query = "SELECT COUNT(*) FROM club_allocation WHERE club_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, clubId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}