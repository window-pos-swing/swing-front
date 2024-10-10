package org.example.screen.setting.centerPanel.holidayPanel.holiday_modal.regularHoliday

import RoundedComboBox
import RoundedComboBox2
import org.example.MyFont
import org.example.widgets.IconRoundBorder
import org.example.widgets.IconRoundBorder2
import org.example.widgets.PlusMinusButton
import java.awt.*
import javax.swing.*

class RegularHoliday: JPanel() {
    private val holidayListPanel = JPanel(GridBagLayout())  // 정기 휴무 리스트를 추가할 패널
    private var holidayCount = 0

    init {
        layout = GridBagLayout()
        background = Color.WHITE
        border = null

        val gbc = GridBagConstraints().apply {
            gridy = 0
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(5, 5, 5, 5)
        }

        // 첫 번째 행 - 요일 및 주차 선택
        val weekComboBox = RoundedComboBox(DefaultComboBoxModel(arrayOf("매월 첫째", "매월 둘째", "매월 셋째", "매월 넷째")), Color(13, 130, 191), 2f).apply {
            preferredSize = Dimension(160, 50)
            font = MyFont.Bold(18f)
        }

        gbc.gridx = 0
        add(weekComboBox, gbc)

        val dayComboBox = RoundedComboBox(DefaultComboBoxModel(arrayOf("월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일")), Color(13, 130, 191), 2f).apply {
            preferredSize = Dimension(160, 50)
            font = MyFont.Bold(18f)
        }

        gbc.gridx = 1
        add(dayComboBox, gbc)

        val plusButton = IconRoundBorder2.createRoundedButton("/+.png", Color(13, 130, 191)).apply {
            preferredSize = Dimension(50, 50)
            background = Color.BLACK
            addActionListener {
                if (holidayCount >= 3) {
                    JOptionPane.showMessageDialog(null, "최대 3개의 정기 휴무만 설정할 수 있습니다.")
                    return@addActionListener
                }
                // 버튼이 눌렸을 때 선택된 값 출력
                val selectedWeek = weekComboBox.selectedItem as String
                val selectedDay = dayComboBox.selectedItem as String
                addHolidayItem(selectedWeek, selectedDay)
            }
        }

        // 추가 버튼
        gbc.gridx = 2
        gbc.fill = GridBagConstraints.NONE
        add(plusButton, gbc)

        // 정기 휴무 리스트 패널 추가
        gbc.gridy = 1
        gbc.gridx = 0
        gbc.gridwidth = 3
        gbc.fill = GridBagConstraints.HORIZONTAL
        add(holidayListPanel, gbc)

        holidayListPanel.background = Color.WHITE
    }

    // 선택된 항목을 UI에 추가하는 메서드
    private fun addHolidayItem(week: String, day: String) {
        val gbc = GridBagConstraints().apply {
            gridy = holidayCount
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(5, 5, 5, 5)
        }

        val weekLabel = RoundedComboBox2(week, Color(255, 177, 177), 2f)
        gbc.gridx = 0  // 첫 번째 열
        gbc.weightx = 0.5  // 수평 공간 배분
        holidayListPanel.add(weekLabel, gbc)

        val weekLabel2 = RoundedComboBox2(day, Color(255, 177, 177), 2f)
        gbc.gridx = 1  // 두 번째 열
        gbc.weightx = 0.5  // 수평 공간 배분
        holidayListPanel.add(weekLabel2, gbc)

        // 삭제 버튼 추가
        gbc.gridx = 2  // 세 번째 열
        gbc.weightx = 0.0  // 버튼은 크기를 고정
        val removeButton = IconRoundBorder2.createRoundedButton("/x.png", Color(255, 177, 177)).apply {
            preferredSize = Dimension(50, 50)
            background = Color.BLACK
            addActionListener {
                holidayListPanel.remove(weekLabel)
                holidayListPanel.remove(weekLabel2)
                holidayListPanel.remove(this)
                holidayCount--
                updateHolidayItems()
            }
        }

        holidayListPanel.add(removeButton, gbc)

        holidayListPanel.revalidate()
        holidayListPanel.repaint()
        holidayCount++
    }

    // 전체 항목의 레이아웃을 다시 설정하는 메서드
    private fun updateHolidayItems() {
        val components = holidayListPanel.components
        holidayListPanel.removeAll()  // 기존 항목 모두 제거
        holidayCount = 0  // 항목 개수 초기화

        // 다시 항목을 추가하며 레이아웃 갱신
        for (i in components.indices step 3) {  // 3개의 컴포넌트 (weekLabel, weekLabel2, removeButton)를 한 세트로 처리
            val weekLabel = components[i] as RoundedComboBox2  // 주차 라벨
            val dayLabel = components[i + 1] as RoundedComboBox2  // 요일 라벨
            val removeButton = components[i + 2] as JButton  // 삭제 버튼
            val gbc = GridBagConstraints().apply {
                gridy = holidayCount  // 각 행의 y 위치를 증가시킴
                insets = Insets(5, 5, 5, 5)  // 컴포넌트 간 여백 설정
                fill = GridBagConstraints.HORIZONTAL  // 수평으로 확장 가능하게 설정
                weightx = 1.0  // 여백을 모두 채우도록 설정
            }

            // 주차 라벨 다시 추가
            gbc.gridx = 0  // 첫 번째 열
            gbc.weightx = 0.5  // 수평 공간 배분
            holidayListPanel.add(weekLabel, gbc)

            // 요일 라벨 다시 추가
            gbc.gridx = 1  // 두 번째 열
            gbc.weightx = 0.5  // 수평 공간 배분
            holidayListPanel.add(dayLabel, gbc)

            // 삭제 버튼 다시 추가
            gbc.gridx = 2  // 세 번째 열
            gbc.weightx = 0.0  // 버튼은 크기를 고정
            gbc.fill = GridBagConstraints.NONE  // 버튼 크기 고정
            holidayListPanel.add(removeButton, gbc)

            holidayCount++  // 항목 수 증가
        }

        // 패널 레이아웃을 다시 계산하고 새로 고침
        holidayListPanel.revalidate()
        holidayListPanel.repaint()
    }
}