package org.example.screen.setting.centerPanel.breakTimePanel.breakTime_modal

import CustomRoundedDialog
import CustomToggleButton3
import org.example.MyFont
import org.example.screen.setting.centerPanel.breakTimePanel.breakTime_modal.allDays.AllDays
import org.example.screen.setting.centerPanel.breakTimePanel.breakTime_modal.selectByDay.SelectByDays
import org.example.screen.setting.centerPanel.breakTimePanel.breakTime_modal.weekDaysAndWeekEnds.WeekDaysAndWeekEnds
import java.awt.*
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel

class BreakTimeModalDialog(parent: JFrame, title: String, callback: ((Boolean) -> Unit)? = null) : CustomRoundedDialog(
    parent,
    title,
    1000,
    700,
    callback
)  {
    init {
        setSize(1000, 700)  // 다이얼로그 크기 설정
        setLocationRelativeTo(parent)

        // 중앙 패널
        val centerPanel = JPanel(BorderLayout()).apply {
            background = Color.LIGHT_GRAY
            isOpaque = true
            preferredSize = Dimension(700, 70) // 패널 크기 설정
        }

        // CustomToggleButton3 생성 및 선택 변경 시 동작 정의
        val toggleButton = CustomToggleButton3 { selectedIndex ->
            centerPanel.removeAll() // 기존 패널 제거

            // 선택된 인덱스에 따라 새로운 패널 추가
            val selectedPanel = when (selectedIndex) {
                0 -> AllDays() // 전체 선택 패널
                1 -> WeekDaysAndWeekEnds() // 평일/주말 선택 패널
                2 -> SelectByDays() // 요일별 선택 패널
                else -> JPanel()
            }

            // 새로운 패널을 중앙에 추가
            centerPanel.add(selectedPanel, BorderLayout.CENTER)
            centerPanel.revalidate() // 레이아웃 갱신
            centerPanel.repaint() // 화면 다시 그리기
        }.apply {
            preferredSize = Dimension(680, 50)
            maximumSize = Dimension(680, 50)
            minimumSize = Dimension(680, 50)
        }

        // 초기 패널 설정 (프로그램 시작 시 기본 화면)
        centerPanel.add(AllDays(), BorderLayout.CENTER)

        // 상단 패널 (CustomToggleButton3 버튼 포함)
        val topPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER)
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(20, 15, 0, 15)  // 패널 마진 추가 (좌우 15, 상단 20)
            add(toggleButton)
        }

        // 설정 저장 버튼 (하단에 고정)
        val saveButton = JButton("설정 저장").apply {
            background = Color(27, 43, 66)
            foreground = Color.WHITE
            font = MyFont.Bold(26f)
            isOpaque = true
            isBorderPainted = false
            preferredSize = Dimension(300, 60)
        }

        val bottomPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER)
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(0, 15, 30, 15)  // 패널 마진 추가 (좌우 15, 하단 30)
            add(saveButton)
        }

        // 메인 패널 구성
        val contentPanel = JPanel(BorderLayout()).apply {
            isOpaque = false
            add(topPanel, BorderLayout.NORTH)    // 상단에 고정
            add(centerPanel, BorderLayout.CENTER) // 중앙 패널 추가
            add(bottomPanel, BorderLayout.SOUTH) // 하단에 고정
        }

        add(contentPanel)

        isVisible = true
    }
}