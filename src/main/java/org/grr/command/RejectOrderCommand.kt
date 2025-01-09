package org.grr.command

import Command
import org.grr.api.OrderAPI
import org.grr.enum.OrderReceiveType
import org.grr.enum.PosOrderStatus
import org.grr.enum.ServerOrderStatus
import org.grr.model.ReceiveOrderModel
import org.grr.screen.main.main_widget.order_states_ui.RejectedState
import javax.swing.JOptionPane

enum class RejectedReasonType {
    CUSTOMER_CANCEL,
    STORE_REJECT,
    REFUND
}

class RejectOrderCommand(
    private val order: ReceiveOrderModel,
    private val rejectReason: String,
    private val rejectType: RejectedReasonType, // 원래 상태 (PendingState 또는 ProcessingState)
    private val rejectPanel : PosOrderStatus
) : Command {
    override fun execute() {
        val result = OrderAPI().orderStatusChangeToServer(
            ServerOrderStatus.STORE_CANCEL,
            reason = rejectReason,
            orderId = order.orderId,
        )
        // 결과 처리
        if (!result.first) {
            JOptionPane.showMessageDialog(null, "주문접수 실패: ${result.second}", "오류", JOptionPane.ERROR_MESSAGE)
            return
        }
        val rejectDate = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd")) // HH:mm뺌
        // 주문 상태를 RejectedState로 변경 (거절 사유와 원래 상태 포함)
        order.changeState(RejectedState(rejectReason, rejectDate, rejectType, rejectPanel))
        println("[주문] #${order.orderNumber} 거절상태로 변경 with reason: [$rejectType] - $rejectReason at $rejectDate")
    }
}