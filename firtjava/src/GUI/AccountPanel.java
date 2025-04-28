package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import BUS.AccountBUS;
import BUS.StaffBUS;
import DTO.Account;
import DTO.Staff;

public class AccountPanel extends JPanel {
    private AccountBUS accountBUS;
    private StaffBUS staffBUS;
    private JTable accountTable;
    private DefaultTableModel tableModel;
    private JTextField txtAccountID, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole, cmbSearchType;
    private JComboBox<Staff> cmbStaff;
    private JTextField txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnReset;

    public AccountPanel() {
        accountBUS = new AccountBUS();
        staffBUS = new StaffBUS();
        initComponents();
        loadAccountData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));
        searchPanel.add(new JLabel("Kiểu tìm kiếm:"));
        String[] searchTypes = { "ID", "Username", "Role" };
        cmbSearchType = new JComboBox<>(searchTypes);
        searchPanel.add(cmbSearchType);
        searchPanel.add(new JLabel("Từ khóa:"));
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);
        btnSearch = new JButton("Tìm");
        btnReset = new JButton("Reset");
        searchPanel.add(btnSearch);
        searchPanel.add(btnReset);

        // Panel chứa các trường nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Mã tài khoản:"));
        txtAccountID = new JTextField();
        txtAccountID.setEditable(false);
        inputPanel.add(txtAccountID);

        inputPanel.add(new JLabel("Tên đăng nhập:"));
        txtUsername = new JTextField();
        inputPanel.add(txtUsername);

        inputPanel.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField();
        inputPanel.add(txtPassword);

        inputPanel.add(new JLabel("Vai trò:"));
        String[] roles = { "manager", "staff" };
        cmbRole = new JComboBox<>(roles);
        inputPanel.add(cmbRole);

        inputPanel.add(new JLabel("Nhân viên:"));
        cmbStaff = new JComboBox<>();
        loadAvailableStaff();
        inputPanel.add(cmbStaff);

        // Panel chứa các nút
        JPanel buttonPanel = new JPanel(new FlowLayout());

        btnAdd = new JButton("Thêm");
        btnAdd.setBackground(Color.GREEN);

        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Clear");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        // Bảng hiển thị danh sách tài khoản
        String[] columns = { "Mã tài khoản", "Tên đăng nhập", "Mật khẩu", "Vai trò", "Mã nhân viên", "Tên nhân viên" };
        tableModel = new DefaultTableModel(columns, 0);
        accountTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(accountTable);

        // Layout chính
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Sự kiện cho bảng
        accountTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = accountTable.getSelectedRow();
                if (row >= 0) {
                    txtAccountID.setText(tableModel.getValueAt(row, 0).toString());
                    txtUsername.setText(tableModel.getValueAt(row, 1).toString());
                    txtPassword.setText(tableModel.getValueAt(row, 2).toString());
                    cmbRole.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                    int staffID = Integer.parseInt(tableModel.getValueAt(row, 4).toString());
                    loadAvailableStaff(staffID); // Tải lại danh sách, bao gồm nhân viên hiện tại
                }
            }
        });

        // Sự kiện cho nút Add
        btnAdd.addActionListener(e -> {
            try {
                if (txtUsername.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên đăng nhập không được để trống!");
                    return;
                }
                if (txtPassword.getPassword().length == 0) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống!");
                    return;
                }
                if (cmbStaff.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!");
                    return;
                }
                Account account = new Account();
                account.setUsername(txtUsername.getText());
                account.setPassword(new String(txtPassword.getPassword()));
                account.setRole((String) cmbRole.getSelectedItem());
                account.setStaffID(((Staff) cmbStaff.getSelectedItem()).getStaffID());
                accountBUS.addAccount(account);
                JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!");
                clearFields();
                loadAccountData();
                loadAvailableStaff();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Sự kiện cho nút Update
        btnUpdate.addActionListener(e -> {
            try {
                if (txtAccountID.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản để sửa!");
                    return;
                }
                if (txtUsername.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên đăng nhập không được để trống!");
                    return;
                }
                if (txtPassword.getPassword().length == 0) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống!");
                    return;
                }
                if (cmbStaff.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!");
                    return;
                }
                Account account = new Account();
                account.setAccountID(Integer.parseInt(txtAccountID.getText()));
                account.setUsername(txtUsername.getText());
                account.setPassword(new String(txtPassword.getPassword()));
                account.setRole((String) cmbRole.getSelectedItem());
                account.setStaffID(((Staff) cmbStaff.getSelectedItem()).getStaffID());
                accountBUS.updateAccount(account);
                JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!");
                clearFields();
                loadAccountData();
                loadAvailableStaff();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu số không hợp lệ!");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Sự kiện cho nút Delete
        btnDelete.addActionListener(e -> {
            try {
                if (txtAccountID.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản để xóa!");
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa tài khoản này?", "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int accountID = Integer.parseInt(txtAccountID.getText());
                    accountBUS.deleteAccount(accountID);
                    JOptionPane.showMessageDialog(this, "Xóa tài khoản thành công!");
                    clearFields();
                    loadAccountData();
                    loadAvailableStaff();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Mã tài khoản không hợp lệ!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Sự kiện cho nút Clear
        btnClear.addActionListener(e -> clearFields());

        // Sự kiện cho nút Search
        btnSearch.addActionListener(e -> {
            try {
                String keyword = txtSearch.getText().trim();
                String searchType = (String) cmbSearchType.getSelectedItem();
                if (keyword.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!");
                    return;
                }
                if (searchType.equals("ID") && !keyword.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "Mã tài khoản phải là số!");
                    return;
                }
                loadAccountData(searchType, keyword);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error searching account: " + ex.getMessage());
            }
        });

        // Sự kiện cho nút Reset
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cmbSearchType.setSelectedIndex(0);
            loadAccountData();
        });
    }

    private void loadAccountData() {
        loadAccountData(null, "");
    }

    private void loadAccountData(String searchType, String keyword) {
        tableModel.setRowCount(0);
        try {
            List<Account> accountList = (searchType == null) ? accountBUS.getAllAccounts()
                    : accountBUS.searchAccount(searchType, keyword);
            for (Account account : accountList) {
                Staff staff = staffBUS.getStaffByID(account.getStaffID());
                Object[] row = {
                        account.getAccountID(),
                        account.getUsername(),
                        account.getPassword(),
                        account.getRole(),
                        account.getStaffID(),
                        staff != null ? staff.getStaffName() : "N/A"
                };
                tableModel.addRow(row);
            }
            if (accountList.isEmpty() && searchType != null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản nào!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading account data: " + ex.getMessage());
        }
    }

    private void loadAvailableStaff() {
        loadAvailableStaff(-1); // Không chọn nhân viên nào
    }

    private void loadAvailableStaff(int currentStaffID) {
        cmbStaff.removeAllItems();
        try {
            List<Staff> allStaff = staffBUS.getAllStaff();
            List<Account> accounts = accountBUS.getAllAccounts();
            List<Integer> usedStaffIDs = accounts.stream()
                    .map(Account::getStaffID)
                    .collect(Collectors.toList());
            // Thêm nhân viên hiện tại (nếu đang sửa)
            if (currentStaffID > 0 && !usedStaffIDs.contains(currentStaffID)) {
                usedStaffIDs.add(currentStaffID);
            }
            for (Staff staff : allStaff) {
                if (!usedStaffIDs.contains(staff.getStaffID()) || staff.getStaffID() == currentStaffID) {
                    cmbStaff.addItem(staff);
                }
            }
            // Đặt nhân viên hiện tại làm mặc định nếu đang sửa
            if (currentStaffID > 0) {
                for (int i = 0; i < cmbStaff.getItemCount(); i++) {
                    if (cmbStaff.getItemAt(i).getStaffID() == currentStaffID) {
                        cmbStaff.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading staff list: " + ex.getMessage());
        }
    }

    private void clearFields() {
        txtAccountID.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        cmbRole.setSelectedIndex(0);
        loadAvailableStaff();
    }
}