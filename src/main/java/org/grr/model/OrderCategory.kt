package org.grr.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class OrderCategory (
    val orderDate: LocalDateTime,
    val orderNumber: String,
    val orderType: String,
    val orderStatus: String,
    val orderPrice: Int,
    val orderMethod: String
)

fun LocalDateTime.formatToDisplay(): String {
    val formatter = DateTimeFormatter.ofPattern("MM월 dd일 HH:mm:ss")
    return this.format(formatter)
}