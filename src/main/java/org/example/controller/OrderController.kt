
import org.example.CustomTabbedPane
import org.example.model.Order
import org.example.observer.OrderObserver
import org.example.view.states.ProcessingState
import java.awt.Dimension
import javax.swing.JPanel


class OrderController(private val tabbedPane: CustomTabbedPane) {  // 이제 탭과 직접 상호작용

    private val observers = mutableListOf<OrderObserver>()

    // 주문 추가
    fun addOrder(order: Order) {
        order.addObserver(object : OrderObserver {
            override fun update(order: Order) {
                handleOrderStateChange(order)
            }
        })

        // '전체보기'와 '접수대기' 탭에 각각 다른 프레임을 생성해서 추가
        val orderFrameForAllOrders = createOrderFrame(order)  // 전체보기용 프레임
        val orderFrameForPending = createOrderFrame(order)  // 접수대기용 프레임

        tabbedPane.addOrderToAllOrders(orderFrameForAllOrders)  // 전체보기 탭에 추가
        tabbedPane.addOrderToPending(orderFrameForPending)  // 접수대기 탭에 추가
    }

    // 상태 변화에 따른 주문 처리
    private fun handleOrderStateChange(order: Order) {
        when (order.state) {
            is ProcessingState -> {
                moveOrderToProcessing(order)
            }
            else -> {
                tabbedPane.updateOrderInAllOrders(order)  // 전체보기 탭에서 업데이트
            }
        }
    }

    // 주문을 접수진행 탭으로 이동
    private fun moveOrderToProcessing(order: Order) {
        tabbedPane.removeOrderFromPending(order)  // 접수대기 탭에서 삭제

        // 접수진행 탭에 주문 프레임 추가
        val processingOrderFrame = createOrderFrame(order)
        tabbedPane.addOrderToProcessing(processingOrderFrame)

        // 전체보기 탭에서 주문 UI를 업데이트 (삭제하지 않고 UI만 갱신)
        updateOrderUIInAllOrders(order)
    }

    // 전체보기 탭에서 주문 UI 업데이트 (삭제 없이 UI만 갱신)
    private fun updateOrderUIInAllOrders(order: Order) {
        tabbedPane.updateOrderInAllOrders(order)  // 기존 프레임을 삭제하지 않고 UI 갱신
    }

    // 주문 프레임 생성
    private fun createOrderFrame(order: Order): JPanel {
        return order.getUI().apply {
            maximumSize = Dimension(tabbedPane.size.width, 200)
            preferredSize = Dimension(tabbedPane.size.width, 200)
            putClientProperty("orderNumber", order.orderNumber)  // 주문 번호 저장
            println("Created order frame for Order #${order.orderNumber}")
        }
    }

    // 옵저버에게 알림
    private fun notifyObservers(order: Order) {
        observers.forEach { it.update(order) }
    }
}