package org.grr.api

import okhttp3.OkHttpClient
import okhttp3.Request
import org.grr.`object`.Api
import org.grr.`object`.Storage
import org.json.JSONObject
import java.io.IOException

class CurrentLoginStoreMemberToServer {

    fun currentLoginStoreMemberToServer(): Pair<Boolean, String> {
        val client = OkHttpClient()
        //        토큰
        val accessToken = Storage.getToken()

        val request = Request.Builder()
            .url("${Api.BASE_URL}/api/v1/store-member/current-login") // 현재 로그인 회원 엔드포인트
            .get() // 빈 요청 바디
            .addHeader("Authorization", accessToken!!) // 토큰 헤더 추가
            .build()

        // 비동기 요청 실행
        try {
            client.newCall(request).execute().use { response ->
                return if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    val jsonResponse = JSONObject(responseBody)

//                    회원 정보 갖고오기 실패했을때
                    if (jsonResponse.getInt("resultCode") == 400) {
                        val errorMessage = jsonResponse.getString("resultMessage")
                        Pair(false, errorMessage)
                    } else {
//                        회원 정보 갖고오기 성공했을때
                        val currentstoreMemberData = jsonResponse.getJSONObject("data")
                        Pair(true, "${currentstoreMemberData}")
                    }
                } else {
                    Pair(false, "회원 조회 실패: ${response.message}")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Pair(false, "서버 연결 실패: ${e.message}")
        }
    }
}