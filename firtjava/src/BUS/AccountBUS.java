package BUS;

import java.util.List;

import DAO.AccountDAO;
import DAO.StaffDAO;
import DTO.Account;

public class AccountBUS {
    private AccountDAO accountDAO;
    private StaffDAO staffDAO;

    public AccountBUS() {
        accountDAO = new AccountDAO();
        staffDAO = new StaffDAO();
    }

    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    public void addAccount(Account account) {
        if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }
        if (account.getPassword() == null || account.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        if (!account.getRole().equals("manager") && !account.getRole().equals("staff")) {
            throw new IllegalArgumentException("Vai trò phải là 'manager' hoặc 'staff'");
        }
        if (account.getStaffID() <= 0) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ");
        }
        // Kiểm tra staffID tồn tại
        if (staffDAO.getStaffByID(account.getStaffID()) == null) {
            throw new IllegalArgumentException("Mã nhân viên không tồn tại");
        }
        // Kiểm tra staffID chưa được sử dụng
        if (accountDAO.isStaffIDUsed(account.getStaffID())) {
            throw new IllegalArgumentException("Mã nhân viên đã được liên kết với một tài khoản khác");
        }
        accountDAO.addAccount(account);
    }

    public void updateAccount(Account account) {
        if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }
        if (account.getPassword() == null || account.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        if (!account.getRole().equals("manager") && !account.getRole().equals("staff")) {
            throw new IllegalArgumentException("Vai trò phải là 'manager' hoặc 'staff'");
        }
        if (account.getStaffID() <= 0) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ");
        }
        // Kiểm tra staffID tồn tại
        if (staffDAO.getStaffByID(account.getStaffID()) == null) {
            throw new IllegalArgumentException("Mã nhân viên không tồn tại");
        }
        // Kiểm tra staffID không được sử dụng bởi tài khoản khác
        Account existingAccount = accountDAO.getAccountByID(account.getAccountID());
        if (existingAccount != null && existingAccount.getStaffID() != account.getStaffID()) {
            if (accountDAO.isStaffIDUsed(account.getStaffID())) {
                throw new IllegalArgumentException("Mã nhân viên đã được liên kết với một tài khoản khác");
            }
        }
        accountDAO.updateAccount(account);
    }

    public void deleteAccount(int accountID) {
        accountDAO.deleteAccount(accountID);
    }

    public Account getAccountByID(int accountID) {
        return accountDAO.getAccountByID(accountID);
    }

    public List<Account> searchAccount(String searchType, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllAccounts();
        }
        if (!searchType.equals("ID") && !searchType.equals("Username") && !searchType.equals("Role")) {
            throw new IllegalArgumentException("Kiểu tìm kiếm không hợp lệ: " + searchType);
        }
        return accountDAO.searchAccount(searchType, keyword.trim());
    }
}