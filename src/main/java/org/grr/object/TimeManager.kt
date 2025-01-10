package org.grr.`object`

import org.grr.screen.setting.centerPanel.breakTimePanel.breakTime_modal.BreakTimeData
import org.json.JSONArray
import org.json.JSONObject

object TimeManager {
    val breakTimeDataList = mutableListOf<BreakTimeData>()

    fun check(labelText: String): Boolean {
        val conflicting = breakTimeDataList.any { existingData ->
            val existingDays = existingData.labelText.split(", ").toSet()
            val newDays = labelText.split(", ").toSet()

            // 충돌 조건: 기존 요일과 새로운 요일이 서로 중복되거나 상위/하위 집합 관계인 경우
            existingDays.intersect(newDays).isNotEmpty() ||
                    newDays.contains("평일") && existingDays.any { it in arrayOf("월", "화", "수", "목", "금") } ||
                    newDays.contains("주말") && existingDays.any { it in arrayOf("토", "일") } ||
                    existingDays.contains("평일") && newDays.any { it in arrayOf("월", "화", "수", "목", "금") } ||
                    existingDays.contains("주말") && newDays.any { it in arrayOf("토", "일") }
        }

        return conflicting
    }

    private val weekdays = listOf("monday", "tuesday", "wednesday", "thursday", "friday")
    private val weekends = listOf("saturday", "sunday")
    private val days = weekdays + weekends

    // 요일별로 정리된 오전/오후 시간대 데이터를 저장
    private val timeRanges = mutableMapOf<String, MutableList<String>>()

    fun initialize(jsonData: JSONObject, isBreakTime: Boolean) {
        timeRanges.clear()

        days.forEach { day ->
            val allDayKey = "${day}AllDay"
            val startTimeKey = "${day}StartTime"
            val endTimeKey = "${day}EndTime"

            // StartTime과 EndTime 체크
            val startTime = jsonData.optJSONArray(startTimeKey)
            val endTime = jsonData.optJSONArray(endTimeKey)

            if (startTime != null && endTime != null) {
                // 시간 범위 처리
                val startHour = startTime.getInt(0)
                val startMinute = startTime.getInt(1)
                val endHour = endTime.getInt(0)
                val endMinute = endTime.getInt(1)

                val timeRange = "${if (startHour < 12) "오전" else "오후"} ${startHour % 12}시 ${
                    startMinute.toString().padStart(2, '0')
                }분 ~ " +
                        "${if (endHour < 12) "오전" else "오후"} ${endHour % 12}시 ${
                            endMinute.toString().padStart(2, '0')
                        }분 "

                timeRanges.computeIfAbsent(timeRange) { mutableListOf() }.add(day)
            } else {
                if(!isBreakTime){
                    // AllDay 체크
                    val isAllDay = jsonData.optBoolean(allDayKey, true)
                    if (isAllDay) {
                        timeRanges.computeIfAbsent("24시간 ") { mutableListOf() }.add(day)
                    }
                }

            }
        }
    }


    // 정리된 데이터를 포맷팅하여 반환
    fun getFormattedBreakTimes(): String {
        val result = mutableListOf<String>()
        println("getFormattedTime : $timeRanges")
        // 시간대 처리
        val formattedTimes = formatTimes(timeRanges)
        result.addAll(formattedTimes)

        return result.joinToString("\n")
    }

    private fun formatTimes(times: Map<String, List<String>>): List<String> {
        println("formatTimes")
        val result = mutableListOf<String>()

        // 전체 요일 처리
        val allDaysGroup =
            times.filter { (_, days) -> days.containsAll(weekdays + weekends) && days.size == weekdays.size + weekends.size }
        allDaysGroup.forEach { (timeRange, _) ->
            result.add("전체요일: $timeRange")
        }

        // 평일 처리
        val weekdaysGroup = times.filter { (_, days) ->
            days.containsAll(weekdays) && days.size == weekdays.size && !days.containsAll(weekends)
        }
        weekdaysGroup.forEach { (timeRange, _) ->
            result.add("평일: $timeRange")
        }

        // 주말 처리
        val weekendsGroup = times.filter { (_, days) ->
            days.containsAll(weekends) && days.size == weekends.size && !days.containsAll(weekdays)
        }
        weekendsGroup.forEach { (timeRange, _) ->
            result.add("주말: $timeRange")
        }

        // 나머지 요일 처리
        val otherDaysGroup = times.filter { (_, days) ->
            !(days.containsAll(weekdays + weekends) && days.size == weekdays.size + weekends.size) &&
                    !(days.containsAll(weekdays) && days.size == weekdays.size) &&
                    !(days.containsAll(weekends) && days.size == weekends.size)
        }
        otherDaysGroup.forEach { (timeRange, days) ->
            val dayNames = days.joinToString(", ") { mapDayToKorean(it) }
            result.add("$dayNames: $timeRange")
        }
        return result
    }


    // 영어 요일을 한글 요일로 변환
    private fun mapDayToKorean(day: String): String {
        return when (day) {
            "monday" -> "월"
            "tuesday" -> "화"
            "wednesday" -> "수"
            "thursday" -> "목"
            "friday" -> "금"
            "saturday" -> "토"
            "sunday" -> "일"
            else -> day
        }
    }

    ///저장할때 사용
    // 포맷된 데이터를 언포맷팅하여 JSON으로 변환
    fun unformatBreakTimes(formattedData: String, isBreakTime : Boolean): JSONObject {
        val breakTimeJson = JSONObject()
        val daysOfWeek = listOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
        val populatedDays = mutableSetOf<String>() // 채워진 요일 추적
        val lines = formattedData.split("\n")

        for (line in lines) {
            val parts = line.split(": ", limit = 2)
            if (parts.size == 2) {
                val daysText = parts[0]
                val timeRange = parts[1]

                // 전체 요일, 평일, 주말 등의 처리
                val days = when (daysText) {
                    "전체요일" -> weekdays + weekends // 전체 요일 처리
                    "평일" -> weekdays
                    "주말" -> weekends
                    else -> daysText.split(", ").map { mapKoreanToDay(it) }
                }

                if(isBreakTime) {
                    println("isBreakTime")
                    val timeParts = timeRange.split(" ~ ")
                    if (timeParts.size == 2) {
                        val startTime = parseKoreanTime(timeParts[0].trim())
                        val endTime = parseKoreanTime(timeParts[1].trim())

                        days.forEach { day ->
                            breakTimeJson.put("${day}StartTime", JSONArray().put(startTime.first).put(startTime.second))
                            breakTimeJson.put("${day}EndTime", JSONArray().put(endTime.first).put(endTime.second))
                            populatedDays.add(day) // 요일 추가
                        }
                    }
                }else{
                    if (timeRange == "24시간") {

                        // "24시간" 처리
                        days.forEach { day ->
                            breakTimeJson.put("${day}StartTime", JSONObject.NULL)
                            breakTimeJson.put("${day}EndTime", JSONObject.NULL)
                            breakTimeJson.put("${day}AllDay", true)
                            populatedDays.add(day)
                        }

                    } else {
                        val timeParts = timeRange.split(" ~ ")
                        if (timeParts.size == 2) {
                            val startTime = parseKoreanTime(timeParts[0].trim())
                            val endTime = parseKoreanTime(timeParts[1].trim())

                            days.forEach { day ->
                                breakTimeJson.put("${day}StartTime", JSONArray().put(startTime.first).put(startTime.second))
                                breakTimeJson.put("${day}EndTime", JSONArray().put(endTime.first).put(endTime.second))
                                breakTimeJson.put("${day}AllDay", false)

                                populatedDays.add(day) // 요일 추가
                            }
                            daysOfWeek.forEach { day ->
                                if (!populatedDays.contains(day)) {
                                    breakTimeJson.put("${day}AllDay", false)
                                }
                            }
                        }
                    }
                }


            }
        }

        // 누락된 요일은 JSONObject.NULL로 처리
        daysOfWeek.forEach { day ->
            if (!populatedDays.contains(day)) {
                breakTimeJson.put("${day}StartTime", JSONObject.NULL) // JSONObject.NULL로 설정
                breakTimeJson.put("${day}EndTime", JSONObject.NULL) // JSONObject.NULL로 설정
            }
        }

        return breakTimeJson
    }

    // 한국어 요일을 영어 요일로 변환
    private fun mapKoreanToDay(koreanDay: String): String {
        return when (koreanDay) {
            "월" -> "monday"
            "화" -> "tuesday"
            "수" -> "wednesday"
            "목" -> "thursday"
            "금" -> "friday"
            "토" -> "saturday"
            "일" -> "sunday"
            else -> koreanDay
        }
    }

    // 한국어 시간 문자열을 시간 값으로 변환
    private fun parseKoreanTime(timeString: String): Pair<Int, Int> {
        val isPM = timeString.contains("오후")
        val cleanTime = timeString
            .replace("오전", "")
            .replace("오후", "")
            .replace("시", ":")
            .replace("분", "")
            .trim() // 공백 제거
        val parts = cleanTime.split(":")

        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid time format: $timeString")
        }

        val hour = parts[0].trim().toInt() + if (isPM && parts[0].trim().toInt() != 12) 12 else 0
        val minute = parts[1].trim().toInt()

        return Pair(hour, minute)
    }
}
