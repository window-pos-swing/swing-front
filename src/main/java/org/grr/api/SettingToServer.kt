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
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        val requestBody = JSONObject()
            .put("estimatedArrivalTime", estimatedDeliveryTime)
            .put("estimatedArrivalTimeControl", estimatedDeliveryTimeControl)
        return sendPostRequest("${Api.BASE_URL}/api/v1/store-pos-setting/delivery-update", requestBody, accessToken)
    }

    fun cookingTimeToServer(estimatedCookingTimeControl: Boolean, estimatedCookingTime: Int): Pair<Boolean, String> {
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        val requestBody = JSONObject()
            .put("estimatedCookingTime", estimatedCookingTime)
            .put("estimatedCookingTimeControl", estimatedCookingTimeControl)
        return sendPostRequest("${Api.BASE_URL}/api/v1/store-pos-setting/cooking-update", requestBody, accessToken)
    }

    fun updateBreakTimeToServer(breakTimeJson : JSONObject): Pair<Boolean, String> {
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        // breakTime과 id 항목 제거
        val filteredData2 = JSONObject(breakTimeJson.toString()).apply {
            remove("breakTime")
            remove("id")
        }
        println("[서버로 전송 Body] ${filteredData2.toString(2)}")
        return sendPostRequest("${Api.BASE_URL}/api/v1/store-pos-setting/break-time-update", filteredData2, accessToken)
    }

    fun updateBusinessHourToServer(operatorTimeJson : JSONObject): Pair<Boolean, String> {
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        // breakTime과 id 항목 제거
        val filteredData2 = JSONObject(operatorTimeJson.toString()).apply {
            remove("breakTime")
            remove("id")
        }
        println("[서버로 전송 Body] ${filteredData2.toString(2)}")
        return sendPostRequest("${Api.BASE_URL}/api/v1/store-pos-setting/business-hour-update", filteredData2, accessToken)
    }

    fun updateHolidayToServer(holidayJson : JSONArray): Pair<Boolean, String> {
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")
        println("[서버로 전송 Body] ${holidayJson.toString(2)}")
        return sendPostRequest("${Api.BASE_URL}/api/v1/store-pos-setting/holiday-update", holidayJson, accessToken)
    }

    fun businessStatusToServer(
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): Pair<Boolean, String> {
        val updatedMemberInfo = SettingModel.storeInfo ?: JSONObject()
        val pause = updatedMemberInfo.getBoolean("pause")

        val (savedEmail, savedPassword, autoCheck, storeCode) = Storage.getLoginInfo()
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")

        // 요청 본문 생성
        val requestBody = JSONObject().apply {
            if (storeCode != null) {
                put("storeCode", storeCode)
            } else {
                return Pair(false, "storeCode 값이 없습니다.")
            }

            if (!pause) {
                requireNotNull(startTime) { "startTime은 필수입니다." }
                requireNotNull(endTime) { "endTime은 필수입니다." }

                put("businessPauseStartTime", JSONArray().apply {
                    put(startTime.year)
                    put(startTime.monthValue)
                    put(startTime.dayOfMonth)
                    put(startTime.hour)
                    put(startTime.minute)
                })
                put("businessPauseEndTime", JSONArray().apply {
                    put(endTime.year)
                    put(endTime.monthValue)
                    put(endTime.dayOfMonth)
                    put(endTime.hour)
                    put(endTime.minute)
                })
            } else {
                //서버에서 StartTime , EndTime NULL이면 못바꿔서 00:00으로 초기화 후 보냄
                val today = LocalDate.now()
                val defaultTime = LocalTime.MIDNIGHT
                put("businessPauseStartTime", JSONArray().apply {
                    put(today.year)
                    put(today.monthValue)
                    put(today.dayOfMonth)
                    put(defaultTime.hour)
                    put(defaultTime.minute)
                })
                put("businessPauseEndTime", JSONArray().apply {
                    put(today.year)
                    put(today.monthValue)
                    put(today.dayOfMonth)
                    put(defaultTime.hour)
                    put(defaultTime.minute)
                })
            }
        }

        // 요청 바디 출력 (보기 좋게 정렬)
        println("[서버로 전송 Body] \n${requestBody.toString(4)}")
        return sendPostRequest("${Api.BASE_URL}/api/v1/store-pos-setting/pause-update", requestBody, accessToken)
    }
}