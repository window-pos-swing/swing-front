package org.grr.api

import org.grr.enum.ServerOrderStatus
import org.grr.model.OrderFilter
import org.grr.`object`.Api
import org.grr.`object`.OrderListSingleTon
import org.grr.`object`.Storage
import org.json.JSONObject
import javax.swing.JFrame
import javax.swing.JPanel

class OrderAPI : BaseAPI() {

    //TODO(주문리스트 가져오기) API
    fun fetchOrders(
        parentFrame: JFrame,
        cardPanel: JPanel,
        filter: OrderFilter,
        pageNumber : Int,
    ): Pair<Boolean, String> {
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        val serverOrderStatusParam = filter.serverOrderStatus?.name?.let { "&orderStatusType=$it" } ?: ""
        val posOrderStatusParam = filter.posOrderStatus?.name?.let { "&orderStatus=$it" } ?: ""
        val orderReceiveTypeParam = filter.orderReceiveType?.name?.let { "&orderReceiveType=$it" } ?: ""
        val url =
            "${Api.BASE_URL}/api/v1/orders/list?pageNumber=${pageNumber}&pageSize=${OrderListSingleTon.PAGE_SIZE}$serverOrderStatusParam$posOrderStatusParam$orderReceiveTypeParam"

        val orderList = sendGetRequest(
            url,
            accessToken
        )
        if (!orderList.first) {
            return orderList // 실패 시 그대로 반환
        }
        try {
            // 전체 JSON 문자열 파싱
            val jsonObject = JSONObject(orderList.second)
            if (!jsonObject.has("data")) {
                return Pair(false, "'data' 필드가 응답에 없습니다.")
            }
            // `data` 배열 추출
            val dataArray = jsonObject.getJSONArray("data")
            if (dataArray == null) {
                return Pair(false, "'data' 필드가 JSON 배열이 아닙니다.")
            }

            // `data` 배열을 `addAllOrder`에 전달
            var totalElements = jsonObject["totalElements"] as Int
            OrderListSingleTon.addAllOrder(
                dataArray.toString(),
                parentFrame,
                cardPanel,
                filter,
                totalElements,

            )
            return Pair(true, dataArray.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(false, "JSON 파싱 오류: ${e.message}")
        }
    }

    //TODO(주문 상태 변경) API
    fun orderStatusChangeToServer(
        serverOrderStatus: ServerOrderStatus,
        orderId: Int,
        reason: String? = null,
        estimatedCookingTime: Int? = null,
        estimatedArrivalTime: Int? = null
    ): Pair<Boolean, String> {
        println("orderId : $orderId")
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        // JSON 객체 생성
        val orderStatusBody = JSONObject().apply {
            put("status", serverOrderStatus.name)  // Enum의 이름 사용
            if (estimatedCookingTime != null && estimatedArrivalTime != null) {
                put("estimatedCookingTime", estimatedCookingTime)  // 예상 조리 시간
                put("estimatedArrivalTime", estimatedArrivalTime)  // 예상 도착 시간
            }
            if (reason != null) put("reason", reason)
        }
        println("[서버로 전송 Body] ${orderStatusBody.toString(2)}")
        return sendPostRequest("${Api.BASE_URL}/api/v1/orders/status?orderId=$orderId", orderStatusBody, accessToken)
    }

}
