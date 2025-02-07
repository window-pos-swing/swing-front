package org.grr.screen.main.main_widget.tab_manager.processing.processing_sub_tabs

import org.grr.api.OrderAPI
import org.grr.enum.OrderReceiveType
import org.grr.enum.PosOrderStatus
import org.grr.model.OrderFilter
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderController
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.tab_manager.CustomTabbedPane
import org.grr.screen.main.main_widget.tab_manager.ScrollPaginationHandler
import org.grr.style.MyColor
import org.grr.widgets.SelectButtonRoundedBorder
import org.json.JSONArray
import java.awt.Color
import java.awt.Dimension
import javax.swing.*
import java.awt.BorderLayout
import java.awt.CardLayout
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JScrollPane

class ProcessingSubTabs(private val tabbedPane: CustomTabbedPane) : JPanel() {

    // 서브탭별 패널
    val processingOrdersPanel = createPanel()
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

    lateinit var scrollPaginationHandler : ScrollPaginationHandler

    init {
        layout = BorderLayout()
        background = Color.WHITE

        // 서브탭 버튼 생성
        val buttonPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = LEFT_ALIGNMENT

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

        // 카드 컨테이너에 각 패널 추가
        addPanelToContainer(processingOrdersPanel, "전체보기" , panelType = "processingOrders")
        addPanelToContainer(deliveryOrdersPanel, "배달" , panelType = "processingDeliveryOrders")
        addPanelToContainer(takeoutOrdersPanel, "포장" , panelType = "processingTakeOutOrders")

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

    private fun addPanelToContainer(panel: JPanel, tabName: String , panelType : String) {
        val processScrollPane = JScrollPane(panel).apply {
            border = javax.swing.BorderFactory.createEmptyBorder(0, 20, 20, 20)
            viewportBorder = null
            background = Color.WHITE
            isOpaque = true
            viewport.apply {
                background = Color.WHITE
                isOpaque = true
            }
        }
        cardContainer.add(processScrollPane, tabName)

        var filter = OrderFilter(posOrderStatus = PosOrderStatus.IN_PROGRESS)
        var orderStatus = "processingOrders"
        if(panelType == "processingDeliveryOrders"){
            filter = OrderFilter(posOrderStatus = PosOrderStatus.IN_PROGRESS, orderReceiveType = OrderReceiveType.DELIVERY)
            orderStatus = "processingDeliveryOrders"
        }else if(panelType == "processingTakeOutOrders"){
            filter = OrderFilter(posOrderStatus = PosOrderStatus.IN_PROGRESS , orderReceiveType = OrderReceiveType.TAKEOUT)
            orderStatus = "processingTakeOutOrders"
        }
        scrollPaginationHandler = ScrollPaginationHandler(
            orderStatus = orderStatus,
            scrollPane = processScrollPane,
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
        val totalCount = OrderListSingleTon.counts["processingOrders"] ?: 0
        val deliveryCount = OrderListSingleTon.counts["processingDeliveryOrders"] ?: 0
        val takeoutCount = OrderListSingleTon.counts["processingTakeOutOrders"] ?: 0

        println("Counts updated: 전체=$totalCount, 배달=$deliveryCount, 포장=$takeoutCount")
        allOrdersButton.button.text = "전체보기  $totalCount"
        deliveryButton.button.text = "배달  $deliveryCount"
        takeoutButton.button.text = "포장  $takeoutCount"
    }

    fun initializePanels() {
        updatePanel(processingOrdersPanel, OrderListSingleTon.orders["processingOrders"])
        updatePanel(deliveryOrdersPanel, OrderListSingleTon.orders["processingDeliveryOrders"])
        updatePanel(takeoutOrdersPanel, OrderListSingleTon.orders["processingTakeOutOrders"])
    }

    private fun updatePanel(panel: JPanel, orders: MutableList<ReceiveOrderModel>?) {
        panel.removeAll()
        orders?.forEach { order ->
            val orderFrame = tabbedPane.createOrderFrame(order, true)
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
