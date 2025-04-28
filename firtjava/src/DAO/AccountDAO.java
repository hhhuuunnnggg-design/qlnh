package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DTO.Account;
import config.JDBCUtil;

public class AccountDAO {
    private static final String SELECT_ALL_ACCOUNTS = "SELECT * FROM Account";
    private static final String SELECT_ACCOUNT_BY_ID = "SELECT * FROM Account WHERE accountID = ?";
    private static final String INSERT_ACCOUNT = "INSERT INTO Account (username, password, role, staffID) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_ACCOUNT = "UPDATE Account SET username = ?, password = ?, role = ?, staffID = ? WHERE accountID = ?";
    private static final String DELETE_ACCOUNT = "DELETE FROM Account WHERE accountID = ?";
    private static final String SEARCH_BY_ID = "SELECT * FROM Account WHERE accountID = ?";
    private static final String SEARCH_BY_USERNAME = "SELECT * FROM Account WHERE username LIKE ?";
    private static final String SEARCH_BY_ROLE = "SELECT * FROM Account WHERE role LIKE ?";
    private static final String CHECK_STAFF_ID = "SELECT COUNT(*) FROM Account WHERE staffID = ?";

    // Lấy tất cả tài khoản
    public List<Account> getAllAccounts() {
        List<Account> accountList = new ArrayList<>();
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_ACCOUNTS)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int accountID = rs.getInt("accountID");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");
                int staffID = rs.getInt("staffID");
                accountList.add(new Account(accountID, username, password, role, staffID));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving account list: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
        return accountList;
    }

    // Thêm tài khoản
    public void addAccount(Account account) {
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ACCOUNT)) {
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());
            preparedStatement.setString(3, account.getRole());
            preparedStatement.setInt(4, account.getStaffID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding account: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
    }

    // Cập nhật tài khoản
    public void updateAccount(Account account) {
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ACCOUNT)) {
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());
            preparedStatement.setString(3, account.getRole());
            preparedStatement.setInt(4, account.getStaffID());
            preparedStatement.setInt(5, account.getAccountID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating account: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
    }

    // Xóa tài khoản
    public void deleteAccount(int accountID) {
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ACCOUNT)) {
            preparedStatement.setInt(1, accountID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting account: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
    }

    // Lấy tài khoản theo ID
    public Account getAccountByID(int accountID) {
        Account account = null;
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ACCOUNT_BY_ID)) {
            preparedStatement.setInt(1, accountID);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");
                int staffID = rs.getInt("staffID");
                account = new Account(accountID, username, password, role, staffID);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving account: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
        return account;
    }

    // Tìm kiếm tài khoản theo kiểu (ID, username, hoặc role)
    public List<Account> searchAccount(String searchType, String keyword) {
        List<Account> accountList = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllAccounts();
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
            case "Username":
                query = SEARCH_BY_USERNAME;
                break;
            case "Role":
                query = SEARCH_BY_ROLE;
                break;
            default:
                throw new IllegalArgumentException("Kiểu tìm kiếm không hợp lệ: " + searchType);
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (searchType.equals("ID")) {
                try {
                    preparedStatement.setInt(1, Integer.parseInt(keyword));
                } catch (NumberFormatException e) {
                    return accountList; // Trả về danh sách rỗng nếu ID không hợp lệ
                }
            } else {
                preparedStatement.setString(1, "%" + keyword.trim() + "%");
            }
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int accountID = rs.getInt("accountID");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");
                int staffID = rs.getInt("staffID");
                accountList.add(new Account(accountID, username, password, role, staffID));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching account: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
        return accountList;
    }

    // Kiểm tra xem staffID đã được sử dụng trong tài khoản nào chưa
    public boolean isStaffIDUsed(int staffID) {
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(CHECK_STAFF_ID)) {
            preparedStatement.setInt(1, staffID);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking staffID: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
    }
}