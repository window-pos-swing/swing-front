package org.grr.`object`

import SoldOutManagementDialog
import org.grr.api.MenuAPI
import org.grr.model.Menu
import org.grr.model.MenuCategory
import javax.swing.JComboBox
import javax.swing.table.DefaultTableModel

object MenuListSingleTon {
    val PAGE_SIZE: Int = 10
    val MAX_PAGE_REQUESTS: Int = 100

    private val menuList = mutableMapOf<String, MutableList<MenuCategory>>() // 카테고리별 메뉴 리스트
    private val pageNumbers = mutableMapOf<String, Int>() // 카테고리별 현재 페이지
    private val counts = mutableMapOf<String, Int>() // 요청 횟수 제한
    private var isLoading = false // 중복 호출 방지
    val copiedMenuCategories: MutableList<MenuCategory> = mutableListOf()
    var tableModel: DefaultTableModel? = null
    val categoryComboBox: JComboBox<String>? = null

    fun getMenuList(categoryName: String? = null): List<MenuCategory> {
        return menuList[categoryName ?: "전체"] ?: emptyList()
    }

    fun configureTableModel(model: DefaultTableModel) {
        tableModel = model
    }

    fun loadMenuData(categoryName: String? = null, soldOut: Boolean? = null) {
        if (isLoading) return
        val categoryKey = categoryName ?: "전체"
        val currentPage = pageNumbers[categoryKey] ?: 0

        if ((counts[categoryKey] ?: 0) >= MAX_PAGE_REQUESTS) {
            return
        }
        isLoading = true

        val (success, menuDataList, categoryList) = MenuAPI().fetchMenuList(currentPage, PAGE_SIZE, categoryName, soldOut)

        copiedMenuCategories.clear()
        copiedMenuCategories.addAll(categoryList!!.map {
            MenuCategory(
                id = -1,
                categoryName = it.toString(),
                menuList = emptyList()
            )
        })

        if (success && menuDataList != null) {
            val menuListTable = MenuAPI().parseMenuData(menuList.toString())

            if (menuListTable.isNotEmpty()) {
                val existingList = menuList.getOrPut(categoryKey) { mutableListOf() }
                existingList.addAll(menuListTable)

                pageNumbers[categoryKey] = currentPage + 1
                counts[categoryKey] = (counts[categoryKey] ?: 0) + 1

                updateTable()
            }
        }

        isLoading = false
    }

    fun updateTable() {
        tableModel?.setRowCount(0) // 기존 테이블 데이터 초기화
        val selectedCategory = categoryComboBox?.selectedItem as String? ?: "전체"

        menuList[selectedCategory]?.forEach { category ->
            if (selectedCategory == "전체" || selectedCategory == category.categoryName) {
                category.menuList.forEach { menu ->
                    tableModel?.addRow(
                        arrayOf(
                            menu.isSoldOut,
                            category.categoryName,
                            menu.menuName
                        )
                    )
                }
            }
        }
    }

    fun filterSoldOut() {
        tableModel?.setRowCount(0)
        val selectedCategory = categoryComboBox?.selectedItem as String? ?: "전체"

        menuList[selectedCategory]?.forEach { category ->
//            println("카테고리 명 : ${category}, ${selectedCategory}")
            if (selectedCategory == "전체" || selectedCategory == category.categoryName) {
                category.menuList.filter { it.isSoldOut }.forEach { menu ->
                    tableModel?.addRow(arrayOf(menu.isSoldOut, category.categoryName, menu.menuName))
                }
            }
        }
    }
}