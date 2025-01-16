package org.grr.screen.main.main_widget.tab_manager

import kotlinx.coroutines.*
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderListSingleTon
import javax.swing.*

class ScrollPaginationHandler(
    private val scrollPane: JScrollPane,
    private val panel: JPanel,
    private val fetchOrders: suspend (Int) -> List<ReceiveOrderModel>,
    private val initializeOrders: (List<ReceiveOrderModel>) -> Unit,
    private val getPageNumber: () -> Int,
    private val updatePageNumber: (Int) -> Unit
) {
    private var isAtBottom = false // 플래그로 스크롤 상태 관리
    private var isAdjustingUI = false // UI 업데이트 중 플래그

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
            if (isAdjustingUI) return@addAdjustmentListener // UI 변경 중 이벤트 무시

            val scrollBar = event.source as JScrollBar
            val atBottom = scrollBar.maximum - (scrollBar.value + scrollBar.visibleAmount) <= 0

            if (atBottom && !isAtBottom && getPageNumber() != -1) {
                println("페이지네이션 실행")
                println("Current Page: ${getPageNumber()}")

                isAtBottom = true // 사용자 스크롤 위치 플래그 설정
                isAdjustingUI = true // UI 업데이트 중 플래그 설정

                // 데이터 로드
                CoroutineScope(Dispatchers.IO).launch {
                    val newOrders = fetchOrders(getPageNumber())

                    // UI 갱신은 Swing 스레드에서 처리
                    SwingUtilities.invokeLater {
                        initializeOrders(newOrders)

                        // 페이지 번호 업데이트
                        updatePageNumber(if (newOrders.size < OrderListSingleTon.PAGE_SIZE) -1 else getPageNumber() + 1)

                        // UI 업데이트 후 플래그 초기화
                        isAdjustingUI = false
                        isAtBottom = false // 새로운 데이터 로드 후 스크롤 위치 초기화
                        scrollBar.value = scrollBar.value - 100 // 스크롤 위치 조정
                        panel.revalidate()
                        panel.repaint()
                    }
                }
            } else if (!atBottom) {
                isAtBottom = false // 위로 스크롤 시 플래그 초기화
            }
        }
    }
}
