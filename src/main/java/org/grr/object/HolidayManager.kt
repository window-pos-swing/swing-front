package org.grr.`object`

import org.json.JSONArray
import org.json.JSONObject

object HolidayManager {

    fun parseHolidays(holidayList: JSONArray?): String {
        if (holidayList == null) return "휴무일 정보가 없습니다."

        val weeklyHolidays = mutableMapOf<Int, MutableList<String>>()
        val monthlyHolidays = mutableListOf<String>()
        val specificHolidays = mutableListOf<String>()

        for (i in 0 until holidayList.length()) {
            val holiday = holidayList.getJSONObject(i)
            val holidayType = holiday.getString("holidayType")

            when (holidayType) {
                "WEEKLY" -> {
                    val weeksOfMonth = holiday.optJSONArray("weeksOfMonth")
                    val daysOfWeek = holiday.optJSONArray("daysOfWeek")
                    if (weeksOfMonth != null && daysOfWeek != null) {
                        for (weekIndex in 0 until weeksOfMonth.length()) {
                            val week = weeksOfMonth.getInt(weekIndex)
                            val days = mutableListOf<String>()
                            for (dayIndex in 0 until daysOfWeek.length()) {
                                days.add(mapDayToKorean(daysOfWeek.getInt(dayIndex)))
                            }
                            weeklyHolidays.computeIfAbsent(week) { mutableListOf() }
                                .addAll(days.distinct())
                        }
                    } else if (daysOfWeek != null) {
                        for (dayIndex in 0 until daysOfWeek.length()) {
                            weeklyHolidays.computeIfAbsent(-1) { mutableListOf() }
                                .add(mapDayToKorean(daysOfWeek.getInt(dayIndex)))
                        }
                    }
                }
                "MONTHLY" -> {
                    val daysOfMonth = holiday.optJSONArray("daysOfMonth")
                    if (daysOfMonth != null) {
                        for (j in 0 until daysOfMonth.length()) {
                            monthlyHolidays.add("${daysOfMonth.getInt(j)}일")
                        }
                    }
                }
                "SPECIFIC_DATE" -> {
                    val specificDates = holiday.optJSONArray("specificDates")
                    if (specificDates != null && specificDates.length() >= 2) {
                        // 첫 번째 날짜
                        val startDate = specificDates.getJSONObject(0)
                        val startYear = startDate.getInt("first")
                        val startMonth = startDate.getInt("second")
                        val startDay = startDate.getInt("third")

                        // 마지막 날짜
                        val endDate = specificDates.getJSONObject(1)
                        val endYear = endDate.getInt("first")
                        val endMonth = endDate.getInt("second")
                        val endDay = endDate.getInt("third")

                        specificHolidays.add(
                            "${startYear}년 ${startMonth}월 ${startDay}일 ~ ${endYear}년 ${endMonth}월 ${endDay}일"
                        )
                    }
                }
            }
        }

        val result = mutableListOf<String>()

        if (weeklyHolidays.isNotEmpty()) {
            weeklyHolidays.forEach { (week, days) ->
                val weekText = if (week == -1) "" else "매월 ${mapWeekToKorean(week)}째:"
                result.add("$weekText ${days.distinct().joinToString(", ")}  ")
            }
        }
        if (monthlyHolidays.isNotEmpty()) {
            result.add("월간: ${monthlyHolidays.joinToString(", ")}  ")
        }
        if (specificHolidays.isNotEmpty()) {
            result.add("임시: ${specificHolidays.joinToString(", ")} ")
        }

        return result.joinToString("\n")
    }

    private fun mapDayToKorean(day: Int): String {
        return when (day) {
            1 -> "월요일"
            2 -> "화요일"
            3 -> "수요일"
            4 -> "목요일"
            5 -> "금요일"
            6 -> "토요일"
            7 -> "일요일"
            else -> "알 수 없음"
        }
    }

    private fun mapWeekToKorean(week: Int): String {
        return when (week) {
            1 -> "첫"
            2 -> "둘"
            3 -> "셋"
            4 -> "넷"
            5 -> "다섯"
            else -> "$week"
        }
    }

    fun unFormatHoliday(
        regularHolidays: List<Pair<String, String>>,
        temporaryHolidays: List<Pair<String, String>>
    ): JSONArray {
        val holidayList = JSONArray()

        // 정기 휴무 처리
        regularHolidays.forEach { (week, day) ->
            val weekNumber = mapWeekToNumber(week.replace("매월 ", "").replace("째", ""))
            val dayNumber = mapDayToNumber(day.replace("요일", ""))

            if (weekNumber != null && dayNumber != null) {
                holidayList.put(JSONObject().apply {
                    put("holidayType", "WEEKLY")
                    put("weeksOfMonth", JSONArray().put(weekNumber))
                    put("daysOfWeek", JSONArray().put(dayNumber))
                })
            }
        }

        // 임시 휴무 처리
        temporaryHolidays.forEach { (startDate, endDate) ->
            holidayList.put(JSONObject().apply {
                put("holidayType", "SPECIFIC_DATE")
                put("specificDates", JSONArray().apply {
                    put(JSONObject(mapOf("first" to startDate.split("-")[0].toInt(), "second" to startDate.split("-")[1].toInt(), "third" to startDate.split("-")[2].toInt())))
                    put(JSONObject(mapOf("first" to endDate.split("-")[0].toInt(), "second" to endDate.split("-")[1].toInt(), "third" to endDate.split("-")[2].toInt())))
                })
            })
        }

        return holidayList
    }


    private fun mapWeekToNumber(week: String): Int? {
        return when (week) {
            "첫" -> 1
            "둘" -> 2
            "셋" -> 3
            "넷" -> 4
            "다섯" -> 5
            else -> null
        }
    }

    private fun mapDayToNumber(day: String): Int? {
        return when (day) {
            "월" -> 1
            "화" -> 2
            "수" -> 3
            "목" -> 4
            "금" -> 5
            "토" -> 6
            "일" -> 7
            else -> null
        }
    }
}