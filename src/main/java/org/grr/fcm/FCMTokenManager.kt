package org.grr.fcm

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.FileNotFoundException

class FCMTokenManager {

    private val apiKey = "AIzaSyCEKTVMq2lvekjZiGCOWbLPz7cnWKHKTF0"

    init {
        initializeFirebase()
    }

    private fun initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) { // FirebaseApp 중복 초기화 방지
                val serviceAccount = this::class.java.classLoader.getResourceAsStream("grr-project-4d8a9-firebase-adminsdk-oj5a7-f1caf2561d.json")
                    ?: throw FileNotFoundException("Firebase JSON 파일을 찾을 수 없습니다.")
                val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build()
                FirebaseApp.initializeApp(options)
                println("Firebase 초기화 성공")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Firebase 초기화 실패: ${e.message}")
        }
    }

    fun getTokenFromFirebase(): String? {
        val client = OkHttpClient()
        val url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=$apiKey"

        val jsonRequest = """
            {
                "returnSecureToken": true
            }
        """.trimIndent()

        val requestBody = RequestBody.create("application/json".toMediaType(), jsonRequest)
        val request = Request.Builder().url(url).post(requestBody).build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()
                if (!response.isSuccessful) {
                    println("토큰 요청 실패: HTTP ${response.code} - ${response.message}")
                    println("응답 내용: $responseBody")
                    return null
                }

                val jsonObject = JsonParser.parseString(responseBody).asJsonObject
                val idToken = jsonObject["idToken"].asString

                return idToken
            }
        } catch (e: Exception) {
            println("토큰 요청 중 오류 발생: ${e.message}")
        }
        return null
    }

    fun postToken() {
        try {
            val registrationToken = "DEVICE_FCM_TOKEN" // 토큰을 서버에서 받아와야 함
            val message = Message.builder()
                .putData("title", "새 주문 알림")
                .putData("body", "주문이 도착했습니다. 확인해 주세요!")
                .setToken(registrationToken)
                .build()

            val response = FirebaseMessaging.getInstance().send(message)
            println("FCM 메시지 전송 성공: $response")
        } catch (e: Exception) {
            e.printStackTrace()
            println("FCM 메시지 전송 실패: ${e.message}")
        }
    }
}