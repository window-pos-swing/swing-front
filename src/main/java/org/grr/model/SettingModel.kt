package org.grr.model;

import org.grr.`object`.Storage
import org.json.JSONObject

object SettingModel {
    var cookingTime: Int = 0
    var cookingTimeControl: Boolean = false
    var deliveryTime: Int = 0
    var deliveryTimeControl: Boolean = false

    val memberInfo = Storage.getMemberInfo()

    //로컬에 저장된 요리시간 배달시간 초기화
    fun loadCookDeliveryTime() {
        //요리시간, on/off 여부
        cookingTime = memberInfo
            ?.optJSONObject("setting")
            ?.optInt("estimatedCookingTime", 30) ?: 30
        cookingTimeControl = memberInfo
            ?.optJSONObject("setting")
            ?.optBoolean("estimatedCookingTimeControl", false) ?: false
        //배달시간, on/off 여부
        deliveryTime = memberInfo
            ?.optJSONObject("setting")
            ?.optInt("estimatedArrivalTime", 40) ?: 40
        deliveryTimeControl = memberInfo
            ?.optJSONObject("setting")
            ?.optBoolean("estimatedArrivalTimeControl", false) ?: false
        println("저장된 요리시간 : $cookingTime")
        println("저장된 요리 on/off : $cookingTimeControl")
        println("저장된 배달시간 : $deliveryTime")
        println("저장된 배달 on/off : $deliveryTimeControl")
    }

    // 로컬에 요리정보 업데이트
    fun updateLocalCook() {
        val updatedMemberInfo = memberInfo ?: JSONObject() // 기존 데이터를 가져오거나 새로 생성
        val settings = updatedMemberInfo.optJSONObject("setting") ?: JSONObject()

        settings.put("estimatedCookingTime", cookingTime)
        settings.put("estimatedCookingTimeControl", cookingTimeControl)

        updatedMemberInfo.put("setting", settings)
        Storage.saveMemberInfo(updatedMemberInfo) // 업데이트된 데이터를 저장

        println("요리 정보 업데이트됨: 요리시간=$cookingTime, 요리시간 on/off=$cookingTimeControl")
    }

    // 로컬에 배달정보 업데이트
    fun updateLocalDelivery() {
        val updatedMemberInfo = memberInfo ?: JSONObject() // 기존 데이터를 가져오거나 새로 생성
        val settings = updatedMemberInfo.optJSONObject("setting") ?: JSONObject()

        settings.put("estimatedArrivalTime", deliveryTime)
        settings.put("estimatedArrivalTimeControl", deliveryTimeControl)

        updatedMemberInfo.put("setting", settings)
        Storage.saveMemberInfo(updatedMemberInfo) // 업데이트된 데이터를 저장

        println("배달 정보 업데이트됨: 배달시간=$deliveryTime, 배달시간 on/off=$deliveryTimeControl")
    }
}