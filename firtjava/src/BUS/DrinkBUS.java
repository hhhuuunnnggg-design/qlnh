package BUS;

import java.util.List;

import DAO.DrinkDAO;
import DTO.Drink;

public class DrinkBUS {
    private DrinkDAO drinkDAO;

    public DrinkBUS() {
        drinkDAO = new DrinkDAO();
    }

    public List<Drink> getAllDrinks() {
        return drinkDAO.getAllDrinks();
    }

    public void addDrink(Drink drink) {
        if (drink.getDrinkName() == null || drink.getDrinkName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đồ uống không được để trống");
        }
        if (drink.getDrinkPrice() < 0) {
            throw new IllegalArgumentException("Giá đồ uống không được âm");
        }
        drinkDAO.addDrink(drink);
    }

    public void updateDrink(Drink drink) {
        if (drink.getDrinkName() == null || drink.getDrinkName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đồ uống không được để trống");
        }
        if (drink.getDrinkPrice() < 0) {
            throw new IllegalArgumentException("Giá đồ uống không được âm");
        }
        drinkDAO.updateDrink(drink);
    }

    public void deleteDrink(int drinkID) {
        drinkDAO.deleteDrink(drinkID);
    }

    public Drink getDrinkByID(int drinkID) {
        return drinkDAO.getDrinkByID(drinkID);
    }

    public List<Drink> searchDrink(String searchType, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllDrinks();
        }
        if (!searchType.equals("ID") && !searchType.equals("Tên")) {
            throw new IllegalArgumentException("Kiểu tìm kiếm không hợp lệ: " + searchType);
        }
        return drinkDAO.searchDrink(searchType, keyword.trim());
    }
}