package org.grr.api

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.grr.`object`.Api
import org.grr.`object`.Storage
import org.json.JSONObject
import java.io.IOException

class FcmTokenToServer {

    //    서버로 fcm 토큰 전송하는 구문
    fun sendTokenToServer(token: String): Pair<Boolean, String> {
        val client = OkHttpClient()
//        토큰
        val accessToken = Storage.getToken()

        val requestBody = JSONObject()
            .put("token", token)
            .toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("${Api.BASE_URL}/api/v1/fcm/token")
            .post(requestBody)
            .addHeader("Authorization", accessToken!!) // 토큰 헤더 추가
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
                        Pair(true, "토큰 전송 성공")
                    }
                } else {
                    Pair(false, "토큰 전송 실패: ${response.message}")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Pair(false, "서버 연결 실패: ${e.message}")
        }
    }
}