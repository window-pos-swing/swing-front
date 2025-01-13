package org.grr.`object`

import org.grr.screen.main.main_widget.tab_manager.CustomTabbedPane
import org.grr.enum.PosOrderStatus.*
import org.grr.model.ReceiveOrderModel
import org.grr.observer.OrderObserver
import org.grr.screen.main.main_widget.order_states_ui.CompletedState
import org.grr.screen.main.main_widget.order_states_ui.ProcessingState
import org.grr.screen.main.main_widget.order_states_ui.RejectedState

object OrderController {
    private lateinit var tabbedPane: CustomTabbedPane

    // 싱글톤 초기화
    fun initialize(tabbedPane: CustomTabbedPane) {
        OrderController.tabbedPane = tabbedPane
    }

    // 주문 추가
    fun addOrder(order: ReceiveOrderModel) {
        println("org.grr.`object`.OrderController: addOrder called for order: ${order.id}")

        // 상태 옵저버 등록
        order.addStateObserver(object : OrderObserver {
            override fun update(order: ReceiveOrderModel) {
                handleOrderStateChange(order)
            }
        })

        // '전체보기'와 '접수대기' 탭에 각각 다른 프레임을 생성해서 추가
        val orderFrameForAllOrders = tabbedPane.createOrderFrame(order) // 전체보기용 프레임
        val orderFrameForPending = tabbedPane.createOrderFrame(order)  // 접수대기용 프레임

        tabbedPane.addOrderToAllOrders(orderFrameForAllOrders, false)  // 전체보기 탭에 추가
        tabbedPane.addOrderToPending(orderFrameForPending)  // 접수대기 탭에 추가
        tabbedPane.refreshPendingOrders()

        println("주문 추가")
    }

    // 상태 변화에 따른 주문 처리
    private fun handleOrderStateChange(order: ReceiveOrderModel) {
        when (order.state) {
            is ProcessingState -> {
                if (!tabbedPane.isOrderInProcessing(order)) {
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

    fun updateOrderInAllOrders(order: ReceiveOrderModel) {
        tabbedPane.updateOrderInAllOrders(order)
    }

    private fun moveOrderToProcessing(order: ReceiveOrderModel) {
        tabbedPane.removeOrderFromPending(order)

        val processingOrderFrame = tabbedPane.createOrderFrame(order, forProcessing = true)
        tabbedPane.addOrderToProcessing(processingOrderFrame)
        tabbedPane.updateOrderInAllOrders(order)
        tabbedPane.refreshPendingOrders()
        tabbedPane.ProcessingSubTabsCountUpdate()
    }

    private fun moveOrderToCompleted(order: ReceiveOrderModel) {
        tabbedPane.updateOrderInAllOrders(order)
        tabbedPane.removeOrderFromProcessing(order)
        val completedOrderFrame = tabbedPane.createOrderFrame(order, forProcessing = true)
        tabbedPane.addOrderToCompleted(completedOrderFrame)
        tabbedPane.refreshCompletedOrders()
        tabbedPane.refreshProcessingOrders()
    }

    private fun moveOrderToReject(order: ReceiveOrderModel) {
        val rejectedState = order.state as RejectedState
        updateOrderUIInAllOrders(order)

        val rejectedOrderFrame = tabbedPane.createOrderFrame(order)

        when (rejectedState.rejectPanel) {
            PENDING -> {
                tabbedPane.removeOrderFromPending(order)
                tabbedPane.addOrderToRejected(rejectedOrderFrame)
                tabbedPane.refreshPendingOrders()
            }
            PROCESSING -> {
                tabbedPane.removeOrderFromProcessing(order)
                tabbedPane.addOrderToRejected(rejectedOrderFrame)
                tabbedPane.refreshProcessingOrders()
            }
            else -> println("Unhandled state for rejection: ${rejectedState.rejectPanel}")
        }

        tabbedPane.refreshRejectedOrders()
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
            order.addStateObserver(object : OrderObserver {
                override fun update(order: ReceiveOrderModel) {
                    handleOrderStateChange(order)
                }
            })

            val orderFrame = tabbedPane.createOrderFrame(order)
            tabbedPane.addOrderToAllOrders(orderFrame, true)
        }
    }
}
