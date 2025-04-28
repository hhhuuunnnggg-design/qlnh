package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import BUS.DrinkBUS;
import DTO.Drink;

public class DrinkPanel extends JPanel {
    private DrinkBUS drinkBUS;
    private JTable drinkTable;
    private DefaultTableModel tableModel;
    private JTextField txtDrinkID, txtDrinkName, txtDrinkPrice;
    private JLabel lblImgPath; // Hiển thị đường dẫn ảnh đã chọn
    private JLabel imgPreview; // Hiển thị hình ảnh xem trước
    private JButton btnChooseImg; // Nút để chọn ảnh
    private JComboBox<String> cmbSearchType;
    private JTextField txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnReset;
    private String selectedImgPath; // Lưu đường dẫn ảnh đã chọn

    public DrinkPanel() {
        drinkBUS = new DrinkBUS();
        selectedImgPath = null; // Khởi tạo đường dẫn ảnh
        initComponents();
        loadDrinkData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));
        searchPanel.add(new JLabel("Kiểu tìm kiếm:"));
        String[] searchTypes = { "ID", "Tên" };
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

        inputPanel.add(new JLabel("Mã đồ uống:"));
        txtDrinkID = new JTextField();
        txtDrinkID.setEditable(false);
        inputPanel.add(txtDrinkID);

        inputPanel.add(new JLabel("Tên đồ uống:"));
        txtDrinkName = new JTextField();
        inputPanel.add(txtDrinkName);

        inputPanel.add(new JLabel("Giá:"));
        txtDrinkPrice = new JTextField();
        inputPanel.add(txtDrinkPrice);

        inputPanel.add(new JLabel("Hình ảnh:"));
        JPanel imgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnChooseImg = new JButton("Chọn ảnh");
        lblImgPath = new JLabel("Chưa chọn ảnh");
        btnChooseImg.addActionListener(e -> chooseImage());
        imgPanel.add(btnChooseImg);
        imgPanel.add(lblImgPath);
        inputPanel.add(imgPanel);

        inputPanel.add(new JLabel("Xem trước:"));
        imgPreview = new JLabel();
        imgPreview.setPreferredSize(new java.awt.Dimension(200, 20)); // Kích thước xem trước
        inputPanel.add(imgPreview);

        // Panel chứa các nút
        JPanel buttonPanel = new JPanel(new FlowLayout());

        btnAdd = new JButton("Thêm");
        btnAdd.setBackground(Color.GREEN);
        btnAdd.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Clear");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        // Bảng hiển thị danh sách đồ uống
        String[] columns = { "Mã đồ uống", "Tên đồ uống", "Giá", "Hình ảnh" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) { // Cột "Hình ảnh"
                    return ImageIcon.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        drinkTable = new JTable(tableModel);
        drinkTable.setRowHeight(50); // Chiều cao hàng để hiển thị hình ảnh
        drinkTable.getColumnModel().getColumn(3).setCellRenderer(new ImageRenderer()); // Renderer tùy chỉnh cho cột
                                                                                       // hình ảnh
        JScrollPane scrollPane = new JScrollPane(drinkTable);

        // Layout chính
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Sự kiện cho bảng
        drinkTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = drinkTable.getSelectedRow();
                if (row >= 0) {
                    txtDrinkID.setText(tableModel.getValueAt(row, 0).toString());
                    txtDrinkName.setText(tableModel.getValueAt(row, 1).toString());
                    txtDrinkPrice.setText(tableModel.getValueAt(row, 2).toString());
                    String imgPath = tableModel.getValueAt(row, 3) != null
                            ? (String) ((ImageIcon) tableModel.getValueAt(row, 3)).getDescription()
                            : "";
                    selectedImgPath = imgPath.isEmpty() ? null : imgPath;
                    lblImgPath.setText(imgPath.isEmpty() ? "Chưa chọn ảnh" : imgPath);
                    imgPreview.setIcon(imgPath.isEmpty() ? null
                            : new ImageIcon(
                                    new ImageIcon(imgPath).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
                }
            }
        });

        // Sự kiện cho nút Add
        btnAdd.addActionListener(e -> {
            try {
                if (txtDrinkName.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên đồ uống không được để trống!");
                    return;
                }
                if (!txtDrinkPrice.getText().matches("\\d+(\\.\\d+)?")) {
                    JOptionPane.showMessageDialog(this, "Giá phải là số hợp lệ!");
                    return;
                }
                Drink drink = new Drink();
                drink.setDrinkName(txtDrinkName.getText());
                drink.setDrinkPrice(Double.parseDouble(txtDrinkPrice.getText()));
                drink.setImg(selectedImgPath);
                drinkBUS.addDrink(drink);
                JOptionPane.showMessageDialog(this, "Thêm đồ uống thành công!");
                clearFields();
                loadDrinkData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Sự kiện cho nút Update
        btnUpdate.addActionListener(e -> {
            try {
                if (txtDrinkID.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn đồ uống để sửa!");
                    return;
                }
                if (txtDrinkName.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên đồ uống không được để trống!");
                    return;
                }
                if (!txtDrinkPrice.getText().matches("\\d+(\\.\\d+)?")) {
                    JOptionPane.showMessageDialog(this, "Giá phải là số hợp lệ!");
                    return;
                }
                Drink drink = new Drink();
                drink.setDrinkID(Integer.parseInt(txtDrinkID.getText()));
                drink.setDrinkName(txtDrinkName.getText());
                drink.setDrinkPrice(Double.parseDouble(txtDrinkPrice.getText()));
                drink.setImg(selectedImgPath);
                drinkBUS.updateDrink(drink);
                JOptionPane.showMessageDialog(this, "Cập nhật đồ uống thành công!");
                clearFields();
                loadDrinkData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Sự kiện cho nút Delete
        btnDelete.addActionListener(e -> {
            try {
                if (txtDrinkID.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn đồ uống để xóa!");
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa đồ uống này?", "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int drinkID = Integer.parseInt(txtDrinkID.getText());
                    drinkBUS.deleteDrink(drinkID);
                    JOptionPane.showMessageDialog(this, "Xóa đồ uống thành công!");
                    clearFields();
                    loadDrinkData();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Mã đồ uống không hợp lệ!");
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
                    JOptionPane.showMessageDialog(this, "Mã đồ uống phải là số!");
                    return;
                }
                loadDrinkData(searchType, keyword);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error searching drink: " + ex.getMessage());
            }
        });

        // Sự kiện cho nút Reset
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cmbSearchType.setSelectedIndex(0);
            loadDrinkData();
        });
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn hình ảnh đồ uống");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImgPath = selectedFile.getAbsolutePath();
            lblImgPath.setText(selectedImgPath);
            // Hiển thị hình ảnh xem trước
            ImageIcon icon = new ImageIcon(selectedImgPath);
            Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imgPreview.setIcon(new ImageIcon(scaledImage));
        }
    }

    private void loadDrinkData() {
        loadDrinkData(null, "");
    }

    private void loadDrinkData(String searchType, String keyword) {
        tableModel.setRowCount(0);
        try {
            List<Drink> drinkList = (searchType == null) ? drinkBUS.getAllDrinks()
                    : drinkBUS.searchDrink(searchType, keyword);
            for (Drink drink : drinkList) {
                ImageIcon icon = null;
                if (drink.getImg() != null && !drink.getImg().isEmpty()) {
                    icon = new ImageIcon(drink.getImg());
                    Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(scaledImage);
                    icon.setDescription(drink.getImg()); // Lưu đường dẫn để sử dụng sau
                }
                Object[] row = {
                        drink.getDrinkID(),
                        drink.getDrinkName(),
                        drink.getDrinkPrice(),
                        icon
                };
                tableModel.addRow(row);
            }
            if (drinkList.isEmpty() && searchType != null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy đồ uống nào!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading drink data: " + ex.getMessage());
        }
    }

    private void clearFields() {
        txtDrinkID.setText("");
        txtDrinkName.setText("");
        txtDrinkPrice.setText("");
        selectedImgPath = null;
        lblImgPath.setText("Chưa chọn ảnh");
        imgPreview.setIcon(null);
    }

    // Renderer tùy chỉnh để hiển thị hình ảnh trong bảng
    private class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                    column);
            if (value instanceof ImageIcon) {
                label.setIcon((ImageIcon) value);
                label.setText("");
            } else {
                label.setIcon(null);
                label.setText("");
            }
            return label;
        }
    }

    public static void main(String[] args) {
        javax.swing.JFrame frame = new javax.swing.JFrame("Drink Management");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.add(new DrinkPanel());
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}