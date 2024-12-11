package org.grr.screen.setting.centerPanel.breakTimePanel.breakTime_modal.selectByDay

import RoundedComboBox
import org.grr.`object`.TimeManager
import org.grr.screen.setting.centerPanel.breakTimePanel.breakTime_modal.ShareButton
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.RoundButton
import org.grr.widgets.RoundedButton
import java.awt.*
import javax.swing.*

class SelectByDays(
    private val onAdd: (String, String) -> Unit
) : JPanel() {
    private var itemCount = 0
    private var startHourCombo: JComboBox<String>
    private var startMinCombo: JComboBox<String>
    private var endHourCombo: JComboBox<String>
    private var endMinCombo: JComboBox<String>
    val days = arrayOf("월", "화", "수", "목", "금", "토", "일")

    init {
        layout = BorderLayout()
        background = Color.WHITE

        // 요일 버튼과 시간 선택을 하나의 패널로 묶음
        val dayAndTimePanel = JPanel().apply {
            preferredSize = Dimension(940, 250)
            maximumSize = Dimension(940, 250)
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 0),
                BorderFactory.createLineBorder(MyColor.LIGHT_GREY, 1)
            )

            // 상단 요일 버튼 패널
            val dayPanel = JPanel().apply {
                layout = FlowLayout(FlowLayout.CENTER, 20, 15)
                background = Color.WHITE

                for (day in days) {
                    val dayButton = RoundButton(day).apply {
                        isClickable = true

                        // 평일과 주말 요일 그룹
                        val weekdays = arrayOf("월", "화", "수", "목", "금")
                        val weekends = arrayOf("토", "일")

                        // 평일 또는 주말이 포함되어 있는지 확인
                        val isWeekdaysIncluded = ShareButton.selectedDay2.any { selectedDay ->
                            selectedDay.split(", ").map { it.trim() }.contains("평일")
                        }
                        val isWeekendsIncluded = ShareButton.selectedDay2.any { selectedDay ->
                            selectedDay.split(", ").map { it.trim() }.contains("주말")
                        }

                        val isWeekdaysIncluded2 = ShareButton.selectedDays.any { selectedDay ->
                            selectedDay.split(", ").map { it.trim() }.contains("평일")
                        }
                        val isWeekendsIncluded2 = ShareButton.selectedDays.any { selectedDay ->
                            selectedDay.split(", ").map { it.trim() }.contains("주말")
                        }

                        // 현재 요일이 포함 여부 확인
                        val isDayIncluded = ShareButton.selectedDay2.any { selectedDay ->
                            selectedDay.split(", ").map { it.trim() }.contains(day)
                        }

                        // 버튼 비활성화 조건
                        if ((isWeekdaysIncluded2 && weekdays.contains(day)) ||
                            (isWeekendsIncluded2 && weekends.contains(day)) ||
                            ShareButton.selectedDays.contains(day)
                        ) {
                            isSelected = false
                            foreground = Color.GRAY
                        } else if ((isWeekdaysIncluded && weekdays.contains(day)) ||
                            (isWeekendsIncluded && weekends.contains(day)) ||
                            isDayIncluded
                        ) {
                            isEnabled = false
                            foreground = Color.GRAY
                        } else {
                            foreground = Color(255, 177, 177)
                        }

                        font = MyFont.Bold(20f)

                        addActionListener {
                            if ((isWeekdaysIncluded2 && weekdays.contains(day)) ||
                                (isWeekendsIncluded2 && weekends.contains(day))
                            ) {
                                setSelected(true)
                                return@addActionListener
                            }

                            if (TimeManager.check(day.trim())) {
//                                JOptionPane.showMessageDialog(this, "선택된 요일과 충돌하는 항목이 이미 존재합니다.")
                                setSelected(true)
                                return@addActionListener
                            }

                            // 이미 추가된 요일일 경우 선택 불가능
                            if (ShareButton.selectedDay2.contains(day.trim())) {
//                                JOptionPane.showMessageDialog(this@SelectByDays, "이미 추가된 요일입니다.")
                                setSelected(true)
                                return@addActionListener // 추가하지 않고 종료
                            }

                            // 선택된 요일을 토글
                            if (ShareButton.selectedDays.contains(day)) {
                                ShareButton.selectedDays.remove(day)
                                setSelected(false)
                                foreground = Color(255, 177, 177)
                            } else {
                                ShareButton.selectedDays.add(day)
                                setSelected(true)
                                foreground = Color.GRAY
                            }
                        }
                    }
                    ShareButton.dayButtons.add(dayButton)
                    add(dayButton)
                }
            }

            // 시간 선택 패널
            val timePanel = JPanel().apply {
                layout = FlowLayout(FlowLayout.CENTER, 10, 10)
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

                add(startHourCombo)
                add(startMinCombo)
                add(JLabel("~").apply { font = MyFont.Bold(24f) })
                add(endHourCombo)
                add(endMinCombo)
            }

            // 추가 버튼
            val addButton = RoundedButton("추가하기").apply {
                preferredSize = Dimension(150, 40)
                maximumSize = Dimension(150, 40)
                minimumSize = Dimension(150, 40)
                alignmentX = Component.CENTER_ALIGNMENT
                font = MyFont.Bold(18f)

                addActionListener {
                    if (itemCount >= 7) {
                        JOptionPane.showMessageDialog(this@SelectByDays, "최대 7개의 항목만 추가할 수 있습니다.")
                        return@addActionListener
                    }

                    // selectedDays를 daysOrder의 순서에 맞게 정렬
                    val sortedSelectedDays = ShareButton.selectedDays.sortedWith(Comparator { day1, day2 ->
                        days.indexOf(day1) - days.indexOf(day2)
                    })

                    // 하단 패널에서 이미 추가된 항목이 있는지 검사
                    val daysText = sortedSelectedDays.joinToString(", ")

                    // 이미 추가된 요일이 있는지 검사
                    if (ShareButton.selectedDay2.any { day -> ShareButton.selectedDays.contains(day) }) {
                        JOptionPane.showMessageDialog(this@SelectByDays, "이미 추가된 요일이 있습니다.")
                        return@addActionListener
                    }

                    // 시간 선택 값 가져오기
                    val startHour = startHourCombo.selectedItem?.toString() ?: "오전 0시"
                    val startMin = startMinCombo.selectedItem?.toString() ?: "00분"
                    val endHour = endHourCombo.selectedItem?.toString() ?: "오전 0시"
                    val endMin = endMinCombo.selectedItem?.toString() ?: "00분"

                    // 선택된 시간과 요일을 사용하여 timeRangeText 생성
                    val timeRangeText = "$startHour $startMin ~ $endHour $endMin"
                    onAdd(daysText, timeRangeText)
                    ShareButton.selectedDay2.addAll(ShareButton.selectedDays)
                    ShareButton.selectedDays.clear()
                    itemCount++
                }
            }

            // 패널에 추가
            add(dayPanel)
            add(timePanel)
            add(addButton)
            add(Box.createVerticalStrut(20))
        }


        // 메인 패널 구성
        val mainPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            add(dayAndTimePanel)
        }

        add(mainPanel, BorderLayout.CENTER)
    }
}