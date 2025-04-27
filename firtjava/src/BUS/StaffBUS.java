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
            throw new IllegalArgumentException("Tên nhân viên không được để trống");
        }
        if (staff.getSalary() < 0) {
            throw new IllegalArgumentException("Lương không được âm");
        }
        if (staff.getWorkYears() < 0) {
            throw new IllegalArgumentException("công năm không được âm");
        }
        staffDAO.addStaff(staff);
    }

    public void updateStaff(Staff staff) {
        if (staff.getStaffName() == null || staff.getStaffName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhân viên không được để trống");
        }
        if (staff.getSalary() < 0) {
            throw new IllegalArgumentException("Lương không được âm");
        }
        if (staff.getWorkYears() < 0) {
            throw new IllegalArgumentException("công năm không được âm");
        }
        staffDAO.updateStaff(staff);
    }

    public void deleteStaff(int staffID) {
        staffDAO.deleteStaff(staffID);
    }

    public Staff getStaffByID(int staffID) {
        return staffDAO.getStaffByID(staffID);
    }

    public List<Staff> searchStaff(String searchType, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllStaff(); // Trả về toàn bộ danh sách nếu từ khóa rỗng
        }
        if (!searchType.equals("ID") && !searchType.equals("Tên") && !searchType.equals("Công việc")) {
            throw new IllegalArgumentException("Kiểu tìm kiếm không hợp lệ: " + searchType);
        }
        return staffDAO.searchStaff(searchType, keyword.trim());
    }
}