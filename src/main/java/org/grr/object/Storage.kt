package org.grr.`object`

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object Storage {

    /*
        프린터 정보
    */
    val printerInfo = File("printers.json")

    // ✅ 프린터 정보 저장
    fun savePrinterSettings(printerList: List<PrinterInfo>) {
        val jsonArray = JSONArray()
        printerList.forEach { printer ->
            val jsonObject = JSONObject().apply {
                put("name", printer.name)
                put("port", printer.port)
                put("speed", printer.speed)
                put("receiptPrint", printer.receiptPrint)
                put("kitchenPrint", printer.kitchenPrint)
                put("selectPrint", printer.selectPrint)
            }
            jsonArray.put(jsonObject)
        }
        printerInfo.writeText(jsonArray.toString(4)) // JSON 형식 저장
    }

    // ✅ 저장된 프린터 정보 불러오기
    fun loadPrinterSettings(): List<PrinterInfo> {
        if (!printerInfo.exists()) return emptyList()

        val jsonArray = JSONArray(printerInfo.readText())
        val printerList = mutableListOf<PrinterInfo>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val printer = PrinterInfo(
                name = obj.getString("name"),
                port = obj.getString("port"),
                speed = obj.getString("speed"),
                receiptPrint = obj.getBoolean("receiptPrint"),
                kitchenPrint = obj.getBoolean("kitchenPrint"),
                selectPrint = obj.getBoolean("selectPrint"),
            )
            printerList.add(printer)
        }
        return printerList
    }

    // ✅ 프린터 정보 삭제
    fun clearPrinterSettings() {
        if (printerInfo.exists()) {
            printerInfo.delete()
        }
    }

    data class PrinterInfo(
        var name: String, // 프린터 이름
        var port: String, // 프린터 포트
        var speed: String, // 프린터 속도
        var receiptPrint: Boolean, //영수증 출력
        var kitchenPrint: Boolean, // 주방 주문서 출력
        var selectPrint : Boolean // 현재 선택된 프린터
    )



    /*
        현재 로그인한 회원 정보
    */
    private val storeInfo = File("storeInfo.json")

    // 상점 정보 저장
    fun saveStoreInfo(userInfo: JSONObject) {
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