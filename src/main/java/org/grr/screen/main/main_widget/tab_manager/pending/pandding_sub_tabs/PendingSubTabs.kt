package org.grr.screen.main.main_widget.tab_manager.pending.pandding_sub_tabs

import org.grr.enum.OrderReceiveType
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.tab_manager.CustomTabbedPane
import org.grr.style.MyColor
import org.grr.widgets.SelectButtonRoundedBorder
import java.awt.*
import javax.swing.*

// PendingSubTabs 클래스에서 버튼 생성 및 관리

class PendingSubTabs(private val tabbedPane: CustomTabbedPane) : JPanel() {

    // 서브탭별 패널
    val pendingOrdersPanel = createPanel()
    val deliveryOrdersPanel = createPanel()
    val takeoutOrdersPanel = createPanel()

    val allOrdersButton = SelectButtonRoundedBorder(50)
    private val deliveryButton = SelectButtonRoundedBorder(50)
    private val takeoutButton = SelectButtonRoundedBorder(50)
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
        addPanelToContainer(pendingOrdersPanel, "전체보기")
        addPanelToContainer(deliveryOrdersPanel, "배달")
        addPanelToContainer(takeoutOrdersPanel, "포장")

        // 서브탭 초기화
        add(buttonPanel, BorderLayout.NORTH)
        add(cardContainer, BorderLayout.CENTER)

        // 기본 탭 표시
        selectButton(allOrdersButton)
        showTab("전체보기")
        initializePanels()
    }

    private fun createPanel(): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
        }
    }

    private fun addPanelToContainer(panel: JPanel, tabName: String) {
        cardContainer.add(JScrollPane(panel).apply {
            border = BorderFactory.createEmptyBorder(0, 20, 20, 20)
            viewportBorder = null
            background = Color.WHITE
            isOpaque = true
            viewport.apply {
                background = Color.WHITE
                isOpaque = true
            }
        }, tabName)
    }

    fun showTab(tabName: String) {
        selectedTab = tabName
        cardLayout.show(cardContainer, tabName)
        updateCounts()
    }

    fun updateCounts() {
        val totalCount = OrderListSingleTon.counts["pendingOrders"] ?: 0
        val deliveryCount = OrderListSingleTon.counts["pendingDeliveryOrders"] ?: 0
        val takeoutCount = OrderListSingleTon.counts["pendingTakeOutOrders"] ?: 0

        println("Counts updated: 전체=$totalCount, 배달=$deliveryCount, 포장=$takeoutCount")
        allOrdersButton.button.text = "전체보기  $totalCount"
        deliveryButton.button.text = "배달  $deliveryCount"
        takeoutButton.button.text = "포장  $takeoutCount"
    }

    private fun initializePanels() {
        updatePanel(pendingOrdersPanel, OrderListSingleTon.orders["pendingOrders"])
        updatePanel(deliveryOrdersPanel, OrderListSingleTon.orders["pendingDeliveryOrders"])
        updatePanel(takeoutOrdersPanel, OrderListSingleTon.orders["pendingTakeOutOrders"])
    }

    private fun updatePanel(panel: JPanel, orders: MutableList<ReceiveOrderModel>?) {
        panel.removeAll()
        orders?.forEach { order ->
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

    //TODO [ADD]


    fun addOrderToPending(orderFrame: JPanel, typeOrderFrame : JPanel ,order: ReceiveOrderModel) {
        pendingOrdersPanel.add(orderFrame)
        pendingOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)))
        pendingOrdersPanel.revalidate()
        pendingOrdersPanel.repaint()
        tabbedPane.updateTabTitle(1, "접수대기", OrderListSingleTon.counts["pendingOrders"] ?: 0)
        if(order.orderReceiveType == OrderReceiveType.DELIVERY.name){
            deliveryOrdersPanel.add(typeOrderFrame)
        }else if(order.orderReceiveType == OrderReceiveType.TAKEOUT.name){
            takeoutOrdersPanel.add(typeOrderFrame)
        }
    }

    //TODO [REMOVE]
    fun removeOrderFromPending(order: ReceiveOrderModel) {

        val frameToRemove = pendingOrdersPanel.components
            .filterIsInstance<JPanel>()
            .find { it.getClientProperty("orderNumber") == order.orderNumber }

        frameToRemove?.let {
            pendingOrdersPanel.remove(it)
            pendingOrdersPanel.revalidate()
            pendingOrdersPanel.repaint()
            tabbedPane.updateTabTitle(1, "접수대기", OrderListSingleTon.counts["pendingOrders"] ?: 0)
        }

        if(order.orderReceiveType == OrderReceiveType.DELIVERY.name){
            val deliveryOrdersPanelRemove = deliveryOrdersPanel.components
                .filterIsInstance<JPanel>()
                .find { it.getClientProperty("orderNumber") == order.orderNumber }

            deliveryOrdersPanelRemove?.let{
                deliveryOrdersPanel.remove(it)
                deliveryOrdersPanel.revalidate()
                deliveryOrdersPanel.repaint()
            }

        }else if(order.orderReceiveType == OrderReceiveType.TAKEOUT.name){
            val takeOutOrdersPanelRemove = takeoutOrdersPanel.components
                .filterIsInstance<JPanel>()
                .find { it.getClientProperty("orderNumber") == order.orderNumber }

            takeOutOrdersPanelRemove?.let{
                takeoutOrdersPanel.remove(it)
                takeoutOrdersPanel.revalidate()
                takeoutOrdersPanel.repaint()
            }
        }
    }
}
