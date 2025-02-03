package org.grr.screen.main.main_widget.dialog.PauseOperations.widgets

import org.grr.api.SettingToServer
import org.grr.enum.BusinessStatus
import java.awt.Dimension
import java.time.LocalDateTime
import javax.swing.JButton
import javax.swing.JOptionPane
import org.grr.model.BusinessPause
import org.grr.model.BusinessPause.Companion.toJson
import org.grr.model.SettingModel
import org.grr.screen.main.main_widget.dialog.ConfirmationDialog
import org.grr.screen.main.main_widget.dialog.PauseOperations.PauseOperationsDialog
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.FillRoundedButton
import java.awt.Color
import java.time.format.DateTimeFormatter

class ConfirmButton(
    private val parentDialog: PauseOperationsDialog, // 상위 다이얼로그 참조
    private val callback: (Boolean) -> Unit
) : JButton("임시 중지") {

    init {
        preferredSize = Dimension(300, 62)
        font = MyFont.Bold(24f)
        background = Color.WHITE
        foreground = MyColor.DARK_RED
        border = javax.swing.BorderFactory.createLineBorder(MyColor.DARK_RED)
        addActionListener { handleButtonClick() }
    }

    private fun handleButtonClick() {
        val selectedType = determineSelectedType()
        val currentTime = LocalDateTime.now()
        val (startTime, endTime) = calculateStartAndEndTime(selectedType, currentTime)

        if (endTime == null) {
            JOptionPane.showMessageDialog(
                parentDialog,
                "시간을 지정해주세요.",
                "시간 지정 오류",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        val formattedEndTime = formatDateTime(endTime)
        val businessPause = BusinessPause.fromLocalDateTimes(startTime, endTime)
        print("startTime : $startTime")
        print("endTime : $endTime")
        val json = businessPause.toJson()
        // 확인 다이얼로그 호출
        ConfirmationDialog(
            parent = parentDialog,
            title = "확인",
            endTime = formattedEndTime,
            callback = { confirmed ->
                if (confirmed) {
//                    println("사용자가 확인을 클릭했습니다.")
                    // 로컬 및 서버 저장
                    SettingModel.savePauseTime(startTime, endTime)
                    val result = SettingToServer().businessStatusToServer(startTime, endTime)

                    // 결과 처리
                    if (result.first) {

                    } else {
                        JOptionPane.showMessageDialog(null, "임시정지 실패: ${result.second}", "오류", JOptionPane.ERROR_MESSAGE)
                    }
                    println(json)
                } else {
//                    println("사용자가 취소를 클릭했습니다.")
                }
                callback(confirmed)
            }
        )
    }

    private fun determineSelectedType(): String {
        return if (parentDialog.thirtyMinutePanel.background == MyColor.DARK_NAVY) {
            "30분 단위"
        } else {
            "시간 지정"
        }
    }

    private fun calculateStartAndEndTime(selectedType: String, currentTime: LocalDateTime): Pair<LocalDateTime, LocalDateTime?> {
        var startTime = currentTime
        var endTime: LocalDateTime? = null

        if (selectedType == "30분 단위") {
            endTime = startTime.plusMinutes(parentDialog.operatePauseFirst.toLong())
        } else {
            val selectedHour = parentDialog.hourComboBox.selectedIndex
            val selectedMinute = parentDialog.minuteComboBox.selectedIndex * 5

            if (selectedHour == 0) return Pair(startTime, null)

            val hour24 = if (parentDialog.amButton.backgroundColor == MyColor.DARK_RED) {
                selectedHour
            } else {
                selectedHour + 12
            }

            endTime = LocalDateTime.of(
                currentTime.year,
                currentTime.month,
                currentTime.dayOfMonth,
                hour24,
                selectedMinute
            )

            if (endTime.isBefore(startTime)) {
                endTime = endTime.plusDays(1)
            }
        }
        return Pair(startTime, endTime)
    }

    private fun formatDateTime(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("M월 d일 HH시 mm분")
        return dateTime.format(formatter)
    }
}
