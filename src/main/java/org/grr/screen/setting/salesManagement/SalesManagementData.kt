package org.grr.screen.setting.salesManagement

import org.grr.screen.setting.salesManagement.SalesManagementData.SalesSelectedDate
import org.grr.screen.setting.salesManagement.SalesManagementData.totalSalesSummary
import org.grr.screen.setting.salesManagement.salesManagementForm.CreateOrderTotalLabelPanelForm
import org.grr.screen.setting.salesManagement.salesManagementForm.CreateSummaryPanelForm
import org.grr.widgets.RoundedButton
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.swing.JLabel
import javax.swing.table.DefaultTableModel
import kotlin.properties.Delegates

object SalesManagementData {
    //조회날짜
    var SalesSelectedDate : String = getTodayDate()
//    옵저버 패턴으로 값 감지 시 업데이트
    var totalSalesSummary: JSONObject? by Delegates.observable(null) { _, _, _ ->
        summaryPanel?.updateSummary()
        totalLabelPanel?.updateTotal()
    }

    var summaryPanel: CreateSummaryPanelForm? = null
    var totalLabelPanel: CreateOrderTotalLabelPanelForm? = null
}
private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")

//  결제 완료 건수, 총합
fun getPaymentCompletedCount(): Long { return totalSalesSummary?.optLong("paymentCompletedCount") ?: 0 }
fun getPaymentCompletedPrice(): Long { return totalSalesSummary?.optLong("paymentCompletedPrice") ?: 0 }

//  만나서 카드결제 건수, 총합
fun getMeetPaymentCompletedCardCount(): Long { return totalSalesSummary?.optLong("meetPaymentCompletedCardCount") ?: 0 }
fun getMeetPaymentCompletedCardPrice(): Long { return totalSalesSummary?.optLong("meetPaymentCompletedCardPrice") ?: 0 }

//  만나서 현금결제 건수, 총합
fun getMeetPaymentCompletedCashCount(): Long { return totalSalesSummary?.optLong("meetPaymentCompletedCashCount") ?: 0 }
fun getMeetPaymentCompletedCashPrice(): Long { return totalSalesSummary?.optLong("meetPaymentCompletedCashPrice") ?: 0 }

//  배달 완료 건수, 총합
fun getDeliveryCompletedCount(): Long { return totalSalesSummary?.optLong("deliveryCompletedCount") ?: 0 }
fun getDeliveryCompletedPrice(): Long { return totalSalesSummary?.optLong("deliveryCompletedPrice") ?: 0 }

//  배달 취소 건수, 총합
fun getDeliveryCancelCount(): Long { return totalSalesSummary?.optLong("deliveryCancelCount") ?: 0 }
fun getDeliveryCancelPrice(): Long { return totalSalesSummary?.optLong("deliveryCancelPrice") ?: 0 }

//  포장 완료 건수, 총헙
fun getTakeOutCompletedCount(): Long { return totalSalesSummary?.optLong("takeOutCompletedCount") ?: 0 }
fun getTakeOutCompletedPrice(): Long { return totalSalesSummary?.optLong("takeOutCompletedPrice") ?: 0 }

//  포장 취소 건수, 총합
fun getTakeOutCancelCount(): Long { return totalSalesSummary?.optLong("takeOutCancelCount") ?: 0 }
fun getTakeOutCancelPrice(): Long { return totalSalesSummary?.optLong("takeOutCancelPrice") ?: 0 }

//  오늘 날짜
fun getTodayDate(): String {
    return LocalDate.now().format(DATE_FORMATTER)
}

//  어제 날짜
fun getYesterdayDate(): String {
    return LocalDate.now().minusDays(1).format(DATE_FORMATTER)
}

fun setSalesSelectedDate(changeSalesDate : String) {
    SalesSelectedDate = changeSalesDate
}
fun getSalesSelectedDate(): String {
    return SalesSelectedDate
}