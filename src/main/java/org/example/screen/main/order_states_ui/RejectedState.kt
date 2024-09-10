package org.example.view.states


import org.example.model.Order
import org.example.model.OrderState
import javax.swing.JLabel
import javax.swing.JPanel
import java.awt.BorderLayout

class RejectedState : OrderState {
    override fun handle(order: Order) {
        // Rejected 상태 처리 로직
    }

    override fun getUI(order: Order): JPanel {
        return JPanel().apply {
            layout = BorderLayout()
            add(JLabel("주문 거절 주문번호: ${order.orderNumber}"), BorderLayout.NORTH)
        }
    }
}
