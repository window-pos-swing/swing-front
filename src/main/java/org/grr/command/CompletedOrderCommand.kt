package org.grr.command

import Command
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.grr.api.OrderAPI
import org.grr.enum.OrderReceiveType
import org.grr.enum.PosOrderStatus
import org.grr.enum.ServerOrderStatus
import org.grr.model.OrderFilter
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.FormManager
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.order_states_ui.CompletedState
import javax.swing.JOptionPane

class CompletedOrderCommand(
    private val order: ReceiveOrderModel,
) : Command {
    override fun execute() {

        if(OrderListSingleTon.pageNumbers["processingOrders"]!! > 0){
            OrderListSingleTon.pageNumbers["processingOrders"] = (OrderListSingleTon.pageNumbers["processingOrders"] ?: 0) - 1
        }
        if(order.orderReceiveType == OrderReceiveType.DELIVERY.name){
            if(OrderListSingleTon.pageNumbers["processingDeliveryOrders"]!! > 0){
                OrderListSingleTon.pageNumbers["processingDeliveryOrders"] = (OrderListSingleTon.pageNumbers["processingDeliveryOrders"] ?: 0) - 1
            }
        }else{
            if(OrderListSingleTon.pageNumbers["processingTakeOutOrders"]!! > 0 ){
                OrderListSingleTon.pageNumbers["processingTakeOutOrders"] = (OrderListSingleTon.pageNumbers["processingTakeOutOrders"] ?: 0) - 1
            }
        }


        // 주문 상태를 RejectedState로 변경 (거절 사유와 원래 상태 포함)
        val status = if (order.orderReceiveType == OrderReceiveType.DELIVERY.name) ServerOrderStatus.DELIVERY_COMPLETE else ServerOrderStatus.PICKUP_COMPLETE
        val result = OrderAPI().orderStatusChangeToServer(
            status,
            orderId = order.orderId,
        )
        // 결과 처리
        if (!result.first) {
            JOptionPane.showMessageDialog(null, "주문접수 실패: ${result.second}", "오류", JOptionPane.ERROR_MESSAGE)
            return
        }
        OrderListSingleTon.counts["processingOrders"] = (OrderListSingleTon.counts["processingOrders"] ?: 0) - 1
        val processingOrder = OrderListSingleTon.findOrderByNumber("processingOrders", order.orderNumber)
        if (processingOrder != null) {
            OrderListSingleTon.orders["processingOrders"]?.remove(processingOrder)
            if (order.orderReceiveType == OrderReceiveType.DELIVERY.name) {
                OrderListSingleTon.orders["processingDeliveryOrders"]?.remove(processingOrder)
            } else if (order.orderReceiveType == OrderReceiveType.TAKEOUT.name) {
                OrderListSingleTon.orders["processingTakeOutOrders"]?.remove(processingOrder)
            }
        }

        OrderListSingleTon.orders["completedOrders"]?.add(0,order)
        OrderListSingleTon.counts["completedOrders"] = ( OrderListSingleTon.counts["completedOrders"] ?:0) + 1
        if(order.orderReceiveType == OrderReceiveType.DELIVERY.name){
            OrderListSingleTon.orders["completedDeliveryOrders"]?.add(0,order)
            OrderListSingleTon.counts["completedDeliveryOrders"] = ( OrderListSingleTon.counts["completedDeliveryOrders"] ?:0) + 1
        }else if(order.orderReceiveType == OrderReceiveType.TAKEOUT.name){
            OrderListSingleTon.orders["completedTakeOutOrders"]?.add(0,order)
            OrderListSingleTon.counts["completedTakeOutOrders"] = ( OrderListSingleTon.counts["completedTakeOutOrders"] ?:0) + 1
        }

        order.changeState(CompletedState())
        val allOrder = OrderListSingleTon.findOrderByNumber("allOrders", order.orderNumber)
        allOrder?.changeState(CompletedState())
        println("[주문] #${order.orderNumber} 주문완료 상태로 변경")

    }

}