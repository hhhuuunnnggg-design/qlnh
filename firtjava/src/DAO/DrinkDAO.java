package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DTO.Drink;
import config.JDBCUtil;

public class DrinkDAO {
    private static final String SELECT_ALL_DRINKS = "SELECT * FROM Drink";
    private static final String SELECT_DRINK_BY_ID = "SELECT * FROM Drink WHERE DrinkID = ?";
    private static final String INSERT_DRINK = "INSERT INTO Drink (DrinkName, DrinkPrice, img) VALUES (?, ?, ?)";
    private static final String UPDATE_DRINK = "UPDATE Drink SET DrinkName = ?, DrinkPrice = ?, img = ? WHERE DrinkID = ?";
    private static final String DELETE_DRINK = "DELETE FROM Drink WHERE DrinkID = ?";
    private static final String SEARCH_BY_ID = "SELECT * FROM Drink WHERE DrinkID = ?";
    private static final String SEARCH_BY_NAME = "SELECT * FROM Drink WHERE DrinkName LIKE ?";

    // Lấy tất cả đồ uống
    public List<Drink> getAllDrinks() {
        List<Drink> drinkList = new ArrayList<>();
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_DRINKS)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int drinkID = rs.getInt("DrinkID");
                String drinkName = rs.getString("DrinkName");
                double drinkPrice = rs.getDouble("DrinkPrice");
                String img = rs.getString("img");
                drinkList.add(new Drink(drinkID, drinkName, drinkPrice, img));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving drink list: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
        return drinkList;
    }

    // Thêm đồ uống
    public void addDrink(Drink drink) {
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_DRINK)) {
            preparedStatement.setString(1, drink.getDrinkName());
            preparedStatement.setDouble(2, drink.getDrinkPrice());
            preparedStatement.setString(3, drink.getImg());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding drink: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
    }

    // Cập nhật đồ uống
    public void updateDrink(Drink drink) {
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_DRINK)) {
            preparedStatement.setString(1, drink.getDrinkName());
            preparedStatement.setDouble(2, drink.getDrinkPrice());
            preparedStatement.setString(3, drink.getImg());
            preparedStatement.setInt(4, drink.getDrinkID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating drink: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
    }

    // Xóa đồ uống
    public void deleteDrink(int drinkID) {
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_DRINK)) {
            preparedStatement.setInt(1, drinkID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting drink: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
    }

    // Lấy đồ uống theo ID
    public Drink getDrinkByID(int drinkID) {
        Drink drink = null;
        Connection connection = JDBCUtil.getConnection();
        if (connection == null) {
            throw new RuntimeException("Cannot connect to database");
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_DRINK_BY_ID)) {
            preparedStatement.setInt(1, drinkID);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String drinkName = rs.getString("DrinkName");
                double drinkPrice = rs.getDouble("DrinkPrice");
                String img = rs.getString("img");
                drink = new Drink(drinkID, drinkName, drinkPrice, img);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving drink: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
        return drink;
    }

    // Tìm kiếm đồ uống theo kiểu (ID hoặc tên)
    public List<Drink> searchDrink(String searchType, String keyword) {
        List<Drink> drinkList = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllDrinks();
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
            default:
                throw new IllegalArgumentException("Kiểu tìm kiếm không hợp lệ: " + searchType);
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (searchType.equals("ID")) {
                try {
                    preparedStatement.setInt(1, Integer.parseInt(keyword));
                } catch (NumberFormatException e) {
                    return drinkList; // Trả về danh sách rỗng nếu ID không hợp lệ
                }
            } else {
                preparedStatement.setString(1, "%" + keyword.trim() + "%");
            }
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int drinkID = rs.getInt("DrinkID");
                String drinkName = rs.getString("DrinkName");
                double drinkPrice = rs.getDouble("DrinkPrice");
                String img = rs.getString("img");
                drinkList.add(new Drink(drinkID, drinkName, drinkPrice, img));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching drink: " + e.getMessage(), e);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
        return drinkList;
    }
}