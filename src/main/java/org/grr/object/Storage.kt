package org.grr.`object`

import org.json.JSONObject
import java.io.File

object Storage {
    private val autoLogin = File("autoLogin.json")

    // 로그인 정보 저장
    fun saveLoginInfo(email: String, password: String, autoCheck: Boolean) {
        val jsonObject = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("autoCheck", autoCheck)
        }
        autoLogin.writeText(jsonObject.toString())
    }

    // 저장된 로그인 정보 불러오기
    fun getLoginInfo(): Triple<String?, String?, Boolean> {
        if (autoLogin.exists()) {
            val jsonObject = JSONObject(autoLogin.readText())
            val email = jsonObject.optString("email", null)
            val token = jsonObject.optString("password", null)
            val autoLogin = jsonObject.optBoolean("autoCheck", false)
            return Triple(email, token, autoLogin)
        }
        return Triple(null, null, false) // 기본값 반환
    }

    // 로그인 정보 삭제
    fun clearLoginInfo() {
        if (autoLogin.exists()) {
            autoLogin.delete()
        }
    }

    private val accessTokenFile = File("accessToken.txt")
//    private val refreshTokenFile = File("refreshToken.txt")

    //    토큰 저장
    fun saveToken(email: String, token: String) {
        val jsonObject = if (accessTokenFile.exists()) {
            JSONObject(accessTokenFile.readText()) // 기존 데이터 읽기
        } else {
            JSONObject()
        }
        jsonObject.put(email, token) // 이메일을 키로 사용해 토큰 저장
        accessTokenFile.writeText(jsonObject.toString()) // 데이터 저장
    }

    // 저장된 토큰 읽기
    fun getToken(email: String): String? {
        if (accessTokenFile.exists()) {
            val jsonObject = JSONObject(accessTokenFile.readText())
            return jsonObject.optString(email, null) // 이메일 키에 해당하는 토큰 반환
        }
        return null
    }

    // 특정 사용자의 토큰 삭제
    fun clearToken(email: String) {
        if (accessTokenFile.exists()) {
            val jsonObject = JSONObject(accessTokenFile.readText())
            jsonObject.remove(email) // 특정 키 삭제
            accessTokenFile.writeText(jsonObject.toString()) // 갱신된 데이터 저장
        }
    }

    // 모든 토큰 삭제
    fun deleteToken() {
        if (accessTokenFile.exists()) {
            accessTokenFile.delete()
        }
    }
}