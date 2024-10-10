package org.example.widgets.custom_titlebar.TitleDateUpdate

import java.text.SimpleDateFormat
import java.util.*
import javax.swing.JLabel
import javax.swing.Timer

class TitleDateUpdater(private val label: JLabel) {

    private val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 hh:mm a")

    init {
        // 초기 시간 설정
        updateLabel()

        // 현재 시간을 기준으로 다음 "정각"까지 남은 시간 계산
        val now = Calendar.getInstance()
        val secondsUntilNextMinute = 60 - now.get(Calendar.SECOND)

        // Timer 설정: 첫 번째 타이머는 다음 "정각"에 실행
        Timer(secondsUntilNextMinute * 1000) {
            // 다음 정각에 도달했을 때 라벨 업데이트
            updateLabel()

            // 이후 매 1분마다 업데이트
            Timer(60000) {
                updateLabel()
            }.start()
        }.start()
    }

    private fun updateLabel() {
        val currentTime = dateFormat.format(Date())
        label.text = currentTime
    }
}