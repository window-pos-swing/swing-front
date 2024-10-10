
import org.example.CustomTabbedPane
import org.example.command.RejectedReasonType
import org.example.model.Order
import org.example.observer.OrderObserver
import org.example.view.states.CompletedState
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

        //주문대기탭 리프레쉬
        tabbedPane.refreshPendingOrders()

        //주문처리중탭 리프레쉬
//        tabbedPane.refreshProcessingOrders() // * 이거 넣으면 전체보기탭에서 주문완료버튼, 재전송버튼 업데이트 안되는 이슈 있음
    }
    // 전체보기 탭에서 주문 UI 업데이트 (삭제 없이 UI만 갱신)
    private fun updateOrderUIInAllOrders(order: Order) {
        tabbedPane.updateOrderInAllOrders(order)  // 기존 프레임을 삭제하지 않고 UI 갱신
    }
    //==============================================================================


    // [주문을 접수완료 탭으로 이동] ====================================================
    private fun moveOrderToCompleted(order: Order) {
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
    private fun moveOrderToReject(order: Order) {
        val rejectedState = order.state as RejectedState

        // 1. 전체보기 탭에서 UI를 거절 상태로 업데이트
        updateOrderUIInAllOrders(order)

        when (rejectedState.rejectType) {
            RejectedReasonType.CUSTOMER_CANCEL -> {
                println("주문이 고객에 의해 취소되었습니다.")
                // 고객 취소에 맞는 UI 처리 추가 가능
            }

            RejectedReasonType.STORE_REJECT -> {
                tabbedPane.removeOrderFromPending(order)
                println("주문이 가게에 의해 거절되었습니다.")
                // 가게 거절에 맞는 UI 처리 추가 가능
            }

            RejectedReasonType.STORE_CANCEL -> {
                println("주문이 가게에 의해 취소되었습니다.")
                tabbedPane.removeOrderFromProcessing(order)
                // 가게 취소에 맞는 UI 처리 추가 가능
            }
        }

        // 4. 주문거절 탭에 UI 추가
        val rejectedOrderFrame = tabbedPane.createOrderFrame(order)
        tabbedPane.addOrderToRejected(rejectedOrderFrame)

        //5.주문대기탭 리프레쉬
        tabbedPane.refreshPendingOrders()

        //6.주문처리중탭 리프레쉬
        tabbedPane.refreshProcessingOrders()

        //7.주문거절탭 리프레쉬
        tabbedPane.refreshRejectedOrders()
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