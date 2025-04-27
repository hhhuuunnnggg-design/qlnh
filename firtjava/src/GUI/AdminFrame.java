package GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import BUS.StaffBUS;
import DTO.Staff;
import config.JDBCUtil;

public class AdminFrame extends JFrame {
    private StaffBUS staffBUS;
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField txtStaffID, txtStaffName, txtSalary, txtWorkYears, txtJob;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public AdminFrame() {
        staffBUS = new StaffBUS();
        initComponents();
        loadStaffData();
    }

    private void initComponents() {
        setTitle("Admin - Staff Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel chứa các trường nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Staff ID:"));
        txtStaffID = new JTextField();
        txtStaffID.setEditable(false);
        inputPanel.add(txtStaffID);

        inputPanel.add(new JLabel("Staff Name:"));
        txtStaffName = new JTextField();
        inputPanel.add(txtStaffName);

        inputPanel.add(new JLabel("Salary:"));
        txtSalary = new JTextField();
        inputPanel.add(txtSalary);

        inputPanel.add(new JLabel("Work Years:"));
        txtWorkYears = new JTextField();
        inputPanel.add(txtWorkYears);

        inputPanel.add(new JLabel("Job:"));
        txtJob = new JTextField();
        inputPanel.add(txtJob);

        // Panel chứa các nút
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        // Bảng hiển thị danh sách nhân viên
        String[] columns = { "Staff ID", "Staff Name", "Salary", "Work Years", "Job" };
        tableModel = new DefaultTableModel(columns, 0);
        staffTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(staffTable);

        // Layout chính
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Sự kiện cho bảng
        staffTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = staffTable.getSelectedRow();
                if (row >= 0) {
                    txtStaffID.setText(tableModel.getValueAt(row, 0).toString());
                    txtStaffName.setText(tableModel.getValueAt(row, 1).toString());
                    txtSalary.setText(tableModel.getValueAt(row, 2).toString());
                    txtWorkYears.setText(tableModel.getValueAt(row, 3).toString());
                    txtJob.setText(tableModel.getValueAt(row, 4).toString());
                }
            }
        });

        // Sự kiện cho nút Add
        btnAdd.addActionListener(e -> {
            try {
                Staff staff = new Staff();
                staff.setStaffName(txtStaffName.getText());
                staff.setSalary(Double.parseDouble(txtSalary.getText()));
                staff.setWorkYears(Integer.parseInt(txtWorkYears.getText()));
                staff.setJob(txtJob.getText());
                staffBUS.addStaff(staff);
                JOptionPane.showMessageDialog(this, "Staff added successfully!");
                clearFields();
                loadStaffData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Sự kiện cho nút Update
        btnUpdate.addActionListener(e -> {
            try {
                Staff staff = new Staff();
                staff.setStaffID(Integer.parseInt(txtStaffID.getText()));
                staff.setStaffName(txtStaffName.getText());
                staff.setSalary(Double.parseDouble(txtSalary.getText()));
                staff.setWorkYears(Integer.parseInt(txtWorkYears.getText()));
                staff.setJob(txtJob.getText());
                staffBUS.updateStaff(staff);
                JOptionPane.showMessageDialog(this, "Staff updated successfully!");
                clearFields();
                loadStaffData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Sự kiện cho nút Delete
        btnDelete.addActionListener(e -> {
            try {
                int staffID = Integer.parseInt(txtStaffID.getText());
                staffBUS.deleteStaff(staffID);
                JOptionPane.showMessageDialog(this, "Staff deleted successfully!");
                clearFields();
                loadStaffData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Sự kiện cho nút Clear
        btnClear.addActionListener(e -> clearFields());
    }

    private void loadStaffData() {
        tableModel.setRowCount(0);
        try {
            List<Staff> staffList = staffBUS.getAllStaff();
            for (Staff staff : staffList) {
                Object[] row = {
                        staff.getStaffID(),
                        staff.getStaffName(),
                        staff.getSalary(),
                        staff.getWorkYears(),
                        staff.getJob()
                };
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading staff data: " + ex.getMessage());
        }
    }

    private void clearFields() {
        txtStaffID.setText("");
        txtStaffName.setText("");
        txtSalary.setText("");
        txtWorkYears.setText("");
        txtJob.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Connection conn = JDBCUtil.getConnection();
            if (conn != null) {
                try {
                    new AdminFrame().setVisible(true);
                } finally {
                    JDBCUtil.closeConnection(conn);
                }
            } else {
                System.err.println("Failed to connect to database");
                // JOptionPane đã được hiển thị trong JDBCUtil
            }
        });
    }
}