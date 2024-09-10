package org.example.view.states

import org.example.command.AcceptOrderCommand
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
                add(JButton("주문 거절").apply {
                    font = Font("Arial", Font.PLAIN, 14)
                })
                add(JButton("접수하기").apply {
                    font = Font("Arial", Font.BOLD, 16)
                    background = Color(255, 153, 51)
                    foreground = Color.WHITE
                    isOpaque = true
                    border = BorderFactory.createEmptyBorder()

                    addActionListener {
                        // CookingTimeDialog 생성
                        CookingTimeDialog(SwingUtilities.getWindowAncestor(this) as JFrame) { cookingTime ->
                            // CookingTimeDialog 닫기 후 DeliveryTimeDialog 생성
                            DeliveryTimeDialog(SwingUtilities.getWindowAncestor(this) as JFrame, cookingTime) { deliveryTime ->
                                // AcceptOrderCommand 실행 후 주문 상태 업데이트 및 UI 갱신
                                val acceptOrderCommand = AcceptOrderCommand(order, cookingTime, deliveryTime)
                                acceptOrderCommand.execute()  // 상태 변경 및 notifyObservers 호출됨
                            }.isVisible = true
                        }.isVisible = true
                    }
                })
            }
            add(rightPanel, BorderLayout.EAST)
        }
    }
}


