package com.clubmanagement.models;

public class ClubAllocation {
    private int id;
    private int studentId;
    private int clubId;

    public ClubAllocation() {}

    public ClubAllocation(int studentId, int clubId) {
        this.studentId = studentId;
        this.clubId = clubId;
    }

    public ClubAllocation(int id, int studentId, int clubId) {
        this.id = id;
        this.studentId = studentId;
        this.clubId = clubId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    @Override
    public String toString() {
        return "ClubAllocation{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", clubId=" + clubId +
                '}';
    }
}