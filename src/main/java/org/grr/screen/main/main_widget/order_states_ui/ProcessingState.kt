package org.grr.screen.main.main_widget.order_states_ui

import OrderRejectCancelDialog
import RoundedProgressBar
import org.grr.command.RejectOrderCommand
import org.grr.command.RejectedReasonType
import org.grr.enum.OrderReceiveType
import org.grr.enum.PosOrderStatus
import org.grr.enum.ServerOrderStatus
import org.grr.`interface`.OrderEventListener
import org.grr.model.OrderState
import org.grr.model.ReceiveOrderModel
import org.grr.observer.OrderObserver
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.FillRoundedButton
import org.grr.`object`.OverlayManager
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

class ProcessingState(
    val totalTime: Int,
    parentFrame: JFrame, cardPanel: JPanel,
) : OrderState, OrderEventListener {
    private lateinit var rightPanel: JPanel  // 버튼을 추가할 패널을 멤버로 선언

    override fun handle(order: ReceiveOrderModel) {
        // 주문 진행 처리 로직
        //order.startTimer(totalTime)  // 타이머 시작
    }

    private lateinit var overlayManager: OverlayManager
    val _cardPanel = cardPanel

    override fun getUI(order: ReceiveOrderModel): JPanel {
        return BaseOrderPanel(order).apply {

            overlayManager = OverlayManager
            layout = BorderLayout()  // 전체 레이아웃을 BorderLayout으로 설정

            border = BorderFactory.createEmptyBorder(0, 0, 0, 0)

            // 1. headerPanel의 오른쪽에 프린트 버튼 추가
            val buttonPanel = JPanel().apply {
                layout = FlowLayout(FlowLayout.RIGHT, 0, 0)  // 오른쪽 정렬
                background = Color.WHITE  // 배경색 설정
                border = BorderFactory.createEmptyBorder(15, 0, 0, 0)

                // 프린터 버튼
                add(createPrintButton())
                add(Box.createRigidArea(Dimension(15, 0)))
                add(
                    createCancelButton(
                        overlayManager = overlayManager,
                        _cardPanel = _cardPanel,
                        order = order,
                    )
                )
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

                    // 초기 상태에 따라 적절한 버튼을 추가
                    if (order.isPickupCompleted) {
                        add(createPickupCompleteButton(order))
                    } else if (order.isPickupWait) {
                        add(createPickupWaitWidget(order))
                    } else if (order.isOnDelivery) {
                        add(createOnDelivery(order))
                    } else {
                        // 기본 조리 완료 버튼 추가
                        val cookedButton = createCookedButton(order = order)

                        // 둥근 패널과 프로그레스바 통합 클래스 적용
                        val roundedProgressBar = RoundedProgressBar(0, totalTime).apply {
                            preferredSize = Dimension(255, 154)  // 전체 패널 크기
                        }

                        // GridBagConstraints로 각 컴포넌트를 독립적으로 배치
                        val gbc = GridBagConstraints().apply {
                            fill = GridBagConstraints.HORIZONTAL  // 컴포넌트가 가로로 꽉 차도록 설정
                            gridx = 0
                            insets = Insets(0, 0, 5, 0)  // 여백 줄이기
                            weightx = 1.0  // 가로 방향으로 크기를 동일하게 배분
                            weighty = 0.0  // 세로 방향 여분 공간을 없애기
                        }

                        add(cookedButton, gbc.apply { gridy = 0 })  // 첫 번째 행에 조리 완료 버튼 배치
                        add(Box.createRigidArea(Dimension(0, 0)), gbc.apply { gridy = 1 })
                        add(roundedProgressBar, gbc.apply { gridy = 2 })  // 두 번째 행에 프로그레스바 배치

                        // 프로그레스바 업데이트 로직
                        order.addTimerObserver(object : OrderObserver {
                            override fun update(order: ReceiveOrderModel) {
                                // 프로그레스바만 다시 그리기
                                roundedProgressBar.updateProgress(order.elapsedTime)
                                roundedProgressBar.repaint()  // 프로그레스바만 리페인트
                            }
                        })
                    }
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

    //TODO 주문 취소 버튼
    fun createCancelButton(
        overlayManager: OverlayManager,
        _cardPanel: JPanel,
        order: ReceiveOrderModel,
    ): JButton {
        return FillRoundedButton(
            text = "주문취소",
            borderColor = MyColor.GREY300,
            backgroundColor = MyColor.GREY300,
            textColor = MyColor.GREY500,
            borderRadius = 20,
            borderWidth = 1,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(10, 20, 10, 20),
            buttonSize = Dimension(130, 50),
            customFont = MyFont.Bold(20f)
        ).apply {
            addActionListener {
                overlayManager.addOverlayPanel()

                val dialog = OrderRejectCancelDialog(
                    SwingUtilities.getWindowAncestor(this) as JFrame,
                    _cardPanel,
                    "주문 취소 사유 선택",
                    "주문 취소 사유를 선택해 주세요.",
                    "주문 취소",
                    onReject = { rejectReason ->
                        val rejectOrderCommand = RejectOrderCommand(
                            order,
                            rejectReason,
                            RejectedReasonType.STORE_REJECT,
                            PosOrderStatus.PROCESSING
                        )
                        rejectOrderCommand.execute()
                    }
                )

                // 다이얼로그가 닫힐 때 오버레이 패널 제거
                dialog.addWindowListener(object : WindowAdapter() {
                    override fun windowClosed(e: WindowEvent?) {
                        overlayManager.removeOverlayPanel()
                        dialog.dispose()
                    }
                })

                // 다이얼로그 보이기
                dialog.isVisible = true
            }
        }
    }

    //TODO 조리완료 버튼
    fun createCookedButton(
        order: ReceiveOrderModel,
    ): JButton {
        return FillRoundedButton(
            text = "조리완료",
            borderColor = MyColor.LIGHT_RED,
            backgroundColor = MyColor.LIGHT_RED,
            textColor = Color.WHITE,
            borderRadius = 20,
            borderWidth = 1,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(10, 20, 10, 20),
            buttonSize = Dimension(130, 50),
            customFont = MyFont.Bold(28f)
        ).apply {
            addActionListener {
                if (order.orderReceiveType == OrderReceiveType.DELIVERY.name) {
                    print("픽업대기 버튼으로 변경해!!!!@")
                    order.stopTimers()
                    changePickupWaitWidget(order)
                } else {
                    print("픽업완료 버튼으로 변경해!!!!@")
                    order.stopTimers()
                    changePickupCompleteButton(order)
                }
                //API호출
//                val cookedCommand = CookedCommand(order)
//                cookedCommand.execute()
            }
        }
    }


    // 주문 상태에 따라 UI 업데이트
    fun simulateOrderEvents(order: ReceiveOrderModel, eventListener: OrderEventListener) {

        //배달대행사 재전송 버튼으로 변경
        if (order.isResent) {
            eventListener.onResendOrder(order)
            return
        }

        if(order.isPickupWait){
            eventListener.onPickUpWait(order)
            return
        }

        // 픽업 대기 상태에서 배달 중으로 변경
        //TODO 배달타입이고 , 주문이배달중이고, 주문이 요리완료 일때 시그널 받을 수 있음
        if (order.orderReceiveType == OrderReceiveType.DELIVERY.name &&
            order.isOnDelivery &&
            order.posOrderStatusType == ServerOrderStatus.COOKED.name
        ) {
            eventListener.onDelivery(order)
            return
        }

        //주문완료 처리
//        if(order.){
//            return
//        }

        // 주문 번호에 따라 이벤트 타이머 설정
//        if (order.orderReceiveType == OrderReceiveType.DELIVERY.name) {
//            order.initializeEventTimer(10000) {
//                eventListener.onDelivery(order)
//            }
//        }
//        if (order.id % 2 == 0) {
//            order.initializeEventTimer(7000) {
//                eventListener.onResendOrder(order)
//            }
//        } else {
//            order.initializeEventTimer(7000) {
//                eventListener.onCompleteOrder(order)
//            }
//        }

    }

    override fun onPickUpWait(order: ReceiveOrderModel) {
        println("onPickUpWait")
    }

    // Resend Order 이벤트 처리: 프로그레스바를 버튼으로 변환
    override fun onResendOrder(order: ReceiveOrderModel) {
        order.isResent = true
        // '주문취소' 버튼 생성
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
            buttonSize = Dimension(255, 232),
            customFont = MyFont.Bold(28f)
        ).apply {
            addActionListener {
                // 배달대행사로 주문번호 재전송 api 호출
                println("Resend Order for #${order.orderNumber}")
            }
        }

        println("onResendOrder called")

        rightPanel.removeAll()
        rightPanel.add(resendOrderButton)
        rightPanel.revalidate()
        rightPanel.repaint()
    }

    //TODO 배달중 위젯
    fun createOnDelivery(order: ReceiveOrderModel): FillRoundedButton {
        return FillRoundedButton(
            text = "배달중",
            borderColor = MyColor.LIGHT_BLUE_200,
            backgroundColor = MyColor.LIGHT_BLUE_200,
            textColor = Color.WHITE,
            borderRadius = 20,
            borderWidth = 1,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(10, 20, 10, 20),
            buttonSize = Dimension(255, 232),
            customFont = MyFont.Bold(34f)
        )
    }

    //TODO 배달중 위젯으로 변경
    override fun onDelivery(order: ReceiveOrderModel) {
        order.isOnDelivery = true
        order.isPickupWait = false
        val resendOrderButton = createOnDelivery(order)

        println("onDelivery called")

        rightPanel.removeAll()
        rightPanel.add(resendOrderButton)
        rightPanel.revalidate()
        rightPanel.repaint()
    }

    //TODO 픽업 대기중 버튼
    fun createPickupWaitWidget(order: ReceiveOrderModel): FillRoundedButton {
        return FillRoundedButton(
            text = "픽업 대기중",  // 줄바꿈을 위해 "\n" 사용
            borderColor = MyColor.Yellow_200,
            backgroundColor = MyColor.Yellow_200,
            textColor = Color.WHITE,  // 텍스트 색상 (핑크)
            borderRadius = 20,  // 둥근 버튼
            borderWidth = 2,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(10, 20, 10, 20),
            buttonSize = Dimension(255, 232),  // 크기 설정
            customFont = MyFont.Bold(36f)  // 커스텀 폰트 설정
        ).apply {

        }
    }

    //TODO 픽업 대기중 버튼 업데이트
    fun changePickupWaitWidget(order: ReceiveOrderModel) {
        order.isPickupWait = true
        rightPanel.border = BorderFactory.createEmptyBorder(-15, 0, 0, 0)
        // 주문 완료 버튼 생성
        val completeOrderButton = createPickupWaitWidget(order)

        // rightPanel을 사용해 프로그레스바를 제거하고 완료 버튼 추가
        rightPanel.removeAll()
        rightPanel.add(completeOrderButton)
        rightPanel.revalidate()
        rightPanel.repaint()
    }

    //TODO 픽업 완료 버튼
    fun createPickupCompleteButton(order: ReceiveOrderModel): FillRoundedButton {
        return FillRoundedButton(
            text = "픽업 완료",  // 줄바꿈을 위해 "\n" 사용
            borderColor = MyColor.LIGHT_RED,
            backgroundColor = MyColor.LIGHT_RED,
            textColor = Color.WHITE,  // 텍스트 색상 (핑크)
            borderRadius = 20,  // 둥근 버튼
            borderWidth = 2,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(10, 20, 10, 20),
            buttonSize = Dimension(255, 232),  // 크기 설정
            customFont = MyFont.Bold(36f)  // 커스텀 폰트 설정
        ).apply {}
    }

    //TODO 픽업 완료 버튼 업데이트
    fun changePickupCompleteButton(order: ReceiveOrderModel) {
        order.isPickupCompleted = true
        rightPanel.border = BorderFactory.createEmptyBorder(-15, 0, 0, 0)
        // 주문 완료 버튼 생성
        val completeOrderButton = createPickupCompleteButton(order)

        // rightPanel을 사용해 프로그레스바를 제거하고 완료 버튼 추가
        rightPanel.removeAll()
        rightPanel.add(completeOrderButton)
        rightPanel.revalidate()
        rightPanel.repaint()
    }

    //TODO 프린터 버튼
    fun createPrintButton(): FillRoundedButton {
        return FillRoundedButton(
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
        }
    }
}





