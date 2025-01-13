package org.grr.util

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class MyDateFormat {

    companion object {
        fun calculateTimeDifferenceInMinutes(modifyOrderDate: List<Int>): Int {
            // modifyOrderDate를 LocalDateTime으로 변환
            val modifyDateTime = LocalDateTime.of(
                modifyOrderDate[0], // 연도
                modifyOrderDate[1], // 월
                modifyOrderDate[2], // 일
                modifyOrderDate[3], // 시
                modifyOrderDate[4], // 분
                modifyOrderDate[5]  // 초
            )

            // 현재 시간 가져오기
            val currentDateTime = LocalDateTime.now()

            // 분 단위 차이 계산 후 Int로 변환
            return ChronoUnit.MINUTES.between(modifyDateTime, currentDateTime).toInt()
        }
    }

}