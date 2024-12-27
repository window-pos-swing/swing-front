package org.grr.api

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.grr.`object`.Api
import org.json.JSONObject
import java.io.IOException
import javax.swing.JOptionPane

class LoginToServer {

    fun loginToServer(email: String, password: String): Pair<Boolean, String> {
        val client = OkHttpClient()

        val requestBody = JSONObject()
            .put("email", email)
            .put("password", password)
            .toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("${Api.BASE_URL}/api/v0/store-member/login")
            .post(requestBody)
            .build()

        println("requestBody : $requestBody");
        println("request : $request");
        try {
            client.newCall(request).execute().use { response ->
//                성공했을때. 200일때
                return if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    val jsonResponse = JSONObject(responseBody)

//                    로그인 실패했을때 (이메일, 비밀번호 틀렸을때)
                    if (jsonResponse.getInt("resultCode") == 400) {
                        val errorMessage = jsonResponse.getString("resultMessage")
                        Pair(false, errorMessage)
                    } else {
//                    로그인 성공했을때
                        if(jsonResponse["resultCode"] != 200) {
                            println("[jsonResponse]: $jsonResponse");
                            JOptionPane.showMessageDialog(null, jsonResponse["resultMessage"], "오류", JOptionPane.ERROR_MESSAGE)
                        }
                        val accessToken = jsonResponse.getJSONObject("data").getString("authorization")
                        Pair(true, accessToken)
                    }
                } else {
                    Pair(false, "로그인 실패: ${response.message}")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Pair(false, "서버 연결 실패: ${e.message}")
        }
    }
}