package org.grr.model;

data class MenuCategory(
    val id: Int,
    val categoryName: String,
    val menuList: List<SoldOutMenu>
)

