package org.grr.api

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.grr.`object`.Api
import org.grr.`object`.Storage
import org.json.JSONObject
import java.io.IOException

class SettingToServer {

//    브레이크 타임 업데이트 구문


    //    도착예정시간 업데이트 구문
    fun deliveryTimeToServer(estimatedDeliveryTimeControl: Boolean, estimatedDeliveryTime: Int): Pair<Boolean, String> {
        val client = OkHttpClient()
//        토큰
        val accessToken = Storage.getToken()

        val requestBody = JSONObject()
            .put("estimatedArrivalTime", estimatedDeliveryTime)
            .put("estimatedArrivalTimeControl", estimatedDeliveryTimeControl)
            .toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("${Api.BASE_URL}/api/v1/setting/update")
            .post(requestBody) // 빈 요청 바디
            .addHeader("Authorization", accessToken!!) // 토큰 헤더 추가
            .build()

        try {
            client.newCall(request).execute().use { response ->
//                성공했을때. 200일때
                return if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    val jsonResponse = JSONObject(responseBody)

//                    도착예정시간 업데이트 실패했을때
                    if (jsonResponse.getInt("resultCode") == 400) {
                        val errorMessage = jsonResponse.getString("resultMessage")
                        Pair(false, errorMessage)
                    } else {
//                    도착예정시간 업데이트 성공했을때
                        Pair(true, "도착예상시간 업데이트 성공")
                    }
                } else {
                    Pair(false, "도착예상시간 업데이트 실패: ${response.message}")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Pair(false, "서버 연결 실패: ${e.message}")
        }
    }

    //    조리완료시간 업데이트 구문
    fun cookingTimeToServer(estimatedCookingTimeControl: Boolean, estimatedCookingTime: Int): Pair<Boolean, String> {
        val client = OkHttpClient()
//        토큰
        val accessToken = Storage.getToken()

        val requestBody = JSONObject()
            .put("estimatedCookingTime", estimatedCookingTime)
            .put("estimatedCookingTimeControl", estimatedCookingTimeControl)
            .toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("${Api.BASE_URL}/api/v1/setting/update")
            .post(requestBody) // 빈 요청 바디
            .addHeader("Authorization", accessToken!!) // 토큰 헤더 추가
            .build()

        try {
            client.newCall(request).execute().use { response ->
//                성공했을때. 200일때
                return if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    val jsonResponse = JSONObject(responseBody)

//                    조리시간 업데이트 실패했을때
                    if (jsonResponse.getInt("resultCode") == 400) {
                        val errorMessage = jsonResponse.getString("resultMessage")
                        Pair(false, errorMessage)
                    } else {
//                    조리시간 업데이트 성공했을때
//                        val getAccessToken = jsonResponse.getJSONObject("data")
                        Pair(true, "조리완료시간 업데이트 성공")
                    }
                } else {
                    Pair(false, "조리완료시간 업데이트 실패: ${response.message}")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Pair(false, "서버 연결 실패: ${e.message}")
        }
    }
}