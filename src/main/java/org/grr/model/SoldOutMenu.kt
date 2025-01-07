package org.grr.model;

data class SoldOutMenu(
        val id: Int,
        val menuName: String,
        var isSoldOut: Boolean
)
