package org.grr.screen.main.main_widget.tab_manager

import CustomToggleButton
import RoundedProgressBar
import org.grr.api.OrderAPI
import org.grr.api.SettingToServer
import org.grr.enum.BusinessStatus
import org.grr.enum.OrderReceiveType
import org.grr.model.OrderFilter
import org.grr.model.ReceiveOrderModel
import org.grr.model.SettingModel
import org.grr.`object`.OrderController.initializeOrders
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.dialog.OrderDetailDialog
import org.grr.screen.main.main_widget.dialog.PauseOperations.PauseOperationsDialog
import org.grr.screen.main.main_widget.tab_manager.completed.completed_sub_tabs.CompletedSubTabs
import org.grr.screen.main.main_widget.tab_manager.pending.pandding_sub_tabs.PendingSubTabs
import org.grr.screen.main.main_widget.tab_manager.processing.processing_sub_tabs.ProcessingSubTabs
import org.grr.style.MyColor
import org.grr.util.LoadImage
import org.grr.util.MyFont
import org.grr.`object`.OverlayManager
import org.grr.screen.main.main_widget.order_states_ui.*
import org.grr.screen.main.main_widget.tab_manager.rejected.rejected_sub_tabs.RejectedSubTabs
import org.json.JSONArray
import java.awt.*
import java.awt.event.ItemEvent
import javax.swing.*

class CustomTabbedPane(val parentFrame: JFrame) : JPanel() {
    internal var cardPanel: JPanel? = null  // 외부에서 전달받을 cardPanel을 nullable로 변경
    private var overlayManager: OverlayManager
    private val menuPanel = JPanel()  // 탭 메뉴 패널 (세로로 정렬)
    private val tabButtonMap = mutableMapOf<String, JPanel>()
    private var selectedTabName: String = ""
    var isHandling = false // 이벤트 중복 처리를 막기 위한 플래그

    // UI 패널들 (각 탭별로 구분)
    val allOrdersPanel = createOrderPanel()
    fun createOrderPanel(): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
        }
    }

    var pendingSubTabs: PendingSubTabs = PendingSubTabs(this)
    var processingSubTabs: ProcessingSubTabs = ProcessingSubTabs(this)
    var completedSubTabs: CompletedSubTabs = CompletedSubTabs(this)
    var rejectedSubTabs: RejectedSubTabs = RejectedSubTabs(this)

    init {
        layout = BorderLayout()
        overlayManager = OverlayManager
        // 세로 탭 메뉴 패널 설정
        menuPanel.layout = BoxLayout(menuPanel, BoxLayout.Y_AXIS)
        menuPanel.background = MyColor.DARK_RED
        menuPanel.preferredSize = Dimension(200, height)

        // 최상단 로고 추가 및 중앙 정렬 (로고 크기 100x100으로 설정)
        val logoLabel = JLabel(
            ImageIcon(
                ImageIcon(javaClass.getResource("/Logo_white.png"))
                    .image.getScaledInstance(100, 100, Image.SCALE_SMOOTH)
            )
        ).apply {
            alignmentX = CENTER_ALIGNMENT
            border = BorderFactory.createEmptyBorder(20, 0, 20, 0)  // 하단 마진 40 추가
        }
        menuPanel.add(logoLabel)

        // 각 탭 메뉴 버튼 생성 및 추가
        val tabButtons = arrayOf(
            createTabButton("/home.png", "전체보기", "전체보기"),
            createTabButton("/접수대기.png", "접수대기", "접수대기"),
            createTabButton("/접수처리중.png", "접수처리중", "접수처리중"),
            createTabButton("/접수완료.png", "접수완료", "접수완료"),
            createTabButton("/주문거절.png", "주문거절", "주문거절")
        )

        for (button in tabButtons) {
            menuPanel.add(button)
        }

        // 각 탭 버튼과 이름을 매핑
        tabButtonMap["전체보기"] = tabButtons[0]
        tabButtonMap["접수대기"] = tabButtons[1]
        tabButtonMap["접수처리중"] = tabButtons[2]
        tabButtonMap["접수완료"] = tabButtons[3]
        tabButtonMap["주문거절"] = tabButtons[4]

        // 각 탭에 초기 주문 수를 설정
        updateTabTitle(0, "전체보기", OrderListSingleTon.counts["allOrders"] ?: 0)
        updateTabTitle(1, "접수대기", OrderListSingleTon.counts["pendingOrders"] ?: 0)
        updateTabTitle(2, "접수처리중", OrderListSingleTon.counts["processingOrders"] ?: 0)
        updateTabTitle(3, "접수완료", OrderListSingleTon.counts["completedOrders"] ?: 0)
        updateTabTitle(
            4, "주문거절", (
                    OrderListSingleTon.counts["rejectStoreOrders"]
                        ?: 0) + (OrderListSingleTon.counts["rejectUserOrders"]
                ?: 0) + (OrderListSingleTon.counts["rejectRefundOrders"] ?: 0)
        )

        // 하단 운영시간 패널 추가
        val operationPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = MyColor.DARK_RED
            border = BorderFactory.createEmptyBorder(10, 0, 20, 0)  // 상하 여백 추가
        }

        // 운영시간 레이블 추가
        val operationLabel = JLabel("운영시간").apply {
            font = MyFont.Bold(20f)
            foreground = Color.WHITE
            alignmentX = CENTER_ALIGNMENT
            border = BorderFactory.createEmptyBorder(0, 0, 10, 0)
        }
        operationPanel.add(operationLabel)

        // ON/OFF 토글 버튼
        val togglePanel = JPanel(null).apply {
            background = MyColor.DARK_RED  // 패널의 배경색을 DARK_RED로 설정
            isOpaque = true  // 패널을 불투명하게 설정하여 배경색이 적용되도록 함
            preferredSize = Dimension(120, 40)
            maximumSize = Dimension(120, 40)
            minimumSize = Dimension(120, 40)
            alignmentX = CENTER_ALIGNMENT
        }

        // 커스텀 토글 버튼 생성 및 추가
        val customToggleButton = CustomToggleButton().apply {
            bounds = Rectangle(0, 0, 120, 40)
            addItemListener { event ->
                if (isHandling) return@addItemListener // 이벤트 중복 처리 방지

                if (event.stateChange == ItemEvent.SELECTED) {
                    // OFF 상태로 전환
                    isHandling = true
                    overlayManager.addOverlayPanel()
                    PauseOperationsDialog(parentFrame, cardPanel!!, "영업 임시 중지", callback = { confirmed ->
                        if (confirmed) {
                            isSelected = true // 임시 중지
                        } else {
                            isSelected = false // 운영 시작
                        }
                    }).apply {
                        addWindowListener(object : java.awt.event.WindowAdapter() {
                            override fun windowClosed(e: java.awt.event.WindowEvent?) {
//                                    dialog.dispose()
                                overlayManager.removeOverlayPanel()
                            }
                        })
                    }
                    isHandling = false
                } else {
                    // ON 상태로 전환
                    isHandling = true
//                    SettingModel.savePauseTime()
                    val settingToServer = SettingToServer()
                    val result = settingToServer.businessStatusToServer()
                    if (result.first) {
//                    JOptionPane.showMessageDialog(menuPanel, "운영시간 업데이트 완료 ! ", "성공", JOptionPane.INFORMATION_MESSAGE)
                    } else {
                        JOptionPane.showMessageDialog(
                            menuPanel,
                            "업데이트 실패: ${result.second}",
                            "오류",
                            JOptionPane.ERROR_MESSAGE
                        )
                    }
//                    isSelected = false // ON 상태 유지
                    isHandling = false
                }
            }
        }

        // 패널에 토글 버튼 추가
        togglePanel.add(customToggleButton)
        operationPanel.add(togglePanel)  // 운영시간 패널에 토글 패널 추가

        // 운영시간 텍스트
        val hoursLabel = JLabel("<html>월 - 금: 24시간<br/>토요일: 24시간<br/>일요일: 24시간</html>").apply {
            font = MyFont.Bold(14f)
            foreground = Color.WHITE
            alignmentX = CENTER_ALIGNMENT
            horizontalAlignment = SwingConstants.CENTER  // 텍스트 중앙 정렬
            border = BorderFactory.createEmptyBorder(10, 0, 0, 0)
        }
        operationPanel.add(hoursLabel)

//        operationPanel.add(Box.createVerticalStrut(20))  // 10px 공간 추가

        // 하단에 운영시간 패널 추가
        menuPanel.add(Box.createVerticalGlue())  // 기존 컴포넌트와 하단 운영시간 사이 공간 확보
        menuPanel.add(operationPanel)


        // 메인 패널에 세로 탭 메뉴 추가
        add(menuPanel, BorderLayout.WEST)
        println("TabbedPane 셋팅 완료")
    }


    // 외부에서 cardPanel을 전달받는 함수
    fun setCardPanel(cardPanel: JPanel) {
        this.cardPanel = cardPanel
        // 서브탭 초기화
        pendingSubTabs = PendingSubTabs(this)
        processingSubTabs = ProcessingSubTabs(this)
        completedSubTabs = CompletedSubTabs(this)
        rejectedSubTabs = RejectedSubTabs(this)
        // 전체보기 패널 추가
        val allOrdersScrollPane = JScrollPane(allOrdersPanel).apply {
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
        }
        ScrollPaginationHandler(
            orderStatus = "allOrders",
            scrollPane = allOrdersScrollPane,
            panel = allOrdersPanel,
            fetchOrders = { pageNumber ->
                val result = OrderAPI().fetchOrders(parentFrame, cardPanel, OrderFilter(), pageNumber)
                JSONArray(result.second).let { ReceiveOrderModel.fromJsonArray(it, parentFrame, cardPanel) }
            },
            initializeOrders = { newOrders ->
                initializeOrders(OrderListSingleTon.orders["allOrders"]!!)
                // 디버깅 출력
                println("Added new orders to allOrdersPanel: ${newOrders.map { it.orderNumber }}")
            },
            getPageNumber = { OrderListSingleTon.pageNumbers["allOrders"] ?: 0 },
        )

        cardPanel.add(allOrdersScrollPane, "전체보기")
        // 접수대기 탭에 PendingSubTabs 추가
        cardPanel.add(pendingSubTabs, "접수대기")

        // 접수처리중 탭에 ProcessingSubTabs 추가
        cardPanel.add(processingSubTabs, "접수처리중")

        // 나머지 탭 추가
        cardPanel.add(completedSubTabs, "접수완료")
        cardPanel.add(rejectedSubTabs, "주문거절")

        // 기본 선택된 탭 설정
        setTab("전체보기")
    }

    // 각 탭 버튼 생성 함수
    private fun createTabButton(iconPath: String, buttonText: String, tabName: String): JPanel {
        val panel = JPanel().apply {
            layout = GridBagLayout()  // 중앙 배치를 위한 GridBagLayout 사용
            background = MyColor.DARK_RED
            isOpaque = true  // 배경을 불투명하게 설정

            // 패널 크기를 부모 크기에 맞게 설정
            preferredSize = Dimension(200, 135)  // 크기 설정
            maximumSize = Dimension(200, 135)
            minimumSize = Dimension(200, 135)

            // 좌우에 패딩을 추가하고 하단에 구분선을 적용하는 border 설정
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 20, 0, 20),  // 좌우에 20픽셀 패딩
                BorderFactory.createMatteBorder(0, 0, 1, 0, MyColor.DIVISION_PINK)  // 하단에 1픽셀 구분선
            )
        }

        val icon = JLabel(LoadImage.loadImage(iconPath, 56, 56))

        val text = JLabel(buttonText).apply {
            font = MyFont.Bold(20f)
            foreground = Color.WHITE
            horizontalAlignment = SwingConstants.CENTER
        }

        // GridBagConstraints 설정 (컴포넌트 배치를 위한 설정)
        val gbc = GridBagConstraints().apply {
            anchor = GridBagConstraints.CENTER  // 컴포넌트를 중앙에 배치
            gridx = 0
            gridy = GridBagConstraints.RELATIVE  // y축에서 상대적으로 배치
            insets = Insets(5, 0, 5, 0)  // 상하 여백 설정
        }

        // 아이콘과 텍스트를 패널에 추가
        panel.add(icon, gbc)
        panel.add(text, gbc)

        // 클릭 이벤트를 추가하여 탭 변경
        panel.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mousePressed(e: java.awt.event.MouseEvent?) {
                setTab(tabName)
            }
        })

        return panel
    }

    // CustomTabbedPane의 setTab 함수 수정
    fun setTab(tabName: String) {
        print("CustomTabb setTab")
        if (cardPanel == null) return
        val cardLayout = cardPanel!!.layout as CardLayout

        if (tabName == "전체보기") {
            OrderListSingleTon.orders["allOrders"]?.forEach { order ->
                updateOrderInAllOrders(order)  // 전체보기 탭을 눌렀을 때만 호출

                // 주문이 ProcessingState일 경우 프로그레스바 업데이트
                if (order.state is ProcessingState) {
                    val orderPanel = findOrderPanelByOrderNumber(order.orderNumber)
                    if (orderPanel != null) {
                        updateProgressBar(orderPanel, order)  // 프로그레스바 업데이트
                    } else {
                        println("no find updateProgressBar")
                    }
                }
            }
        }

        if (tabName == "접수대기") {
            // PendingSubTabs에서 버튼과 패널 설정
            pendingSubTabs.selectButton(pendingSubTabs.allOrdersButton)
            pendingSubTabs.showTab("전체보기") // "전체보기" 패널 표시
            cardPanel!!.add(pendingSubTabs, "접수대기 하위탭")
            cardLayout.show(cardPanel, "접수대기 하위탭")

        } else if (tabName == "접수처리중") {
            processingSubTabs.selectButton(processingSubTabs.allOrdersButton)
            processingSubTabs.showTab("전체보기")
            cardPanel!!.add(processingSubTabs, "접수처리중 하위탭")
            cardLayout.show(cardPanel, "접수처리중 하위탭")

        } else if (tabName == "접수완료") {
            completedSubTabs.selectButton(completedSubTabs.allOrdersButton)
            cardPanel!!.add(completedSubTabs, "접수완료 하위탭")
            cardLayout.show(cardPanel, "접수완료 하위탭")

        } else if (tabName == "주문거절") {
            rejectedSubTabs.selectButton(rejectedSubTabs.storeRejectButton)
            cardPanel!!.add(rejectedSubTabs, "주문거절 하위탭")
            cardLayout.show(cardPanel, "주문거절 하위탭")

        } else {
            cardLayout.show(cardPanel, tabName)
        }

        // 기존 선택된 탭의 배경색, 아이콘, 텍스트 색상 복구
        tabButtonMap.forEach { (name, panel) ->
            val iconLabel = panel.getComponent(0) as JLabel  // 첫 번째 컴포넌트는 아이콘
            val textLabel = panel.getComponent(1) as JLabel  // 두 번째 컴포넌트는 텍스트
            if (name == tabName) {
                // 선택된 탭: 배경색을 DARK_NAVY로, 텍스트는 흰색으로, 아이콘을 흰색 버전으로
                panel.background = MyColor.DARK_NAVY
                textLabel.foreground = Color.WHITE
                val whiteIconPath = when (name) {
                    "전체보기" -> "/home_white.png"
                    "접수대기" -> "/접수대기_white.png"
                    "접수처리중" -> "/접수처리중_white.png"
                    "접수완료" -> "/접수완료_white.png"
                    "주문거절" -> "/주문거절_white.png"
                    else -> ""  // 여기에 기본값 또는 에러 처리를 추가할 수 있음
                }
                iconLabel.icon = ImageIcon(javaClass.getResource(whiteIconPath))
            } else {
                // 선택되지 않은 탭: 배경색은 DARK_RED, 텍스트는 UNSELECTED_TAP 색상, 기본 아이콘
                panel.background = MyColor.DARK_RED
                textLabel.foreground = MyColor.PINK
                val defaultIconPath = when (name) {
                    "전체보기" -> "/home.png"
                    "접수대기" -> "/접수대기.png"
                    "접수처리중" -> "/접수처리중.png"
                    "접수완료" -> "/접수완료.png"
                    "주문거절" -> "/주문거절.png"
                    else -> ""  // 여기에 기본값 또는 에러 처리를 추가할 수 있음
                }
                iconLabel.icon = ImageIcon(javaClass.getResource(defaultIconPath))
            }
        }

        // 현재 선택된 탭 이름을 업데이트
        selectedTabName = tabName
    }

    // 탭 타이틀 업데이트 메서드
    fun updateTabTitle(tabIndex: Int, tabName: String, count: Int) {
        try {
            val panel = menuPanel.getComponent(tabIndex + 1) as? JPanel ?: return
            val textLabel = panel.getComponent(1) as? JLabel
            if (textLabel != null) {
                textLabel.text = "$tabName $count"
            } else {
                println("Error: 두 번째 컴포넌트가 JLabel이 아닙니다.")
            }
        } catch (e: Exception) {
            println("Error updating tab title: ${e.message}")
        }
    }


    // TODO [ADD]
    fun addOrderToPending(orderFrame: JPanel, typeOrderFrame: JPanel, order: ReceiveOrderModel) {
        pendingSubTabs.addOrderToPending(orderFrame, typeOrderFrame, order)
    }

    fun addOrderToProcessing(orderFrame: JPanel, typeOrderFrame: JPanel, order: ReceiveOrderModel) {
        processingSubTabs.addOrderToProcessing(orderFrame, typeOrderFrame, order)
    }

    fun addOrderToCompleted(orderFrame: JPanel, order: ReceiveOrderModel) {
        completedSubTabs.addOrderToCompleted(orderFrame, order)
    }

    fun addOrderToRejected(orderFrame: JPanel) {
        rejectedSubTabs.addOrderToRejected(orderFrame)
    }

    fun addOrderToAllOrders(orderFrame: JPanel, isInit: Boolean) {

        println("addOrderToAllOrders called. isInit: $isInit")

        orderFrame.maximumSize = Dimension(Int.MAX_VALUE, orderFrame.preferredSize.height)
        orderFrame.alignmentX = Component.LEFT_ALIGNMENT // 패널을 왼쪽 정렬

        if (isInit) {
            allOrdersPanel.add(orderFrame)
            allOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)))  // 간격 컴포넌트도 추가
        } else {
            allOrdersPanel.add(orderFrame, 0)
            allOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)), 1)  // 간격 컴포넌트도 추가
        }

        // UI 갱신
        allOrdersPanel.revalidate()
        allOrdersPanel.repaint()

        updateTabTitle(0, "전체보기", OrderListSingleTon.counts["allOrders"] ?: 0)
    }

    //================================================================================

    // [REMOVE & UPDATE] ======================================================================
    fun removeOrderFromPending(order: ReceiveOrderModel) {
        pendingSubTabs.removeOrderFromPending(order)
    }

    fun removeOrderFromProcessing(order: ReceiveOrderModel) {
        processingSubTabs.removeOrderFromProcessing(order)
    }

    fun updateOrderInAllOrders(order: ReceiveOrderModel) {
        println("updateOrderInAllOrders : ${order.orderNumber}, 현재 상태: ${order.state::class.simpleName}")
        val frameToUpdate = allOrdersPanel.components
            .filterIsInstance<BaseOrderPanel>()
            .find { it.getClientProperty("orderNumber") == order.orderNumber }

        //조건부 테두리 설정
        if (order.state is PendingState || order.state is CompletedState || order.state is RejectedState) {
            frameToUpdate?.border = BorderFactory.createCompoundBorder()
        } else {
            frameToUpdate?.border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color(27, 43, 66), 2), // 테두리 설정
                BorderFactory.createEmptyBorder(0, 20, 0, 20)  // 바깥쪽 여백 설정
            )
        }

        if (frameToUpdate == null) {
            println("Error: Frame not found for order OrderNumber ${order.orderNumber}")
            return
        }

        //UI갱신
        frameToUpdate.let {
            val updatedUI = order.getUI()
            it.removeAll()
            it.add(updatedUI)
            it.revalidate()
            it.repaint()
        }
    }
    //================================================================================


    // 주문 번호로 패널을 찾는 함수
    fun findOrderPanelByOrderNumber(orderNumber: String): JPanel? {
        return allOrdersPanel.components
            .filterIsInstance<JPanel>()
            .find { it.getClientProperty("orderNumber") == orderNumber }
    }

    // 주문 프레임을 생성하는 함수
    fun createOrderFrame(order: ReceiveOrderModel, forProcessing: Boolean = false): JPanel {
        println("createOrderFrame orderNumber : ${order.orderNumber}")
//        println("createOrderFrame orderState : ${order.state}")
        val orderPanel = order.getUI().apply {
            minimumSize = Dimension(1162, 340)
            preferredSize = Dimension(1162, 340)
            maximumSize = Dimension(1162, 340)
            putClientProperty("orderNumber", order.orderNumber)

            // 접수처리중 탭일 때 별도의 테두리 설정
            if (forProcessing) {
                setProcessingBorder()
                updateProgressBar(this, order)
            }

            addOrderClickListener(order)
        }
        return orderPanel
    }

    // 접수처리중 탭일 때의 테두리 설정
    private fun JPanel.setProcessingBorder() {
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color(27, 43, 66), 2), // 테두리 설정
            BorderFactory.createEmptyBorder(0, 20, 0, 20)  // 바깥쪽 여백 설정
        )
    }

    // 프로그레스바를 찾고 업데이트하는 함수
    private fun updateProgressBar(orderPanel: JPanel, order: ReceiveOrderModel) {
        val progressBar = findProgressBar(orderPanel)
        if (progressBar != null && order.state is ProcessingState) {
            progressBar.updateProgress(order.elapsedTime)
            progressBar.repaint()
            println("progressBar 업데이트  : ${order.elapsedTime}")
        }
    }

    // 재귀적으로 모든 자식 컴포넌트를 탐색하여 RoundedProgressBar를 찾는 함수
    fun findProgressBar(component: Component): RoundedProgressBar? {
        return when (component) {
            is RoundedProgressBar -> component
            is Container -> component.components
                .mapNotNull { findProgressBar(it) }
                .firstOrNull()

            else -> null
        }
    }

    // 주문 프레임에 클릭 리스너 추가하는 함수
    private fun JPanel.addOrderClickListener(order: ReceiveOrderModel) {
        addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mousePressed(e: java.awt.event.MouseEvent?) {
                println("Detail Order #${order.orderNumber}")
                // 다이얼로그가 열리기 전에 투명한 검은색 레이어 추가
                overlayManager.addOverlayPanel()

                // 다이얼로그 열기
                val dialogTitle = getOrderDialogTitle(order)
                val dialog = OrderDetailDialog(
                    SwingUtilities.getWindowAncestor(this@addOrderClickListener) as JFrame,
                    cardPanel!!,
                    dialogTitle,
                    order
                )

                // 다이얼로그가 닫힐 때 투명한 레이어 제거
                dialog.addWindowListener(object : java.awt.event.WindowAdapter() {
                    override fun windowClosed(e: java.awt.event.WindowEvent?) {
                        overlayManager.removeOverlayPanel()
                    }
                })
            }
        })
    }

    // 주문 타입에 따른 다이얼로그 타이틀 설정 함수
    private fun getOrderDialogTitle(order: ReceiveOrderModel): String {
        val customFont = MyFont.Bold(32f)
        val fontFamily = customFont.fontName
        val orderTypeText = if (order.orderReceiveType == OrderReceiveType.DELIVERY.toString()) {
            "<font color='red' style='font-family:$fontFamily; font-size:26px;'>배달</font>"
        } else {
            "<font color='blue' style='font-family:$fontFamily; font-size:26px;'>포장</font>"
        }
        return "<html><span style='font-family:$fontFamily; font-size:26px;'>$orderTypeText 주문 상세</span></html>"
    }
}