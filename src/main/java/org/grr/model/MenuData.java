package org.grr.model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuData {
    public static List<MenuCategory> createSampleData() {
        return Arrays.asList(
                new MenuCategory(1, "음료", new ArrayList<>(Arrays.asList(
                        new SoldOutMenu(101, "아메리카노", false),
                        new SoldOutMenu(102, "카페라떼", true),
                        new SoldOutMenu(103, "초코 라떼", false)
                ))),
                new MenuCategory(2, "디저트", new ArrayList<>(Arrays.asList(
                        new SoldOutMenu(201, "치즈케이크", false),
                        new SoldOutMenu(202, "브라우니", true),
                        new SoldOutMenu(203, "마카롱", false)
                ))),
                new MenuCategory(3, "샌드위치", new ArrayList<>(Arrays.asList(
                        new SoldOutMenu(301, "햄 샌드위치", false),
                        new SoldOutMenu(302, "치킨 샌드위치", true),
                        new SoldOutMenu(303, "클럽 샌드위치", false)
                )))
        );
    }
}
