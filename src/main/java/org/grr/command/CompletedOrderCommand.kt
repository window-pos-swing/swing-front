package org.grr.command

import Command
import org.grr.model.ReceiveOrderModel
import org.grr.screen.main.main_widget.order_states_ui.CompletedState

class CompletedOrderCommand(
    private val order: ReceiveOrderModel,
) : Command {
    override fun execute() {
        // 주문 상태를 RejectedState로 변경 (거절 사유와 원래 상태 포함)
        order.changeState(CompletedState())
        println("[주문] #${order.orderNumber} 주문완료 상태로 변경")
    }
}