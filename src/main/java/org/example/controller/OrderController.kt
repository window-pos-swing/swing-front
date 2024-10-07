
import org.example.CustomTabbedPane
import org.example.model.Order
import org.example.observer.OrderObserver
import org.example.view.states.CompletedState
import org.example.view.states.PendingState
import org.example.view.states.ProcessingState
import org.example.view.states.RejectedState


class OrderController(private val tabbedPane: CustomTabbedPane) {  // 이제 탭과 직접 상호작용

    // 주문 추가
    fun addOrder(order: Order) {
        //상태 옵저버 등록하여 이벤트 호출 시 handleOrderStateChange실행되게 함.
        order.addStateObserver(object : OrderObserver {
            override fun update(order: Order) {
                handleOrderStateChange(order)
            }
        })

        // '전체보기'와 '접수대기' 탭에 각각 다른 프레임을 생성해서 추가
        val orderFrameForAllOrders = tabbedPane.createOrderFrame(order)  // 전체보기용 프레임
        val orderFrameForPending = tabbedPane.createOrderFrame(order)  // 접수대기용 프레임

        tabbedPane.addOrderToAllOrders(orderFrameForAllOrders)  // 전체보기 탭에 추가
        tabbedPane.addOrderToPending(orderFrameForPending)  // 접수대기 탭에 추가
        tabbedPane.refreshPendingOrders()
        println("주문 추가")
    }

    // 상태 변화에 따른 주문 처리
    private fun handleOrderStateChange(order: Order) {
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
    private fun moveOrderToProcessing(order: Order) {
        // 접수대기 서브탭에서 주문을 제거
        tabbedPane.removeOrderFromPending(order)

        // 접수처리중 탭에 주문 프레임 추가
        val processingOrderFrame = tabbedPane.createOrderFrame(order, forProcessing = true)
        tabbedPane.addOrderToProcessing(processingOrderFrame)

        // 전체보기 탭에서 주문 UI를 업데이트 (삭제하지 않고 UI만 갱신)
        tabbedPane.updateOrderInAllOrders(order)  // 상태에 맞게 UI 업데이트
    }
    // 전체보기 탭에서 주문 UI 업데이트 (삭제 없이 UI만 갱신)
    private fun updateOrderUIInAllOrders(order: Order) {
        tabbedPane.updateOrderInAllOrders(order)  // 기존 프레임을 삭제하지 않고 UI 갱신
    }
    //==============================================================================


    // [주문을 접수완료 탭으로 이동] ====================================================
    private fun moveOrderToCompleted(order: Order) {
        tabbedPane.removeOrderFromProcessing(order)
        val completedOrderFrame = tabbedPane.createOrderFrame(order, forProcessing = true)
        tabbedPane.addOrderToCompleted(completedOrderFrame)
        tabbedPane.updateOrderInAllOrders(order)
        tabbedPane.filterCompletedOrders()
    }
    //============================================================================


    // [주문을 접수거절 탭으로 이동] =====================================================
    // 접수거절 처리 함수
    private fun moveOrderToReject(order: Order) {
        val rejectedState = order.state as RejectedState

        // 1. 전체보기 탭에서 UI를 거절 상태로 업데이트
        updateOrderUIInAllOrders(order)

        // 2. 접수대기 상태에서 호출된 경우 (거절)
        if (rejectedState.originState is PendingState) {
            tabbedPane.removeOrderFromPending(order)
        }
        // 3. 접수진행 상태에서 호출된 경우 (취소)
        else if (rejectedState.originState is ProcessingState) {
            tabbedPane.removeOrderFromProcessing(order)
        }

        // 4. 주문거절 탭에 UI 추가
        val rejectedOrderFrame = tabbedPane.createOrderFrame(order)
        tabbedPane.addOrderToRejected(rejectedOrderFrame)
    }
    //===========================================================================

    fun onOrderStateChanged(order: Order) {
        when (order.state) {
            is ProcessingState -> {
                // 주문이 처리중일 때 타이머 시작 및 필터링 상태 유지
                order.startTimer((order.state as ProcessingState).totalTime)
            }
            // 다른 상태에 따른 처리 추가 가능
        }
    }

}