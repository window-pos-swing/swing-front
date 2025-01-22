package org.grr.`object`

import org.json.JSONObject
import java.io.File

object Storage {

    /*
        현재 로그인한 회원 정보
    */
    private val memberInfo = File("memberInfo.json")
    // 회원 정보 저장
    fun saveMemberInfo(userInfo: JSONObject) {
        memberInfo.writeText(userInfo.toString(4)) // JSON 형식으로 저장
    }

    // 회원 정보 불러오기
    fun getMemberInfo(): JSONObject? {
        return if (memberInfo.exists()) {
            JSONObject(memberInfo.readText()) // JSON 객체로 변환
        } else {
            null // 파일이 없으면 null 반환
        }
    }

    // 회원 정보 삭제
    fun clearMemberInfo() {
        if (memberInfo.exists()) {
            memberInfo.delete()
        }
    }

    /*
        로그인 정보
    */
    private val autoLogin = File("autoLogin.json")

    // 로그인 정보 저장
    fun saveLoginInfo(email: String, password: String, autoCheck: Boolean) {
        val jsonObject = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("autoCheck", autoCheck)
        }
        autoLogin.writeText(jsonObject.toString(4))
    }

    // 저장된 로그인 정보 불러오기
    fun getLoginInfo(): Triple<String?, String?, Boolean> {
        if (autoLogin.exists()) {
            val jsonObject = JSONObject(autoLogin.readText())
            val email = jsonObject.optString("email", null)
            val token = jsonObject.optString("password", null)
            val autoLogin = jsonObject.optBoolean("autoCheck", false)
            val storeCode = jsonObject.optString("storeCode", null)
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
}