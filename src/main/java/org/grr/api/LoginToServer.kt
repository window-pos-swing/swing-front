package org.grr.api

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.use
import org.grr.`object`.Api
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import javax.swing.JOptionPane

class LoginToServer {

    fun loginToServer(email: String, password: String): Triple<Boolean, String, JSONArray?> {
        val client = OkHttpClient()

        val requestBody = JSONObject()
            .put("email", email)
            .put("password", password)
            .toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("${Api.BASE_URL}/api/v0/pos-store-member/login")
            .post(requestBody)
            .build()
        try {
            client.newCall(request).execute().use { response ->
//                성공했을때. 200일때
                return if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    val jsonResponse = JSONObject(responseBody)

//                    로그인 실패했을때 (이메일, 비밀번호 틀렸을때)
                    if (jsonResponse.getInt("resultCode") == 400) {
                        val errorMessage = jsonResponse.getString("resultMessage")
                        Triple(false, errorMessage, null)
                    } else {
                        println("TOKEN")
                        println("${jsonResponse.getJSONObject("data")}")
//                    로그인 성공했을때
                        if(jsonResponse["resultCode"] != 200) {
                            println("[jsonResponse]: $jsonResponse");
                            JOptionPane.showMessageDialog(null, jsonResponse["resultMessage"], "오류", JOptionPane.ERROR_MESSAGE)
                        }
                        val storeList = jsonResponse.getJSONObject("data").getJSONArray("storeList")
                        val accessToken = jsonResponse.getJSONObject("data").getString("accessToken")
                        Triple(true, accessToken, storeList)
                    }
                } else {
                    Triple(false, "로그인 실패: ${response.message}", null)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Triple(false, "서버 연결 실패: ${e.message}", null)
        }
    }
}