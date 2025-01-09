package org.grr.api


import org.grr.`object`.Api
import org.grr.`object`.OrderListSingleTon
import org.grr.`object`.Storage
import org.json.JSONObject
import javax.swing.JFrame
import javax.swing.JPanel

class OrderAPI : BaseAPI(){
    fun fetchOrders(parentFrame: JFrame, cardPanel: JPanel): Pair<Boolean, String> {
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        val orderList = sendGetRequest("${Api.BASE_URL}/api/v1/orders/list?pageNumber=0&pageSize=5&orderStatus=", accessToken)

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

}
