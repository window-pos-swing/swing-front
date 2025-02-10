package org.grr.api;

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Request
import org.grr.`object`.Storage
import org.json.JSONObject
import java.io.IOException


open class BaseAPI {
    private val client = OkHttpClient()

    fun sendPostRequest(
        url: String,
        requestBody: Any,// JSONObject 또는 JSONArray를 허용
        accessToken: String
    ): Pair<Boolean, String> {
        val request = Request.Builder()
            .url(url)
            .post(requestBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
            .addHeader("Authorization", accessToken)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                return if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    val jsonResponse = JSONObject(responseBody)

                    if (jsonResponse.getInt("resultCode") == 400) {
                        val errorMessage = jsonResponse.getString("resultMessage")
                        Pair(false, errorMessage)
                    } else {
                        println("세팅 수정된 정보 : ${jsonResponse.getJSONObject("data")}")
                        val storeData = jsonResponse.getJSONObject("data")
                        Storage.saveStoreInfo(storeData)
                        Pair(true, jsonResponse.optString("resultMessage", "성공"))
                    }
                } else {
                    Pair(false, "요청 실패: ${response.message}")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Pair(false, "서버 연결 실패: ${e.message}")
        }
    }

    fun sendGetRequest(
        url: String,
        accessToken: String
    ): Pair<Boolean, String> {
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", accessToken) // Bearer 추가
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: "" // 한 번만 읽기

                return if (response.isSuccessful) {
                    Pair(true, responseBody) // 성공 시 저장한 응답 본문 반환
                } else {
                    Pair(false, "주문정보 가져오기 실패: ${response.message}")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Pair(false, "서버 연결 실패: ${e.message}")
        }
    }
}