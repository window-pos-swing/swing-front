package org.example.view.states

import OrderRejectCancelDialog
import org.example.command.AcceptOrderCommand
import org.example.command.RejectOrderCommand
import org.example.model.Order
import org.example.model.OrderState
import org.example.view.components.BaseOrderPanel
import org.example.view.dialog.CookingTimeDialog
import org.example.view.dialog.DeliveryTimeDialog
import javax.swing.*
import java.awt.*

class PendingState : OrderState {
    override fun handle(order: Order) {
        // 접수대기 상태에서 필요한 처리
    }

    override fun getUI(order: Order): JPanel {
        return BaseOrderPanel(order).apply {
            val rightPanel = JPanel().apply {
                layout = GridLayout(2, 1, 5, 5)

                // 주문 거절 버튼: 주문 거절 다이얼로그 호출
                add(JButton("주문 거절").apply {
                    font = Font("Arial", Font.PLAIN, 14)
                    addActionListener {
                        OrderRejectCancelDialog(
                            SwingUtilities.getWindowAncestor(this) as JFrame,
                            "주문 거절 사유 선택",
                            "주문 거절 사유를 선택해 주세요.",
                            "주문 거절",
                            onReject = { rejectReason ->
                                val rejectOrderCommand = RejectOrderCommand(order, rejectReason, this@PendingState)
                                rejectOrderCommand.execute()
                            }
                        )
                    }
                })

                // 접수하기 버튼: CookingTimeDialog -> DeliveryTimeDialog 순으로 호출
                add(JButton("접수하기").apply {
                    font = Font("Arial", Font.BOLD, 16)
                    background = Color(255, 153, 51)
                    foreground = Color.WHITE
                    isOpaque = true
                    border = BorderFactory.createEmptyBorder()

                    addActionListener {
                        CookingTimeDialog(SwingUtilities.getWindowAncestor(this) as JFrame) { cookingTime ->
                            DeliveryTimeDialog(SwingUtilities.getWindowAncestor(this) as JFrame, cookingTime) { deliveryTime ->
                                val acceptOrderCommand = AcceptOrderCommand(order, cookingTime, deliveryTime)
                                acceptOrderCommand.execute()  // 상태 변경 및 notifyObservers 호출됨
                            }.isVisible = true
                        }.isVisible = true
                    }
                })
            }
//            add(rightPanel, BorderLayout.EAST)
        }
    }
}



