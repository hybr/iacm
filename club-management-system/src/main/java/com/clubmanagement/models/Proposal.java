package com.clubmanagement.models;

public class Proposal {
    private int id;
    private int studentId;
    private String filePath;
    private ProposalStatus status;

    public enum ProposalStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    public Proposal() {}

    public Proposal(int studentId, String filePath) {
        this.studentId = studentId;
        this.filePath = filePath;
        this.status = ProposalStatus.PENDING;
    }

    public Proposal(int id, int studentId, String filePath, ProposalStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.filePath = filePath;
        this.status = status;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ProposalStatus getStatus() {
        return status;
    }

    public void setStatus(ProposalStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Proposal{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", filePath='" + filePath + '\'' +
                ", status=" + status +
                '}';
    }
}