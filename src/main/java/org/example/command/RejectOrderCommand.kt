package org.example.command

import Command
import org.example.model.Order
import org.example.model.OrderState
import org.example.view.states.RejectedState


class RejectOrderCommand(
    private val order: Order,
    private val rejectReason: String,
    private val originState: OrderState  // 원래 상태 (PendingState 또는 ProcessingState)
) : Command {
    override fun execute() {
        val rejectDate = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd")) // HH:mm뺌

        // 주문 상태를 RejectedState로 변경 (거절 사유와 원래 상태 포함)
        order.changeState(RejectedState(rejectReason, rejectDate, originState))

        // 옵저버들에게 상태 변경 알림
        order.notifyStateObservers()
        println("[주문] #${order.orderNumber} 거절상태로 변경  with reason: $rejectReason at $rejectDate")
    }
}