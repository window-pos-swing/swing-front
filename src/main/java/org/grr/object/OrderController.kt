package org.grr.`object`

import org.grr.screen.main.main_widget.tab_manager.CustomTabbedPane
import org.grr.enum.ServerOrderStatus
import org.grr.model.ReceiveOrderModel
import org.grr.screen.main.main_widget.order_states_ui.ProcessingState


object OrderController {
    lateinit var tabbedPane: CustomTabbedPane
    var isLoading : Boolean = false

    // 싱글톤 초기화
    fun initialize(tabbedPane: CustomTabbedPane) {
        OrderController.tabbedPane = tabbedPane
    }

    // 주문 추가
    fun addNewOrder() {
        println("주문 추가")
        tabbedPane.setTab("전체보기")
        tabbedPane.setTab("접수대기")
    }

    fun onOrderStateChanged(order: ReceiveOrderModel) {
        when (order.state) {
            is ProcessingState -> {
                order.startTimer((order.state as ProcessingState).totalTime)
            }
        }
    }

    fun initializeOrders(orders: List<ReceiveOrderModel>) {
        tabbedPane.allOrdersPanel.removeAll()
        orders.forEach { order ->
            val forProcessing =
                if (order.posOrderStatusType == ServerOrderStatus.COOKING.name || order.posOrderStatusType == ServerOrderStatus.ACCEPT.name) true else false
            val orderFrame = tabbedPane.createOrderFrame(order, forProcessing)
            if (!forProcessing) {
                tabbedPane.addOrderToAllOrders(orderFrame, true)
                tabbedPane.updateOrderInAllOrders(order)
            } else {
                tabbedPane.updateOrderInAllOrders(order)
                tabbedPane.addOrderToAllOrders(orderFrame, true)
            }
        }
    }
}
