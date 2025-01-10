package org.grr.screen.main.main_widget.tab_manager

import CustomToggleButton
import RoundedProgressBar
import org.grr.api.SettingToServer
import org.grr.command.RejectedReasonType
import org.grr.enum.BusinessStatus
import org.grr.enum.OrderReceiveType
import org.grr.model.ReceiveOrderModel
import org.grr.model.SettingModel
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.dialog.OrderDetailDialog
import org.grr.screen.main.main_widget.dialog.PauseOperations.PauseOperationsDialog
import org.grr.screen.main.main_widget.order_states_ui.CompletedState
import org.grr.screen.main.main_widget.order_states_ui.PendingState
import org.grr.screen.main.main_widget.order_states_ui.ProcessingState
import org.grr.screen.main.main_widget.order_states_ui.RejectedState
import org.grr.screen.main.main_widget.tab_manager.completed_sub_tabs.CompletedSubTabs
import org.grr.screen.main.main_widget.tab_manager.pandding_sub_tabs.PendingSubTabs
import org.grr.screen.main.main_widget.tab_manager.processing_sub_tabs.ProcessingSubTabs
import org.grr.screen.main.main_widget.tab_manager.rejected_sub_tabs.RejectedSubTabs
import org.grr.style.MyColor
import org.grr.util.LoadImage
import org.grr.util.MyFont
import org.grr.`object`.OverlayManager
import java.awt.*
import java.awt.event.ItemEvent
import javax.swing.*

class CustomTabbedPane(val parentFrame: JFrame) : JPanel() {
    private var allOrders = OrderListSingleTon.getOrders() // 모든 주문이 담긴 리스트

    internal var cardPanel: JPanel? = null  // 외부에서 전달받을 cardPanel을 nullable로 변경
    private lateinit var overlayManager: OverlayManager
    private val menuPanel = JPanel()  // 탭 메뉴 패널 (세로로 정렬)
    private val tabButtonMap = mutableMapOf<String, JPanel>()
    private var selectedTabName: String = ""
    var isHandling = false // 이벤트 중복 처리를 막기 위한 플래그

    var pendingSubTabsState = ""
    var processingSubTabsState = ""
    var completedSubTabsState = ""
    var rejectedSubTabsState = ""

    // UI 패널들 (각 탭별로 구분)
    private val allOrdersPanel = createOrderPanel()
    val pendingOrdersPanel = createOrderPanel()
    val processingOrdersPanel = createOrderPanel()
    val completedOrdersPanel = createOrderPanel()
    val rejectedOrdersPanel = createOrderPanel()
     fun createOrderPanel(): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
        }
    }

    var pendingSubTabs: PendingSubTabs =  PendingSubTabs(this)
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
        val logoLabel = JLabel(ImageIcon(
            ImageIcon(javaClass.getResource("/Logo_white.png"))
                .image.getScaledInstance(100, 100, Image.SCALE_SMOOTH)
        )).apply {
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
        updateTabTitle(0, "전체보기", allOrdersPanel.componentCount)
        updateTabTitle(1, "접수대기", pendingOrdersPanel.componentCount)
        updateTabTitle(2, "접수처리중", processingOrdersPanel.componentCount)
        updateTabTitle(3, "접수완료", completedOrdersPanel.componentCount)
        updateTabTitle(4, "주문거절", rejectedOrdersPanel.componentCount)

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
            minimumSize = Dimension(120,40)
            alignmentX = CENTER_ALIGNMENT

        }

        // 커스텀 토글 버튼 생성 및 추가
        val customToggleButton = CustomToggleButton().apply {
            bounds = Rectangle(0, 0, 120, 40)
            addItemListener { event ->
                if (isHandling) return@addItemListener // 이벤트 중복 처리 방지

                if (event.stateChange == ItemEvent.SELECTED) {
                    // ON 상태로 전환
                    isHandling = true
                    SettingModel.savePauseTime(BusinessStatus.START)
                    val settingToServer = SettingToServer()
                    val result = settingToServer.businessStatusToServer(BusinessStatus.START)
                    if (result.first) {
//                    JOptionPane.showMessageDialog(menuPanel, "운영시간 업데이트 완료 ! ", "성공", JOptionPane.INFORMATION_MESSAGE)
                    } else {
                        JOptionPane.showMessageDialog(menuPanel, "업데이트 실패: ${result.second}", "오류", JOptionPane.ERROR_MESSAGE)
                    }
                    isSelected = true // ON 상태 유지
                    isHandling = false
                } else {
                    // OFF 상태로 전환
                    isHandling = true
                    overlayManager.addOverlayPanel()
                    PauseOperationsDialog(parentFrame, cardPanel!!, "영업 임시 중지", callback = { confirmed ->
                        if (confirmed) {
                            isSelected = false // 사용자가 임시 중지를 확인한 경우 OFF로 변경
                        } else {
                            isSelected = true // X 버튼으로 다이얼로그를 닫았을 때는 ON 상태로 유지
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
        cardPanel.add(JScrollPane(allOrdersPanel).apply {
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
        }, "전체보기")

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
        if (cardPanel == null) return
        val cardLayout = cardPanel!!.layout as CardLayout

        if (tabName == "전체보기") {
            allOrders.forEach { order ->
                updateOrderInAllOrders(order)  // 전체보기 탭을 눌렀을 때만 호출

                // 주문이 ProcessingState일 경우 프로그레스바 업데이트
                if (order.state is ProcessingState) {
                    val orderPanel = findOrderPanelByOrderNumber(order.id)
                    if (orderPanel != null) {
                        updateProgressBar(orderPanel, order)  // 프로그레스바 업데이트
                    }
                }
            }
        }

        if (tabName == "접수대기") {
            pendingSubTabs.selectButton(pendingSubTabs.allOrdersButton)
            filterPendingOrders()
            cardPanel!!.add(pendingSubTabs, "접수대기 하위탭")
            cardLayout.show(cardPanel, "접수대기 하위탭")

        } else if (tabName == "접수처리중") {
            processingSubTabs.selectButton(processingSubTabs.allOrdersButton)
            filterProcessingOrders()
            cardPanel!!.add(processingSubTabs, "접수처리중 하위탭")
            cardLayout.show(cardPanel, "접수처리중 하위탭")

        } else if(tabName == "접수완료") {
            completedSubTabs.selectButton(completedSubTabs.allOrdersButton)
            filterCompletedOrders()
            cardPanel!!.add(completedSubTabs, "접수완료 하위탭")
            cardLayout.show(cardPanel, "접수완료 하위탭")

        }else if(tabName == "주문거절"){
            rejectedSubTabs.selectButton(rejectedSubTabs.storeRejectButton)
            filterRejectedOrders()
            cardPanel!!.add(rejectedSubTabs, "주문거절 하위탭")
            cardLayout.show(cardPanel, "주문거절 하위탭")

        }else {
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
    private fun updateTabTitle(tabIndex: Int, tabName: String, count: Int) {
        try {
            val panel = menuPanel.getComponent(tabIndex + 1) as? JPanel ?: return
            val textLabel = panel.getComponent(1) as? JLabel
            if (textLabel != null) {
                val actualOrderCount = when (tabName) {
                    "전체보기" -> allOrdersPanel.components.filterIsInstance<JPanel>().size
                    "접수대기" -> pendingOrdersPanel.components.filterIsInstance<JPanel>().size
                    "접수처리중" -> processingOrdersPanel.components.filterIsInstance<JPanel>().size
                    "접수완료" -> completedOrdersPanel.components.filterIsInstance<JPanel>().size
                    "주문거절" -> rejectedOrdersPanel.components.filterIsInstance<JPanel>().size
                    else -> 0
                }
                textLabel.text = "$tabName $actualOrderCount"
            } else {
                println("Error: 두 번째 컴포넌트가 JLabel이 아닙니다.")
            }
        } catch (e: Exception) {
            println("Error updating tab title: ${e.message}")
        }
    }

    //[Filter] ========================================================================
    fun filterPendingOrders(orderType: String? = null) {
        println("filterPendingOrders")
        pendingSubTabsState = orderType ?: ""  // null이면 전체보기 서브탭 상태로 설정

        pendingOrdersPanel.removeAll()

        // 주문 타입에 따른 필터링: orderType이 null이면 전체보기, 아니면 해당 타입 필터링
        val filteredOrders = if (orderType == null) {
            allOrders.filter { it.state is PendingState }  // 전체보기: Pending 상태인 모든 주문
        } else {
            allOrders.filter { it.orderReceiveType == orderType && it.state is PendingState }  // 특정 주문 타입 필터링
        }

        // 필터링된 주문을 패널에 추가
        filteredOrders.forEach { order ->
            val orderFrame = createOrderFrame(order)
            orderFrame.maximumSize = Dimension(Int.MAX_VALUE, orderFrame.preferredSize.height)
            pendingOrdersPanel.add(orderFrame)
            pendingOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)))
        }

        // 레이아웃과 화면 갱신
        pendingOrdersPanel.revalidate()
        pendingOrdersPanel.repaint()
    }


    // Pending 주문 목록을 새로 고침하는 함수
    fun refreshPendingOrders() {
        // pendingSubTabsState 값을 확인해 현재 선택된 서브탭에 맞춰 필터링 적용
        println("refreshPendingOrders")
        if (pendingSubTabsState.isEmpty()) {
            filterPendingOrders()  // 전체보기일 때
        } else {
            filterPendingOrders(pendingSubTabsState)  // 서브탭이 선택되어 있을 때 해당 필터 적용
        }
        pendingSubTabs.PendingSubTabsUpdateCounts()
    }

    fun filterProcessingOrders(orderType: String? = null) {
        processingSubTabsState = orderType ?: ""  // null이면 전체보기 서브탭 상태로 설정

        processingOrdersPanel.removeAll()

        // 주문 타입에 따른 필터링: orderType이 null이면 전체보기, 아니면 해당 타입 필터링
        val filteredOrders = if (orderType == null) {
            allOrders.filter { it.state is ProcessingState }  // 전체보기: Processing 상태인 모든 주문
        } else {
            allOrders.filter { it.orderReceiveType == orderType && it.state is ProcessingState }  // 특정 주문 타입 필터링
        }

        // 필터링된 주문을 패널에 추가
        filteredOrders.forEach { order ->
            val orderFrame = createOrderFrame(order, forProcessing = true)
            orderFrame.maximumSize = Dimension(Int.MAX_VALUE, orderFrame.preferredSize.height)
            processingOrdersPanel.add(orderFrame)
            processingOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)))
        }

        // 레이아웃과 화면 갱신
        processingOrdersPanel.revalidate()
        processingOrdersPanel.repaint()
    }

    // Processing 주문 목록을 새로 고침하는 함수
    fun refreshProcessingOrders() {
        // processingSubTabsState 값을 확인해 현재 선택된 서브탭에 맞춰 필터링 적용
        if (processingSubTabsState.isEmpty()) {
            filterProcessingOrders()  // 전체보기일 때
        } else {
            filterProcessingOrders(processingSubTabsState)  // 서브탭이 선택되어 있을 때 해당 필터 적용
        }
        processingSubTabs.ProcessingSubTabsUpdateCounts()
    }

    fun ProcessingSubTabsCountUpdate(){
        processingSubTabs.ProcessingSubTabsUpdateCounts()
    }

    fun filterCompletedOrders(orderType: String? = null) {
        completedSubTabsState = orderType ?: ""  // null이면 전체보기 서브탭 상태로 설정

        completedOrdersPanel.removeAll()

        // 주문 타입에 따른 필터링: orderType이 null이면 전체보기, 아니면 해당 타입 필터링
        val filteredOrders = if (orderType == null) {
            allOrders.filter { it.state is CompletedState }  // 전체보기: Processing 상태인 모든 주문
        } else {
            allOrders.filter { it.orderReceiveType == orderType && it.state is CompletedState }  // 특정 주문 타입 필터링
        }

        // 필터링된 주문을 패널에 추가
        filteredOrders.forEach { order ->
            val orderFrame = createOrderFrame(order, forProcessing = false)
            orderFrame.maximumSize = Dimension(Int.MAX_VALUE, orderFrame.preferredSize.height)
            completedOrdersPanel.add(orderFrame)
            completedOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)))
        }

        // 레이아웃과 화면 갱신
        completedOrdersPanel.revalidate()
        completedOrdersPanel.repaint()
    }

    fun refreshCompletedOrders() {
        // processingSubTabsState 값을 확인해 현재 선택된 서브탭에 맞춰 필터링 적용
        if (completedSubTabsState.isEmpty()) {
            filterProcessingOrders()  // 전체보기일 때
        } else {
            filterProcessingOrders(completedSubTabsState)  // 서브탭이 선택되어 있을 때 해당 필터 적용
        }
        completedSubTabs.CompletedSubTabsUpdateCounts()
    }

    fun filterRejectedOrders(rejectType: RejectedReasonType? = null) {
        // rejectedSubTabsState 값을 업데이트
        rejectedSubTabsState = rejectType?.name ?: ""  // null이면 전체보기 상태로 설정

        rejectedOrdersPanel.removeAll()  // 기존 패널 비우기

        // 주문 타입에 따른 필터링: rejectType이 null이면 전체보기, 아니면 해당 거절 타입으로 필터링
        val filteredOrders = if (rejectType == null) {
            allOrders.filter { it.state is RejectedState }  // 전체 거절 주문
        } else {
            allOrders.filter { it.state is RejectedState && (it.state as RejectedState).rejectType == rejectType }
        }

        // 필터링된 주문을 패널에 추가
        filteredOrders.forEach { order ->
            val orderFrame = createOrderFrame(order)
            orderFrame.maximumSize = Dimension(Int.MAX_VALUE, orderFrame.preferredSize.height)
            rejectedOrdersPanel.add(orderFrame)
            rejectedOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)))
        }

        // 패널을 갱신
        rejectedOrdersPanel.revalidate()
        rejectedOrdersPanel.repaint()

        // 탭 타이틀 업데이트
        updateTabTitle(4, "주문거절", rejectedOrdersPanel.componentCount)
    }

    fun refreshRejectedOrders() {
        // processingSubTabsState 값을 확인해 현재 선택된 서브탭에 맞춰 필터링 적용
        if (rejectedSubTabsState.isEmpty()) {
            filterProcessingOrders()  // 전체보기일 때
        } else {
            filterProcessingOrders(rejectedSubTabsState)  // 서브탭이 선택되어 있을 때 해당 필터 적용
        }
        rejectedSubTabs.RejectedSubTabsUpdateCounts()
    }
    //=================================================================================


    // [ADD] =========================================================================
    fun addOrderToPending(orderFrame: JPanel) {
        pendingOrdersPanel.add(orderFrame)
        pendingOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)))
        pendingOrdersPanel.revalidate()
        pendingOrdersPanel.repaint()

        updateTabTitle(1, "접수대기", pendingOrdersPanel.componentCount)
    }

    fun addOrderToProcessing(orderFrame: JPanel) {
        orderFrame.maximumSize = Dimension(Int.MAX_VALUE, orderFrame.preferredSize.height)
        processingOrdersPanel.add(orderFrame)
        processingOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)))
        processingOrdersPanel.revalidate()
        processingOrdersPanel.repaint()
        updateTabTitle(2, "접수처리중", processingOrdersPanel.componentCount)
    }

    fun addOrderToCompleted(orderFrame: JPanel) {
        completedOrdersPanel.add(orderFrame)
        completedOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)))
        completedOrdersPanel.revalidate()
        completedOrdersPanel.repaint()
        updateTabTitle(3, "접수완료", completedOrdersPanel.componentCount)
    }

    fun addOrderToRejected(orderFrame: JPanel) {
        orderFrame.maximumSize = Dimension(Int.MAX_VALUE, orderFrame.preferredSize.height)
        rejectedOrdersPanel.add(orderFrame)
        rejectedOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)))
        rejectedOrdersPanel.revalidate()
        rejectedOrdersPanel.repaint()
        updateTabTitle(4, "주문거절", rejectedOrdersPanel.componentCount)
    }

    fun addOrderToAllOrders(orderFrame: JPanel , isInit : Boolean) {
        orderFrame.maximumSize = Dimension(Int.MAX_VALUE, orderFrame.preferredSize.height)
        orderFrame.alignmentX = Component.LEFT_ALIGNMENT // 패널을 왼쪽 정렬
        if(isInit){
            allOrdersPanel.add(orderFrame )
            allOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)))  // 간격 컴포넌트도 그 다음에 추가
        }else{
            allOrdersPanel.add(orderFrame , 0)
            allOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)), 1)  // 간격 컴포넌트도 그 다음에 추가
        }

        allOrdersPanel.revalidate()
        allOrdersPanel.repaint()
        updateTabTitle(0, "전체보기", allOrdersPanel.componentCount)
    }
    //================================================================================

    // [REMOVE & UPDATE] ======================================================================
    fun removeOrderFromPending(order: ReceiveOrderModel) {
        val frameToRemove = pendingOrdersPanel.components
            .filterIsInstance<JPanel>()
            .find { it.getClientProperty("orderNumber") == order.orderNumber }

        frameToRemove?.let {
            pendingOrdersPanel.remove(it)
            pendingOrdersPanel.revalidate()
            pendingOrdersPanel.repaint()
            updateTabTitle(1, "접수대기", pendingOrdersPanel.componentCount)
        }
    }

    fun removeOrderFromProcessing(order: ReceiveOrderModel) {
        val frameToRemove = processingOrdersPanel.components
            .filterIsInstance<JPanel>()
            .find { it.getClientProperty("orderNumber") == order.orderNumber }

        frameToRemove?.let {
            processingOrdersPanel.remove(it)
            processingOrdersPanel.revalidate()
            processingOrdersPanel.repaint()
            updateTabTitle(2, "접수처리중", processingOrdersPanel.componentCount)
        }
    }

    fun updateOrderInAllOrders(order: ReceiveOrderModel ) {
        val frameToUpdate = allOrdersPanel.components
            .filterIsInstance<JPanel>()
            .find { it.getClientProperty("orderNumber") == order.orderNumber }
//        println("updateOrderInAllOrders 전체보기 업데이트 상태 : ${order.state}")

        //조건부 테두리 설정
        if (order.state is PendingState || order.state is CompletedState || order.state is RejectedState) {
            frameToUpdate?.border = BorderFactory.createCompoundBorder()
        }else{
            frameToUpdate?.border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color(27, 43, 66), 2), // 테두리 설정
                BorderFactory.createEmptyBorder(0, 20, 0, 20)  // 바깥쪽 여백 설정
            )
        }
        //UI갱신
        frameToUpdate?.let {
            val updatedUI = order.getUI()
            it.removeAll()
            it.add(updatedUI)
            it.revalidate()
            it.repaint()
        }
    }
    //================================================================================

    // CustomTabbedPane 클래스에 해당 주문이 이미 처리중 상태인지 확인하는 메서드 추가
    fun isOrderInProcessing(order: ReceiveOrderModel): Boolean {
        // 처리중 주문 리스트에서 해당 주문이 이미 존재하는지 확인
        return processingOrdersPanel.components
            .filterIsInstance<JPanel>()
            .any { it.getClientProperty("orderNumber") == order.orderNumber }
    }

    fun myGetAllOrders(): List<ReceiveOrderModel> {
        return allOrders
    }

    // 주문 번호로 패널을 찾는 함수
    fun findOrderPanelByOrderNumber(orderNumber: Int): JPanel? {
        return allOrdersPanel.components
            .filterIsInstance<JPanel>()
            .find { it.getClientProperty("orderNumber") == orderNumber }
    }

    // 주문 프레임을 생성하는 함수
    fun createOrderFrame(order: ReceiveOrderModel, forProcessing: Boolean = false): JPanel {
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
            println("createOrderFrame에서 progressBar 업데이트 ")
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
