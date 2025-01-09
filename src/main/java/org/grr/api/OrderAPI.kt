package org.grr.api


import org.grr.enum.ServerOrderStatus
import org.grr.`object`.Api
import org.grr.`object`.OrderListSingleTon
import org.grr.`object`.Storage
import org.json.JSONObject
import javax.swing.JFrame
import javax.swing.JPanel

class OrderAPI : BaseAPI(){

    //TODO(주문리스트 가져오기) API
    fun fetchOrders(parentFrame: JFrame, cardPanel: JPanel): Pair<Boolean, String> {
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        val orderList = sendGetRequest("${Api.BASE_URL}/api/v1/orders/list?pageNumber=0&pageSize=5&orderStatusType=", accessToken)
        if (!orderList.first) {
            return orderList // 실패 시 그대로 반환
        }
        try {
            // 전체 JSON 문자열 파싱
            val jsonObject = JSONObject(orderList.second)
            // `data` 배열 추출
            val dataArray = jsonObject.getJSONArray("data")
            // `data` 배열을 `addAllOrder`에 전달
            OrderListSingleTon.addAllOrder(dataArray.toString(), parentFrame, cardPanel)
            return Pair(true, "주문 목록이 성공적으로 로드되었습니다.")
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(false, "JSON 파싱 오류: ${e.message}")
        }
    }

    //TODO(주문 상태 변경) API
    fun orderStatusChangeToServer(
        serverOrderStatus: ServerOrderStatus,
        orderId: Int,
        reason : String? = null,
        estimatedCookingTime: Int? = null,
        estimatedArrivalTime: Int? = null
    ) :  Pair<Boolean, String>  {
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        // JSON 객체 생성
        val orderStatusBody = JSONObject().apply {
            put("status", serverOrderStatus.name)  // Enum의 이름 사용
            if(estimatedCookingTime != null && estimatedArrivalTime!= null){
                put("estimatedCookingTime", estimatedCookingTime)  // 예상 조리 시간
                put("estimatedArrivalTime", estimatedArrivalTime)  // 예상 도착 시간
            }
            if(reason != null) put("reason", reason)
        }
        println("[서버로 전송 Body] ${orderStatusBody.toString(2)}")
        return sendPostRequest("${Api.BASE_URL}/api/v1/orders/status?orderId=$orderId", orderStatusBody, accessToken)
    }

}
