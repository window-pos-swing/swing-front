package org.grr.screen.main.main_widget.tab_manager.completed.completed_sub_tabs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.grr.api.OrderAPI
import org.grr.enum.PosOrderStatus
import org.grr.enum.ServerOrderStatus
import org.grr.model.OrderFilter
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.tab_manager.CustomTabbedPane
import org.grr.style.MyColor
import org.grr.widgets.SelectButtonRoundedBorder
import org.json.JSONArray
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

class CompletedSubTabs(private val tabbedPane: CustomTabbedPane) : JPanel() {

    // 현재 선택된 버튼을 저장할 변수
    private var selectedButton: SelectButtonRoundedBorder? = null

    // 상태별 주문 개수를 저장할 변수
    private var totalCount = 0
    private var deliveryCount = 0
    private var takeoutCount = 0

    // 버튼들을 클래스 멤버 변수로 선언
    val allOrdersButton = SelectButtonRoundedBorder(50)
    private val deliveryButton = SelectButtonRoundedBorder(50)
    private val takeoutButton = SelectButtonRoundedBorder(50)

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE

        val buttonPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT

            preferredSize = Dimension(750, 100)
            maximumSize = Dimension(750, 100)
            minimumSize = Dimension(750, 100)
            background = Color.WHITE

            // SelectButtonRoundedBorder 사용하여 버튼 생성
            allOrdersButton.apply {
                createRoundedButton(
                    "전체보기",
                    MyColor.SELECTED_BACKGROUND_COLOR,
                    MyColor.UNSELECTED_BACKGROUND_COLOR,
                    MyColor.SELECTED_TEXT_COLOR,
                    MyColor.GREY600,
                    Dimension(230, 60)
                )
            }
            deliveryButton.apply {
                createRoundedButton(
                    "배달",
                    MyColor.SELECTED_BACKGROUND_COLOR,
                    MyColor.UNSELECTED_BACKGROUND_COLOR,
                    MyColor.SELECTED_TEXT_COLOR,
                    MyColor.GREY600,
                    Dimension(230, 60)
                )
            }
            takeoutButton.apply {
                createRoundedButton(
                    "포장",
                    MyColor.SELECTED_BACKGROUND_COLOR,
                    MyColor.UNSELECTED_BACKGROUND_COLOR,
                    MyColor.SELECTED_TEXT_COLOR,
                    MyColor.GREY600,
                    Dimension(230, 60)
                )
            }


            // 버튼 간 간격 추가
            add(allOrdersButton.button)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(deliveryButton.button)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(takeoutButton.button)


            // 버튼에 클릭 리스너 추가
            allOrdersButton.button.addActionListener {
                selectButton(allOrdersButton)
                println("Subtab Filter Changed: 전체보기")
            }
            deliveryButton.button.addActionListener {
                selectButton(deliveryButton)
                println("Subtab Filter Changed: 배달")
            }
            takeoutButton.button.addActionListener {
                selectButton(takeoutButton)
                println("Subtab Filter Changed: 포장")
            }

            // 초기 선택된 버튼 설정 (전체보기)
            selectButton(allOrdersButton)
        }

        add(buttonPanel)

        // 중복 생성 방지 로직 추가
        tabbedPane.completedOrdersPanel.removeAll()  // 기존 패널 초기화
        val completedOrdersScrollPane =JScrollPane(tabbedPane.completedOrdersPanel).apply {
            border = BorderFactory.createEmptyBorder(0, 20, 20, 20)
            viewportBorder = null
            isOpaque = false
            viewport.isOpaque = false
        }
        completedOrdersScrollPane.addMouseWheelListener { event ->
            val scrollBar = completedOrdersScrollPane.verticalScrollBar
            val unitsToScroll = event.unitsToScroll * 20 // 한 번의 휠 이벤트로 스크롤할 픽셀 수
            scrollBar.value = (scrollBar.value + unitsToScroll).coerceIn(0, scrollBar.maximum - scrollBar.visibleAmount)
        }
        //스크롤 플래그 상태 관리
        var isAtBottom = false // 플래그로 상태 관리
        var isAdjustingUI = false // UI 업데이트 중 상태를 나타내는 플래그
        completedOrdersScrollPane.verticalScrollBar.addAdjustmentListener { event ->
            if (isAdjustingUI) {
                // UI 변경으로 발생한 스크롤 이벤트는 무시
                return@addAdjustmentListener
            }

            val scrollBar = event.source as JScrollBar
            val atBottom = scrollBar.maximum - (scrollBar.value + scrollBar.visibleAmount) <= 0

            if (atBottom && !isAtBottom && OrderListSingleTon.pageNumbers["completedOrders"] != -1) {
                // 데이터 로드 조건
                println("페이지네이션 실향")
                println("OrderListSingleTon.allOrdersPageNumber : ${OrderListSingleTon.pageNumbers["allOrdersPageNumber"]}")
                // 스크롤 위치 저장
                val currentScrollValue = scrollBar.value
                isAtBottom = true // 사용자 스크롤 위치 플래그 설정
                isAdjustingUI = true // UI 업데이트 중 플래그 설정

                // 데이터 로드
                CoroutineScope(Dispatchers.IO).launch {
                    println("completedOrdersPageNumber : ${OrderListSingleTon.pageNumbers["completedOrders"]}")
                    val result = OrderAPI().fetchOrders(
                        parentFrame = tabbedPane.parentFrame,
                        cardPanel = tabbedPane.cardPanel!!,
                        filter = OrderFilter(
                            posOrderStatus = PosOrderStatus.COMPLETED,
                        ),
                        pageNumber = OrderListSingleTon.pageNumbers["completedOrders"]!!
                    )
                    val jsonArray = JSONArray(result.second)
                    val newOrders = ReceiveOrderModel.fromJsonArray(jsonArray, tabbedPane.parentFrame, tabbedPane.cardPanel!!)

                    // UI 갱신은 Swing 스레드에서 처리
                    SwingUtilities.invokeLater {
                        tabbedPane.completeOrdersPanelManager.initCompletedOrders(newOrders)

                        // UI 업데이트 후 플래그 초기화
                        isAdjustingUI = false
                        isAtBottom = false // 새로운 데이터 로드 후 스크롤 위치 초기화
                        scrollBar.value = currentScrollValue
                    }
                }
            } else if (!atBottom) {
                // 사용자가 위로 스크롤하면 플래그 초기화
                isAtBottom = false
            }
        }
        add(completedOrdersScrollPane)

        // 기본 선택: 전체보기
        CompletedSubTabsUpdateCounts()
    }

    fun selectButton(button: SelectButtonRoundedBorder) {
        selectedButton?.setButtonStyle(false)  // 이전 선택된 버튼을 선택 해제 상태로 설정
        button.setButtonStyle(true)  // 현재 선택된 버튼을 선택 상태로 설정
        selectedButton = button
    }

    fun CompletedSubTabsUpdateCounts() {
        // 전체보기: 모든 접수대기 상태의 주문 개수
        totalCount = OrderListSingleTon.counts["completedOrders"]!!

        // 배달: 접수대기 상태 중 배달 타입인 주문 개수
        deliveryCount = OrderListSingleTon.counts["completedDeliveryOrders"]!!

        // 포장: 접수대기 상태 중 포장 타입인 주문 개수
        takeoutCount = OrderListSingleTon.counts["completedTakeOutOrders"]!!

        // 버튼의 텍스트 업데이트
        allOrdersButton.button.text = "전체보기  $totalCount"
        deliveryButton.button.text = "배달  $deliveryCount"
        takeoutButton.button.text = "포장  $takeoutCount"
    }

}
