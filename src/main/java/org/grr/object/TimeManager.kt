package org.grr.`object`

import org.grr.screen.setting.centerPanel.breakTimePanel.breakTime_modal.BreakTimeData
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

    // 요일별로 정리된 시간대 데이터를 저장
    private val times = mutableMapOf<String, MutableList<String>>()

    // 초기화 메서드: JSON 데이터를 받아 처리
    fun initialize(jsonData: JSONObject) {
        times.clear() // 기존 데이터 초기화

        days.forEach { day ->
            val startTime = jsonData.optJSONArray("${day}StartTime")
            val endTime = jsonData.optJSONArray("${day}EndTime")

            if (startTime != null && endTime != null) {
                val timeRange = "${startTime.getInt(0)}:${startTime.getInt(1).toString().padStart(2, '0')} ~ " +
                        "${endTime.getInt(0)}:${endTime.getInt(1).toString().padStart(2, '0')} "
                times.computeIfAbsent(timeRange) { mutableListOf() }.add(day)
            }
        }
    }

    // 정리된 데이터를 포맷팅하여 반환
    fun getFormattedBreakTimes(): String {
        val result = mutableListOf<String>()

        // 평일 처리
        val weekdaysGroup = times.filter { (_, days) -> days.containsAll(weekdays) && days.size == weekdays.size }
        weekdaysGroup.forEach { (timeRange, _) ->
            result.add("평일: $timeRange")
        }

        // 주말 처리
        val weekendsGroup = times.filter { (_, days) -> days.containsAll(weekends) && days.size == weekends.size }
        weekendsGroup.forEach { (timeRange, _) ->
            result.add("주말: $timeRange")
        }

        // 나머지 요일 처리
        val otherDaysGroup = times.filter { (_, days) ->
            !(days.containsAll(weekdays) && days.size == weekdays.size) &&
                    !(days.containsAll(weekends) && days.size == weekends.size)
        }
        otherDaysGroup.forEach { (timeRange, days) ->
            val dayNames = days.joinToString(", ") { mapDayToKorean(it) }
            result.add("$dayNames: $timeRange")
        }

        return result.joinToString("\n")
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
}