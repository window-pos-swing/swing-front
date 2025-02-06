package org.grr.api

import org.grr.enum.BusinessStatus
import org.grr.model.SettingModel
import org.grr.model.SettingModel.businessStatus
import org.grr.`object`.Api
import org.grr.`object`.Storage
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class SettingToServer : BaseAPI() {

    fun deliveryTimeToServer(estimatedDeliveryTimeControl: Boolean, estimatedDeliveryTime: Int): Pair<Boolean, String> {
        val (savedEmail, savedPassword, autoCheck, storeCode) = Storage.getLoginInfo()
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        val requestBody = JSONObject()
            .put("storeCode", storeCode)
            .put("estimatedArrivalTime", estimatedDeliveryTime)
            .put("estimatedArrivalTimeControl", estimatedDeliveryTimeControl)
        return sendPostRequest("${Api.BASE_URL}/api/v1/store-pos-setting/delivery-update", requestBody, accessToken)
    }

    fun cookingTimeToServer(estimatedCookingTimeControl: Boolean, estimatedCookingTime: Int): Pair<Boolean, String> {
        val (savedEmail, savedPassword, autoCheck, storeCode) = Storage.getLoginInfo()
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        val requestBody = JSONObject()
            .put("storeCode", storeCode)
            .put("estimatedCookingTime", estimatedCookingTime)
            .put("estimatedCookingTimeControl", estimatedCookingTimeControl)
        return sendPostRequest("${Api.BASE_URL}/api/v1/store-pos-setting/cooking-update", requestBody, accessToken)
    }

    fun updateBreakTimeToServer(breakTimeJson: JSONObject): Pair<Boolean, String> {
        val (savedEmail, savedPassword, autoCheck, storeCode) = Storage.getLoginInfo()
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        // breakTime과 id 항목 제거
        val filteredData2 = JSONObject(breakTimeJson.toString()).apply {
            remove("breakTime")
            remove("id")
        }
        println("[서버로 전송 Body] ${filteredData2.toString(2)}")
        return sendPostRequest("${Api.BASE_URL}/api/v1/store-pos-setting/break-time-update?storeCode=$storeCode", filteredData2, accessToken)
    }

    fun updateBusinessHourToServer(operatorTimeJson: JSONObject): Pair<Boolean, String> {
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        // breakTime과 id 항목 제거
        val filteredData2 = JSONObject(operatorTimeJson.toString()).apply {
            remove("breakTime")
            remove("id")
        }
        println("[서버로 전송 Body] ${filteredData2.toString(2)}")
        return sendPostRequest(
            "${Api.BASE_URL}/api/v1/store-pos-setting/business-hour-update",
            filteredData2,
            accessToken
        )
    }

    fun updateHolidayToServer(holidayJson: JSONArray): Pair<Boolean, String> {
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        println("[서버로 전송 Body] ${holidayJson.toString(2)}")
        return sendPostRequest("${Api.BASE_URL}/api/v1/store-pos-setting/holiday-update", holidayJson, accessToken)
    }

    fun businessStatusToServer(
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): Pair<Boolean, String> {
        val storeInfo = SettingModel.storeInfo ?: JSONObject()
        val pause = storeInfo.optBoolean("pause", false) // 기본값 false

        val (savedEmail, savedPassword, autoCheck, storeCode) = Storage.getLoginInfo()
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")

        val today = LocalDate.now()

        // 요청 본문 생성
        val requestBody = JSONObject().apply {
            if (storeCode != null) {
                put("storeCode", storeCode)
            } else {
                return Pair(false, "storeCode 값이 없습니다.")
            }

            // startTime과 endTime이 null이면 서버에 null로 보냄
            put("businessPauseStartTime", if (startTime != null) {
                JSONArray().apply {
                    put(startTime.year)
                    put(startTime.monthValue)
                    put(startTime.dayOfMonth)
                    put(startTime.hour)
                    put(startTime.minute)
                }
            } else JSONObject.NULL)

            put("businessPauseEndTime", if (endTime != null) {
                JSONArray().apply {
                    put(endTime.year)
                    put(endTime.monthValue)
                    put(endTime.dayOfMonth)
                    put(endTime.hour)
                    put(endTime.minute)
                }
            } else JSONObject.NULL)
        }

        // 요청 바디 출력 (보기 좋게 정렬)
        println("[서버로 전송 Body] \n${requestBody.toString(4)}")
        return sendPostRequest("${Api.BASE_URL}/api/v1/store-pos-setting/pause-update", requestBody, accessToken)
    }
}