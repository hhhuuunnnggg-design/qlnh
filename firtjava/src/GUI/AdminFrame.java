package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import BUS.StaffBUS;
import DTO.Staff;

public class AdminFrame extends JFrame {
    private StaffBUS staffBUS;
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField txtStaffID, txtStaffName, txtSalary, txtWorkYears, txtJob, txtSearch;
    private JComboBox<String> cmbSearchType;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnReset, btnExport, btnImport;

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

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));
        searchPanel.add(new JLabel("Kiểu tìm kiếm:"));
        String[] searchTypes = { "ID", "Tên", "Công việc" };
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

        inputPanel.add(new JLabel("Mã nhân viên:"));
        txtStaffID = new JTextField();
        txtStaffID.setEditable(false);
        inputPanel.add(txtStaffID);

        inputPanel.add(new JLabel("Tên nhân viên:"));
        txtStaffName = new JTextField();
        inputPanel.add(txtStaffName);

        inputPanel.add(new JLabel("Lương:"));
        txtSalary = new JTextField();
        inputPanel.add(txtSalary);

        inputPanel.add(new JLabel("Năm kinh nghiệm:"));
        txtWorkYears = new JTextField();
        inputPanel.add(txtWorkYears);

        inputPanel.add(new JLabel("Công việc:"));
        txtJob = new JTextField();
        inputPanel.add(txtJob);

        // Panel chứa các nút
        JPanel buttonPanel = new JPanel(new FlowLayout());

        btnAdd = new JButton("Thêm");
        btnAdd.setBackground(Color.GREEN);

        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Clear");
        btnExport = new JButton("Export");
        btnImport = new JButton("Import");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnExport);
        buttonPanel.add(btnImport);

        // Bảng hiển thị danh sách nhân viên
        String[] columns = { "Mã nhân viên", "Tên nhân viên", "Lương", "Năm kinh nghiệm", "Công việc" };
        tableModel = new DefaultTableModel(columns, 0);
        staffTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(staffTable);

        // Layout chính
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
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
                if (txtStaffName.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên nhân viên không được để trống!");
                    return;
                }
                if (!txtSalary.getText().matches("\\d+(\\.\\d+)?")) {
                    JOptionPane.showMessageDialog(this, "Lương phải là số hợp lệ!");
                    return;
                }
                if (!txtWorkYears.getText().matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "Năm kinh nghiệm phải là số nguyên!");
                    return;
                }
                Staff staff = new Staff();
                staff.setStaffName(txtStaffName.getText());
                staff.setSalary(Double.parseDouble(txtSalary.getText()));
                staff.setWorkYears(Integer.parseInt(txtWorkYears.getText()));
                staff.setJob(txtJob.getText());
                staffBUS.addStaff(staff);
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
                clearFields();
                loadStaffData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Sự kiện cho nút Update
        btnUpdate.addActionListener(e -> {
            try {
                if (txtStaffID.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để sửa!");
                    return;
                }
                if (txtStaffName.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên nhân viên không được để trống!");
                    return;
                }
                if (!txtSalary.getText().matches("\\d+(\\.\\d+)?")) {
                    JOptionPane.showMessageDialog(this, "Lương phải là số hợp lệ!");
                    return;
                }
                if (!txtWorkYears.getText().matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "Năm kinh nghiệm phải là số nguyên!");
                    return;
                }
                Staff staff = new Staff();
                staff.setStaffID(Integer.parseInt(txtStaffID.getText()));
                staff.setStaffName(txtStaffName.getText());
                staff.setSalary(Double.parseDouble(txtSalary.getText()));
                staff.setWorkYears(Integer.parseInt(txtWorkYears.getText()));
                staff.setJob(txtJob.getText());
                staffBUS.updateStaff(staff);
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
                clearFields();
                loadStaffData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Sự kiện cho nút Delete
        btnDelete.addActionListener(e -> {
            try {
                if (txtStaffID.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để xóa!");
                    return;
                }
                int staffID = Integer.parseInt(txtStaffID.getText());
                staffBUS.deleteStaff(staffID);
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
                clearFields();
                loadStaffData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Mã nhân viên không hợp lệ!");
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
                    JOptionPane.showMessageDialog(this, "Mã nhân viên phải là số!");
                    return;
                }
                loadStaffData(searchType, keyword);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error searching staff: " + ex.getMessage());
            }
        });

        // Sự kiện cho nút Reset
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cmbSearchType.setSelectedIndex(0);
            loadStaffData();
        });

        // Sự kiện cho nút Export
        btnExport.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn nơi lưu file CSV");
            fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".csv")) {
                    file = new File(file.getParentFile(), file.getName() + ".csv");
                }
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("staffID,staffName,salary,workYears,job\n");
                    List<Staff> staffList = staffBUS.getAllStaff();
                    for (Staff staff : staffList) {
                        writer.write(String.format("%d,%s,%.2f,%d,%s\n",
                                staff.getStaffID(),
                                staff.getStaffName().replace(",", ""),
                                staff.getSalary(),
                                staff.getWorkYears(),
                                staff.getJob().replace(",", "")));
                    }
                    JOptionPane.showMessageDialog(this, "Xuất file CSV thành công!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error exporting file: " + ex.getMessage());
                }
            }
        });

        // Sự kiện cho nút Import
        btnImport.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn file CSV để nhập");
            fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    List<Staff> staffList = new ArrayList<>();
                    String line = reader.readLine(); // Bỏ qua header
                    while ((line = reader.readLine()) != null) {
                        String[] data = line.split(",");
                        if (data.length != 5) {
                            throw new IllegalArgumentException("Định dạng dòng không hợp lệ: " + line);
                        }
                        Staff staff = new Staff();
                        try {
                            staff.setStaffID(Integer.parseInt(data[0]));
                        } catch (NumberFormatException ex) {
                            staff.setStaffID(0); // Đặt ID = 0 để thêm mới
                        }
                        staff.setStaffName(data[1]);
                        staff.setSalary(Double.parseDouble(data[2]));
                        staff.setWorkYears(Integer.parseInt(data[3]));
                        staff.setJob(data[4]);
                        staffList.add(staff);
                    }
                    staffBUS.importStaff(staffList);
                    JOptionPane.showMessageDialog(this, "Nhập file CSV thành công!");
                    loadStaffData();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error importing file: " + ex.getMessage());
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Error in file format: " + ex.getMessage());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error importing staff: " + ex.getMessage());
                }
            }
        });
    }

    private void loadStaffData() {
        loadStaffData(null, "");
    }

    private void loadStaffData(String searchType, String keyword) {
        tableModel.setRowCount(0);
        try {
            List<Staff> staffList = (searchType == null) ? staffBUS.getAllStaff()
                    : staffBUS.searchStaff(searchType, keyword);
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
            if (staffList.isEmpty() && searchType != null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên nào!");
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
            new AdminFrame().setVisible(true);
        });
    }
}