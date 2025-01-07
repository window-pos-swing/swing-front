package org.example.model

data class Menu(
    val menuName: String,
    val price: Int,
    val count: Int,
    val options: List<MenuOption>
)

data class MenuOption(
    val optionName: String,
    val optionPrice: Int
)
