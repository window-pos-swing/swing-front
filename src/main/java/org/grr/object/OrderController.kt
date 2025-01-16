package org.grr.`object`

import org.grr.enum.OrderReceiveType
import org.grr.screen.main.main_widget.tab_manager.CustomTabbedPane
import org.grr.enum.PosOrderStatus.*
import org.grr.enum.ServerOrderStatus
import org.grr.model.ReceiveOrderModel
import org.grr.observer.OrderObserver
import org.grr.screen.main.main_widget.order_states_ui.CompletedState
import org.grr.screen.main.main_widget.order_states_ui.ProcessingState
import org.grr.screen.main.main_widget.order_states_ui.RejectedState
import javax.swing.JPanel

object OrderController {
    lateinit var tabbedPane: CustomTabbedPane

    // 싱글톤 초기화
    fun initialize(tabbedPane: CustomTabbedPane) {
        OrderController.tabbedPane = tabbedPane
    }

    // 주문 추가
    fun addNewOrder(order: ReceiveOrderModel) {
        // 상태 옵저버 등록
        order.addStateObserver(object : OrderObserver {
            override fun update(order: ReceiveOrderModel) {
                handleOrderStateChange(order)
            }
        })

        // '전체보기'와 '접수대기' 탭에 각각 다른 프레임을 생성해서 추가 !!! @@ 이렇게 안하면 한곳에는 안생겨요
        val allOrdersFrame = tabbedPane.createOrderFrame(order) // 전체보기용 프레임
        val pendingOrdersFrame = tabbedPane.createOrderFrame(order) // 접수대기용 프레임
        val pendingOrderTypeOrdersFrame = tabbedPane.createOrderFrame(order) // 접수대기용 프레임

        tabbedPane.addOrderToAllOrders(allOrdersFrame, false)  // 전체보기 탭에 추가
        tabbedPane.updateOrderInAllOrders(order)
        tabbedPane.addOrderToPending(pendingOrdersFrame, pendingOrderTypeOrdersFrame,order)  // 접수대기 탭에 추가

        println("주문 추가")
    }

    // 상태 변화에 따른 주문 처리
    fun handleOrderStateChange(order: ReceiveOrderModel) {
        println("handleOrderStateChange")
        when (order.state) {
            is ProcessingState -> {
                if (!isOrderInProcessing(order)) {
                    moveOrderToProcessing(order)
                }
            }

            is RejectedState -> {
                moveOrderToReject(order)
            }

            is CompletedState -> {
                moveOrderToCompleted(order)
            }
        }
    }

    // CustomTabbedPane 클래스에 해당 주문이 이미 처리중 상태인지 확인하는 메서드 추가
    fun isOrderInProcessing(order: ReceiveOrderModel): Boolean {
        // 처리중 주문 리스트에서 해당 주문이 이미 존재하는지 확인
        return tabbedPane.processingOrdersPanel.components
            .filterIsInstance<JPanel>()
            .any { it.getClientProperty("orderNumber") == order.orderNumber }
    }


    private fun moveOrderToProcessing(order: ReceiveOrderModel) {
        println("moveOrderToProcessing")

        val processingOrderFrame = tabbedPane.createOrderFrame(order, forProcessing = true)
        val processingTypeOrderFrame = tabbedPane.createOrderFrame(order, forProcessing = true)
        val allOrder = OrderListSingleTon.findOrderByNumber("allOrders", order.orderNumber)

        tabbedPane.addOrderToProcessing(processingOrderFrame,processingTypeOrderFrame, order)
        tabbedPane.updateOrderInAllOrders(order)
        tabbedPane.removeOrderFromPending(order)
        if(allOrder != null){
            tabbedPane.updateOrderInAllOrders(allOrder)
            tabbedPane.removeOrderFromPending(allOrder)
        }
        tabbedPane.pendingSubTabs.updateCounts()
        tabbedPane.processingSubTabs.updateCounts()
    }

    private fun moveOrderToCompleted(order: ReceiveOrderModel) {
        println("moveOrderToCompleted")
        OrderListSingleTon.counts["processingOrders"] = (OrderListSingleTon.counts["processingOrders"] ?: 0) - 1
        OrderListSingleTon.counts["completedOrders"] = (OrderListSingleTon.counts["completedOrders"] ?: 0) + 1
        val removeOrder = OrderListSingleTon.orders["processingOrders"]?.find { it.orderNumber == order.orderNumber }
        val allOrder = OrderListSingleTon.findOrderByNumber("allOrders", order.orderNumber)
        OrderListSingleTon.orders["processingOrders"]?.remove(removeOrder)
        OrderListSingleTon.orders["completedOrders"]?.add(0,order)
        tabbedPane.updateOrderInAllOrders(order)
        tabbedPane.removeOrderFromProcessing(order)
        val completedOrderFrame = tabbedPane.createOrderFrame(order, forProcessing = true)
        tabbedPane.addOrderToCompleted(completedOrderFrame)
    }

    private fun moveOrderToReject(order: ReceiveOrderModel) {
        println("moveOrderToReject")
        val rejectedState = order.state as RejectedState
        updateOrderUIInAllOrders(order)

        val rejectedOrderFrame = tabbedPane.createOrderFrame(order)

        when (rejectedState.rejectPanel) {
            WAITING -> {
                OrderListSingleTon.counts["pendingOrders"] = (OrderListSingleTon.counts["pendingOrders"] ?: 0) - 1
                OrderListSingleTon.counts["rejectStoreOrders"] = (OrderListSingleTon.counts["rejectStoreOrders"] ?: 0) + 1
                val removeOrder = OrderListSingleTon.findOrderByNumber("pendingOrders", order.orderNumber)
                if(removeOrder != null) {
                    OrderListSingleTon.orders["pendingOrders"]?.remove(removeOrder)
                    OrderListSingleTon.orders["rejectStoreOrders"]?.add(0,order)
                }
                tabbedPane.removeOrderFromPending(order)
                tabbedPane.addOrderToRejected(rejectedOrderFrame)
            }

            IN_PROGRESS -> {
                OrderListSingleTon.counts["processingOrders"] = (OrderListSingleTon.counts["processingOrders"] ?: 0) - 1
                OrderListSingleTon.counts["rejectStoreOrders"] = (OrderListSingleTon.counts["rejectStoreOrders"] ?: 0) + 1
                val removeOrder = OrderListSingleTon.findOrderByNumber("processingOrders", order.orderNumber)
                if(removeOrder != null) {
                    OrderListSingleTon.orders["processingOrders"]?.remove(removeOrder)
                    OrderListSingleTon.orders["rejectStoreOrders"]?.add(0,order)
                }
                tabbedPane.removeOrderFromProcessing(order)
                tabbedPane.addOrderToRejected(rejectedOrderFrame)
            }

            else -> println("Unhandled state for rejection: ${rejectedState.rejectPanel}")
        }
    }

    private fun updateOrderUIInAllOrders(order: ReceiveOrderModel) {
        tabbedPane.updateOrderInAllOrders(order)
    }

    fun onOrderStateChanged(order: ReceiveOrderModel) {
        when (order.state) {
            is ProcessingState -> {
                order.startTimer((order.state as ProcessingState).totalTime)
            }
        }
    }

    fun initializeOrders(orders: List<ReceiveOrderModel>) {
        orders.forEach { order ->
            val forProcessing =
                if (order.posOrderStatusType == ServerOrderStatus.COOKING.name || order.posOrderStatusType == ServerOrderStatus.ACCEPT.name) true else false
            val orderFrame = tabbedPane.createOrderFrame(order, forProcessing)
            if(!forProcessing){
                tabbedPane.addOrderToAllOrders(orderFrame, true)
                tabbedPane.updateOrderInAllOrders(order)
            }else{
                println("타이머 시작!!")
                tabbedPane.updateOrderInAllOrders(order)
                tabbedPane.addOrderToAllOrders(orderFrame, true)
            }
        }
    }
}
