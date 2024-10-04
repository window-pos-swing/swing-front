package org.example.view.states
import RoundedProgressBar
import org.example.MyFont
import org.example.model.Order
import org.example.model.OrderState
import org.example.observer.OrderObserver
import org.example.view.components.BaseOrderPanel
import org.example.widgets.FillRoundedButton
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

            // 1. headerPanel의 오른쪽에 프린트 버튼 추가
            val buttonPanel = JPanel().apply {
                layout = FlowLayout(FlowLayout.RIGHT, 15, 0)  // 오른쪽 정렬
                background = Color.WHITE  // 배경색 설정
                border = BorderFactory.createEmptyBorder(15, 0, 0, 0)

                // 프린터 버튼
                add(
                    FillRoundedButton(
                    text = "",
                    borderColor = Color(230, 230, 230),
                    backgroundColor = Color(230, 230, 230),
                    textColor = Color.BLACK,
                    borderRadius = 20,
                    borderWidth = 1,
                    textAlignment = SwingConstants.CENTER,
                    padding = Insets(10, 20, 10, 20),
                    iconPath = "/print_icon.png",
                    buttonSize = Dimension(50, 50),
                    iconWidth = 45,
                    iconHeight = 45
                ).apply {
                    addActionListener {
                        // 인쇄 기능 추가
                        println("프린터 버튼 클릭")
                    }
                })
            }

            // getHeaderPanel()을 사용하여 headerPanel을 가져와서 버튼 패널 추가
            val headerPanel = getHeaderPanel()
            headerPanel.add(Box.createHorizontalGlue())  // 오른쪽 정렬을 위해 공간 추가
            headerPanel.add(buttonPanel)  // 버튼 패널을 headerPanel의 오른쪽에 추가

            // 프린터버튼 추가한 headerPanel을 NORTH에 추가
            add(headerPanel, BorderLayout.NORTH)

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
                    add(Box.createRigidArea(Dimension(0, 5)))  // 간격 추가
                    add(getFooterPanel())  // 수저/포크, 요청사항
                }

                // 오른쪽 패널: 주문취소 버튼과 프로그레스바
                val rightPanel = JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)  // 세로로 배치
                    isOpaque = false
                    alignmentY = Component.TOP_ALIGNMENT
                    border = BorderFactory.createEmptyBorder(0, 0, 15, 0)

                    // 주문취소 버튼
                    val cancelButton = JButton("주문취소").apply {
                        preferredSize = Dimension(255, 63)
                        maximumSize = Dimension(255, 63)
                        font = MyFont.Bold(20f)
                        background = Color(240, 240, 240)
                        alignmentX = Component.CENTER_ALIGNMENT
                    }

                    // 둥근 패널과 프로그레스바 통합 클래스 적용
                    val roundedProgressBar = RoundedProgressBar(0, totalTime).apply {
                        preferredSize = Dimension(255, 155)  // 전체 패널 크기
                    }

                    // 패널에 추가
                    add(roundedProgressBar, BorderLayout.CENTER)

                    // 프로그레스바 업데이트 로직
                    order.addTimerObserver(object : OrderObserver {
                        override fun update(order: Order) {
                            roundedProgressBar.updateProgress(order.elapsedTime)
                        }
                    })

                    // 요소 추가
                    add(cancelButton)
                    add(Box.createRigidArea(Dimension(0, 15)))  // 간격 추가
                    add(roundedProgressBar)
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





