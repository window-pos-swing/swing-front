package org.grr.screen.setting.centerPanel.holidayPanel.holiday_modal

import CustomRoundedDialog
import org.grr.model.SettingModel
import org.grr.util.MyFont
import org.grr.screen.setting.centerPanel.holidayPanel.holiday_modal.regularHoliday.RegularHoliday
import org.grr.screen.setting.centerPanel.holidayPanel.holiday_modal.temporaryHoliday.TemporaryHoliday
import org.grr.style.MyColor
import java.awt.*
import javax.swing.*

class HolidayModalDialog(parent: JFrame, title: String, callback: ((Boolean) -> Unit)? = null) : CustomRoundedDialog(
    parent,
    title,
    1000,
    580,
    callback
) {
    init {
        setSize(1000, 580)  // 다이얼로그 크기 설정
        setLocationRelativeTo(parent)

        // 설정 패널 구성 (전체 레이아웃은 GridBagLayout)
        val mainPanel = JPanel(GridBagLayout()).apply {
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(20, 15, 30, 15)  // 패널 마진 추가 (좌우 20, 상하 20)
        }

        val gbc = GridBagConstraints()

        // 정기 휴무 패널과 텍스트 묶기
        val regularHolidayPanel = JPanel(BorderLayout()).apply {
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }

        val regularHolidayLabel = JLabel("정기 휴무 설정").apply {
            font = MyFont.Bold(24f)
            horizontalAlignment = SwingConstants.CENTER
        }
        regularHolidayPanel.add(regularHolidayLabel, BorderLayout.NORTH)

        // 정기 휴무 패널
        val regularHoliday = RegularHoliday()
        regularHolidayPanel.add(regularHoliday, BorderLayout.CENTER)

        //기존 저장된 정기휴무 초기화
        val regularHolidayData = SettingModel.regularHoliday.split("\n").filter { it.isNotBlank() }

        for (line in regularHolidayData) {
            val parts = line.split(":").map { it.trim() } // "매월 셋째: 금요일" 같은 형식

            if (parts.size == 2) {
                val week = parts[0] // "매월 셋째"
                val day = parts[1]  // "금요일"

                regularHoliday.addHolidayItem(week, day)
            }
        }

        // 임시 휴무 패널과 텍스트 묶기
        val temporaryHolidayPanel = JPanel(BorderLayout()).apply {
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }

        val temporaryHolidayLabel = JLabel("임시 휴무 설정").apply {
            font = MyFont.Bold(24f)
            horizontalAlignment = SwingConstants.CENTER
        }
        temporaryHolidayPanel.add(temporaryHolidayLabel, BorderLayout.NORTH)

        // 임시 휴무 패널
        val temporaryHoliday = TemporaryHoliday()
        temporaryHolidayPanel.add(temporaryHoliday, BorderLayout.CENTER)

        val temporaryHolidayData = SettingModel.temporaryHoliday

        // "임시: 2024년 12월 25일 ~ 2025년 1월 1일" 형태 파싱
        val pattern = Regex("""(\d{4})년 (\d{1,2})월 (\d{1,2})일 ~ (\d{4})년 (\d{1,2})월 (\d{1,2})일""")
        val matchResult = pattern.find(temporaryHolidayData)

        if (matchResult != null) {
            val (startYear, startMonth, startDay, endYear, endMonth, endDay) = matchResult.destructured

            // 날짜를 "YYYY-MM-DD" 형식으로 변환
            val startDate = "$startYear-${startMonth.padStart(2, '0')}-${startDay.padStart(2, '0')}"
            val endDate = "$endYear-${endMonth.padStart(2, '0')}-${endDay.padStart(2, '0')}"

            // addTemporaryHoliday 호출
            temporaryHoliday.addTemporaryHoliday(startDate, endDate)
        } else {
            println("임시 휴무일 데이터를 파싱할 수 없습니다: $temporaryHolidayData")
        }

        // 정기 휴무 패널을 mainPanel에 추가
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridwidth = 1
        gbc.weightx = 0.48
        gbc.weighty = 1.0
        gbc.fill = GridBagConstraints.BOTH
        gbc.insets = Insets(10, 10, 10, 0)
        mainPanel.add(regularHolidayPanel, gbc)

        // 세로 경계선
        gbc.gridx = 1
        gbc.gridy = 0
        gbc.gridwidth = 1
        gbc.weightx = 0.04
        gbc.insets = Insets(10, 0, 10, 0)
        mainPanel.add(createSeparator(SwingConstants.VERTICAL, 4, 340), gbc)

        // 임시 휴무 패널을 mainPanel에 추가
        gbc.gridx = 2
        gbc.gridy = 0
        gbc.gridwidth = 1
        gbc.weightx = 0.48
        gbc.weighty = 1.0
        gbc.insets = Insets(10, 0, 10, 10)
        gbc.fill = GridBagConstraints.BOTH
        mainPanel.add(temporaryHolidayPanel, gbc)

        // 설정 저장 버튼
        val saveButton = JButton("설정 저장").apply {
            background = Color(27, 43, 66)
            foreground = Color.WHITE
            font = MyFont.Bold(26f)
            isOpaque = true
            isBorderPainted = false
            preferredSize = Dimension(300, 60)
        }

        // 버튼 추가
        gbc.gridx = 0
        gbc.gridy = 1
        gbc.gridwidth = 3
        gbc.weightx = 1.0
        gbc.weighty = 0.0
        gbc.fill = GridBagConstraints.NONE
        gbc.anchor = GridBagConstraints.CENTER
        gbc.insets = Insets(20, 0, 0, 0)
        mainPanel.add(saveButton, gbc)

        val contentPanel = JPanel(BorderLayout()).apply {
            add(mainPanel, BorderLayout.CENTER)
        }

        add(contentPanel)

        isVisible = true
    }


    private fun createSeparator(orientation: Int, width: Int, height: Int): JSeparator {
        return JSeparator(orientation).apply {
            foreground = MyColor.LIGHT_GREY
            preferredSize = Dimension(width, height)
            maximumSize = Dimension(width, height)
            minimumSize = Dimension(width, height)
        }
    }

}