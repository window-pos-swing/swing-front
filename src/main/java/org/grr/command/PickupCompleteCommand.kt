package org.grr.command

import Command
import org.grr.model.ReceiveOrderModel
import org.grr.screen.main.main_widget.order_states_ui.CompletedState

class PickupCompleteCommand(
    private val order: ReceiveOrderModel,
) : Command {
    override fun execute() {
        order.changeState(CompletedState())
        println("[주문] #${order.orderNumber} 조리완료 상태 변경")
    }
}