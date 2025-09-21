package com.clubmanagement.dao;

import com.clubmanagement.database.DatabaseManager;
import com.clubmanagement.models.Proposal;
import com.clubmanagement.models.Proposal.ProposalStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProposalDAO {

    public boolean insertProposal(Proposal proposal) throws SQLException {
        String query = "INSERT INTO proposals (student_id, file_path, status) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, proposal.getStudentId());
            pstmt.setString(2, proposal.getFilePath());
            pstmt.setString(3, proposal.getStatus().name());

            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Proposal> getAllProposals() throws SQLException {
        String query = "SELECT * FROM proposals";
        List<Proposal> proposals = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                proposals.add(new Proposal(
                    rs.getInt("id"),
                    rs.getInt("student_id"),
                    rs.getString("file_path"),
                    ProposalStatus.valueOf(rs.getString("status"))
                ));
            }
        }
        return proposals;
    }

    public List<Proposal> getProposalsByStudentId(int studentId) throws SQLException {
        String query = "SELECT * FROM proposals WHERE student_id = ?";
        List<Proposal> proposals = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    proposals.add(new Proposal(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getString("file_path"),
                        ProposalStatus.valueOf(rs.getString("status"))
                    ));
                }
            }
        }
        return proposals;
    }

    public boolean updateProposalStatus(int proposalId, ProposalStatus status) throws SQLException {
        String query = "UPDATE proposals SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status.name());
            pstmt.setInt(2, proposalId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public Proposal getProposalById(int id) throws SQLException {
        String query = "SELECT * FROM proposals WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Proposal(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getString("file_path"),
                        ProposalStatus.valueOf(rs.getString("status"))
                    );
                }
            }
        }
        return null;
    }
}