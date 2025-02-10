package org.grr.screen.main.main_widget.tab_manager.pending.pandding_sub_tabs

import org.grr.api.OrderAPI
import org.grr.enum.OrderReceiveType
import org.grr.enum.PosOrderStatus
import org.grr.model.OrderFilter
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderListSingleTon
import org.grr.`object`.OrderListSingleTon.currentPendingSubTabName
import org.grr.screen.main.main_widget.tab_manager.CustomTabbedPane
import org.grr.screen.main.main_widget.tab_manager.ScrollPaginationHandler
import org.grr.style.MyColor
import org.grr.widgets.SelectButtonRoundedBorder
import org.json.JSONArray
import java.awt.*
import javax.swing.*

// PendingSubTabs 클래스에서 버튼 생성 및 관리

class PendingSubTabs(private val tabbedPane: CustomTabbedPane) : JPanel() {

    // 서브탭별 패널
    val pendingOrdersPanel = createPanel()
    val deliveryOrdersPanel = createPanel()
    val takeoutOrdersPanel = createPanel()

    val allOrdersButton = SelectButtonRoundedBorder(50)
    val deliveryButton = SelectButtonRoundedBorder(50)
    val takeoutButton = SelectButtonRoundedBorder(50)
    private var selectedButton: SelectButtonRoundedBorder? = null

    // 서브탭 상태 관리
    private var selectedTab: String = "전체보기"

    // CardLayout으로 서브탭 패널 관리
    private val cardLayout = CardLayout()

    // 카드 컨테이너
    private val cardContainer = JPanel(cardLayout)

    init {
        layout = BorderLayout()
        background = Color.WHITE

        // 서브탭 버튼 생성
        val buttonPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT

            preferredSize = Dimension(750, 100)
            maximumSize = Dimension(750, 100)
            minimumSize = Dimension(750, 100)
            background = Color.WHITE

            setupButton(allOrdersButton, "전체보기") { showTab("전체보기") }
            setupButton(deliveryButton, "배달") { showTab("배달") }
            setupButton(takeoutButton, "포장") { showTab("포장") }

            allOrdersButton.button.addActionListener {
                selectButton(allOrdersButton)
                showTab("전체보기")
            }
            deliveryButton.button.addActionListener {
                selectButton(deliveryButton)
                showTab("배달")
            }
            takeoutButton.button.addActionListener {
                selectButton(takeoutButton)
                showTab("포장")
            }
            // 버튼 배치
            add(Box.createRigidArea(Dimension(20, 0)))
            add(allOrdersButton.button)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(deliveryButton.button)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(takeoutButton.button)
        }

        // 카드 컨테이너에 각 패널 추가
        addPanelToContainer(pendingOrdersPanel, "전체보기", panelType = "pendingOrders")
        addPanelToContainer(deliveryOrdersPanel, "배달", panelType = "pendingDeliveryOrders")
        addPanelToContainer(takeoutOrdersPanel, "포장", panelType = "pendingTakeOutOrders")

        // 서브탭 초기화
        add(buttonPanel, BorderLayout.NORTH)
        add(cardContainer, BorderLayout.CENTER)

        // 기본 탭 표시
        selectButton(allOrdersButton)
        initializePanels()
    }

    private fun createPanel(): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
        }
    }

    private fun addPanelToContainer(panel: JPanel, tabName: String, panelType: String) {
        val pendingScrollPane = JScrollPane(panel).apply {
            border = BorderFactory.createEmptyBorder(0, 20, 20, 20)
            viewportBorder = null
            background = Color.WHITE
            isOpaque = true
            viewport.apply {
                background = Color.WHITE
                isOpaque = true
            }
        }
        cardContainer.add(pendingScrollPane, tabName)
        var filter = OrderFilter(posOrderStatus = PosOrderStatus.WAITING)
        var orderStatus = "pendingOrders"
        if (panelType == "pendingDeliveryOrders") {
            filter = OrderFilter(posOrderStatus = PosOrderStatus.WAITING, orderReceiveType = OrderReceiveType.DELIVERY)
            orderStatus = "pendingDeliveryOrders"
        } else if (panelType == "pendingTakeOutOrders") {
            filter = OrderFilter(posOrderStatus = PosOrderStatus.WAITING, orderReceiveType = OrderReceiveType.TAKEOUT)
            orderStatus = "pendingTakeOutOrders"
        }
        ScrollPaginationHandler(
            orderStatus = orderStatus,
            scrollPane = pendingScrollPane,
            panel = panel,
            fetchOrders = { pageNumber ->
                val result = OrderAPI().fetchOrders(
                    tabbedPane.parentFrame,
                    tabbedPane.cardPanel!!,
                    filter,
                    pageNumber
                )
                JSONArray(result.second).let {
                    ReceiveOrderModel.fromJsonArray(
                        it,
                        tabbedPane.parentFrame,
                        tabbedPane.cardPanel!!
                    )
                }
            },
            initializeOrders = {
                initializePanels()
            },
            getPageNumber = { OrderListSingleTon.pageNumbers[panelType] ?: 0 },
        )
    }

    fun showTab(tabName: String) {
        OrderListSingleTon.myCurrentOrders.clear()
        OrderListSingleTon.pageNumbers["pendingOrders"] = 0
        OrderListSingleTon.pageNumbers["pendingDeliveryOrders"] = 0
        OrderListSingleTon.pageNumbers["pendingTakeOutOrders"] = 0
        if(tabName == "전체보기"){
            currentPendingSubTabName = "전체보기"
            tabbedPane.fetchAndUpdateOrders(
                orderKey = "pendingOrders",
                targetPanel = pendingOrdersPanel,
                filter = OrderFilter(posOrderStatus = PosOrderStatus.WAITING)
            )
        }else if(tabName == "배달"){
            currentPendingSubTabName = "배달"
            tabbedPane.fetchAndUpdateOrders(
                orderKey = "pendingDeliveryOrders",
                targetPanel = deliveryOrdersPanel,
                filter = OrderFilter(posOrderStatus = PosOrderStatus.WAITING, orderReceiveType = OrderReceiveType.DELIVERY)
            )
        }else {
            currentPendingSubTabName = "포장"
            tabbedPane.fetchAndUpdateOrders(
                orderKey = "pendingTakeOutOrders",
                targetPanel = takeoutOrdersPanel,
                filter = OrderFilter(
                    posOrderStatus = PosOrderStatus.WAITING,
                    orderReceiveType = OrderReceiveType.TAKEOUT
                )
            )
        }

        initializePanels()
        selectedTab = tabName
        cardLayout.show(cardContainer, tabName)
    }

    fun initializePanels() {
        // 각각의 패널 업데이트
        updatePanel(pendingOrdersPanel, OrderListSingleTon.myCurrentOrders)
        updatePanel(deliveryOrdersPanel, OrderListSingleTon.myCurrentOrders)
        updatePanel(takeoutOrdersPanel, OrderListSingleTon.myCurrentOrders)
    }

    private fun updatePanel(panel: JPanel, orders: MutableList<ReceiveOrderModel>?) {
        panel.removeAll()
        val ordersCopy = orders?.toList()  // 복사본 생성

        ordersCopy?.forEach { order ->
            val orderFrame = tabbedPane.createOrderFrame(order)
            orderFrame.maximumSize = Dimension(Int.MAX_VALUE, orderFrame.preferredSize.height)
            panel.add(orderFrame)
            panel.add(Box.createRigidArea(Dimension(0, 30)))
        }
        panel.revalidate()
        panel.repaint()
    }

    fun selectButton(button: SelectButtonRoundedBorder) {
        selectedButton?.setButtonStyle(false)  // 이전 선택된 버튼을 선택 해제 상태로 설정
        button.setButtonStyle(true)  // 현재 선택된 버튼을 선택 상태로 설정
        selectedButton = button
    }

    private fun setupButton(button: SelectButtonRoundedBorder, text: String, action: () -> Unit) {
        button.apply {
            createRoundedButton(
                text,
                MyColor.SELECTED_BACKGROUND_COLOR,
                MyColor.UNSELECTED_BACKGROUND_COLOR,
                MyColor.SELECTED_TEXT_COLOR,
                MyColor.GREY600,
                Dimension(230, 60)
            )
        }
    }


}
