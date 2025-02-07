package org.grr.api

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.grr.model.MenuCategory
import org.grr.model.SoldOutMenu
import org.grr.`object`.Api
import org.grr.`object`.Storage
import org.json.JSONArray
import org.json.JSONObject
import javax.swing.JOptionPane

class MenuAPI : BaseAPI() {
    // TODO(메뉴리스트 가져오기) API
    fun fetchMenuList(
        pageNumber: Int,
        pageSize: Int,
        categoryName: String? = null,
        soldOut: Boolean? = null
    ): Triple<Boolean, JSONArray?, JSONArray?> {
        val (savedEmail, savedPassword, autoCheck, storeCode) = Storage.getLoginInfo()
        val accessToken = Storage.getToken() ?: return Triple(false, null, null)
        val urlBuilder = StringBuilder("${Api.BASE_URL}/api/v1/store-pos-setting/sold-out-management")
        urlBuilder.append("?pageNumber=$pageNumber&pageSize=$pageSize&storeCode=$storeCode")

        if (soldOut != null) {
            urlBuilder.append("&soldOut=$soldOut")
        }

        if (!categoryName.isNullOrEmpty()) {
            urlBuilder.append("&categoryName=$categoryName")
        }

        val menuListResponse = sendGetRequest(urlBuilder.toString(), accessToken)

        if (!menuListResponse.first || menuListResponse.second.isBlank()) {
            println(" API 응답 오류: ${menuListResponse.second}") // 에러 로깅
            return Triple(false, null, null)
        }

        return try {
            val jsonResponse = JSONObject(menuListResponse.second)

            if (jsonResponse.has("data") && jsonResponse.get("data") is JSONObject) {
                val dataObject = jsonResponse.getJSONObject("data")

                val menuListData = if (dataObject.has("menuList") && dataObject.get("menuList") is JSONArray) {
                    dataObject.getJSONArray("menuList")
                } else JSONArray() // 데이터가 없으면 빈 배열 반환

                val categoryListData =
                    if (dataObject.has("menuCategoryNameList") && dataObject.get("menuCategoryNameList") is JSONArray) {
                        dataObject.getJSONArray("menuCategoryNameList")
                    } else JSONArray() // 데이터가 없으면 빈 배열 반환

                Triple(true, menuListData, categoryListData)
            } else {
                Triple(false, null, null)
            }
        } catch (e: Exception) {
            println("JSON 파싱 오류: ${e.message}")
            Triple(false, null, null)
        }
    }

    //    TODO(메뉴 품절 처리) API
    fun soldOut(menuIdList: List<Int>): Pair<Boolean, String> {
        val (savedEmail, savedPassword, autoCheck, storeCode) = Storage.getLoginInfo()
        val accessToken = Storage.getToken() ?: return Pair(false, "토큰이 없습니다.")

        if (menuIdList.isEmpty()) {
            return Pair(false, "품절 처리할 메뉴가 없습니다.")
        }

        val requestBody = JSONObject()
            .put("storeCode", storeCode) // 가게 코드
            .put("menuIdList", JSONArray(menuIdList)) // 품절 처리할 메뉴 ID 리스트
            .toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("${Api.BASE_URL}/api/v1/store-pos-setting/sold-out")
            .addHeader("Authorization", accessToken)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                return if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    val jsonResponse = JSONObject(responseBody)

                    if (jsonResponse.getInt("resultCode") == 400) {
                        val errorMessage = jsonResponse.getString("메뉴 품절 400Error")
                        Pair(false, errorMessage)
                    } else {
                        if (jsonResponse["resultCode"] != 200) {
                            JOptionPane.showMessageDialog(null, jsonResponse["resultMessage"], "오류", JOptionPane.ERROR_MESSAGE)
                        }
                        Pair(true, "메뉴 품절 성공")
                    }
                } else {
                    Pair(false, "메뉴 품절 실패: ${response.message}")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Pair(false, "서버 연결 실패: ${e.message}")
        }
    }

    fun parseMenuData(response: String): List<MenuCategory> {
        val menuCategoriesMap = mutableMapOf<String, MutableList<SoldOutMenu>>()

        val menuList = try {
            JSONArray(response) // JSON이 JSONArray이므로 바로 변환
        } catch (e: Exception) {
            println("JSON 파싱 오류: ${e.message}")
            return emptyList()
        }

        for (i in 0 until menuList.length()) {
            val menu = menuList.getJSONObject(i)

            val categoryName = menu.getString("menuCategoryName")

            val soldOutMenu = SoldOutMenu(
                id = menu.getInt("menuId"),
                menuName = menu.getString("menuName"),
                isSoldOut = menu.getBoolean("menuSoldOut")
            )

            // 해당 카테고리가 없으면 새로 추가
            menuCategoriesMap.computeIfAbsent(categoryName) { mutableListOf() }.add(soldOutMenu)
        }

        return menuCategoriesMap.map { (categoryName, menuList) ->
            MenuCategory(id = menuList.first().id, categoryName = categoryName, menuList = menuList)
        }
    }
}