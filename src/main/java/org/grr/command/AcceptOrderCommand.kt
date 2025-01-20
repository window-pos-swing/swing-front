package org.grr.command

import Command
import org.grr.`object`.OrderController
import kotlinx.coroutines.*
import org.grr.api.OrderAPI
import org.grr.enum.OrderReceiveType
import org.grr.enum.PosOrderStatus
import org.grr.enum.ServerOrderStatus
import org.grr.model.OrderFilter
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
            if(order.state is RejectedState || order.state is CompletedState || order.isPickupWait) { return@launch }
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

    private fun fetchAdditionalOrdersIfNeeded(orderKey: String, filter: OrderFilter) {
        val remainingCount = OrderListSingleTon.orders[orderKey]?.size ?: 0
        val totalCount = OrderListSingleTon.counts[orderKey] ?: 0

        // 남은 데이터가 PAGE_SIZE보다 작으면 페이징 요청
        if (remainingCount < OrderListSingleTon.PAGE_SIZE && totalCount > remainingCount) {
            val currentPage = OrderListSingleTon.pageNumbers[orderKey] ?: 0
            val newPageNumber = if (currentPage > 0) currentPage - 1 else currentPage
            OrderListSingleTon.pageNumbers[orderKey] = newPageNumber
            println("[$orderKey] 요청할 페이지 번호: $newPageNumber")

            // 16:35, 16: 28, 16: 27, 16: 21, 16: 13, 15: 24
            CoroutineScope(Dispatchers.IO).launch {
                // 1초 딜레이 추가
                val result = OrderAPI().fetchOrders(parent, cardPanel, filter, newPageNumber)
                if (!result.first) {
                    println("[$orderKey] 페이징 데이터 요청 실패: ${result.second}")
                }
            }
        }
    }

    private fun updateOrderStateToProcessing(deliveryTime: Int, cookTime: Int, orderReceiveType: String) {
        // 상태 변경 및 컨트롤러 업데이트
        SwingUtilities.invokeLater {
            OrderListSingleTon.counts["processingOrders"] = (OrderListSingleTon.counts["processingOrders"] ?: 0) + 1
            OrderListSingleTon.counts["pendingOrders"] = (OrderListSingleTon.counts["pendingOrders"] ?: 0) - 1
            // 주문 타입별 카운트 업데이트

            var targetOrder=  OrderListSingleTon.orders["allOrders"]?.find { it.orderNumber == order.orderNumber }
            // 다른 컬렉션에서 검색
            if (targetOrder == null) {
                targetOrder = OrderListSingleTon.orders["pendingOrders"]?.find { it.orderNumber == order.orderNumber }
                targetOrder = targetOrder ?: OrderListSingleTon.orders["pendingDeliveryOrders"]?.find { it.orderNumber == order.orderNumber }
                targetOrder = targetOrder ?: OrderListSingleTon.orders["pendingTakeOutOrders"]?.find { it.orderNumber == order.orderNumber }
            }

            // 주문이 없으면 로그 출력 후 종료
            if (targetOrder == null) {
                println("@@@ Error: Order not found in any collection. OrderNumber: ${order.orderNumber}")
                return@invokeLater
            }

            // 상태 업데이트
            targetOrder.changeState(ProcessingState(cookTime + deliveryTime, parent, cardPanel))
            orderController.onOrderStateChanged(targetOrder)

            println("===========================================================================")
            println("[AcceptOrderCommand] #${order.orderNumber} 접수처리중으로 상태 변경 with total time: ${cookTime + deliveryTime} minutes")
            println("[CookTime] $cookTime")
            println("[DeliveryTime] $deliveryTime")
            println("===========================================================================")

            fetchAdditionalOrdersIfNeeded("pendingOrders", OrderFilter(posOrderStatus = PosOrderStatus.WAITING))
            fetchAdditionalOrdersIfNeeded(
                "pendingDeliveryOrders",
                OrderFilter(posOrderStatus = PosOrderStatus.WAITING, orderReceiveType = OrderReceiveType.DELIVERY)
            )
            fetchAdditionalOrdersIfNeeded(
                "pendingTakeOutOrders",
                OrderFilter(posOrderStatus = PosOrderStatus.WAITING, orderReceiveType = OrderReceiveType.TAKEOUT)
            )
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
