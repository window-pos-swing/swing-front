package org.grr.api

import org.grr.enum.BusinessStatus
import org.grr.`object`.Api
import org.grr.`object`.Storage
import org.json.JSONArray
import org.json.JSONObject

class SettingToServer : BaseAPI() {

    fun deliveryTimeToServer(estimatedDeliveryTimeControl: Boolean, estimatedDeliveryTime: Int): Pair<Boolean, String> {
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        val requestBody = JSONObject()
            .put("estimatedArrivalTime", estimatedDeliveryTime)
            .put("estimatedArrivalTimeControl", estimatedDeliveryTimeControl)
        return sendPostRequest("${Api.BASE_URL}/api/v1/setting/delivery-update", requestBody, accessToken)
    }

    fun cookingTimeToServer(estimatedCookingTimeControl: Boolean, estimatedCookingTime: Int): Pair<Boolean, String> {
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        val requestBody = JSONObject()
            .put("estimatedCookingTime", estimatedCookingTime)
            .put("estimatedCookingTimeControl", estimatedCookingTimeControl)
        return sendPostRequest("${Api.BASE_URL}/api/v1/setting/cooking-update", requestBody, accessToken)
    }

    fun updateBreakTimeToServer(breakTimeJson : JSONObject): Pair<Boolean, String> {
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        // breakTime과 id 항목 제거
        val filteredData2 = JSONObject(breakTimeJson.toString()).apply {
            remove("breakTime")
            remove("id")
        }
        println("[서버로 전송 Body] ${filteredData2.toString(2)}")
        return sendPostRequest("${Api.BASE_URL}/api/v1/setting/break-time-update", filteredData2, accessToken)
    }

    fun updateBusinessHourToServer(operatorTimeJson : JSONObject): Pair<Boolean, String> {
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        // breakTime과 id 항목 제거
        val filteredData2 = JSONObject(operatorTimeJson.toString()).apply {
            remove("breakTime")
            remove("id")
        }
        println("[서버로 전송 Body] ${filteredData2.toString(2)}")
        return sendPostRequest("${Api.BASE_URL}/api/v1/setting/business-hour-update", filteredData2, accessToken)
    }

    fun updateHolidayToServer(holidayJson : JSONArray): Pair<Boolean, String> {
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        println("[서버로 전송 Body] ${holidayJson.toString(2)}")
        return sendPostRequest("${Api.BASE_URL}/api/v1/setting/holiday-update", holidayJson, accessToken)
    }

    fun businessStatusToServer(businessStatus: BusinessStatus): Pair<Boolean, String> {
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        val requestBody = JSONObject()
            .put("businessStatus", businessStatus.name)
        return sendPostRequest("${Api.BASE_URL}/api/v1/setting/update", requestBody, accessToken)
    }
}