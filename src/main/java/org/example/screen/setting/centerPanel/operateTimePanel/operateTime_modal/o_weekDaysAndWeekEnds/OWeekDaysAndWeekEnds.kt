package org.example.screen.setting.centerPanel.operateTimePanel.operateTime_modal.o_weekDaysAndWeekEnds

import RoundedComboBox
import org.example.style.MyColor
import org.example.util.MyFont
import org.example.widgets.IconRoundBorder2
import org.example.widgets.IconRoundBorder3
import org.example.widgets.RoundButton2
import org.example.widgets.RoundedButton
import java.awt.*
import javax.swing.*

class OWeekDaysAndWeekEnds : JPanel() {
    private var isWeekDays = false
    private var isWeekEnds = false
    private var startHourCombo: JComboBox<String>
    private var startMinCombo: JComboBox<String>
    private var endHourCombo: JComboBox<String>
    private var endMinCombo: JComboBox<String>
    private val dayButtons = mutableListOf<RoundButton2>()
    private var selectedDays = mutableSetOf<String>()
    private val selectedDay2 = mutableSetOf<String>()
    private val bottomPanel: JPanel = JPanel()

    private var is24HoursSelected = false
    lateinit var timePanel: JPanel

    val selectThis = arrayOf("평일", "주말")

    init {
        layout = BorderLayout()
        background = Color.WHITE

//        평일/주말 버튼과 시간 선택, 추가하기 버튼을 하나의 패널로 묶음
        val topPanel = JPanel().apply {
            preferredSize = Dimension(940, 250)
            maximumSize = Dimension(940, 250)
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 0),
                BorderFactory.createLineBorder(MyColor.LIGHT_GREY, 1)
            )

//            상단 평일 / 주말 선택 버튼 패널
            val WeekDaysAndWeekEndsPanel = JPanel().apply {
                layout = FlowLayout(FlowLayout.CENTER, 20, 15)
                isOpaque = false

                for (select in selectThis) {
                    val selectButton = RoundButton2(select).apply {
                        isClickable = true
                        foreground = Color(255, 177, 177)
                        font = MyFont.Bold(20f)

                        addActionListener {
                            if (selectedDay2.contains(select) && !selectedDays.contains(select)) {
                                JOptionPane.showMessageDialog(this@OWeekDaysAndWeekEnds, "이미 선택된 요일입니다.")
                                setSelected(true)
                                return@addActionListener // 추가하지 않고 종료
                            }

                            if (selectedDays.contains(select)) {
                                selectedDays.remove(select)
                                setSelected(false)
                                foreground = Color(255, 177, 177)
                            } else {
                                selectedDays.add(select)
                                setSelected(true)
                                foreground = Color.GRAY
                            }
                        }
                    }
                    dayButtons.add(selectButton)
                    add(selectButton)
                }
            }

//            시간 선택 패널
            timePanel = JPanel().apply {
                layout = FlowLayout(FlowLayout.CENTER, 10, 0)
                background = Color.WHITE

                startHourCombo = RoundedComboBox(
                    DefaultComboBoxModel(
                        arrayOf(
                            "오전 0시",
                            "오전 1시",
                            "오전 2시",
                            "오전 3시",
                            "오전 4시",
                            "오전 5시",
                            "오전 6시",
                            "오전 7시",
                            "오전 8시",
                            "오전 9시",
                            "오전 10시",
                            "오전 11시",
                            "오후 12시",
                            "오후 1시",
                            "오후 2시",
                            "오후 3시",
                            "오후 4시",
                            "오후 5시",
                            "오후 6시",
                            "오후 7시",
                            "오후 8시",
                            "오후 9시",
                            "오후 10시",
                            "오후 11시"
                        )
                    )
                ).apply {
                    preferredSize = Dimension(205, 50)
                    maximumSize = Dimension(205, 50)
                    minimumSize = Dimension(205, 50)
                    font = MyFont.Bold(20f)
                }

                startMinCombo = RoundedComboBox(DefaultComboBoxModel(arrayOf("00분", "15분", "30분", "45분"))).apply {
                    preferredSize = Dimension(125, 50)
                    maximumSize = Dimension(125, 50)
                    minimumSize = Dimension(125, 50)
                    font = MyFont.Bold(20f)
                }

                endHourCombo = RoundedComboBox(
                    DefaultComboBoxModel(
                        arrayOf(
                            "오전 0시",
                            "오전 1시",
                            "오전 2시",
                            "오전 3시",
                            "오전 4시",
                            "오전 5시",
                            "오전 6시",
                            "오전 7시",
                            "오전 8시",
                            "오전 9시",
                            "오전 10시",
                            "오전 11시",
                            "오후 12시",
                            "오후 1시",
                            "오후 2시",
                            "오후 3시",
                            "오후 4시",
                            "오후 5시",
                            "오후 6시",
                            "오후 7시",
                            "오후 8시",
                            "오후 9시",
                            "오후 10시",
                            "오후 11시"
                        )
                    )
                ).apply {
                    preferredSize = Dimension(205, 50)
                    maximumSize = Dimension(205, 50)
                    minimumSize = Dimension(205, 50)
                    font = MyFont.Bold(20f)
                }

                endMinCombo = RoundedComboBox(DefaultComboBoxModel(arrayOf("00분", "15분", "30분", "45분"))).apply {
                    preferredSize = Dimension(125, 50)
                    maximumSize = Dimension(125, 50)
                    minimumSize = Dimension(125, 50)
                    font = MyFont.Bold(20f)
                }

                val dayButton = IconRoundBorder3.createRoundedButton("24시간", Color(255, 177, 177), 30).apply {
                    foreground = Color.WHITE
                    preferredSize = Dimension(95, 50)

                    addActionListener {
                        // 24시간 버튼 클릭 시 패널 비활성화/활성화 토글
                        is24HoursSelected = !is24HoursSelected
                        setTimePanelEnabled(!is24HoursSelected, timePanel, this)
                    }
                }

                add(startHourCombo)
                add(startMinCombo)
                add(JLabel("~").apply { font = MyFont.Bold(24f) })
                add(endHourCombo)
                add(endMinCombo)
                add(dayButton)
            }

            // 추가 버튼
            val addButton = RoundedButton("추가하기").apply {
                preferredSize = Dimension(150, 40)
                maximumSize = Dimension(150, 40)
                minimumSize = Dimension(150, 40)
                alignmentX = Component.CENTER_ALIGNMENT
                font = MyFont.Bold(18f)

                addActionListener {
                    if (selectedDays.contains("평일") && isWeekDays) {
                        JOptionPane.showMessageDialog(this@OWeekDaysAndWeekEnds, "평일은 이미 추가되었습니다.")
                        return@addActionListener
                    }
                    if (selectedDays.contains("주말") && isWeekEnds) {
                        JOptionPane.showMessageDialog(this@OWeekDaysAndWeekEnds, "주말은 이미 추가되었습니다.")
                        return@addActionListener
                    }

                    // 선택된 요일이 없으면 경고 메시지
                    if (selectedDays.isEmpty()) {
                        JOptionPane.showMessageDialog(this@OWeekDaysAndWeekEnds, "적어도 하나의 버튼을 선택해야 합니다.")
                        return@addActionListener
                    }

                    val selectButtonText = selectedDays.joinToString(", ") { it }

                    // 24시간 선택 여부에 따라 timeRangeText 생성
                    val timeRangeText = if (is24HoursSelected) {
                        "24시간"
                    } else {
                        val startHour = startHourCombo.selectedItem?.toString() ?: "오전 0시"
                        val startMin = startMinCombo.selectedItem?.toString() ?: "00분"
                        val endHour = endHourCombo.selectedItem?.toString() ?: "오전 0시"
                        val endMin = endMinCombo.selectedItem?.toString() ?: "00분"
                        "$startHour $startMin ~ $endHour $endMin"
                    }
//                    println("선택된 시간: $timeRangeText")
                    addButtonPanel(selectButtonText, timeRangeText) // 하단 패널에 추가
                    selectedDay2.addAll(selectedDays)

                    // 평일/주말 상태 설정
                    if (selectedDays.contains("평일")) {
                        isWeekDays = true
                    }
                    if (selectedDays.contains("주말")) {
                        isWeekEnds = true
                    }

                    selectedDays.clear()
                }
            }

            // 패널에 추가
            add(WeekDaysAndWeekEndsPanel)
            add(Box.createVerticalStrut(10))
            add(timePanel)
            add(addButton)
            add(Box.createVerticalStrut(20))
        }

        // 하단 패널 설정
        bottomPanel.layout = BoxLayout(bottomPanel, BoxLayout.Y_AXIS)
        bottomPanel.background = Color.WHITE

        // 메인 패널 구성
        val mainPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            add(topPanel)
//            add(Box.createVerticalStrut(10))
            add(bottomPanel)
        }

        add(mainPanel, BorderLayout.CENTER)
    }

    // 시간 선택 패널 활성화/비활성화 함수 (24시간 버튼은 제외)
    private fun setTimePanelEnabled(enabled: Boolean, panel: JPanel, excludeComponent: Component) {
        for (component in panel.components) {
            if (component != excludeComponent) {
                component.isEnabled = enabled
            }
        }
        panel.revalidate()
        panel.repaint()
    }

    private fun addButtonPanel(selectedButtonText: String, timeRangeText: String) {
        lateinit var itemPanel: JPanel

        itemPanel = JPanel().apply {
            preferredSize = Dimension(940, 60)
            maximumSize = Dimension(940, 60)
            minimumSize = Dimension(940, 60)
            layout = BorderLayout()
            background = Color.WHITE
            border = BorderFactory.createLineBorder(Color.GRAY, 1)

            val allDaysLabel = IconRoundBorder2.createRoundedLabel(selectedButtonText, Color(255, 177, 177), 20).apply {
                foreground = Color.WHITE
                preferredSize = Dimension(150, 40)
            }

            val timeRangeLabel = JLabel(timeRangeText).apply {
                font = MyFont.Bold(24f)
            }

            val deleteButton = RoundedButton("삭제").apply {
                font = MyFont.Bold(18f)
                preferredSize = Dimension(100, 35)


                addActionListener {
                    bottomPanel.remove(itemPanel)
                    bottomPanel.revalidate()
                    bottomPanel.repaint()

                    val selectedButtonTextList = selectedButtonText.split(", ")

                    selectedButtonTextList.forEach { selectedButton ->

                        if (selectedDay2.contains(selectedButton)) {
                            selectedDay2.remove(selectedButton)
                        }

                        selectedDays.remove(selectedButton)
                        dayButtons.find { it.text == selectedButton }?.let { button ->
                            button.isEnabled = true
                            button.foreground = Color(255, 177, 177) // 원래 색상으로 복원
                            button.background = Color(255, 177, 177)
                            button.setSelected(true)
                        }

                        // '평일'과 '주말' 상태 구분하여 변경
                        if (selectedButton.contains("평일")) {
                            // 다른 항목에 '평일'이 포함되어 있지 않은 경우에만 isWeekDays를 false로 변경
                            if (!selectedDay2.any { it.contains("평일") }) {
                                isWeekDays = false
                            }
                        }
                        if (selectedButton.contains("주말")) {
                            // 다른 항목에 '주말'이 포함되어 있지 않은 경우에만 isWeekEnds를 false로 변경
                            if (!selectedDay2.any { it.contains("주말") }) {
                                isWeekEnds = false
                            }
                        }
                    }
                }
            }

            val rightPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 10, 10)).apply {
                background = Color.WHITE
                add(deleteButton)
            }

            val centerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 10, 10)).apply {
                background = Color.WHITE
                add(allDaysLabel)
                add(timeRangeLabel)
            }

            add(centerPanel, BorderLayout.CENTER)
            add(rightPanel, BorderLayout.EAST)
        }
        bottomPanel.add(itemPanel)
        bottomPanel.revalidate()
        bottomPanel.repaint()
    }
}