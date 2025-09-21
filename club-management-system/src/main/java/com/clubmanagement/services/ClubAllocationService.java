package com.clubmanagement.services;

import com.clubmanagement.dao.ClubAllocationDAO;
import com.clubmanagement.dao.ClubDAO;
import com.clubmanagement.dao.UserDAO;
import com.clubmanagement.models.Club;
import com.clubmanagement.models.ClubAllocation;
import com.clubmanagement.models.User;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ClubAllocationService {
    private ClubAllocationDAO allocationDAO;
    private ClubDAO clubDAO;
    private UserDAO userDAO;

    public ClubAllocationService() {
        this.allocationDAO = new ClubAllocationDAO();
        this.clubDAO = new ClubDAO();
        this.userDAO = new UserDAO();
    }

    public boolean allocateStudentsToClubs() throws SQLException {
        List<User> grade9Students = userDAO.getUsersByRole(User.UserRole.GRADE_9);
        List<Club> clubs = clubDAO.getAllClubs();

        if (clubs.isEmpty()) {
            return false;
        }

        Collections.shuffle(grade9Students);

        int clubIndex = 0;
        for (User student : grade9Students) {
            Club club = clubs.get(clubIndex % clubs.size());
            allocationDAO.allocateStudentToClub(student.getId(), club.getId());
            clubIndex++;
        }

        return true;
    }

    public List<ClubAllocation> getAllAllocations() throws SQLException {
        return allocationDAO.getAllAllocations();
    }

    public List<ClubAllocation> getAllocationsByClub(int clubId) throws SQLException {
        return allocationDAO.getAllocationsByClubId(clubId);
    }

    public ClubAllocation getStudentAllocation(int studentId) throws SQLException {
        return allocationDAO.getAllocationByStudentId(studentId);
    }

    public boolean reallocateStudent(int studentId, int newClubId) throws SQLException {
        return allocationDAO.allocateStudentToClub(studentId, newClubId);
    }

    public int getClubMemberCount(int clubId) throws SQLException {
        return allocationDAO.getClubMemberCount(clubId);
    }

    public boolean isAllocationComplete() throws SQLException {
        List<User> grade9Students = userDAO.getUsersByRole(User.UserRole.GRADE_9);
        List<ClubAllocation> allocations = allocationDAO.getAllAllocations();
        return allocations.size() == grade9Students.size();
    }
}