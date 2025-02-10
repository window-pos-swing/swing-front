package org.grr.api

import org.grr.`object`.Api
import org.grr.`object`.Storage
import org.json.JSONArray
import org.json.JSONObject

class SaleManagementAPI : BaseAPI() {
    // TODO(주문 완료 목록 리스트 가져오기) API
    fun getOrderListToServer(
        pageNumber: Int,
        pageSize: Int,
        startDate: String? = null,
        endDate: String? = null
    ): Triple<Boolean, JSONArray?, JSONObject?> {
        val (savedEmail, savedPassword, autoCheck, storeCode) = Storage.getLoginInfo()
        val accessToken = Storage.getToken() ?: return Triple(false, null, null)
        val urlBuilder = StringBuilder("${Api.BASE_URL}/api/v1/pos-order/management")
        urlBuilder.append("?pageNumber=$pageNumber&pageSize=$pageSize&storeCode=$storeCode")

        if (startDate != null && endDate != null) {
            urlBuilder.append("&startDate=$startDate&endDate=$endDate")
        }

        val orderListResponse = sendGetRequest(urlBuilder.toString(), accessToken)

        if (!orderListResponse.first || orderListResponse.second.isBlank()) {
            println(" API 응답 오류: ${orderListResponse.second}") // 에러 로깅
            return Triple(false, null, null)
        }

        return try {
            val jsonResponse = JSONObject(orderListResponse.second)

            if (jsonResponse.has("data") && jsonResponse.get("data") is JSONObject) {
                val dataObject = jsonResponse.getJSONObject("data")

                val orderListData = if (dataObject.has("posOrderList") && dataObject.get("posOrderList") is JSONArray) {
                    dataObject.getJSONArray("posOrderList")
                } else JSONArray() // 데이터가 없으면 빈 배열 반환

                val posOrderSaleManagementTotalData =
                    if (dataObject.has("posOrderSaleSummary") && dataObject.get("posOrderSaleSummary") is JSONObject) {
                        dataObject.getJSONObject("posOrderSaleSummary")
                    } else JSONObject() // 데이터가 없으면 빈 배열 반환

                Triple(true, orderListData, posOrderSaleManagementTotalData)
            } else {
                Triple(false, null, null)
            }
        } catch (e: Exception) {
            println("JSON 파싱 오류: ${e.message}")
            Triple(false, null, null)
        }
    }
}