package org.grr.model

import org.grr.enum.PosOrderStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class OrderCategory (
    var id: Long?,
    var createAt: LocalDateTime,
    var orderNumber: String,
    var orderReceiveType: String,
    var posOrderStatus: String,
    var totalOrderPrice: Int,
    var paymentWayTypeStatus: String,
) {
    // 주문 유형 변환
    fun getFormattedOrderReceiveType(): String {
        return when (orderReceiveType) {
            "TAKEOUT" -> "포장"
            "DELIVERY" -> "배달"
            else -> orderReceiveType
        }
    }

    // 주문 상태 변환
    fun getFormattedPosOrderStatus(): String {
        return when (posOrderStatus) {
            "COMPLETED" -> "결제완료"
            "USER_CANCEL" -> "유저취소"
            "STORE_CANCEL" -> "상점취소"
            else -> posOrderStatus
        }
    }

    // 결제 방식 변환
    fun getFormattedPaymentWayTypeStatus(): String {
        return when (paymentWayTypeStatus) {
            "CARD" -> "카드결제"
            "MEET_CARD" -> "만나서 카드결제"
            "MEET_CASH" -> "만나서 현금결제"
            else -> paymentWayTypeStatus
        }
    }
}

fun LocalDateTime.formatToDisplay(): String {
    val formatter = DateTimeFormatter.ofPattern("MM월 dd일 HH:mm:ss")
    return this.format(formatter)
}