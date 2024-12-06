package org.grr.command

import Command
import OrderController
import org.grr.model.Order
import org.grr.screen.main.main_widget.order_states_ui.ProcessingState
import javax.swing.JFrame
import javax.swing.JPanel

class AcceptOrderCommand(
    private val parent : JFrame,
    private val cardPanel : JPanel,
    private val order: Order,      // 처리할 주문
    private val orderController: OrderController // OrderController 추가
) : Command {
    override fun execute() {
        // 주문 상태를 접수진행 상태로 변경
        order.changeState(ProcessingState(order.cookTime + order.deliveryTime , parent ,cardPanel))  // 상태 변경
        orderController.onOrderStateChanged(order)
        // 옵저버들에게 알림
        order.notifyStateObservers()  // notifyObservers는 여기서만 한 번 호출
        println("[AcceptOrderCommand] #${order.orderNumber} 접수처리중으로 상태 변경  with total time: ${order.cookTime + order.deliveryTime} minutes")
    }
}

