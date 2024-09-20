package org.example

import OrderController
import createCustomToggleButton
import org.example.model.MenuOption
import org.example.model.Menu
import org.example.model.Order
import org.example.style.MyColor
import java.awt.*
import javax.swing.*

class CustomTabbedPane : JPanel() {

    private var cardPanel: JPanel? = null  // 외부에서 전달받을 cardPanel을 nullable로 변경
    private val menuPanel = JPanel()  // 탭 메뉴 패널 (세로로 정렬)
    private var orderCounter = 1 ;

    // 주문 목록 패널들 (각 탭별로 구분)
    private val allOrdersPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE
    }
    private val pendingOrdersPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE
    }
    private val processingOrdersPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE
    }
    private val completedOrdersPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE
    }
    private val rejectedOrdersPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE
    }

    init {
        layout = BorderLayout()

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

        // 하단 운영시간 패널 추가
        val operationPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = MyColor.DARK_RED
            border = BorderFactory.createEmptyBorder(20, 0, 20, 0)  // 상하 여백 추가
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
            alignmentX = CENTER_ALIGNMENT

        }

        // 커스텀 토글 버튼 생성 및 추가
        val customToggleButton = createCustomToggleButton().apply {
            bounds = Rectangle(0, 0, 120, 40)  // 버튼 크기와 위치 설정
            isOpaque = false  // 버튼 배경을 투명하게 설정
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


        // 주문발송테스트 버튼 추가
        val sendOrderButton = JButton("주문발송테스트").apply {
            font = MyFont.Bold(14f)
            alignmentX = CENTER_ALIGNMENT
            maximumSize = Dimension(200, 50)
            addActionListener {
                val newOrder = createNewOrder()  // 새로운 주문 생성
                val orderController = OrderController(this@CustomTabbedPane)  // OrderController 생성
                orderController.addOrder(newOrder)  // OrderController를 통해 주문 추가
                println("주문이 생성되었습니다: ${newOrder.orderNumber}")
            }
        }

        // 하단에 주문발송테스트 버튼 추가
        val buttonPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(Box.createVerticalGlue())  // 공간 확보
            add(sendOrderButton)  // 버튼 추가
        }
        menuPanel.add(buttonPanel)

        // 메인 패널에 세로 탭 메뉴 추가
        add(menuPanel, BorderLayout.WEST)
    }

    private fun createNewOrder(): Order {
        return Order(
            orderNumber = orderCounter++,  // 주문 번호 증가
            orderType = "TAKEOUT",  // 포장 주문
            request = "문앞에 놔두고 가주세요",
            address = "대전 대화동 가온비즈타워 120 901호",
            deliveryFee = 3000,
            spoonFork = false,
            menuList = listOf(
                Menu(
                    menuName = "짜장면",
                    price = 9000,
                    count = 2,
                    options = listOf(
                        MenuOption("곱빼기", 1000)
                    )
                )
            ),
            state = org.example.view.states.PendingState()  // 초기 상태는 접수대기
        )
    }

    // 외부에서 cardPanel을 전달받는 함수
    fun setCardPanel(cardPanel: JPanel) {
        this.cardPanel = cardPanel

        // 카드 패널에 스크롤 가능한 주문 패널 추가
        cardPanel.add(JScrollPane(allOrdersPanel), "전체보기")
        cardPanel.add(JScrollPane(pendingOrdersPanel), "접수대기")
        cardPanel.add(JScrollPane(processingOrdersPanel), "접수처리중")
        cardPanel.add(JScrollPane(completedOrdersPanel), "접수완료")
        cardPanel.add(JScrollPane(rejectedOrdersPanel), "주문거절")

        // 기본 선택된 탭 설정
        setTab("전체보기")
    }

    // 각 탭 버튼 생성 함수
    private fun createTabButton(iconPath: String, buttonText: String, tabName: String): JPanel {
        val panel = JPanel().apply {
            layout = GridBagLayout()  // 중앙 배치를 위한 GridBagLayout 사용
            background = MyColor.DARK_NAVY
            isOpaque = true  // 배경을 불투명하게 설정

            // 패널 크기를 부모 크기에 맞게 설정
            preferredSize = Dimension(200, 135)  // 크기 설정
            maximumSize = Dimension(200, 135)
            minimumSize = Dimension(200, 135)

            // 좌우에 패딩을 추가하고 하단에 구분선을 적용하는 border 설정
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 20, 0, 20),  // 좌우에 20픽셀 패딩
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)  // 하단에 1픽셀 구분선
            )
        }

        val icon = JLabel(ImageIcon(
            ImageIcon(javaClass.getResource(iconPath))
                .image.getScaledInstance(56, 56, Image.SCALE_SMOOTH)  // 아이콘 크기를 설정
        ))

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
            override fun mouseClicked(e: java.awt.event.MouseEvent?) {
                setTab(tabName)
            }
        })

        return panel
    }

    // 탭 변경 함수 (선택된 탭에 따라 카드 패널 변경)
    private fun setTab(tabName: String) {
        if (cardPanel == null) return  // cardPanel이 초기화되지 않았으면 리턴

        val cardLayout = cardPanel!!.layout as CardLayout
        cardLayout.show(cardPanel, tabName)

        // 탭 버튼 배경색 업데이트
        for (i in 0 until menuPanel.componentCount) {
            if (menuPanel.getComponent(i) is JPanel) {
                val panel = menuPanel.getComponent(i) as JPanel
                if (panel.components.any { it is JLabel && (it as JLabel).text == tabName }) {
                    panel.background = MyColor.DARK_NAVY  // 선택된 탭 배경색 변경
                } else {
                    panel.background = MyColor.DARK_RED  // 기본 배경색
                }
            }
        }
    }

    // 탭 타이틀 업데이트 메서드
    private fun updateTabTitle(tabIndex: Int, tabName: String, count: Int) {
        val panel = menuPanel.getComponent(tabIndex) as JPanel
        val textLabel = panel.components.last() as JLabel
        textLabel.text = "$tabName $count"
    }

    // 주문 처리 관련 함수들
    fun addOrderToPending(orderFrame: JPanel) {
        orderFrame.maximumSize = Dimension(Int.MAX_VALUE, orderFrame.preferredSize.height)
        pendingOrdersPanel.add(orderFrame)
        pendingOrdersPanel.revalidate()
        pendingOrdersPanel.repaint()
        updateTabTitle(1, "접수대기", pendingOrdersPanel.componentCount)
    }

    fun addOrderToProcessing(orderFrame: JPanel) {
        processingOrdersPanel.add(orderFrame)
        processingOrdersPanel.revalidate()
        processingOrdersPanel.repaint()
        updateTabTitle(2, "접수처리중", processingOrdersPanel.componentCount)
    }

    fun addOrderToCompleted(orderFrame: JPanel) {
        completedOrdersPanel.add(orderFrame)
        completedOrdersPanel.revalidate()
        completedOrdersPanel.repaint()
        updateTabTitle(3, "접수완료", completedOrdersPanel.componentCount)
    }

    fun addOrderToRejected(orderFrame: JPanel) {
        rejectedOrdersPanel.add(orderFrame)
        rejectedOrdersPanel.revalidate()
        rejectedOrdersPanel.repaint()
        updateTabTitle(4, "주문거절", rejectedOrdersPanel.componentCount)
    }

    fun addOrderToAllOrders(orderFrame: JPanel) {
        orderFrame.maximumSize = Dimension(Int.MAX_VALUE, orderFrame.preferredSize.height)
        allOrdersPanel.add(orderFrame)
        allOrdersPanel.revalidate()
        allOrdersPanel.repaint()
        updateTabTitle(0, "전체보기", allOrdersPanel.componentCount)
    }

    // 주문 프레임 삭제 및 업데이트
    fun removeOrderFromPending(order: Order) {
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

    fun removeOrderFromProcessing(order: Order) {
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

    fun updateOrderInAllOrders(order: Order) {
        val frameToUpdate = allOrdersPanel.components
            .filterIsInstance<JPanel>()
            .find { it.getClientProperty("orderNumber") == order.orderNumber }

        frameToUpdate?.let {
            val updatedUI = order.getUI()
            it.removeAll()
            it.add(updatedUI)
            it.revalidate()
            it.repaint()
        }
    }
}
