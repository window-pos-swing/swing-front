package org.grr.`object`

import org.json.JSONObject
import java.io.File

object Storage {

    /*
        현재 로그인한 회원 정보
    */
    private val storeInfo = File("storeInfo.json")

    // 상점 정보 저장
    fun saveMemberInfo(userInfo: JSONObject) {
        storeInfo.writeText(userInfo.toString(4)) // JSON 형식으로 저장
    }

    // 상점 정보 불러오기
    fun getStoreInfo(): JSONObject? {
        return if (storeInfo.exists()) {
            JSONObject(storeInfo.readText()) // JSON 객체로 변환
        } else {
            null // 파일이 없으면 null 반환
        }
    }

    // 상점 정보 삭제
    fun clearMemberInfo() {
        if (storeInfo.exists()) {
            storeInfo.delete()
        }
    }

    /*
        로그인 정보
    */
    private val autoLogin = File("autoLogin.json")

    // 로그인 정보 저장
    fun saveLoginInfo(email: String, password: String, autoCheck: Boolean, storeCode: String?) {
        val jsonObject = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("autoCheck", autoCheck)
            put("storeCode", storeCode)
        }
        autoLogin.writeText(jsonObject.toString(4))
    }

    // 저장된 로그인 정보 불러오기
    fun getLoginInfo(): LoginInfo {
        if (autoLogin.exists()) {
            val jsonObject = JSONObject(autoLogin.readText())
            val email = jsonObject.optString("email", null)
            val password = jsonObject.optString("password", null)
            val autoLogin = jsonObject.optBoolean("autoCheck", false)
            val storeCode = jsonObject.optString("storeCode", null)
            return LoginInfo(email, password, autoLogin, storeCode)
        }
        return LoginInfo(null.toString(), null.toString(), false, null)
    }

    // 로그인 정보 삭제
    fun clearLoginInfo() {
        if (autoLogin.exists()) {
            autoLogin.delete()
        }
    }

    /*
        토큰
    */
    private val accessTokenFile = File("accessToken.txt")
//    private val refreshTokenFile = File("refreshToken.txt")

    //    토큰 저장
    fun saveToken(token: String) {
        accessTokenFile.writeText(token)
    }

    // 저장된 토큰 읽기
    fun getToken(): String? {
        return if (accessTokenFile.exists()) {
            accessTokenFile.readText()
        } else {
            null
        }
    }

    // 토큰 삭제
    fun deleteToken() {
        if (accessTokenFile.exists()) {
            accessTokenFile.delete()
        }
    }

    data class LoginInfo(
        val email: String,
        val password: String,
        val autoLogin: Boolean,
        val storeCode: String?
    )
}