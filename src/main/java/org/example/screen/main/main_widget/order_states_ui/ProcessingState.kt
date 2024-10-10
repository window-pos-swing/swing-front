package org.example.view.states
import OrderRejectCancelDialog
import RoundedProgressBar
import org.example.MyFont
import org.example.command.CompletedOrderCommand
import org.example.command.RejectOrderCommand
import org.example.command.RejectedReasonType
import org.example.`interface`.OrderEventListener
import org.example.model.Order
import org.example.model.OrderState
import org.example.observer.OrderObserver
import org.example.style.MyColor
import org.example.view.components.BaseOrderPanel
import org.example.widgets.FillRoundedButton
import javax.swing.*
import java.awt.*

class ProcessingState(val totalTime: Int) : OrderState, OrderEventListener {
    private lateinit var rightPanel: JPanel  // 버튼을 추가할 패널을 멤버로 선언

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
                layout = FlowLayout(FlowLayout.RIGHT, 0, 0)  // 오른쪽 정렬
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
                    iconPath = "/print_icon_main.png",
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
                rightPanel = JPanel().apply {
                    layout = GridBagLayout()  // 두 컴포넌트를 독립적으로 배치하기 위해 GridBagLayout 사용
                    isOpaque = false
                    border = BorderFactory.createEmptyBorder(0, 0, 5, 0)

                    // GridBagConstraints 생성
                    val gbc = GridBagConstraints().apply {
                        fill = GridBagConstraints.HORIZONTAL  // 컴포넌트가 가로로 꽉 차도록 설정
                        gridx = 0
                        insets = Insets(0, 0, 5, 0)  // 여백 줄이기
                        weightx = 1.0  // 가로 방향으로 크기를 동일하게 배분
                        weighty = 0.0  // 세로 방향 여분 공간을 없애기
                    }

                    // 주문취소 버튼
                    val cancelButton = FillRoundedButton(
                        text = "주문취소",
                        borderColor = MyColor.GREY300,
                        backgroundColor = MyColor.GREY300,
                        textColor = MyColor.GREY500,
                        borderRadius = 20,
                        borderWidth = 1,
                        textAlignment = SwingConstants.CENTER,
                        padding = Insets(10, 20, 10, 20),
                        buttonSize = Dimension(255, 63),  // 고정된 버튼 크기
                        customFont = MyFont.Bold(28f)
                    ).apply {
                        addActionListener {
                            OrderRejectCancelDialog(
                                SwingUtilities.getWindowAncestor(this) as JFrame,
                                "주문 취소 사유 선택",
                                "주문 취소 사유를 선택해 주세요.",
                                "주문 취소",
                                onReject = { rejectReason ->
                                    val rejectOrderCommand = RejectOrderCommand(order, rejectReason, RejectedReasonType.STORE_CANCEL)
                                    rejectOrderCommand.execute()
                                }
                            )
                        }
                    }

                    // 둥근 패널과 프로그레스바 통합 클래스 적용
                    val roundedProgressBar = RoundedProgressBar(0, totalTime).apply {
                        preferredSize = Dimension(255, 154)  // 전체 패널 크기
                    }

                    // GridBagConstraints로 각 컴포넌트를 독립적으로 배치
                    add(cancelButton, gbc.apply { gridy = 0 })  // 첫 번째 행에 주문 취소 버튼 배치
                    add(Box.createRigidArea(Dimension(0, 0)), gbc.apply { gridy = 1 })
                    add(roundedProgressBar, gbc.apply { gridy = 2 })  // 두 번째 행에 프로그레스바 배치

                    // 프로그레스바 업데이트 로직
                    order.addTimerObserver(object : OrderObserver {
                        override fun update(order: Order) {
                            // 프로그레스바만 다시 그리기
                            roundedProgressBar.updateProgress(order.elapsedTime)
                            roundedProgressBar.repaint()  // 프로그레스바만 리페인트
                        }
                    })

                }

                // contentPanel에 좌우 패널 배치
                add(leftPanel, BorderLayout.CENTER)
                add(rightPanel, BorderLayout.EAST)

            }

            // 3. contentPanel을 전체 패널에 CENTER로 추가
            add(contentPanel, BorderLayout.CENTER)
            simulateOrderEvents(order, this@ProcessingState)
        }
    }


    // 주문 상태에 따라 UI 업데이트
    fun simulateOrderEvents(order: Order, eventListener: OrderEventListener) {
        if (order.isCompleted) {
            eventListener.onCompleteOrder(order)
            return
        }

        if (order.isResent) {
            eventListener.onResendOrder(order)
            return
        }

        // 주문 번호에 따라 이벤트 타이머 설정
        if (order.orderNumber % 2 == 0) {
            order.initializeEventTimer(3000) {
                eventListener.onResendOrder(order)
            }
        } else {
            order.initializeEventTimer(3000) {
                eventListener.onCompleteOrder(order)
            }
        }

    }

    // Resend Order 이벤트 처리: 프로그레스바를 버튼으로 변환
    override fun onResendOrder(order: Order) {
        order.isResent = true
        // '주문취소' 버튼 생성
        val cancelButton = FillRoundedButton(
            text = "주문취소",
            borderColor = MyColor.GREY300,
            backgroundColor = MyColor.GREY300,
            textColor = MyColor.GREY500,
            borderRadius = 20,
            borderWidth = 1,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(10, 20, 10, 20),
            buttonSize = Dimension(255, 63),
            customFont = MyFont.Bold(28f)
        ).apply {
            addActionListener {
                OrderRejectCancelDialog(
                    SwingUtilities.getWindowAncestor(this) as JFrame,
                    "주문 취소 사유 선택",
                    "주문 취소 사유를 선택해 주세요.",
                    "주문 취소",
                    onReject = { rejectReason ->
                        val rejectOrderCommand = RejectOrderCommand(order, rejectReason , RejectedReasonType.STORE_CANCEL)
                        rejectOrderCommand.execute()
                    }
                )
            }
        }

        // '배달 대행사로 주문번호 재전송' 버튼 생성
        val resendOrderButton = FillRoundedButton(
            text = "배달 대행사로\n주문번호 재전송",
            borderColor = MyColor.PINK,
            backgroundColor = MyColor.PINK,
            textColor = Color.WHITE,
            borderRadius = 20,
            borderWidth = 1,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(10, 20, 10, 20),
            buttonSize = Dimension(255, 154),
            customFont = MyFont.Bold(28f)
        ).apply {
            addActionListener {
                // 배달대행사로 주문번호 재전송 api 호출
                println("Resend Order for #${order.orderNumber}")
            }
        }

        println("onResendOrder called")

        // rightPanel을 사용해 기존 UI를 제거하고 두 개의 버튼을 추가
        rightPanel.removeAll()
        rightPanel.layout = BoxLayout(rightPanel, BoxLayout.Y_AXIS)  // 세로로 배치

        rightPanel.add(cancelButton)
        rightPanel.add(Box.createRigidArea(Dimension(0, 10)))  // 두 버튼 사이의 간격 추가
        rightPanel.add(resendOrderButton)

        rightPanel.revalidate()
        rightPanel.repaint()
    }

    // Complete Order 이벤트 처리: 주문 완료 처리
    override fun onCompleteOrder(order: Order) {
        order.isCompleted = true
        rightPanel.border = BorderFactory.createEmptyBorder(-15, 0, 0, 0)
        // 주문 완료 버튼 생성
        val completeOrderButton = FillRoundedButton(
            text = "주문\n완료처리",  // 줄바꿈을 위해 "\n" 사용
            borderColor = Color(27, 43, 66),  // 테두리 색상
            backgroundColor = Color(27, 43, 66),  // 배경색
            textColor = Color(255, 182, 193),  // 텍스트 색상 (핑크)
            borderRadius = 20,  // 둥근 버튼
            borderWidth = 2,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(10, 20, 10, 20),
            buttonSize = Dimension(255, 232),  // 크기 설정
            customFont = MyFont.Bold(36f)  // 커스텀 폰트 설정
        ).apply {
            addActionListener {
                // CompletedOrderCommand 실행
                val completedOrderCommand = CompletedOrderCommand(order)
                completedOrderCommand.execute()

                // 완료 후 UI 업데이트
                val updatedUI = order.getUI()
                this@apply.removeAll()
                this@apply.add(updatedUI)
                this@apply.revalidate()
                this@apply.repaint()
            }
        }

        // rightPanel을 사용해 프로그레스바를 제거하고 완료 버튼 추가
        rightPanel.removeAll()
        rightPanel.add(completeOrderButton)
        rightPanel.revalidate()
        rightPanel.repaint()
    }

}





