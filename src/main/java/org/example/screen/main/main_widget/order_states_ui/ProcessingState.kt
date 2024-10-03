package org.example.view.states

import org.example.MyFont
import org.example.model.Order
import org.example.model.OrderState
import org.example.observer.OrderObserver
import org.example.view.components.BaseOrderPanel
import javax.swing.*
import java.awt.*

class ProcessingState(val totalTime: Int) : OrderState {
    override fun handle(order: Order) {
        // 주문 진행 처리 로직
        //order.startTimer(totalTime)  // 타이머 시작
    }

    override fun getUI(order: Order): JPanel {
        return BaseOrderPanel(order).apply {
            layout = BorderLayout()  // 전체 레이아웃을 BorderLayout으로 설정

            border = BorderFactory.createEmptyBorder(0, 0, 0, 0)

            // 1. headerPanel을 NORTH에 배치 (전체 가로폭 차지)
            add(getHeaderPanel(), BorderLayout.NORTH)

            // 2. 좌우로 나누는 메인 패널 contentPanel을 CENTER에 배치
            val contentPanel = JPanel().apply {
                layout = BorderLayout()  // 좌우로 나누기 위해 BorderLayout 사용
                isOpaque = false
                border = BorderFactory.createEmptyBorder(15, 0, 0, 0)  // 패널 내 여백 설정

                // 왼쪽 패널: addressPanel, menuDetailPanel, footerPanel 배치
                val leftPanel = JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)  // 세로로 배치
                    isOpaque = false
                    border = BorderFactory.createEmptyBorder(0, 0, 0, 20)
                    add(getAddressPanel())  // 주소 정보
                    add(Box.createRigidArea(Dimension(0, 15)))  // 간격 추가
                    add(getMenuDetailPanel())  // 메뉴 세부 정보
                    add(Box.createRigidArea(Dimension(0, 10)))  // 간격 추가
                    add(getFooterPanel())  // 수저/포크, 요청사항
                }

                // 오른쪽 패널: 주문취소 버튼과 프로그레스바
                val rightPanel = JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)  // 세로로 배치
                    isOpaque = false
                    alignmentY = Component.TOP_ALIGNMENT

                    // 주문취소 버튼
                    val cancelButton = JButton("주문취소").apply {
                        preferredSize = Dimension(255, 63)
                        maximumSize = Dimension(255, 63)
                        font = MyFont.Bold(20f)
                        background = Color(240, 240, 240)
                        alignmentX = Component.CENTER_ALIGNMENT
                    }

                    // 프로그레스바
                    val progressBar = JProgressBar(0, totalTime).apply {
                        value = order.elapsedTime  // 현재 진행 시간 반영
                        isStringPainted = true
                        string = "${order.elapsedTime}분"
                        preferredSize = Dimension(255, 155)
                        maximumSize = Dimension(255, 155)
                        background = Color(30, 30, 30)
                        foreground = Color.PINK
                        font = MyFont.Bold(28f)
                        alignmentX = Component.CENTER_ALIGNMENT
                    }

                    order.addTimerObserver(object : OrderObserver {
                        override fun update(order: Order) {
                            // 프로그레스바만 다시 그리기
                            progressBar.value = order.elapsedTime
                            progressBar.string = "${order.elapsedTime} / $totalTime 분"
                            progressBar.repaint()  // 프로그레스바만 리페인트
                        }
                    })

                    // 요소 추가
                    add(cancelButton)
                    add(Box.createRigidArea(Dimension(0, 15)))  // 간격 추가
                    add(progressBar)
                }

                // contentPanel에 좌우 패널 배치
                add(leftPanel, BorderLayout.CENTER)
                add(rightPanel, BorderLayout.EAST)
            }

            // 3. contentPanel을 전체 패널에 CENTER로 추가
            add(contentPanel, BorderLayout.CENTER)
        }
    }
}





