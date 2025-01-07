package org.grr.`object`

import org.json.JSONArray

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
}