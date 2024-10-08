package org.example.command

import Command
import OrderController
import org.example.model.Order
import org.example.view.states.ProcessingState

class AcceptOrderCommand(
    private val order: Order,      // 처리할 주문
    private val cookingTime: Int,   // 조리 시간
    private val deliveryTime: Int,   // 배달 시간
    private val orderController: OrderController  // OrderController 추가
) : Command {
    override fun execute() {
        // 주문 상태를 접수진행 상태로 변경
        order.changeState(ProcessingState(cookingTime + deliveryTime))  // 상태 변경
        orderController.onOrderStateChanged(order)
        // 옵저버들에게 알림
        order.notifyStateObservers()  // notifyObservers는 여기서만 한 번 호출
        println("[주문] #${order.orderNumber} 접수처리중으로 상태 변경  with total time: ${cookingTime + deliveryTime} minutes")
    }
}

