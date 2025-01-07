package org.grr.model

import org.json.JSONObject
import java.time.LocalDateTime

data class BusinessPause(
    val businessPauseStartTime: List<Int>,
    val businessPauseEndTime: List<Int>
) {
    companion object {
        fun fromLocalDateTimes(startTime: LocalDateTime, endTime: LocalDateTime): BusinessPause {
            return BusinessPause(
                businessPauseStartTime = listOf(
                    startTime.year,
                    startTime.monthValue,
                    startTime.dayOfMonth,
                    startTime.hour,
                    startTime.minute
                ),
                businessPauseEndTime = listOf(
                    endTime.year,
                    endTime.monthValue,
                    endTime.dayOfMonth,
                    endTime.hour,
                    endTime.minute
                )
            )
        }

        fun BusinessPause.toJson(): String {
            val jsonObject = JSONObject()
            jsonObject.put("businessPauseStartTime", businessPauseStartTime)
            jsonObject.put("businessPauseEndTime", businessPauseEndTime)
            return jsonObject.toString()
        }
    }
}
