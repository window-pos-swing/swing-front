package org.grr.screen.main.main_widget.tab_manager.rejected.rejected_sub_tabs

import org.grr.api.OrderAPI
import org.grr.command.RejectedReasonType
import org.grr.enum.ServerOrderStatus
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

class RejectedSubTabs(private val tabbedPane: CustomTabbedPane) : JPanel() {

    // 서브탭별 패널
    val storeRejectPanel = createPanel()
    val customerCancelPanel = createPanel()
    val refundPanel = createPanel()

    val storeRejectButton = SelectButtonRoundedBorder(50)
    private val customerCancelButton = SelectButtonRoundedBorder(50)
    private val refundButton = SelectButtonRoundedBorder(50)
    private var selectedButton: SelectButtonRoundedBorder? = null

    private var selectedTab: String = "가게거절"
    private val cardLayout = CardLayout()
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

            setupButton(storeRejectButton, "가게거절") { showTab("가게거절") }
            setupButton(customerCancelButton, "고객취소") { showTab("고객취소") }
            setupButton(refundButton, "환불") { showTab("환불") }
            storeRejectButton.button.addActionListener {
                selectButton(storeRejectButton)
                showTab("가게거절")
            }
            customerCancelButton.button.addActionListener {
                selectButton(customerCancelButton)
                showTab("고객취소")
            }
            refundButton.button.addActionListener {
                selectButton(refundButton)
                showTab("환불")
            }
            add(Box.createRigidArea(Dimension(20, 0)))
            add(storeRejectButton.button)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(customerCancelButton.button)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(refundButton.button)
        }

        // 카드 컨테이너에 각 패널 추가
        addPanelToContainer(storeRejectPanel, "가게거절", panelType = "rejectStoreOrders")
        addPanelToContainer(customerCancelPanel, "고객취소", panelType = "rejectUserOrders")
        addPanelToContainer(refundPanel, "환불", panelType = "rejectRefundOrders")

        // 서브탭 초기화
        add(buttonPanel, BorderLayout.NORTH)
        add(cardContainer, BorderLayout.CENTER)

        // 기본 탭 표시
        selectButton(storeRejectButton)
        showTab("가게거절")
        initializePanels()
    }

    private fun createPanel(): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
        }
    }

    private fun addPanelToContainer(panel: JPanel, tabName: String, panelType: String) {
        val rejectedScrollPane = JScrollPane(panel).apply {
            border = BorderFactory.createEmptyBorder(0, 20, 20, 20)
            viewportBorder = null
            background = Color.WHITE
            isOpaque = true
            viewport.apply {
                background = Color.WHITE
                isOpaque = true
            }
        }
        cardContainer.add(rejectedScrollPane, tabName)

        val filter = when (panelType) {
            "rejectStoreOrders" -> OrderFilter(serverOrderStatus = ServerOrderStatus.STORE_CANCEL)
            "rejectUserOrders" -> OrderFilter(serverOrderStatus = ServerOrderStatus.USER_CANCEL)
            "rejectRefundOrders" -> OrderFilter(serverOrderStatus = ServerOrderStatus.REFUND)
            else -> OrderFilter()
        }

        ScrollPaginationHandler(
            scrollPane = rejectedScrollPane,
            panel = panel,
            fetchOrders = { pageNumber ->
                val result = OrderAPI().fetchOrders(
                    tabbedPane.parentFrame,
                    tabbedPane.cardPanel!!,
                    filter,
                    pageNumber
                )
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
        val storeRejectCount = OrderListSingleTon.counts["rejectStoreOrders"] ?: 0
        val customerCancelCount = OrderListSingleTon.counts["rejectUserOrders"] ?: 0
        val refundCount = OrderListSingleTon.counts["rejectRefundOrders"] ?: 0

        println("Counts updated: 가게거절=$storeRejectCount, 고객취소=$customerCancelCount, 환불=$refundCount")
        storeRejectButton.button.text = "가게거절  $storeRejectCount"
        customerCancelButton.button.text = "고객취소  $customerCancelCount"
        refundButton.button.text = "환불  $refundCount"
    }

    fun initializePanels() {
        updatePanel(storeRejectPanel, OrderListSingleTon.orders["rejectStoreOrders"])
        updatePanel(customerCancelPanel, OrderListSingleTon.orders["rejectUserOrders"])
        updatePanel(refundPanel, OrderListSingleTon.orders["rejectRefundOrders"])
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
                MyColor.DARK_RED,
                MyColor.UNSELECTED_BACKGROUND_COLOR,
                MyColor.SELECTED_TEXT_COLOR,
                MyColor.GREY600,
                Dimension(160, 60)
            )
        }
    }

    fun addOrderToRejected(orderFrame: JPanel) {
        orderFrame.maximumSize = Dimension(Int.MAX_VALUE, orderFrame.preferredSize.height)
        storeRejectPanel.add(orderFrame)
        storeRejectPanel.add(Box.createRigidArea(Dimension(0, 30)))
        storeRejectPanel.revalidate()
        storeRejectPanel.repaint()
        tabbedPane.updateTabTitle(4, "주문거절", (
                OrderListSingleTon.counts["rejectStoreOrders"] ?: 0) + (OrderListSingleTon.counts["rejectUserOrders"] ?: 0) + (OrderListSingleTon.counts["rejectRefundOrders"] ?: 0)
        )
        initializePanels()
        updateCounts()
    }
}
