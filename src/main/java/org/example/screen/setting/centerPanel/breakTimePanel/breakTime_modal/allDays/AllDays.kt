package org.example.screen.setting.centerPanel.breakTimePanel.breakTime_modal.allDays

import RoundedComboBox
import org.example.MyFont
import org.example.style.MyColor
import org.example.widgets.IconRoundBorder2
import org.example.widgets.RoundButton
import org.example.widgets.RoundedButton
import java.awt.*
import javax.swing.*

class AllDays : JPanel() {
    private var isItemAdded = false
    private var startHourCombo: JComboBox<String>
    private var startMinCombo: JComboBox<String>
    private var endHourCombo: JComboBox<String>
    private var endMinCombo: JComboBox<String>
    private val bottomPanel: JPanel = JPanel()

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
                BorderFactory.createEmptyBorder(10, 0, 10, 0), // 바깥쪽 마진 추가
                BorderFactory.createLineBorder(MyColor.LIGHT_GREY, 1) // 외곽선
            )

            // 상단 요일 버튼 패널
            val dayPanel = JPanel().apply {
                layout = FlowLayout(FlowLayout.CENTER, 20, 15)
                background = Color.WHITE

                val days = arrayOf("월", "화", "수", "목", "금", "토", "일")
                for (day in days) {
                    val dayButton = RoundButton(day).apply {
                        isClickable = false
                        foreground = Color(255, 177, 177)
                        font = MyFont.Bold(20f)
                    }
                    add(dayButton)
                }
            }

            // 시간 선택 패널
            val timePanel = JPanel().apply {
                layout = FlowLayout(FlowLayout.CENTER, 10, 0)
                background = Color.WHITE

                startHourCombo = RoundedComboBox(DefaultComboBoxModel(arrayOf("오전 0시", "오전 1시", "오전 2시", "오전 3시", "오전 4시", "오전 5시", "오전 6시", "오전 7시", "오전 8시", "오전 9시", "오전 10시", "오전 11시", "오후 12시", "오후 1시", "오후 2시", "오후 3시", "오후 4시", "오후 5시", "오후 6시", "오후 7시", "오후 8시", "오후 9시", "오후 10시", "오후 11시"))).apply {
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

                endHourCombo = RoundedComboBox(DefaultComboBoxModel(arrayOf("오전 0시", "오전 1시", "오전 2시", "오전 3시", "오전 4시", "오전 5시", "오전 6시", "오전 7시", "오전 8시", "오전 9시", "오전 10시", "오전 11시", "오후 12시", "오후 1시", "오후 2시", "오후 3시", "오후 4시", "오후 5시", "오후 6시", "오후 7시", "오후 8시", "오후 9시", "오후 10시", "오후 11시"))).apply {
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
                    if (isItemAdded) {
                        JOptionPane.showMessageDialog(this@AllDays, "최대 하나의 항목만 추가할 수 있습니다.")
                        return@addActionListener
                    }

                    // 시간 유효성 검사
                    val startHour = startHourCombo.selectedItem?.toString() ?: "오전 0시"
                    val startMin = startMinCombo.selectedItem?.toString() ?: "00분"
                    val endHour = endHourCombo.selectedItem?.toString() ?: "오전 0시"
                    val endMin = endMinCombo.selectedItem?.toString() ?: "00분"

                    // 선택된 시간 값을 사용하여 timeRangeText 생성
                    val timeRangeText = "$startHour $startMin ~ $endHour $endMin"
//                    println("선택된 시간: $timeRangeText")
                    addBottomPanel(timeRangeText) // 하단 패널에 추가
                    isItemAdded = true
                }
            }

            // 패널에 추가
            add(dayPanel)
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
            add(Box.createVerticalGlue()) // 상단 여백
            add(dayAndTimePanel)
            add(Box.createVerticalStrut(20)) // 패널 간 간격
            add(bottomPanel)
            add(Box.createVerticalGlue()) // 하단 여백
        }

        add(mainPanel, BorderLayout.CENTER)
    }


    // 하단 패널에 아이템 추가 함수
    private fun addBottomPanel(timeRangeText: String) {
        lateinit var itemPanel: JPanel // 외부에 선언하여 참조 가능하게 설정

        itemPanel = JPanel().apply {
            preferredSize = Dimension(940, 60)
            maximumSize = Dimension(940, 60)
            minimumSize = Dimension(940, 60)
            layout = BorderLayout()
            background = Color.WHITE
            border = BorderFactory.createLineBorder(Color.GRAY, 1) // 외곽선

            val allDaysLabel = IconRoundBorder2.createRoundedLabel("전체요일", Color(255, 177, 177), 20).apply {
                foreground = Color.WHITE
                preferredSize = Dimension(100, 40)
            }

            val timeRangeLabel = JLabel(timeRangeText).apply {
                font = MyFont.Bold(24f)
            }

            val deleteButton = RoundedButton("삭제").apply {
                font = MyFont.Bold(18f)
                preferredSize = Dimension(100, 35)
                addActionListener {
                    // bottomPanel에서 itemPanel을 제거
                    bottomPanel.remove(itemPanel) // 부모 패널인 bottomPanel에서 itemPanel을 제거
                    bottomPanel.revalidate() // 레이아웃 다시 계산
                    bottomPanel.repaint() // 화면 다시 그리기
                    isItemAdded = false // 아이템 추가 상태 초기화
                }
            }

            // 삭제 버튼을 오른쪽에 배치
            val rightPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 10, 10)).apply {
                background = Color.WHITE
                add(deleteButton)
            }

            // 중앙 패널에 라벨과 시간 범위 추가
            val centerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 10, 10)).apply {
                background = Color.WHITE
                add(allDaysLabel)
                add(timeRangeLabel)
            }

            add(centerPanel, BorderLayout.CENTER)
            add(rightPanel, BorderLayout.EAST)
        }

        // bottomPanel에 itemPanel을 추가
        bottomPanel.add(itemPanel)
        bottomPanel.revalidate() // 레이아웃 다시 계산
        bottomPanel.repaint() // 화면 다시 그리기
    }
}