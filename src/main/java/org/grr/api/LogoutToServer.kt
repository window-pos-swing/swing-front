package org.grr.api

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.grr.`object`.Api
import org.grr.`object`.Storage
import org.json.JSONObject
import java.io.IOException

class LogoutToServer {

    fun logoutToServer(): Pair<Boolean, String> {
        val client = OkHttpClient()
//        토큰
        val accessToken = Storage.getToken()

        val request = Request.Builder()
            .url("${Api.BASE_URL}/api/v1/member/logout") // 로그아웃 엔드포인트
            .post(RequestBody.create("application/json; charset=utf-8".toMediaType(), "{}")) // 빈 요청 바디
            .addHeader("Authorization", accessToken!!) // 토큰 헤더 추가
            .build()

        // 비동기 요청 실행
        try {
            client.newCall(request).execute().use { response ->
                return if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    val jsonResponse = JSONObject(responseBody)

//                    로그아웃 실패했을때
                    if (jsonResponse.getInt("resultCode") == 400) {
                        val errorMessage = jsonResponse.getString("resultMessage")
                        Pair(false, errorMessage)
                    } else {
//                        로그아웃 성공했을때
                        Pair(true, "로그아웃에 성공하였습니다.")
                    }
                } else {
                    Pair(false, "로그아웃 실패: ${response.message}")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Pair(false, "서버 연결 실패: ${e.message}")
        }
    }
}