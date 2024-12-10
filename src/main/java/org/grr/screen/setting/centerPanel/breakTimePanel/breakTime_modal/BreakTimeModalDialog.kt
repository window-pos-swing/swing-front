package org.grr.screen.setting.centerPanel.breakTimePanel.breakTime_modal

import CustomRoundedDialog
import CustomToggleButton3
import org.grr.screen.setting.centerPanel.breakTimePanel.breakTime_modal.allDays.AllDays
import org.grr.screen.setting.centerPanel.breakTimePanel.breakTime_modal.selectByDay.SelectByDays
import org.grr.screen.setting.centerPanel.breakTimePanel.breakTime_modal.weekDaysAndWeekEnds.WeekDaysAndWeekEnds
import org.grr.util.MyFont
import org.grr.widgets.IconRoundBorder2
import org.grr.widgets.RoundedButton
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*

class BreakTimeModalDialog(
    parent: JFrame,
    title: String,
    callback: ((Boolean) -> Unit)? = null
) : CustomRoundedDialog(parent, title, 1000, 700, callback) {

    private val breakTimeDataList = mutableListOf<BreakTimeData>()
    val selectThis = arrayOf("전체요일", "평일", "주말", "월", "화", "수", "목", "금", "토", "일")
    private var currentPanel: JPanel? = null
    private val bottomPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE
        isOpaque = true
    }

    init {
        setSize(1000, 700)  // 다이얼로그 크기 설정
        setLocationRelativeTo(parent)
        background = Color.WHITE

        // 중앙 패널
        val centerPanel = JPanel(BorderLayout()).apply {
            background = Color.LIGHT_GRAY
            isOpaque = true
        }

        // CustomToggleButton3 생성 및 선택 변경 시 동작 정의
        val toggleButton = CustomToggleButton3 { selectedIndex ->
            centerPanel.removeAll() // 기존 패널 제거

            // 선택된 인덱스에 따라 새로운 패널 추가
            currentPanel = when (selectedIndex) {
                0 -> AllDays { labelText, timeRangeText ->
                    addBottomPanel(labelText, timeRangeText)
                }
                1 -> WeekDaysAndWeekEnds { labelText, timeRangeText ->
                    addBottomPanel(labelText, timeRangeText)
                }
                2 -> SelectByDays { labelText, timeRangeText ->
                    addBottomPanel(labelText, timeRangeText)
                }
                else -> JPanel()
            }

            currentPanel?.let { centerPanel.add(it, BorderLayout.CENTER) }
            centerPanel.revalidate()
            centerPanel.repaint()
        }.apply {
            preferredSize = Dimension(680, 50)
        }

        // 초기 패널 설정 (프로그램 시작 시 기본 화면)
        currentPanel = AllDays { labelText, timeRangeText ->
            addBottomPanel(labelText, timeRangeText)
        }.apply {
            background = Color.WHITE
            isOpaque = true
        }
        centerPanel.add(currentPanel, BorderLayout.CENTER)

        // 상단 패널
        val topPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER)
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(20, 15, 0, 15)
            add(toggleButton)
        }

        // 하단 패널 (스크롤 가능)
        val scrollPane = JScrollPane(bottomPanel).apply {
            background = Color.WHITE
            isOpaque = true
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            preferredSize = Dimension(940, 200)
            border = BorderFactory.createEmptyBorder()
        }

        // 설정 저장 버튼
        val saveButton = JButton("설정 저장").apply {
            background = Color(27, 43, 66)
            foreground = Color.WHITE
            font = MyFont.Bold(26f)
            isOpaque = true
            isBorderPainted = false
            preferredSize = Dimension(300, 60)

            addActionListener {
                saveBreakTime()
            }
        }

//        저장버튼 가운데 정렬
        val centerSaveButton = JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER)
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(0, 15, 30, 15)  // 패널 마진 추가 (좌우 15, 하단 30)
            add(saveButton)
        }

        // 하단 영역 통합 패널
        val bottomContainerPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            isOpaque = true
            add(scrollPane) // 스크롤 패널 추가
            add(Box.createVerticalStrut(10))
            add(centerSaveButton) // 저장 버튼 추가
        }

        // 메인 패널 구성
        val contentPanel = JPanel(BorderLayout()).apply {
            background = Color.WHITE
            isOpaque = true
            add(topPanel, BorderLayout.NORTH)
            add(centerPanel, BorderLayout.CENTER)
            add(bottomContainerPanel, BorderLayout.PAGE_END)
        }

        add(contentPanel)
        isVisible = true
    }

    private fun addBottomPanel(labelText: String, timeRangeText: String) {
        lateinit var itemPanel: JPanel

        if (breakTimeDataList.any { it.labelText == labelText && it.timeRangeText == timeRangeText }) {
            JOptionPane.showMessageDialog(this, "이미 선택된 요일과 시간입니다.")
            return
        }

        val breakTimeData = BreakTimeData(labelText, timeRangeText)
        breakTimeDataList.add(breakTimeData)

        itemPanel = JPanel().apply {
            preferredSize = Dimension(940, 60)
            maximumSize = Dimension(940, 60)
            minimumSize = Dimension(940, 60)
            layout = BorderLayout()
            background = Color.WHITE
            border = BorderFactory.createLineBorder(Color.GRAY, 1)

            val label = IconRoundBorder2.createRoundedLabel(labelText, Color(255, 177, 177), 20).apply {
                foreground = Color.WHITE
                preferredSize = Dimension(150, 40)
                font = MyFont.Bold(20f)
            }

            val timeLabel = JLabel(timeRangeText).apply { font = MyFont.Bold(24f) }

            val deleteButton = RoundedButton("삭제").apply {
                font = MyFont.Bold(18f)
                preferredSize = Dimension(100, 35)
                addActionListener {
                    bottomPanel.remove(itemPanel)
                    bottomPanel.revalidate()
                    bottomPanel.repaint()

                    breakTimeDataList.remove(breakTimeData)
                    println("삭제된 데이터: $breakTimeData")
                    printAllBreakTimes()
                }
            }

            val centerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 10, 10)).apply {
                background = Color.WHITE
                add(label)
                add(timeLabel)
            }

            val rightPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 10, 10)).apply {
                background = Color.WHITE
                add(deleteButton)
            }

            add(centerPanel, BorderLayout.CENTER)
            add(rightPanel, BorderLayout.EAST)
        }

        bottomPanel.add(itemPanel)
        bottomPanel.revalidate() // 레이아웃 다시 계산
        bottomPanel.repaint()    // 화면 다시 그리기

        println("현재 저장된 데이터:")
        printAllBreakTimes()
    }

    private fun printAllBreakTimes() {
        if (breakTimeDataList.isEmpty()) {
            println("저장된 브레이크 타임 데이터가 없습니다.")
        } else {
            println("저장된 브레이크 타임 데이터:")
            breakTimeDataList.forEach { println("요일: ${it.labelText}, 시간: ${it.timeRangeText}") }
        }
    }

    private fun saveBreakTime() {
        println("저장된 데이터:")
        for (component in bottomPanel.components) {
            if (component is JPanel) {
                val centerPanel = component.getComponent(0) as? JPanel
                val labels = centerPanel?.components?.filterIsInstance<JLabel>()
                labels?.forEach { println(it.text) }
            }
        }
    }
}