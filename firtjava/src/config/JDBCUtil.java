package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class JDBCUtil {

    public static Connection getConnection() {
        Connection result = null;
        try {
            // Đăng ký MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Thông số kết nối
            String url = "jdbc:mysql://localhost:3306/restaurantmanage";
            String userName = "root"; // Thay bằng username MySQL của bạn
            String password = "123456"; // Thay bằng password MySQL của bạn
            // Tạo kết nối
            result = DriverManager.getConnection(url, userName, password);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBCUtil - Driver Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "MySQL Driver không tìm thấy: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            System.err.println("JDBCUtil - SQL Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Không thể kết nối đến cơ sở dữ liệu: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    public static void closeConnection(Connection c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (SQLException e) {
            System.err.println("JDBCUtil - Close Connection Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
