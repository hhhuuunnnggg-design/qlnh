package DTO;

public class Staff {
    private int staffID;
    private String staffName;
    private double salary;
    private int workYears;
    private String job;

    // Constructor
    public Staff() {
    }

    public Staff(int staffID, String staffName, double salary, int workYears, String job) {
        this.staffID = staffID;
        this.staffName = staffName;
        this.salary = salary;
        this.workYears = workYears;
        this.job = job;
    }

    // Getters and Setters
    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public int getWorkYears() {
        return workYears;
    }

    public void setWorkYears(int workYears) {
        this.workYears = workYears;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}