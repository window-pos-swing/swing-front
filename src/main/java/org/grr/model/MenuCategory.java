package org.grr.model;
import java.util.List;

public class MenuCategory {
    private final int id;
    private final String categoryName;
    private final List<SoldOutMenu> menuList;

    public MenuCategory(int id, String categoryName, List<SoldOutMenu> menuList) {
        this.id = id;
        this.categoryName = categoryName;
        this.menuList = menuList;
    }

    public int getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<SoldOutMenu> getMenuList() {
        return menuList;
    }
}
