package org.grr.screen.setting.salesManagement

import org.grr.screen.setting.salesManagement.SalesManagementData.totalSalesSummary
import org.grr.widgets.RoundedButton
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.swing.JLabel
import javax.swing.table.DefaultTableModel

object SalesManagementData {
    var totalSalesSummary: JSONObject? = null
}
private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")

//  결제 완료 건수, 총합
fun getPaymentCompletedCount(): Long { return totalSalesSummary?.getLong("paymentCompletedCount") ?: 0 }
fun getPaymentCompletedPrice(): Long { return totalSalesSummary?.getLong("paymentCompletedPrice") ?: 0 }

//  만나서 카드결제 건수, 총합
fun getMeetPaymentCompletedCardCount(): Long { return totalSalesSummary?.getLong("meetPaymentCompletedCardCount") ?: 0 }
fun getMeetPaymentCompletedCardPrice(): Long { return totalSalesSummary?.getLong("meetPaymentCompletedCardPrice") ?: 0 }

//  만나서 현금결제 건수, 총합
fun getMeetPaymentCompletedCashCount(): Long { return totalSalesSummary?.getLong("meetPaymentCompletedCashCount") ?: 0 }
fun getMeetPaymentCompletedCashPrice(): Long { return totalSalesSummary?.getLong("meetPaymentCompletedCashPrice") ?: 0 }

//  배달 완료 건수, 총합
fun getDeliveryCompletedCount(): Long { return totalSalesSummary?.getLong("deliveryCompletedCount") ?: 0 }
fun getDeliveryCompletedPrice(): Long { return totalSalesSummary?.getLong("deliveryCompletedPrice") ?: 0 }

//  배달 취소 건수, 총합
fun getDeliveryCancelCount(): Long { return totalSalesSummary?.getLong("deliveryCancelCount") ?: 0 }
fun getDeliveryCancelPrice(): Long { return totalSalesSummary?.getLong("deliveryCancelPrice") ?: 0 }

//  오늘 날짜
fun getTodayDate(): String {
    return LocalDate.now().format(DATE_FORMATTER)
}

//  어제 날짜
fun getYesterdayDate(): String {
    return LocalDate.now().minusDays(1).format(DATE_FORMATTER)
}