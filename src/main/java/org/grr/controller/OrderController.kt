
import org.grr.screen.main.main_widget.tab_manager.CustomTabbedPane
import org.grr.command.RejectedReasonType
import org.grr.enum.PosOrderStatus
import org.grr.enum.PosOrderStatus.*
import org.grr.model.ReceiveOrderModel
import org.grr.observer.OrderObserver
import org.grr.screen.main.main_widget.order_states_ui.CompletedState
import org.grr.screen.main.main_widget.order_states_ui.PendingState
import org.grr.screen.main.main_widget.order_states_ui.ProcessingState
import org.grr.screen.main.main_widget.order_states_ui.RejectedState
import javax.swing.JFrame

//주문 UI 관리
class OrderController(private val tabbedPane: CustomTabbedPane) {  // 이제 탭과 직접 상호작용

    // 주문 추가
    fun addOrder(order: ReceiveOrderModel ) {
        println("OrderController: addOrder called for order: ${order.id}")
        //상태 옵저버 등록하여 이벤트 호출 시 handleOrderStateChange실행되게 함.
        order.addStateObserver(object : OrderObserver {
            override fun update(order: ReceiveOrderModel) {
                handleOrderStateChange(order)
            }
        })

        // '전체보기'와 '접수대기' 탭에 각각 다른 프레임을 생성해서 추가
        val orderFrameForAllOrders = tabbedPane.createOrderFrame(order)  // 전체보기용 프레임
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



    //[주문을 접수진행 탭으로 이동] =====================================================
    private fun moveOrderToProcessing(order: ReceiveOrderModel) {
        // 접수대기 서브탭에서 주문을 제거
        tabbedPane.removeOrderFromPending(order)

        // 접수처리중 탭에 주문 프레임 추가
        val processingOrderFrame = tabbedPane.createOrderFrame(order, forProcessing = true)
        tabbedPane.addOrderToProcessing(processingOrderFrame)

        // 전체보기 탭에서 주문 UI를 업데이트 (삭제하지 않고 UI만 갱신)
        tabbedPane.updateOrderInAllOrders(order)  // 상태에 맞게 UI 업데이트

        //주문대기탭 리프레쉬
        tabbedPane.refreshPendingOrders()

        tabbedPane.ProcessingSubTabsCountUpdate()
        //주문처리중탭 리프레쉬
//        tabbedPane.refreshProcessingOrders() // * 이거 넣으면 전체보기탭에서 주문완료버튼, 재전송버튼 업데이트 안되는 이슈 있음
    }
    // 전체보기 탭에서 주문 UI 업데이트 (삭제 없이 UI만 갱신)
    private fun updateOrderUIInAllOrders(order: ReceiveOrderModel) {
        tabbedPane.updateOrderInAllOrders(order)  // 기존 프레임을 삭제하지 않고 UI 갱신
    }
    //==============================================================================


    // [주문을 접수완료 탭으로 이동] ====================================================
    private fun moveOrderToCompleted(order: ReceiveOrderModel) {
        // 1. 전체보기 탭에서 UI를 주문완료 상태로 업데이트
        tabbedPane.updateOrderInAllOrders(order)
        // 2. 접수진행중 탭에서 해당 주문 삭제
        tabbedPane.removeOrderFromProcessing(order)
        // 3. 주문완료 탭에 UI 추가
        val completedOrderFrame = tabbedPane.createOrderFrame(order, forProcessing = true)
        tabbedPane.addOrderToCompleted(completedOrderFrame)
        tabbedPane.refreshCompletedOrders()
        tabbedPane.refreshProcessingOrders()
    }
    //============================================================================


    // [주문을 접수거절 탭으로 이동] =====================================================
    // 접수거절 처리 함수
    private fun moveOrderToReject(order: ReceiveOrderModel) {
        val rejectedState = order.state as RejectedState

        // 1. 전체보기 탭에서 UI를 거절 상태로 업데이트
        updateOrderUIInAllOrders(order)

//        when (rejectedState.rejectType) {
//            RejectedReasonType.CUSTOMER_CANCEL -> TODO()
//            RejectedReasonType.STORE_REJECT -> TODO()
//            RejectedReasonType.REFUND -> TODO()
//        }

        // PosOrderStatus에 따라 처리
        //주문 거절 프레임 생성
        val rejectedOrderFrame = tabbedPane.createOrderFrame(order)

        when (rejectedState.rejectPanel) {
            PENDING -> {
                tabbedPane.removeOrderFromPending(order)
                tabbedPane.addOrderToRejected(rejectedOrderFrame)
                //5.주문대기탭 리프레쉬
                tabbedPane.refreshPendingOrders()
            }
            PROCESSING -> {
                tabbedPane.removeOrderFromProcessing(order)
                tabbedPane.addOrderToRejected(rejectedOrderFrame)
                //6.주문처리중탭 리프레쉬
                tabbedPane.refreshProcessingOrders()
            }
            ALL -> TODO()
            COMPLETED -> TODO()
            REJECTED -> TODO()
        }

        //7.주문거절탭 리프레쉬
        tabbedPane.refreshRejectedOrders()
    }
    //===========================================================================

    fun onOrderStateChanged(order: ReceiveOrderModel) {
        when (order.state) {
            is ProcessingState -> {
                // 주문이 처리중일 때 타이머 시작 및 필터링 상태 유지
                order.startTimer((order.state as ProcessingState).totalTime)
            }
            // 다른 상태에 따른 처리 추가 가능
        }
    }

    fun initializeOrders(orders: List<ReceiveOrderModel>) {
        orders.forEach { order ->
            // 옵저버 등록
            order.addStateObserver(object : OrderObserver {
                override fun update(order: ReceiveOrderModel) {
                    handleOrderStateChange(order)
                }
            })

            // 초기 상태에 따른 UI 추가
            val orderFrame = tabbedPane.createOrderFrame(order)
            tabbedPane.addOrderToAllOrders(orderFrame , true)  // 전체보기 탭에 추가
        }
    }


}