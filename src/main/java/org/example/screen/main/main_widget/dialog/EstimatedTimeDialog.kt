package org.example.screen.main.main_widget.dialog

import CustomRoundedDialog
import OrderController
import org.example.MyFont
import org.example.command.AcceptOrderCommand
import org.example.model.Order
import org.example.style.MyColor
import org.example.widgets.RoundedPanel
import java.awt.*
import javax.swing.*
import java.awt.event.ActionListener

class EstimatedTimeDialog(
    parent: JFrame,
    title: String,
    private val order: Order,  // 현재 주문 객체를 받도록 수정
    private val orderController: OrderController  // OrderController도 받도록 수정
) : CustomRoundedDialog(parent, title, 1000, 465) {

    private var cookingTime: Int = 30  // 기본 조리 시간 30분
    private var deliveryTime: Int = 30  // 기본 배달 시간 30분

    init {

        // 조리시간 및 배달시간 선택 UI 생성
        val timeSelectionPanel = JPanel(GridLayout(1, 2, 20, 10)).apply {
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(20, 30, 30, 30)
            add(createTimeSelectionPanel("예상 조리시간 선택", "조리 예상시간을 설정해주세요", true))
            add(createTimeSelectionPanel("배달 예상시간 선택", "배달(배달대행) 도착 예상시간을 설정해주세요", false))
        }

        // 시간 접수 버튼 생성
        val confirmButton = JButton("시간 접수").apply {
            preferredSize = Dimension(300, 62)
            maximumSize = Dimension(300, 62)
            minimumSize = Dimension(300, 62)
            font = MyFont.Bold(24f)
            background = Color.WHITE
            foreground = MyColor.DARK_RED
            border = BorderFactory.createLineBorder(MyColor.DARK_RED)
            addActionListener {
                // 시간 접수 로직 추가
                println("조리시간: $cookingTime 분, 배달시간: $deliveryTime 분")

                // AcceptOrderCommand 실행
                val acceptOrderCommand = AcceptOrderCommand(order, cookingTime, deliveryTime, orderController)
                acceptOrderCommand.execute()

                dispose()  // 다이얼로그 닫기
            }
        }

        // 패널에 버튼을 추가하고, BoxLayout 또는 FlowLayout을 사용
        val buttonPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER)  // 버튼을 중앙에 배치
            background = Color.WHITE
            add(confirmButton)
            border = BorderFactory.createEmptyBorder(0, 0, 20, 0)
        }

        // 다이얼로그 UI 배치
        add(timeSelectionPanel, BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.SOUTH)

        // 다이얼로그 설정
        setSize(1000, 465)
        setLocationRelativeTo(parent)
        isVisible = true
    }

    // 시간 선택 패널 생성 함수
    private fun createTimeSelectionPanel(titleText: String, subtitleText: String, isCookingTime: Boolean): JPanel {
        // 시간 값 결정 (조리 시간/배달 시간)
        val timeValue = if (isCookingTime) cookingTime else deliveryTime

        val timeLabel = JLabel("${timeValue}분", SwingConstants.CENTER).apply {
            font = MyFont.Bold(38f)
            foreground = Color.BLACK
        }

        // 시간 증가/감소 버튼 생성
        val decreaseButton = JButton(ImageIcon(javaClass.getResource("/minus_icon.png"))).apply {
            font = MyFont.Bold(36f)
            isContentAreaFilled = false  // 버튼 배경색 제거
            isFocusPainted = false
            border = BorderFactory.createEmptyBorder()  // 테두리 제거
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)  // 커서를 손 모양으로 변경
            border = BorderFactory.createEmptyBorder(0, 20, 0, 0)
            addActionListener { adjustTime(timeLabel, -5, isCookingTime) }  // 5분 감소
        }
        val increaseButton = JButton(ImageIcon(javaClass.getResource("/plus_icon.png"))).apply {
            font = MyFont.Bold(36f)
            isContentAreaFilled = false  // 버튼 배경색 제거
            isFocusPainted = false
            border = BorderFactory.createEmptyBorder()  // 테두리 제거
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)  // 커서를 손 모양으로 변경
            border = BorderFactory.createEmptyBorder(0, 0, 0, 20)
            addActionListener { adjustTime(timeLabel, 5, isCookingTime) }  // 5분 증가
        }

        // 둥근 시간 선택 패널 구성
        val buttonPanel = RoundedPanel(30, 30).apply {
            background = Color.WHITE  // 배경을 흰색으로 설정
            preferredSize = Dimension(305, 90)  // 패널 크기를 305x90으로 설정
            layout = BorderLayout()

            add(decreaseButton, BorderLayout.WEST)
            add(timeLabel, BorderLayout.CENTER)
            add(increaseButton, BorderLayout.EAST)
        }

        // 메인 패널을 둥글게 만들기 위해 RoundedPanel 사용
        val mainPanel = RoundedPanel(30, 30).apply {  // mainPanel도 둥글게 설정
            background = MyColor.DARK_NAVY
            preferredSize = Dimension(460, 258)  // 패널 크기를 설정
            layout = GridBagLayout()
            border = BorderFactory.createEmptyBorder(20, 15, 20, 15)  // 상하 20, 좌우 15의 마진 추가

            val gbc = GridBagConstraints().apply {
                gridx = 0
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(10, 0, 10, 0)  // 컴포넌트 간의 수직 간격
            }

            // titleText
            gbc.gridy = 0
            add(JLabel(titleText, SwingConstants.CENTER).apply {
                font = MyFont.Bold(24f)
                foreground = Color.WHITE
            }, gbc)

            // subtitleText
            gbc.gridy = 1
            add(JLabel(subtitleText, SwingConstants.CENTER).apply {
                font = MyFont.SemiBold(16f)
                foreground = Color.WHITE
            }, gbc)

            // 버튼 패널
            gbc.gridy = 2
            add(buttonPanel, gbc)
        }

        return mainPanel
    }

    // 시간 조정 함수 (timeLabel의 값을 업데이트)
    private fun adjustTime(timeLabel: JLabel, delta: Int, isCookingTime: Boolean) {
        val currentTime = timeLabel.text.replace("분", "").toInt()
        val newTime = (currentTime + delta).coerceAtLeast(5)  // 최소 5분

        // 시간 값 업데이트
        if (isCookingTime) {
            cookingTime = newTime
        } else {
            deliveryTime = newTime
        }

        // 라벨 업데이트
        timeLabel.text = "${newTime}분"
    }
}