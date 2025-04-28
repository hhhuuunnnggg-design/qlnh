import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import GUI.AccountPanel;
import GUI.AdminPanel;
import GUI.DrinkPanel;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private AdminPanel adminPanel;
    private AccountPanel accountPanel;
    private DrinkPanel drinkPanel;
    private String currentPanel;

    public MainFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Restaurant Management System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Sidebar with JScrollPane for scrolling
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(180, getHeight())); // Đặt chiều rộng nhỏ hơn cho sidebar
        sidebarPanel.setBackground(new Color(44, 62, 80)); // Màu nền sidebar

        // Tiêu đề sidebar
        JLabel titleLabel = new JLabel("Admin Panel");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        sidebarPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel chứa các nút điều hướng
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS)); // Dùng BoxLayout với chiều dọc
        navPanel.setBackground(new Color(44, 62, 80));

        // Thêm các nút vào sidebar
        JButton staffButton = new JButton("Quản lý nhân viên");
        staffButton.setFont(new Font("Arial", Font.PLAIN, 14));
        staffButton.setForeground(Color.WHITE);
        staffButton.setBackground(new Color(52, 73, 94));
        staffButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        staffButton.setFocusPainted(false);
        staffButton.addActionListener(e -> showPanel("staff"));

        JButton accountButton = new JButton("Quản lý tài khoản");
        accountButton.setFont(new Font("Arial", Font.PLAIN, 14));
        accountButton.setForeground(Color.WHITE);
        accountButton.setBackground(new Color(52, 73, 94));
        accountButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        accountButton.setFocusPainted(false);
        accountButton.addActionListener(e -> showPanel("account"));

        JButton drinkButton = new JButton("Quản lý đồ uống");
        drinkButton.setFont(new Font("Arial", Font.PLAIN, 14));
        drinkButton.setForeground(Color.WHITE);
        drinkButton.setBackground(new Color(52, 73, 94));
        drinkButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        drinkButton.setFocusPainted(false);
        drinkButton.addActionListener(e -> showPanel("drink"));

        navPanel.add(staffButton);
        navPanel.add(accountButton);
        navPanel.add(drinkButton);

        // Bọc panel điều hướng bằng JScrollPane để thanh cuộn xuất hiện khi cần
        JScrollPane scrollPane = new JScrollPane(navPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Luôn hiển thị thanh cuộn dọc
        sidebarPanel.add(scrollPane, BorderLayout.CENTER);

        // Khu vực nội dung chính
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Khởi tạo các panel
        adminPanel = new AdminPanel();
        accountPanel = new AccountPanel();
        drinkPanel = new DrinkPanel();

        // Thiết lập callback để làm mới AccountPanel khi xóa nhân viên
        adminPanel.setOnStaffDeleted(() -> {
            accountPanel.refreshData();
        });

        // Hiển thị panel mặc định (Quản lý nhân viên)
        currentPanel = "staff";
        contentPanel.add(adminPanel, BorderLayout.CENTER);

        // Thêm sidebar và content vào frame
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void showPanel(String panelName) {
        contentPanel.removeAll();
        currentPanel = panelName;
        switch (panelName) {
            case "staff":
                contentPanel.add(adminPanel, BorderLayout.CENTER);
                break;
            case "account":
                accountPanel.refreshData(); // Làm mới dữ liệu khi chuyển sang AccountPanel
                contentPanel.add(accountPanel, BorderLayout.CENTER);
                break;
            case "drink":
                contentPanel.add(drinkPanel, BorderLayout.CENTER);
                break;
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }
}
