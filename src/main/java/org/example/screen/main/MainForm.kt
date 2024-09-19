package org.example

import OrderController
import org.example.model.Menu
import org.example.model.MenuOption
import org.example.model.Order
import org.example.view.states.PendingState
import org.example.widgets.custom_titlebar.CustomTitleBar
import java.awt.*
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane

class MainForm : JFrame() {
    private val mainPanel: JPanel  // 메인 패널
    private val tabbedPane = CustomTabbedPane()  // CustomTabbedPane 생성
    private val orderController = OrderController(tabbedPane)  // OrderController에 CustomTabbedPane을 전달
    private var orderCounter = 1  // 주문 번호 카운터

    init {
        // 기존 타이틀바 제거 및 창 리사이즈 가능 설정
        isUndecorated = true
        isResizable = true  // 창 리사이즈 가능하도록 설정

        // JFrame의 레이아웃을 BorderLayout으로 설정
        layout = BorderLayout()

        // 커스텀 타이틀바 추가
        val customTitleBar = CustomTitleBar(this, true)
        add(customTitleBar, BorderLayout.NORTH)

        // 메인 패널 초기화
        mainPanel = JPanel().apply {
            background = Color(220, 220, 220)
            layout = GridBagLayout()  // 중앙 정렬을 위해 GridBagLayout 사용
        }

        // 주문 관련 패널 추가
        initializeUI()

        // 메인 패널을 JFrame에 추가
        add(mainPanel, BorderLayout.CENTER)
        setSize(1440, 1024)
        setLocationRelativeTo(null)  // 화면 중앙에 배치
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    private fun initializeUI() {
        val gbc = GridBagConstraints().apply {
            insets = Insets(10, 10, 10, 10)
            fill = GridBagConstraints.BOTH  // 탭바가 모든 공간을 차지하도록 설정
            weightx = 1.0  // 가로 공간을 모두 차지하게 설정
            weighty = 1.0  // 세로 공간을 모두 차지하게 설정
            anchor = GridBagConstraints.CENTER  // 중앙 정렬
        }

        // 주문 목록 탭 패널
        val orderListPanel = JPanel().apply {
            background = Color(255, 255, 255)
            layout = GridBagLayout()  // 내부 레이아웃 설정
        }

        // 주문 탭 추가 (스크롤바 설정)
        val scrollPane = JScrollPane(tabbedPane).apply {
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER  // 가로 스크롤 비활성화
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED  // 세로 스크롤만 활성화
        }

        // 주문 목록 패널을 중앙에 배치 및 리사이즈 가능하도록 설정
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridwidth = 2
        gbc.weightx = 1.0  // 가로 공간을 모두 차지
        gbc.weighty = 1.0  // 세로 공간도 모두 차지
        orderListPanel.add(scrollPane, gbc)

        // "주문 발송" 버튼 추가
        val sendOrderButton = JButton("주문 발송").apply {
            preferredSize = Dimension(150, 40)  // 적당한 버튼 크기 설정
        }
        val buttonGbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 1
            weightx = 0.0  // 버튼의 크기를 고정시킴
            weighty = 0.0
            anchor = GridBagConstraints.SOUTHEAST  // 버튼을 하단 우측에 고정
        }
        orderListPanel.add(sendOrderButton, buttonGbc)

        // 주문 발송 버튼 클릭 이벤트 추가
        sendOrderButton.addActionListener {
            val newOrder = createNewOrder()  // 새로운 주문 생성
            println("New order created: Order #${newOrder.orderNumber}")  // 디버깅 출력
            orderController.addOrder(newOrder)  // 주문을 OrderController에 추가
        }

        // 메인 패널에 주문 패널 추가
        val mainGbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.BOTH  // 메인 패널을 화면에 가득 채우도록 설정
            anchor = GridBagConstraints.CENTER  // 중앙에 위치
        }
        mainPanel.add(orderListPanel, mainGbc)
    }


    // 새로운 주문 생성 함수
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
            state = PendingState()  // 초기 상태는 접수대기
        )
    }
}

fun main() {
    val mainForm = MainForm()
    mainForm.isVisible = true
}
