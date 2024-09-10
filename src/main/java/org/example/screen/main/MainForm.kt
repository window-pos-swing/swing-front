package org.example

import OrderController
import org.example.model.Menu
import org.example.model.MenuOption
import org.example.model.Order
import org.example.view.states.PendingState
import javax.swing.JButton
import javax.swing.JFrame

class MainForm : JFrame() {
    private val tabbedPane = CustomTabbedPane()  // CustomTabbedPane 생성
    private val orderController = OrderController(tabbedPane)  // OrderController에 CustomTabbedPane을 전달
    private var orderCounter = 1  // 주문 번호 카운터

    init {
        // JFrame 기본 설정
        title = "메인 폼"
        setSize(1300, 800)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)

        // CustomTabbedPane 추가
        tabbedPane.setBounds(50, 50, 1200, 600)
        add(tabbedPane)

        // 주문 발송 버튼 생성
        val sendOrderButton = JButton("주문 발송").apply {
            text = "주문 발송"
            setBounds(300, 700, 200, 50)
        }
        add(sendOrderButton)

        // 주문 발송 버튼 클릭 이벤트 추가
        sendOrderButton.addActionListener {
            val newOrder = createNewOrder()  // 새로운 주문 생성
            println("New order created: Order #${newOrder.orderNumber}")  // 디버깅 출력
            orderController.addOrder(newOrder)  // 주문을 OrderController에 추가
        }

        layout = null  // 절대 레이아웃 사용
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