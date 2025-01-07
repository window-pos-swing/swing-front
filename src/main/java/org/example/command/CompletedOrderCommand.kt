package org.example.command

import Command
import org.example.model.Order
import org.example.model.OrderState
import org.example.view.states.CompletedState
import org.example.view.states.RejectedState

class CompletedOrderCommand(
    private val order: Order,
) : Command {
    override fun execute() {
        // 주문 상태를 RejectedState로 변경 (거절 사유와 원래 상태 포함)
        order.changeState(CompletedState())

        // 옵저버들에게 상태 변경 알림
        order.notifyStateObservers()
        println("[주문] #${order.orderNumber} 주문완료 상태로 변경")
    }
}