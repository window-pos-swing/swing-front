package org.grr.screen.setting.centerPanel.operateTimePanel.operateTime_modal.o_weekDaysAndWeekEnds

import RoundedComboBox
import kotlinx.coroutines.NonDisposableHandle.dispose
import org.grr.`object`.TimeManager
import org.grr.screen.setting.centerPanel.breakTimePanel.breakTime_modal.ShareButton
import org.grr.screen.setting.centerPanel.breakTimePanel.breakTime_modal.ShareButton.selectedDays
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.*
import java.awt.*
import javax.swing.*

class OWeekDaysAndWeekEnds(
    private val onAdd: (String, String) -> Unit
) : JPanel() {
    private var isWeekDays = false
    private var isWeekEnds = false
    private var startHourCombo: JComboBox<String>
    private var startMinCombo: JComboBox<String>
    private var endHourCombo: JComboBox<String>
    private var endMinCombo: JComboBox<String>
    private var allDayButton: JButton
    val selectThis = arrayOf("평일", "주말")
    var isAllDaySelected = true // 버튼 상태를 저장하는 변수
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
                        font = MyFont.Bold(20f)

                        // 평일과 주말 요일 그룹
                        val weekdays = arrayOf("월", "화", "수", "목", "금")
                        val weekends = arrayOf("토", "일")

//                        각 요일이 포함되어 있는지 확인
                        val isWeekdaysIncluded = weekdays.any { day ->
                            TimeManager.breakTimeDataList.any { selectedDay ->
                                selectedDay.labelText.split(", ").map { it.trim() }.contains(day)
                            }
                        }
                        val isWeekendsIncluded = weekends.any { day ->
                            TimeManager.breakTimeDataList.any { selectedDay ->
                                selectedDay.labelText.split(", ").map { it.trim() }.contains(day)
                            }
                        }

                        val isWeekdaysIncluded2 = weekdays.any { day ->
                            selectedDays.any { selectedDay ->
                                selectedDay.split(", ").map { it.trim() }.contains(day)
                            }
                        }
                        val isWeekendsIncluded2 = weekends.any { day ->
                            selectedDays.any { selectedDay ->
                                selectedDay.split(", ").map { it.trim() }.contains(day)
                            }
                        }

//                        평일, 주말 포함 여부 확인
                        val isDayIncluded = TimeManager.breakTimeDataList.any { selectedDay ->
                            selectedDay.labelText.split(", ").map { it.trim() }.contains(select)
                        }

                        if ((select == "평일" && isWeekdaysIncluded2) ||
                            (select == "주말" && isWeekendsIncluded2) ||
                            selectedDays.contains(select)
                        ) {
                            isSelected = false
//                            isEnabled = false
                            foreground = Color.GRAY
                        } else if ((select == "평일" && isWeekdaysIncluded) ||
                            (select == "주말" && isWeekendsIncluded) ||
                            isDayIncluded ||
                            TimeManager.breakTimeDataList.any { it.labelText == "전체요일" }
                        ) {
                            isEnabled = false
                            foreground = Color.GRAY
                        } else {
                            foreground = Color(255, 177, 177)
                        }

                        addActionListener {
                            if ((select == "평일" && isWeekdaysIncluded2) ||
                                (select == "주말" && isWeekendsIncluded2)
                            ) {
                                setSelected(true)
                                return@addActionListener
                            }

                            if (TimeManager.breakTimeDataList.any { it.labelText == select } && select == "평일" ||
                                TimeManager.breakTimeDataList.any { it.labelText == select } && select == "주말") {
//                                JOptionPane.showMessageDialog(this, "선택된 요일과 충돌하는 항목이 이미 존재합니다.")
                                setSelected(true)
                                return@addActionListener
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
                    ShareButton.dayButtons2.add(selectButton)
                    add(selectButton)
                }
            }

//            시간 선택 패널
            val timePanel = JPanel().apply {
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

                allDayButton = FillRoundedButton(
                    text = "24시간",
                    borderColor = if(isAllDaySelected) MyColor.PINK else MyColor.GREY500,
                    backgroundColor = if(isAllDaySelected)  MyColor.PINK else MyColor.GREY500,
                    textColor = Color.WHITE,
                    borderRadius = 20,
                    borderWidth = 1,
                    textAlignment = SwingConstants.CENTER,
                    padding = Insets(10, 20, 10, 20),
                    buttonSize = Dimension(130, 50),
                    customFont = MyFont.Bold(22f)
                ).apply {
                    addActionListener {
                        isAllDaySelected = !isAllDaySelected // 상태를 토글
                        borderColor = if (isAllDaySelected) MyColor.PINK else MyColor.GREY500
                        backgroundColor = if (isAllDaySelected) MyColor.PINK else MyColor.GREY500
                        repaint()
                        if (isAllDaySelected) {
                            // 24시간 선택
                            startHourCombo.selectedIndex = 0 // 오전 0시
                            startMinCombo.selectedIndex = 0 // 00분
                            endHourCombo.selectedIndex = 0 // 오전 0시
                            endMinCombo.selectedIndex = 0 // 00분
                            startHourCombo.isEnabled = false
                            startMinCombo.isEnabled = false
                            endHourCombo.isEnabled = false
                            endMinCombo.isEnabled = false
                        } else {
                            // 24시간 선택 해제
                            startHourCombo.selectedIndex = 9 // 오전 9시
                            startMinCombo.selectedIndex = 0 // 00분
                            endHourCombo.selectedIndex = 18 // 오후 6시
                            endMinCombo.selectedIndex = 0 // 00분
                            startHourCombo.isEnabled = true
                            startMinCombo.isEnabled = true
                            endHourCombo.isEnabled = true
                            endMinCombo.isEnabled = true
                        }
                    }
                }

                add(startHourCombo)
                add(startMinCombo)
                add(JLabel("~").apply { font = MyFont.Bold(24f) })
                add(endHourCombo)
                add(endMinCombo)
                add(allDayButton)
            }
            // 초기 상태 설정
            if (isAllDaySelected) {
                // 24시간 선택 상태로 초기화
                startHourCombo.selectedIndex = 0 // 오전 0시
                startMinCombo.selectedIndex = 0 // 00분
                endHourCombo.selectedIndex = 0 // 오전 0시
                endMinCombo.selectedIndex = 0 // 00분
                startHourCombo.isEnabled = false
                startMinCombo.isEnabled = false
                endHourCombo.isEnabled = false
                endMinCombo.isEnabled = false
            } else {
                // 기본 시간 설정 (24시간 선택 해제 상태)
                startHourCombo.selectedIndex = 9 // 오전 9시
                startMinCombo.selectedIndex = 0 // 00분
                endHourCombo.selectedIndex = 18 // 오후 6시
                endMinCombo.selectedIndex = 0 // 00분
                startHourCombo.isEnabled = true
                startMinCombo.isEnabled = true
                endHourCombo.isEnabled = true
                endMinCombo.isEnabled = true
            }
            // 추가 버튼
            val addButton = RoundedButton("추가하기").apply {
                preferredSize = Dimension(150, 40)
                maximumSize = Dimension(150, 40)
                minimumSize = Dimension(150, 40)
                alignmentX = Component.CENTER_ALIGNMENT
                font = MyFont.Bold(18f)

                addActionListener {

                    val selectButtonText = selectedDays.joinToString(", ") { it }

                    // 시간 유효성 검사
                    val startHour = startHourCombo.selectedItem?.toString() ?: "오전 0시"
                    val startMin = startMinCombo.selectedItem?.toString() ?: "00분"
                    val endHour = endHourCombo.selectedItem?.toString() ?: "오전 0시"
                    val endMin = endMinCombo.selectedItem?.toString() ?: "00분"

                    // 모든 값이 "0"인 경우 "24시간" 설정
                    val timeRangeText = if (startHour == "오전 0시" && startMin == "00분" &&
                        endHour == "오전 0시" && endMin == "00분"
                    ) {
                        "24시간"
                    } else {
                        // 선택된 시간 값을 사용하여 timeRangeText 생성
                        "$startHour $startMin ~ $endHour $endMin"
                    }

                    onAdd(selectButtonText, timeRangeText)
//                    selectedDay2.addAll(selectedDays)
                    selectedDays.clear()
                    // 평일/주말 상태 설정
                    if (selectedDays.contains("평일")) {
                        isWeekDays = true
                    }
                    if (selectedDays.contains("주말")) {
                        isWeekEnds = true
                    }
                }
            }

            // 패널에 추가
            add(WeekDaysAndWeekEndsPanel)
            add(Box.createVerticalStrut(10))
            add(timePanel)
            add(addButton)
            add(Box.createVerticalStrut(15))
        }


        // 메인 패널 구성
        val mainPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            add(topPanel)
        }

        add(mainPanel, BorderLayout.CENTER)
    }
}