package org.grr.screen.main.main_widget.tab_manager

import kotlinx.coroutines.*
import org.grr.model.ReceiveOrderModel
import javax.swing.*

class ScrollPaginationHandler(
    private val orderStatus : String,
    private val scrollPane: JScrollPane,
    private val panel: JPanel,
    private val fetchOrders: suspend (Int) -> List<ReceiveOrderModel>,
    private val initializeOrders: (List<ReceiveOrderModel>) -> Unit,
    private val getPageNumber: () -> Int,
) {
    var isAdjustingUI = false // UI 업데이트 중 플래그
    var isOrderStatusChange = false;

    init {
        setupMouseWheelListener()
        setupAdjustmentListener()
    }

    private fun setupMouseWheelListener() {
        scrollPane.addMouseWheelListener { event ->
            val scrollBar = scrollPane.verticalScrollBar
            val unitsToScroll = event.unitsToScroll * 20 // 휠 이벤트 당 스크롤 크기 설정
            scrollBar.value = (scrollBar.value + unitsToScroll).coerceIn(0, scrollBar.maximum - scrollBar.visibleAmount)
        }
    }

    private fun setupAdjustmentListener() {

        scrollPane.verticalScrollBar.addAdjustmentListener { event ->
            val scrollBar = event.source as JScrollBar
            if (isOrderStatusChange) return@addAdjustmentListener
            // 사용자가 스크롤바를 움직이는 중이면 무시
            if (event.valueIsAdjusting) return@addAdjustmentListener

            val currentMaxHeight = scrollBar.maximum
            val currentScrollPosition = scrollBar.value + scrollBar.visibleAmount

            // 스크롤이 끝에 도달했는지 확인
            val atBottom = currentScrollPosition >= currentMaxHeight

            if (atBottom && getPageNumber() != -1) {
                println("[${orderStatus}] 페이지네이션 실행")
                println("[Current Page]: ${getPageNumber()}")

                isAdjustingUI = true

                CoroutineScope(Dispatchers.IO).launch {
                    val newOrders = fetchOrders(getPageNumber())

                    SwingUtilities.invokeLater {
                        initializeOrders(newOrders)

                        // 스크롤바 강제 업데이트
                        panel.revalidate()
                        panel.repaint()

                        // 플래그 초기화
                        isAdjustingUI = false
                    }
                }
            }
        }
    }
}
