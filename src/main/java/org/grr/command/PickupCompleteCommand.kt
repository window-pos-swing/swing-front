package org.grr.command

import Command
import org.grr.api.OrderAPI
import org.grr.enum.ServerOrderStatus
import org.grr.model.ReceiveOrderModel
import org.grr.screen.main.main_widget.order_states_ui.CompletedState
import javax.swing.JOptionPane

class PickupCompleteCommand(
    private val order: ReceiveOrderModel,
) : Command {
    override fun execute() {
        val result = OrderAPI().orderStatusChangeToServer(
            ServerOrderStatus.PICKUP_COMPLETE,
            orderId = order.orderId,
        )
        // 결과 처리
        if (!result.first) {
            JOptionPane.showMessageDialog(null, "픽업완료 실패: ${result.second}", "오류", JOptionPane.ERROR_MESSAGE)
            return
        }
        println("[주문] #${order.orderNumber} 픽업완료 상태로 변경")
    }
}