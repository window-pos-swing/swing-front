package org.grr.command

import Command
import org.grr.api.OrderAPI
import org.grr.enum.OrderReceiveType
import org.grr.enum.PosOrderStatus
import org.grr.enum.ServerOrderStatus
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.order_states_ui.PendingState
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
    private val rejectPanel: PosOrderStatus
) : Command {
    override fun execute() {
        if(rejectPanel == PosOrderStatus.IN_PROGRESS){
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
        }else if(rejectPanel == PosOrderStatus.WAITING){

            if(OrderListSingleTon.pageNumbers["pendingOrders"]!! > 0){
                OrderListSingleTon.pageNumbers["pendingOrders"] = (OrderListSingleTon.pageNumbers["pendingOrders"] ?: 0) - 1
            }
            if(order.orderReceiveType == OrderReceiveType.DELIVERY.name){
                if(OrderListSingleTon.pageNumbers["pendingDeliveryOrders"]!! > 0){
                    OrderListSingleTon.pageNumbers["pendingDeliveryOrders"] = (OrderListSingleTon.pageNumbers["pendingDeliveryOrders"] ?: 0) - 1
                }
            }else{
                if(OrderListSingleTon.pageNumbers["pendingTakeOutOrders"]!! > 0 ){
                    OrderListSingleTon.pageNumbers["pendingTakeOutOrders"] = (OrderListSingleTon.pageNumbers["pendingTakeOutOrders"] ?: 0) - 1
                }
            }
        }

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
        val rejectDate =
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd")) // HH:mm뺌
        // 주문 상태를 RejectedState로 변경 (거절 사유와 원래 상태 포함)


        val allOrder = OrderListSingleTon.findOrderByNumber("allOrders", order.orderNumber)
        allOrder?.changeState(RejectedState(rejectReason, rejectDate, rejectType, rejectPanel))
        if (rejectPanel == PosOrderStatus.WAITING) {
            val pendingOrder = OrderListSingleTon.findOrderByNumber("pendingOrders", order.orderNumber)
            pendingOrder?.changeState(RejectedState(rejectReason, rejectDate, rejectType, rejectPanel))
        } else {
            val processOrder = OrderListSingleTon.findOrderByNumber("processingOrders", order.orderNumber)
            processOrder?.changeState(RejectedState(rejectReason, rejectDate, rejectType, rejectPanel))
        }
//        order.changeState(RejectedState(rejectReason, rejectDate, rejectType, rejectPanel))
        println("[주문] #${order.orderNumber} 거절상태로 변경 with reason: [$rejectType] - $rejectReason at $rejectDate")

    }

}