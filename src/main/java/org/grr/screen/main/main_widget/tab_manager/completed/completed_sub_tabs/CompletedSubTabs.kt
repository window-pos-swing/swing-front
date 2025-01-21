package org.grr.screen.main.main_widget.tab_manager.completed.completed_sub_tabs


import org.grr.api.OrderAPI
import org.grr.enum.OrderReceiveType
import org.grr.enum.PosOrderStatus
import org.grr.model.OrderFilter
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.tab_manager.CustomTabbedPane
import org.grr.screen.main.main_widget.tab_manager.ScrollPaginationHandler
import org.grr.style.MyColor
import org.grr.widgets.SelectButtonRoundedBorder
import org.json.JSONArray
import java.awt.*
import javax.swing.*


class CompletedSubTabs(private val tabbedPane: CustomTabbedPane) : JPanel() {

    val completedOrdersPanel = createPanel()
    val deliveryOrdersPanel = createPanel()
    val takeoutOrdersPanel = createPanel()

    val allOrdersButton = SelectButtonRoundedBorder(50)
    private val deliveryButton = SelectButtonRoundedBorder(50)
    private val takeoutButton = SelectButtonRoundedBorder(50)
    private var selectedButton: SelectButtonRoundedBorder? = null

    private var selectedTab: String = "전체보기"
    private val cardLayout = CardLayout()
    private val cardContainer = JPanel(cardLayout)

    init {
        layout = BorderLayout()
        background = Color.WHITE

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

            add(Box.createRigidArea(Dimension(20, 0)))
            add(allOrdersButton.button)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(deliveryButton.button)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(takeoutButton.button)
        }

        addPanelToContainer(completedOrdersPanel, "전체보기" , "completedOrders")
        addPanelToContainer(deliveryOrdersPanel, "배달" , "completedDeliveryOrders")
        addPanelToContainer(takeoutOrdersPanel, "포장" , "completedTakeOutOrders")

        add(buttonPanel, BorderLayout.NORTH)
        add(cardContainer, BorderLayout.CENTER)

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

    private fun addPanelToContainer(panel: JPanel, tabName: String, panelType : String) {
        var completedScrollPane = JScrollPane(panel).apply {
            border = BorderFactory.createEmptyBorder(0, 20, 20, 20)
            viewportBorder = null
            background = Color.WHITE
            isOpaque = true
            viewport.apply {
                background = Color.WHITE
                isOpaque = true
            }
        }
        cardContainer.add(completedScrollPane, tabName)

        var filter = OrderFilter(posOrderStatus = PosOrderStatus.COMPLETED)
        var orderState = "completedOrder"
        if(panelType == "completedDeliveryOrders"){
            filter = OrderFilter(posOrderStatus = PosOrderStatus.COMPLETED, orderReceiveType = OrderReceiveType.DELIVERY)
            orderState = "completedDeliveryOrders"
        }else if(panelType == "completedTakeOutOrders"){
            filter = OrderFilter(posOrderStatus = PosOrderStatus.COMPLETED , orderReceiveType = OrderReceiveType.TAKEOUT)
            orderState = "completedTakeOutOrders"
        }
        ScrollPaginationHandler(
            orderStatus = orderState,
            scrollPane = completedScrollPane,
            panel = panel,
            fetchOrders = { pageNumber ->
                val result = OrderAPI().fetchOrders(
                    tabbedPane.parentFrame,
                    tabbedPane.cardPanel!!,
                    filter ,
                    pageNumber)
                JSONArray(result.second).let { ReceiveOrderModel.fromJsonArray(it, tabbedPane.parentFrame, tabbedPane.cardPanel!!) }
            },
            initializeOrders = {
                initializePanels()
            },
            getPageNumber = { OrderListSingleTon.pageNumbers[panelType] ?: 0 },
        )
    }

    fun showTab(tabName: String) {
        selectedTab = tabName
        cardLayout.show(cardContainer, tabName)
        updateCounts()
    }

    fun updateCounts() {
        val totalCount = OrderListSingleTon.counts["completedOrders"] ?: 0
        val deliveryCount = OrderListSingleTon.counts["completedDeliveryOrders"] ?: 0
        val takeoutCount = OrderListSingleTon.counts["completedTakeOutOrders"] ?: 0

        println("Counts updated: 전체=$totalCount, 배달=$deliveryCount, 포장=$takeoutCount")
        allOrdersButton.button.text = "전체보기  $totalCount"
        deliveryButton.button.text = "배달  $deliveryCount"
        takeoutButton.button.text = "포장  $takeoutCount"
    }

    fun initializePanels() {
        updatePanel(completedOrdersPanel, OrderListSingleTon.orders["completedOrders"])
        updatePanel(deliveryOrdersPanel, OrderListSingleTon.orders["completedDeliveryOrders"])
        updatePanel(takeoutOrdersPanel, OrderListSingleTon.orders["completedTakeOutOrders"])
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
        selectedButton?.setButtonStyle(false)
        button.setButtonStyle(true)
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

    fun addOrderToCompleted(orderFrame: JPanel,order: ReceiveOrderModel) {
        println("addOrderToCompleted")
        completedOrdersPanel.add(orderFrame)
        completedOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)))
        completedOrdersPanel.revalidate()
        completedOrdersPanel.repaint()


        initializePanels()
        updateCounts()

    }
}
