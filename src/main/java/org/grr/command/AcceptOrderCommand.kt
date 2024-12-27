package org.grr.command

import Command
import OrderController
import org.grr.model.Order
import org.grr.model.SettingModel
import org.grr.screen.main.main_widget.order_states_ui.ProcessingState
import javax.swing.JFrame
import javax.swing.JPanel

class AcceptOrderCommand(
    private val parent : JFrame,
    private val cardPanel : JPanel,
    private val order: Order,      // 처리할 주문
    private val orderController: OrderController, // OrderController 추가
    private val takeType: String,
    private val cookTime : Int = 0,
) : Command {
    // 주문 상태를 접수진행 상태로 변경
    override fun execute() {
        // 배달포장 상태에 따라 time 설정
        val deliveryTime = if (takeType == "takeOut") 0 else order.deliveryTime // takeOut 이면 배달 없으니까 0으로 설정
        val sendCookTime = if(cookTime == 0) order.cookTime else cookTime
        order.changeState(ProcessingState(sendCookTime + deliveryTime , parent ,cardPanel))  // 상태 변경
        orderController.onOrderStateChanged(order)
        // 옵저버들에게 알림
        order.notifyStateObservers()  // notifyObservers는 여기서만 한 번 호출
        println("===========================================================================")
        println("[AcceptOrderCommand] #${order.orderNumber} 접수처리중으로 상태 변경  with total time: ${sendCookTime + deliveryTime} minutes")
        println("[CookTime] $sendCookTime")
        println("[DeliveryTime] $deliveryTime")
        println("===========================================================================")
    }
}

