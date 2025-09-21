package com.clubmanagement.services;

import com.clubmanagement.dao.ProposalDAO;
import com.clubmanagement.models.Proposal;

import java.sql.SQLException;
import java.util.List;

public class ProposalService {
    private ProposalDAO proposalDAO;

    public ProposalService() {
        this.proposalDAO = new ProposalDAO();
    }

    public boolean submitProposal(int studentId, String filePath) throws SQLException {
        Proposal proposal = new Proposal(studentId, filePath);
        return proposalDAO.insertProposal(proposal);
    }

    public List<Proposal> getAllProposals() throws SQLException {
        return proposalDAO.getAllProposals();
    }

    public List<Proposal> getProposalsByStudent(int studentId) throws SQLException {
        return proposalDAO.getProposalsByStudentId(studentId);
    }

    public boolean updateProposalStatus(int proposalId, Proposal.ProposalStatus status) throws SQLException {
        return proposalDAO.updateProposalStatus(proposalId, status);
    }

    public boolean acceptProposal(int proposalId) throws SQLException {
        return proposalDAO.updateProposalStatus(proposalId, Proposal.ProposalStatus.ACCEPTED);
    }

    public boolean rejectProposal(int proposalId) throws SQLException {
        return proposalDAO.updateProposalStatus(proposalId, Proposal.ProposalStatus.REJECTED);
    }

    public Proposal getProposalById(int proposalId) throws SQLException {
        return proposalDAO.getProposalById(proposalId);
    }

    public List<Proposal> getPendingProposals() throws SQLException {
        return getAllProposals().stream()
                .filter(p -> p.getStatus() == Proposal.ProposalStatus.PENDING)
                .toList();
    }

    public boolean hasStudentSubmittedProposal(int studentId) throws SQLException {
        List<Proposal> proposals = getProposalsByStudent(studentId);
        return !proposals.isEmpty();
    }
}