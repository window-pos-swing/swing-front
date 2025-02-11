package org.grr.model

data class Menu(
    val id: Int,
    val menuName: String,
    val quantity: Int,
    val menuTotalPrice : Int,
    val menuOptionList: List<MenuOption>
) {
    data class MenuOption(
        val id: Int,
        val categoryName: String,
        val menuOptionName: String,
        val menuOptionPrice: Int
    )
}