package org.grr.model;

import org.grr.enum.BusinessStatus
import org.grr.`object`.HolidayManager
import org.grr.`object`.Storage
import org.grr.`object`.TimeManager
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object SettingModel {
    var cookingTime: Int = 0 // 조리 시간
    var cookingTimeControl: Boolean = false // 조리 시간 (자동여부)
    var deliveryTime: Int = 0 // 배달 시간
    var deliveryTimeControl: Boolean = false // 배달 시간 (자동여부)
    var breakTime: String = "" // 브레이크 타임
    var operateTime: String = ""
    var regularHoliday: String = ""
    var temporaryHoliday: String = ""
    var businessStatus: BusinessStatus = BusinessStatus.END

    val storeInfo = Storage.getStoreInfo()

    //로컬에 저장된 요리시간 배달시간 초기화
    fun loadCookDeliveryTime() {
        //요리시간, on/off 여부
        cookingTime = storeInfo
            ?.optInt("estimatedCookingTime", 25) ?: 25
        cookingTimeControl = storeInfo
            ?.optBoolean("estimatedCookingTimeControl", false) ?: false
        //배달시간, on/off 여부
        deliveryTime = storeInfo
            ?.optInt("estimatedArrivalTime", 40) ?: 40
        deliveryTimeControl = storeInfo
            ?.optBoolean("estimatedArrivalTimeControl", false) ?: false
        println("[저장된 요리시간] : $cookingTime")
        println("[저장된 요리 on/off] : $cookingTimeControl")
        println("[저장된 배달시간] : $deliveryTime")
        println("[저장된 배달 on/off] : $deliveryTimeControl")
    }

    // 로컬에 요리정보 업데이트
    fun updateLocalCook() {
        val updatedMemberInfo = storeInfo ?: JSONObject() // 기존 데이터를 가져오거나 새로 생성
        val settings = updatedMemberInfo.optJSONObject("setting") ?: JSONObject()

        settings.put("estimatedCookingTime", cookingTime)
        settings.put("estimatedCookingTimeControl", cookingTimeControl)

        updatedMemberInfo.put("setting", settings)
        Storage.saveStoreInfo(updatedMemberInfo) // 업데이트된 데이터를 저장

        println("[요리 정보 업데이트] 요리시간=$cookingTime, 요리시간 on/off=$cookingTimeControl")
    }

    // 로컬에 배달정보 업데이트
    fun updateLocalDelivery() {
        val updatedMemberInfo = storeInfo ?: JSONObject() // 기존 데이터를 가져오거나 새로 생성
        val settings = updatedMemberInfo.optJSONObject("setting") ?: JSONObject()

        settings.put("estimatedArrivalTime", deliveryTime)
        settings.put("estimatedArrivalTimeControl", deliveryTimeControl)

        updatedMemberInfo.put("setting", settings)
        Storage.saveStoreInfo(updatedMemberInfo) // 업데이트된 데이터를 저장

        println("[배달 정보 업데이트] 배달시간=$deliveryTime, 배달시간 on/off=$deliveryTimeControl")
    }

    // 브레이크 타임 정보 초기화
    fun loadBreakTime() {
        val myBreakTime = storeInfo
            ?.optJSONObject("breakTime")

        myBreakTime?.let {
            // TimeManager 초기화
            TimeManager.initialize(it, true)

            // 포맷된 데이터를 가져옴
            val formattedBreakTime = TimeManager.getFormattedBreakTimes()
            breakTime = formattedBreakTime;
        }
        println("[브레이크 타임 로컬 정보 로드]")
        println(breakTime)
    }

    // 브레이크 타임 정보 업데이트
    fun saveBreakTime(breakTimeJson: JSONObject) {
        val updatedStoreInfo = storeInfo ?: JSONObject()

        println("저장된 데이터:")
        println(breakTimeJson)

        // 여기에서 breakTimeString 데이터를 JSON으로 변환하여 저장
        updatedStoreInfo.put("breakTime", breakTimeJson)
        Storage.saveStoreInfo(updatedStoreInfo)

        println("[브레이크 타임 정보 업데이트 완료]")
        loadBreakTime()
    }


    //운영시간 정보 초기화
    fun loadOperateTime() {
        val myOperateTime = storeInfo
            ?.optJSONObject("businessHour")

        myOperateTime?.let {
            // TimeManager 초기화
            TimeManager.initialize(it, false)

            // 포맷된 데이터를 가져옴
            val formattedBreakTime = TimeManager.getFormattedBreakTimes()
            operateTime = formattedBreakTime;
        }
        println("[운영시간 정보 로컬 로드]")
        println(operateTime)
    }


    // 운영시간 정보 업데이트
    fun saveOperateTime(operateTimeJson: JSONObject) {
        val updatedStoreInfo = storeInfo ?: JSONObject()

        println("저장된 데이터:")
        println(operateTimeJson)

        updatedStoreInfo.put("businessHour", operateTimeJson)
        Storage.saveStoreInfo(updatedStoreInfo)

        println("[운영시간 정보 로컬 업데이트 완료]")
        loadOperateTime()
    }

    fun loadHoliday() {
        val holidayListJsonArray = storeInfo
            ?.optJSONArray("holidayList")

        if (holidayListJsonArray != null) {
            // HolidayManager를 통해 휴일 데이터를 파싱합니다.
            val formattedHoliday = HolidayManager.parseHolidays(holidayListJsonArray)

            println("[휴무일 정보 로컬 로드]")
            println("포맷된 휴무일: $formattedHoliday")

            // 주간 휴일과 임시 휴일 데이터를 각각 추출
            val (regular, temporary) = splitHolidayData(formattedHoliday)

            // 주간 휴무와 임시 휴무를 조건에 맞게 설정
            regularHoliday = regular.ifBlank { "등록된 주간 휴무가 없습니다." }
            temporaryHoliday = temporary.ifBlank { "등록된 임시 휴무가 없습니다." }

            println("[주간 휴무일] $regularHoliday")
            println("[임시 휴무일] $temporaryHoliday")
        } else {
            println("[휴무일 정보 로컬 로드 실패] 설정된 휴무일 데이터가 없습니다.")
        }
    }

    // 포맷된 휴일 데이터를 주간과 임시로 나눔
    private fun splitHolidayData(formattedHoliday: String): Pair<String, String> {
        val beforeMonthly = formattedHoliday.split("월간").firstOrNull()?.trim() ?: ""
        val temporaryHoliday = formattedHoliday.split("임시").getOrNull(1)?.trim()?.let {
            "임시 $it"
        } ?: ""
        // "임시" 데이터를 제거
        val regularHoliday = beforeMonthly.replace("임시.*".toRegex(), "").trim()
        return Pair(regularHoliday, temporaryHoliday)
    }

    fun saveHoliday(holidayJson: Any) {
        val updatedMemberInfo = storeInfo ?: JSONObject()
        val settings = updatedMemberInfo.optJSONObject("setting") ?: JSONObject()

        println("저장된 데이터:")
        println(holidayJson)

        settings.put("holidayList", holidayJson)
        updatedMemberInfo.put("setting", settings)
        Storage.saveStoreInfo(updatedMemberInfo)

        println("[휴무일 정보 로컬 업데이트 완료]")
        loadHoliday()
    }

}