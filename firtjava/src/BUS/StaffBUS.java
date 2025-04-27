package BUS;

import java.util.List;

import DAO.StaffDAO;
import DTO.Staff;

public class StaffBUS {
    private StaffDAO staffDAO;

    public StaffBUS() {
        staffDAO = new StaffDAO();
    }

    public List<Staff> getAllStaff() {
        return staffDAO.getAllStaff();
    }

    public void addStaff(Staff staff) {
        if (staff.getStaffName() == null || staff.getStaffName().trim().isEmpty()) {
            throw new IllegalArgumentException("Staff name cannot be empty");
        }
        if (staff.getSalary() < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        if (staff.getWorkYears() < 0) {
            throw new IllegalArgumentException("Work years cannot be negative");
        }
        staffDAO.addStaff(staff);
    }

    public void updateStaff(Staff staff) {
        if (staff.getStaffName() == null || staff.getStaffName().trim().isEmpty()) {
            throw new IllegalArgumentException("Staff name cannot be empty");
        }
        if (staff.getSalary() < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        if (staff.getWorkYears() < 0) {
            throw new IllegalArgumentException("Work years cannot be negative");
        }
        staffDAO.updateStaff(staff);
    }

    public void deleteStaff(int staffID) {
        staffDAO.deleteStaff(staffID);
    }

    public Staff getStaffByID(int staffID) {
        return staffDAO.getStaffByID(staffID);
    }
}