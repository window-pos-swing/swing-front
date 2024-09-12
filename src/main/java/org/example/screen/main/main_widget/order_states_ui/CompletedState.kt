package org.example.view.states


import org.example.model.Order
import org.example.model.OrderState
import javax.swing.JLabel
import javax.swing.JPanel
import java.awt.BorderLayout

class CompletedState : OrderState {
    override fun handle(order: Order) {
        // Completed 상태 처리 로직
    }

    override fun getUI(order: Order): JPanel {
        return JPanel().apply {
            layout = BorderLayout()
            add(JLabel("접수완료 주문번호: ${order.orderNumber}"), BorderLayout.NORTH)
        }
    }
}
