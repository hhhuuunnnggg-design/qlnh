
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import GUI.AccountPanel;
import GUI.AdminPanel;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private AdminPanel adminPanel;
    private AccountPanel accountPanel;

    public MainFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Restaurant Management System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Sidebar
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setBackground(new Color(44, 62, 80)); // Màu nền sidebar

        // Tiêu đề sidebar
        JLabel titleLabel = new JLabel("Menu");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        sidebarPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel chứa các nút điều hướng
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BorderLayout());
        navPanel.setBackground(new Color(44, 62, 80));

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

        navPanel.add(staffButton, BorderLayout.NORTH);
        navPanel.add(accountButton, BorderLayout.CENTER);
        sidebarPanel.add(navPanel, BorderLayout.CENTER);

        // Khu vực nội dung chính
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Khởi tạo các panel
        adminPanel = new AdminPanel();
        accountPanel = new AccountPanel();

        // Hiển thị panel mặc định (Quản lý nhân viên)
        contentPanel.add(adminPanel, BorderLayout.CENTER);

        // Thêm sidebar và content vào frame
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void showPanel(String panelName) {
        contentPanel.removeAll();
        switch (panelName) {
            case "staff":
                contentPanel.add(adminPanel, BorderLayout.CENTER);
                break;
            case "account":
                contentPanel.add(accountPanel, BorderLayout.CENTER);
                break;
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}