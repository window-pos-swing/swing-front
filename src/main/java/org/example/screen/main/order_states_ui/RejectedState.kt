package org.example.view.states


import org.example.model.Order
import org.example.model.OrderState
import org.example.view.components.BaseOrderPanel
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import javax.swing.*

class RejectedState(
    val rejectReason: String,
    val rejectDate: String,
    val originState: OrderState  // 거절된 원래 상태 (PendingState 또는 ProcessingState)
) : OrderState {

    override fun handle(order: Order) {
        // Rejected 상태 처리 로직
    }

    override fun getUI(order: Order): JPanel {
        return BaseOrderPanel(order).apply {
            val rightPanel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
                background = Color.WHITE

                // 거절 일시 및 이유 표시
                add(JLabel("거절 일시: $rejectDate").apply {
                    font = Font("Arial", Font.PLAIN, 14)
                })
                add(Box.createVerticalStrut(10))
                add(JLabel("거절 이유: $rejectReason").apply {
                    font = Font("Arial", Font.PLAIN, 14)
                })
            }
            add(rightPanel, BorderLayout.EAST)
        }
    }
}


