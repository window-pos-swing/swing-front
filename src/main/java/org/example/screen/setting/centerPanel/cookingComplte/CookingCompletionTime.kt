package org.example.screen.setting.centerPanel.cookingComplte

import org.example.MyFont
import org.example.style.MyColor
import org.example.widgets.CustomToggleButton2
import org.example.widgets.RoundedPanel
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

class CookingCompletionTime : JPanel() {

    // 클래스 변수로 선언
    private lateinit var timeLabel: JLabel  // 조리 완료 시간 라벨
    private lateinit var decreaseButton: JButton  // 시간 감소 버튼
    private lateinit var increaseButton: JButton  // 시간 증가 버튼

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = MyColor.LOGIN_TITLEBAR

        // 상단 패널: 조리완료시간과 토글 버튼을 한 줄로 나열
        val topPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER)  // 한 줄로 배치
            background = MyColor.LOGIN_TITLEBAR
//            background = Color.RED
            preferredSize = Dimension(400, 50)  // 패널 크기 강제 설정
            maximumSize = Dimension(400, 50)  // 최대 크기도 설정
            minimumSize = Dimension(400, 50)  // 최소 크기도 설정
        }

        // 아이콘 경로 로드
        val watchIconPath = ImageIcon(javaClass.getResource("/watch.png"))
        val storeLabel = JLabel(watchIconPath).apply {
            border = BorderFactory.createEmptyBorder(0, 20, 0, 10)  // 아이콘과 텍스트 사이 여백 추가
        }

        val label = JLabel("조리완료시간").apply {
            font = MyFont.Bold(26f)
            foreground = Color.WHITE
        }

        // CustomToggleButton을 사용하여 토글 버튼 추가
        val toggleButton = CustomToggleButton2().apply {
            addActionListener {
                if (isSelected) {
                    enableTimeAdjustment(true)  // ON 상태에서는 시간 조절 가능
                } else {
                    enableTimeAdjustment(false)  // OFF 상태에서는 시간 조절 불가능
                }
            }
        }

        // 조리완료시간과 토글 버튼을 한 줄에 추가
        topPanel.add(storeLabel)
        topPanel.add(label)
        topPanel.add(toggleButton)

        // 시간 선택 패널
        val timeSelectionPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER)  // 시간 조절 버튼들 한 줄로 배치
            background = MyColor.LOGIN_TITLEBAR
//            background = Color.WHITE
            preferredSize = Dimension(400, 90)  // 패널 크기 강제 설정
            maximumSize = Dimension(400, 90)  // 최대 크기도 설정
            minimumSize = Dimension(400, 90) // 최소 크기도 설정
            add(createTimeSelectionPanel())  // 시간 선택 패널 추가
        }

        // 패널들을 순서대로 추가
        add(topPanel)  // 상단에 조리완료시간과 토글 버튼 추가
        add(timeSelectionPanel)  // 시간 설정 패널을 그 아래에 추가

        // 초기 상태 설정 (ON일 때만 활성화)
        enableTimeAdjustment(false)  // 초기 상태는 조절 가능하도록
    }

    // 시간 선택 패널 생성
    private fun createTimeSelectionPanel(): JPanel {
        // 클래스 레벨의 변수로 설정
        timeLabel = JLabel("30분", SwingConstants.CENTER).apply {
            font = MyFont.Bold(38f)
            foreground = Color.BLACK
            preferredSize = Dimension(100, 50)
        }

        // 시간 증가/감소 버튼 생성
        decreaseButton = JButton(ImageIcon(javaClass.getResource("/minus_icon.png"))).apply {
            font = MyFont.Bold(36f)
            isContentAreaFilled = false  // 버튼 배경색 제거
            isFocusPainted = false
            border = BorderFactory.createEmptyBorder()  // 테두리 제거
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)  // 커서를 손 모양으로 변경
            addActionListener { adjustTime(timeLabel, -5) }  // 5분 감소
        }
        increaseButton = JButton(ImageIcon(javaClass.getResource("/plus_icon.png"))).apply {
            font = MyFont.Bold(36f)
            isContentAreaFilled = false  // 버튼 배경색 제거
            isFocusPainted = false
            border = BorderFactory.createEmptyBorder()  // 테두리 제거
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)  // 커서를 손 모양으로 변경
            addActionListener { adjustTime(timeLabel, 5) }  // 5분 증가
        }

        // 둥근 시간 선택 패널 구성
        val buttonPanel = RoundedPanel(30, 30).apply {
            background = Color.WHITE  // 배경을 흰색으로 설정
            border = EmptyBorder(0, 15, 0, 15)
            preferredSize = Dimension(350, 80)  // 패널 크기를 305x90으로 설정
            layout = BorderLayout()

            add(decreaseButton, BorderLayout.WEST)
            add(timeLabel, BorderLayout.CENTER)
            add(increaseButton, BorderLayout.EAST)
        }

        // 메인 패널을 둥글게 만들기 위해 RoundedPanel 사용
        val mainPanel = RoundedPanel(30, 30).apply {
            background = MyColor.LOGIN_TITLEBAR
            preferredSize = Dimension(350, 80)  // 패널 크기를 설정
            layout = FlowLayout(FlowLayout.CENTER)  // 중앙 정렬
            border = BorderFactory.createEmptyBorder(-5, 0, 0, 0)  // 마진 추가

            add(buttonPanel)
        }

        return mainPanel
    }

    // 시간 조정 함수 (timeLabel의 값을 업데이트)
    private fun adjustTime(timeLabel: JLabel, delta: Int) {
        val currentTime = timeLabel.text.replace("분", "").toInt()
        val newTime = (currentTime + delta).coerceAtLeast(5)  // 최소 5분으로 제한
        timeLabel.text = "${newTime}분"
    }

    // 시간 조절 가능 여부 설정 함수
    private fun enableTimeAdjustment(isEnabled: Boolean) {
        decreaseButton.isEnabled = isEnabled
        increaseButton.isEnabled = isEnabled
        timeLabel.isEnabled = isEnabled

        if (!isEnabled) {
            timeLabel.foreground = Color.GRAY  // 비활성화 시 색상을 회색으로 변경
        } else {
            timeLabel.foreground = Color.BLACK  // 활성화 시 기본 색상으로 변경
        }
    }
}