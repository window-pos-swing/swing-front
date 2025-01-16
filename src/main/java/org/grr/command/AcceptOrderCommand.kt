package org.grr.command

import Command
import org.grr.`object`.OrderController
import kotlinx.coroutines.*
import org.grr.api.OrderAPI
import org.grr.enum.OrderReceiveType
import org.grr.enum.ServerOrderStatus
import org.grr.model.OrderState
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderController.tabbedPane
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.order_states_ui.CompletedState
import org.grr.screen.main.main_widget.order_states_ui.ProcessingState
import org.grr.screen.main.main_widget.order_states_ui.RejectedState
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.SwingUtilities

class AcceptOrderCommand(
    private val parent: JFrame,
    private val cardPanel: JPanel,
    private val order: ReceiveOrderModel,
    private val orderController: OrderController,
    private val takeType: String,
    private val cookTime: Int = 0,
) : Command {
    override fun execute() {
        CoroutineScope(Dispatchers.IO).launch {
            // 배달포장 상태에 따라 시간 설정
            val deliveryTime = if (takeType == "takeOut") 0 else order.deliveryTime
            val sendCookTime = if (cookTime == 0) order.cookTime else cookTime

            //TODO 수락으로 변경 호출
            if (!changeOrderStatusToAccepted(deliveryTime, sendCookTime)) return@launch
            //UI update
            updateOrderStateToProcessing(deliveryTime,sendCookTime , order.orderReceiveType)

            //TODO(5초 뒤 조리중으로 변경)
            delay(5000)
            //거절 주문인지 확인
            if(order.state is RejectedState || order.state is CompletedState) { return@launch }
            changeOrderStatusToCooking(deliveryTime, sendCookTime)

        }
    }

    private suspend fun changeOrderStatusToAccepted(deliveryTime: Int, cookTime: Int): Boolean {
        val result = OrderAPI().orderStatusChangeToServer(
            ServerOrderStatus.ACCEPT,
            orderId = order.orderId,
            estimatedCookingTime = deliveryTime,
            estimatedArrivalTime = cookTime
        )

        if (!result.first) {
            SwingUtilities.invokeLater {
                JOptionPane.showMessageDialog(null, "주문접수 실패: ${result.second}", "오류", JOptionPane.ERROR_MESSAGE)
            }
            return false
        }
        return true
    }


    private fun updateOrderStateToProcessing(deliveryTime: Int, cookTime: Int, orderReceiveType: String) {
        // 상태 변경 및 컨트롤러 업데이트
        SwingUtilities.invokeLater {
            println("OrderListSingleTon.processingOrdersCount++")
            println("OrderListSingleTon.pendingOrdersCount--")
            OrderListSingleTon.counts["processingOrders"] = (OrderListSingleTon.counts["processingOrders"] ?: 0) + 1
            OrderListSingleTon.counts["pendingOrders"] = (OrderListSingleTon.counts["pendingOrders"] ?: 0) - 1
            // 주문 타입별 카운트 업데이트
            when (orderReceiveType) {
                OrderReceiveType.DELIVERY.name -> {
                    OrderListSingleTon.counts["pendingDeliveryOrders"] =
                        (OrderListSingleTon.counts["pendingDeliveryOrders"] ?: 0) - 1
                    OrderListSingleTon.counts["processingDeliveryOrders"] =
                        (OrderListSingleTon.counts["processingDeliveryOrders"] ?: 0) + 1
                }
                OrderReceiveType.TAKEOUT.name -> {
                    OrderListSingleTon.counts["pendingTakeOutOrders"] =
                        (OrderListSingleTon.counts["pendingTakeOutOrders"] ?: 0) - 1
                    OrderListSingleTon.counts["processingTakeOutOrders"] =
                        (OrderListSingleTon.counts["processingTakeOutOrders"] ?: 0) + 1
                }
            }

            val pendingOrder = OrderListSingleTon.findOrderByNumber("pendingOrders", order.orderNumber)
            if (pendingOrder != null) {
                OrderListSingleTon.orders["pendingOrders"]?.remove(pendingOrder)
                if (orderReceiveType == OrderReceiveType.DELIVERY.name) {
                    OrderListSingleTon.orders["pendingDeliveryOrders"]?.remove(pendingOrder)
                } else if (orderReceiveType == OrderReceiveType.TAKEOUT.name) {
                    OrderListSingleTon.orders["pendingTakeOutOrders"]?.remove(pendingOrder)
                }
            }

            val  allOrder=  OrderListSingleTon.orders["allOrders"]?.find { it.orderNumber == order.orderNumber }
            if(allOrder != null) {
                allOrder.changeState(ProcessingState(cookTime + deliveryTime, parent, cardPanel))
                orderController.onOrderStateChanged(allOrder)
            }

            orderController.tabbedPane.pendingSubTabs.updateCounts()
            orderController.tabbedPane.processingSubTabs.updateCounts()
            println("===========================================================================")
            println("[AcceptOrderCommand] #${order.orderNumber} 접수처리중으로 상태 변경 with total time: ${cookTime + deliveryTime} minutes")
            println("[CookTime] $cookTime")
            println("[DeliveryTime] $deliveryTime")
            println("===========================================================================")
        }
    }

    private suspend fun changeOrderStatusToCooking(deliveryTime: Int, cookTime: Int) {
        val result = OrderAPI().orderStatusChangeToServer(
            ServerOrderStatus.COOKING,
            orderId = order.orderId,
            estimatedCookingTime = deliveryTime,
            estimatedArrivalTime = cookTime
        )

        if (!result.first) {
            SwingUtilities.invokeLater {
                JOptionPane.showMessageDialog(null, "조리 상태 전환 실패: ${result.second}", "오류", JOptionPane.ERROR_MESSAGE)
            }
        }
    }

}
