package org.grr.model;

public class SoldOutMenu {
    private final int id;
    private final String menuName;
    private boolean isSoldOut;

    public SoldOutMenu(int id, String menuName, boolean isSoldOut) {
        this.id = id;
        this.menuName = menuName;
        this.isSoldOut = isSoldOut;
    }

    public int getId() {
        return id;
    }

    public String getMenuName() {
        return menuName;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public void setSoldOut(boolean soldOut) {
        isSoldOut = soldOut;
    }
}
