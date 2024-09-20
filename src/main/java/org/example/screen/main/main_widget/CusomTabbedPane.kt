package org.example

import org.example.model.Order
import org.example.style.MyColor
import java.awt.*
import javax.swing.*

class CustomTabbedPane : JPanel() {

    private val cardPanel = JPanel(CardLayout())  // 탭에 따른 다른 콘텐츠를 보여주는 패널
    private val menuPanel = JPanel()  // 탭 메뉴 패널 (세로로 정렬)

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
            border = BorderFactory.createEmptyBorder(20, 0, 40, 0)  // 하단 마진 40 추가
        }
        menuPanel.add(logoLabel)

        // 각 탭 메뉴 버튼 생성 및 추가 (아이콘 아래에 텍스트가 오도록 수정)
        val tabButtons = arrayOf(
            createTabButton("/home.png", "전체보기", "전체보기"),
            createTabButton("/접수대기.png", "접수대기", "접수대기"),
            createTabButton("/접수처리중.png", "접수처리중", "접수처리중"),
            createTabButton("/접수완료.png", "접수완료", "접수완료"),
            createTabButton("/주문거절.png", "주문거절", "주문거절")
        )

        for (button in tabButtons) {
            menuPanel.add(button)

//            menuPanel.add(createSeparator())  // 각 탭 사이에 얇은 구분선 추가

        }


        // 카드 패널에 스크롤 가능한 주문 패널 추가
        cardPanel.add(JScrollPane(allOrdersPanel), "전체보기")
        cardPanel.add(JScrollPane(pendingOrdersPanel), "접수대기")
        cardPanel.add(JScrollPane(processingOrdersPanel), "접수처리중")
        cardPanel.add(JScrollPane(completedOrdersPanel), "접수완료")
        cardPanel.add(JScrollPane(rejectedOrdersPanel), "주문거절")

        // 기본 선택된 탭 설정
        setTab("전체보기")

        // 메인 패널에 세로 탭 메뉴와 카드 패널 추가
        add(menuPanel, BorderLayout.WEST)
        add(cardPanel, BorderLayout.CENTER)
    }
    // 구분선 생성 함수
    private fun createSeparator(): JSeparator {
        return JSeparator(SwingConstants.HORIZONTAL).apply {
            maximumSize = Dimension(180, 50)  // 구분선의 두께를 얇게 설정
            background = MyColor.DIVISION_PINK
            foreground = MyColor.DIVISION_PINK
            alignmentX = CENTER_ALIGNMENT
        }
    }

    // 각 탭 버튼 생성 함수 (아이콘 아래에 텍스트가 오도록 수정)
    private fun createTabButton(iconPath: String, buttonText: String, tabName: String): JPanel {
        val panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = MyColor.DARK_NAVY
            alignmentX = CENTER_ALIGNMENT
            // 패널 크기를 77x95로 설정
            preferredSize = Dimension(77, 95)
            maximumSize = Dimension(77, 95)
            minimumSize = Dimension(77, 95)
            isOpaque = true  // 배경 투명화
        }

        val icon = JLabel(ImageIcon(
            ImageIcon(javaClass.getResource(iconPath))
                .image.getScaledInstance(56, 56, Image.SCALE_SMOOTH)  // 아이콘 크기를 56x56으로 설정
        )).apply {
            alignmentX = CENTER_ALIGNMENT
        }

        val text = JLabel(buttonText).apply {
            alignmentX = CENTER_ALIGNMENT
            font = Font("Arial", Font.BOLD, 16)
            foreground = Color.WHITE
        }

        panel.add(icon)
        panel.add(Box.createVerticalStrut(5))  // 아이콘과 텍스트 간격
        panel.add(text)

        panel.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent?) {
                setTab(tabName)
            }
        })

        return panel
    }

    // 탭 변경 함수 (선택된 탭의 배경색 변경)
    private fun setTab(tabName: String) {
        val cardLayout = cardPanel.layout as CardLayout
        cardLayout.show(cardPanel, tabName)

        // 탭 버튼 배경색 업데이트
        for (i in 1 until menuPanel.componentCount) {
            if (menuPanel.getComponent(i) is JPanel) {
                val panel = menuPanel.getComponent(i) as JPanel
                if (panel.components.any { it is JLabel && (it as JLabel).text == tabName }) {
                    panel.background = MyColor.DARK_NAVY  // 선택된 탭 배경색 변경
                } else {
                    panel.background = Color(200, 0, 0)  // 기본 배경색
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
