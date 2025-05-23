package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DTO.Staff;
import config.JDBCUtil;

public class StaffDAO {
    private static final String SELECT_ALL_STAFF = "SELECT * FROM Staff";
    private static final String SELECT_STAFF_BY_ID = "SELECT * FROM Staff WHERE staffID = ?";
    private static final String INSERT_STAFF = "INSERT INTO Staff (staffName, Salary, workYears, Job) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_STAFF = "UPDATE Staff SET staffName = ?, Salary = ?, workYears = ?, Job = ? WHERE staffID = ?";
    private static final String DELETE_STAFF = "DELETE FROM Staff WHERE staffID = ?";
    private static final String SEARCH_BY_ID = "SELECT * FROM Staff WHERE staffID = ?";
    private static final String SEARCH_BY_NAME = "SELECT * FROM Staff WHERE staffName LIKE ?";
    private static final String SEARCH_BY_JOB = "SELECT * FROM Staff WHERE Job LIKE ?";

    // Lấy tất cả nhân viên
    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_STAFF)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int staffID = rs.getInt("staffID");
                String staffName = rs.getString("staffName");
                double salary = rs.getDouble("Salary");
                int workYears = rs.getInt("workYears");
                String job = rs.getString("Job");
                staffList.add(new Staff(staffID, staffName, salary, workYears, job));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving staff list: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
        return staffList;
    }

    // Thêm nhân viên
    public void addStaff(Staff staff) {
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STAFF)) {
            preparedStatement.setString(1, staff.getStaffName());
            preparedStatement.setDouble(2, staff.getSalary());
            preparedStatement.setInt(3, staff.getWorkYears());
            preparedStatement.setString(4, staff.getJob());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding staff: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
    }

    // Cập nhật nhân viên
    public void updateStaff(Staff staff) {
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STAFF)) {
            preparedStatement.setString(1, staff.getStaffName());
            preparedStatement.setDouble(2, staff.getSalary());
            preparedStatement.setInt(3, staff.getWorkYears());
            preparedStatement.setString(4, staff.getJob());
            preparedStatement.setInt(5, staff.getStaffID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating staff: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
    }

    // Xóa nhân viên
    public void deleteStaff(int staffID) {
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        try {
            // Xóa tài khoản liên quan trước
            AccountDAO accountDAO = new AccountDAO();
            accountDAO.deleteAccountByStaffID(staffID);

            // Sau đó xóa nhân viên
            try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_STAFF)) {
                preparedStatement.setInt(1, staffID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting staff: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
    }

    // Lấy nhân viên theo ID
    public Staff getStaffByID(int staffID) {
        Staff staff = null;
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_STAFF_BY_ID)) {
            preparedStatement.setInt(1, staffID);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String staffName = rs.getString("staffName");
                double salary = rs.getDouble("Salary");
                int workYears = rs.getInt("workYears");
                String job = rs.getString("Job");
                staff = new Staff(staffID, staffName, salary, workYears, job);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving staff: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
        return staff;
    }

    // Tìm kiếm nhân viên theo kiểu (ID, tên, hoặc công việc)
    public List<Staff> searchStaff(String searchType, String keyword) {
        List<Staff> staffList = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllStaff();
        }
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        String query;
        switch (searchType) {
            case "ID":
                query = SEARCH_BY_ID;
                break;
            case "Tên":
                query = SEARCH_BY_NAME;
                break;
            case "Công việc":
                query = SEARCH_BY_JOB;
                break;
            default:
                throw new IllegalArgumentException("Kiểu tìm kiếm không hợp lệ: " + searchType);
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (searchType.equals("ID")) {
                try {
                    preparedStatement.setInt(1, Integer.parseInt(keyword));
                } catch (NumberFormatException e) {
                    return staffList; // Trả về danh sách rỗng nếu ID không hợp lệ
                }
            } else {
                preparedStatement.setString(1, "%" + keyword.trim() + "%");
            }
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int staffID = rs.getInt("staffID");
                String staffName = rs.getString("staffName");
                double salary = rs.getDouble("Salary");
                int workYears = rs.getInt("workYears");
                String job = rs.getString("Job");
                staffList.add(new Staff(staffID, staffName, salary, workYears, job));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching staff: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
        return staffList;
    }
}