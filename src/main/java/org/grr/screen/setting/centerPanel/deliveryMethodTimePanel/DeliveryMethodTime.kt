package org.grr.screen.setting.centerPanel.deliveryMethodTimePanel

import org.grr.api.SettingToServer
import org.grr.model.SettingModel
import org.grr.`object`.Storage
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.RoundedButton
import org.grr.widgets.RoundedPanel
import org.grr.widgets.SwitchButton
import org.grr.widgets.SwitchListener
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

// 투명도를 적용한 커스텀 버튼 클래스
class TransparentButton(text: String) : JButton(text) {

    var alpha: Float = 1.0f  // 기본적으로 투명도는 100%

    init {
        isContentAreaFilled = false
        isFocusPainted = false
        border = BorderFactory.createEmptyBorder()
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 투명도 설정 (alpha 값을 이용)
        g2.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)

        // 버튼 기본 배경과 텍스트 그리기
        super.paintComponent(g)
    }
}

class DeliveryMethodTime : JPanel() {

    // 클래스 변수로 선언
    private lateinit var timeLabel: JLabel  // 배달 예정 시간 라벨
    private lateinit var decreaseButton: TransparentButton  // 시간 감소 버튼
    private lateinit var increaseButton: TransparentButton  // 시간 증가 버튼
    private lateinit var setButton: RoundedButton  // 저장 버튼
    private lateinit var buttonPanel: RoundedPanel

    private var currentDeliveryCompletionTime: Int = 40  // 현재 배달완료시간
    private var deliveryCompletionControl: Boolean = false

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        isOpaque = false

        // 상단 패널: 배달 방법 및 토글 버튼을 한 줄로 나열
        val topPanel = JPanel().apply {
            layout = BorderLayout()  // 한 줄로 배치
            isOpaque = false
//            background = Color.RED
        }

        //        회원정보 갖고오는 구문
        val memberInfo = Storage.getMemberInfo()

        currentDeliveryCompletionTime = SettingModel.deliveryTime

        deliveryCompletionControl = SettingModel.deliveryTimeControl

        // 배달 아이콘 경로 로드
        val deliveryIconPath = ImageIcon(javaClass.getResource("/delivery.png"))
        val resizedIcon = ImageIcon(
            deliveryIconPath.image.getScaledInstance(35, 35, Image.SCALE_SMOOTH)  // 20x20 크기로 리사이징
        )
        val deliveryLabel = JLabel(resizedIcon).apply {
            border = BorderFactory.createEmptyBorder(0, 10, 0, 10)  // 아이콘과 텍스트 사이 여백 추가
        }

        val label = JLabel("배달 예정 시간").apply {
            font = MyFont.Bold(20f)
            foreground = Color.WHITE
        }

        // CustomToggleButton을 사용하여 토글 버튼 추가
        val toggleButton = SwitchButton().apply {

            toggleOn(deliveryCompletionControl)

            addEventSwitchSelected(object : SwitchListener {
                override fun selectChange(isOn: Boolean) {
                    deliveryCompletionControl = isOn
                    enableTimeAdjustment(isOn)
                    updateSaveButtonState()
                }
            })

            border = BorderFactory.createEmptyBorder(15, 60, 15, 60)
        }

        val spacing = Box.createRigidArea(Dimension(30, 0))

        // 왼쪽에 아이콘과 라벨을 담을 패널
        val leftPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.LEFT, 0, 0)  // 왼쪽 정렬, 간격 0
            isOpaque = false
            add(deliveryLabel)
            add(label)
            add(spacing)
            add(toggleButton)
        }

        // 저장 버튼을 오른쪽에 배치
        setButton = RoundedButton("저장").apply {
            isEnabled = false
            addActionListener {
                saveDeliveryCompletionTime()
            }
        }
        val rightPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.RIGHT, 0, 0)  // 오른쪽 정렬
            isOpaque = false
            add(setButton)
        }

        // 배달 방법과 토글 버튼을 한 줄에 추가
        topPanel.add(leftPanel, BorderLayout.WEST)  // 왼쪽 끝에 배치
        topPanel.add(rightPanel, BorderLayout.EAST)  // 오른쪽 끝에 저장 버튼 배치

        // 시간 선택 패널
        val timeSelectionPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER)  // 시간 조절 버튼들 한 줄로 배치
            isOpaque = false
            border = BorderFactory.createEmptyBorder(0, 0, 20, 0)
            add(createTimeSelectionPanel(currentDeliveryCompletionTime))  // 시간 선택 패널 추가
        }

        // 패널들을 순서대로 추가
        add(topPanel)  // 상단에 배달 방법과 토글 버튼 추가
        add(timeSelectionPanel)  // 시간 설정 패널을 그 아래에 추가

        // 초기 상태 설정 (ON일 때만 활성화)
        enableTimeAdjustment(false)  // 초기 상태는 조절 가능하도록
    }

    // 시간 선택 패널 생성
    private fun createTimeSelectionPanel(deliveryCompletionTime: Int): JPanel {
        // 클래스 레벨의 변수로 설정
        timeLabel = JLabel("${deliveryCompletionTime}분", SwingConstants.CENTER).apply {
            font = MyFont.Bold(38f)
            foreground = Color.RED
            preferredSize = Dimension(100, 50)
        }

        decreaseButton = TransparentButton("").apply {
            icon = ImageIcon(javaClass.getResource("/minus_icon.png"))  // 아이콘 설정
            background = Color.GRAY
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addActionListener { adjustTime(timeLabel, -5) }
        }

        increaseButton = TransparentButton("").apply {
            icon = ImageIcon(javaClass.getResource("/plus_icon.png"))  // 아이콘 설정
            background = Color.GRAY
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addActionListener { adjustTime(timeLabel, 5) }
        }

        // 둥근 시간 선택 패널 구성
        buttonPanel = RoundedPanel(30, 30).apply {
            background = Color.WHITE  // 배경을 흰색으로 설정
            border = EmptyBorder(5, 15, 0, 15)
            preferredSize = Dimension(400, 80)  // 패널 크기를 305x90으로 설정
            layout = BorderLayout()

            add(decreaseButton, BorderLayout.WEST)
            add(timeLabel, BorderLayout.CENTER)
            add(increaseButton, BorderLayout.EAST)
        }

        // 메인 패널을 둥글게 만들기 위해 RoundedPanel 사용
        val mainPanel = RoundedPanel(30, 30).apply {
            isOpaque = false
            preferredSize = Dimension(400, 80)  // 패널 크기를 설정
            layout = FlowLayout(FlowLayout.CENTER)  // 중앙 정렬
            border = BorderFactory.createEmptyBorder(-5, 0, 0, 0)  // 마진 추가

            add(buttonPanel)
        }

        return mainPanel
    }

    // 시간 조정 함수 (timeLabel의 값을 업데이트)
    private fun adjustTime(timeLabel: JLabel, delta: Int) {
        currentDeliveryCompletionTime = (currentDeliveryCompletionTime + delta).coerceAtLeast(5)
        timeLabel.text = "${currentDeliveryCompletionTime}분"
        updateSaveButtonState()
    }

    // 시간 조절 가능 여부 설정 함수
    private fun enableTimeAdjustment(isEnabled: Boolean) {
        decreaseButton.isEnabled = isEnabled
        increaseButton.isEnabled = isEnabled
        timeLabel.isEnabled = isEnabled

        if (!isEnabled) {
            timeLabel.foreground = Color.GRAY  // 비활성화 시 색상을 회색으로 변경
            buttonPanel.background = MyColor.DARK_NAVY

            // 버튼의 투명도를 연하게 적용
            decreaseButton.alpha = 0.5f  // 50% 투명도
            increaseButton.alpha = 0.5f
        } else {
            timeLabel.foreground = Color(13, 130, 191)  // 활성화 시 기본 색상으로 변경
            buttonPanel.background = Color.WHITE

            // 버튼의 투명도를 연하게 적용
            decreaseButton.alpha = 1.0f  // 50% 투명도
            increaseButton.alpha = 1.0f
        }
    }

    /*
        서버 api 연결부분
    */
    private fun saveDeliveryCompletionTime() {
        val settingToServer = SettingToServer()
        val result = settingToServer.deliveryTimeToServer(deliveryCompletionControl, currentDeliveryCompletionTime)
        if (result.first) {
//            JOptionPane.showMessageDialog(this, "배달 예상 시간이 업데이트되/**/었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE)
            SettingModel.deliveryTime = currentDeliveryCompletionTime
            SettingModel.deliveryTimeControl = deliveryCompletionControl
            updateSaveButtonState()
            SettingModel.updateLocalDelivery()//로컬에 정보 업데이트
        } else {
            JOptionPane.showMessageDialog(this, "업데이트 실패: ${result.second}", "오류", JOptionPane.ERROR_MESSAGE)
        }
    }

    private fun updateSaveButtonState() {
        val isChanged = currentDeliveryCompletionTime != SettingModel.deliveryTime ||
                deliveryCompletionControl != SettingModel.deliveryTimeControl
        setButton.isEnabled = isChanged
        setButton.foreground = if (isChanged) Color.WHITE else Color.GRAY
    }
}