package org.grr.api

import okhttp3.OkHttpClient
import okhttp3.Request
import okio.use
import org.grr.`object`.Api
import org.grr.`object`.Storage
import org.json.JSONObject
import java.io.IOException

class CurrentLoginStoreToServer {

    fun currentLoginStoreMemberToServer(): Pair<Boolean, String> {
        val client = OkHttpClient()
        //        토큰
        val accessToken = Storage.getToken()
        val (savedEmail, savedPassword, autoCheck, storeCode) = Storage.getLoginInfo()

        val request = Request.Builder()
            .url("${Api.BASE_URL}/api/v1/store-pos-setting?storeCode=${storeCode}") // 현재 로그인 상점 엔드포인트
            .get() // 빈 요청 바디
            .addHeader("Authorization", accessToken!!) // 토큰 헤더 추가
            .build()

        // 비동기 요청 실행
        try {
            client.newCall(request).execute().use { response ->
                return if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    val jsonResponse = JSONObject(responseBody)

//                    상점 정보 갖고오기 실패했을때
                    if (jsonResponse.getInt("resultCode") == 400) {
                        val errorMessage = jsonResponse.getString("resultMessage")
                        Pair(false, errorMessage)
                    } else {
//                        상점 정보 갖고오기 성공했을때
                        val currentStoreData = jsonResponse.getJSONObject("data")
                        Pair(true, "${currentStoreData}")
                    }
                } else {
                    Pair(false, "상점 조회 실패: ${response.message}")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Pair(false, "서버 연결 실패: ${e.message}")
        }
    }
}