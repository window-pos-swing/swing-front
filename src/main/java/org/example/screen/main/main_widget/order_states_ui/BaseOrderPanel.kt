package org.example.view.components

import org.example.model.Order
import javax.swing.*
import java.awt.*

class BaseOrderPanel(order: Order) : JPanel() {
    init {
        layout = BorderLayout(10, 10)
        border = BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        background = Color.WHITE
        preferredSize = Dimension(1100, 100)  // 기본 높이 100으로 설정

        // 좌측 패널 (수저포크, 배달 여부, 주문시간)
        val leftPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JLabel(if (order.spoonFork) "수저포크 O" else "수저포크 X"))  // 수저포크 여부
            add(JLabel(if (order.orderType == "DELIVERY") "배달" else "포장"))  // 배달 or 포장
            add(JLabel("주문시간: 14:52").apply {  // 주문 시간 (실제 시간 사용 가능)
                font = Font("Arial", Font.BOLD, 20)
            })
            add(JButton("주문표 인쇄").apply {
                font = Font("Arial", Font.PLAIN, 12)
            })
        }

        // 중앙 패널 (메뉴 정보, 주소, 요청사항)
        val centerPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JLabel("[메뉴 ${order.menuList.size}개] ${order.menuList.sumOf { it.price * it.count }}원"))  // 메뉴 개수와 총가격
            order.menuList.forEach { menu ->
                add(JLabel("${menu.menuName} x ${menu.count}").apply {
                    menu.options.forEach { option ->
                        add(JLabel("옵션: ${option.optionName} (+${option.optionPrice}원)"))  // 메뉴 옵션
                    }
                })
            }
            add(JLabel(order.address))  // 주소 정보
            add(JLabel(order.request).apply {  // 요청사항
                foreground = Color.WHITE
                background = Color.BLACK
                isOpaque = true
            })
        }

        // 좌, 중 패널 추가
        add(leftPanel, BorderLayout.WEST)
        add(centerPanel, BorderLayout.CENTER)
    }
}
