package org.example.view.components

import org.example.MyFont
import org.example.model.Order
import org.example.style.MyColor
import org.example.widgets.FillRoundedLabel
import org.example.widgets.OutLineRoundBorder
import javax.swing.*
import java.awt.*

class BaseOrderPanel(order: Order) : JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)  // 모든 내용을 수직으로 정렬
        background = Color.WHITE
        preferredSize = Dimension(Int.MAX_VALUE, 350)  // 패널 크기 설정

        // 1. [headerPanel] 주문시간 + 메뉴 갯수와 총 가격
        val headerPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)  // 가로로 배치 (수평)
            isOpaque = false  // 배경 투명
            preferredSize = Dimension(Int.MAX_VALUE, 80)  // BaseOrderPanel의 전체 너비에 맞춤
            maximumSize = Dimension(Int.MAX_VALUE, 80)

            // 아이콘 로드 (아이콘이 패널의 맨 위 왼쪽에 붙게)
            val iconLabel = JLabel(LoadImage.loadImage(if (order.orderType == "DELIVERY") "/delivery_state.png" else "/takeout_state.png", 56, 80)).apply {
                border = BorderFactory.createEmptyBorder(0, 0, 0, 15)  // 오른쪽에 여백 추가, 상하 여백 제거
            }

            // 주문시간과 메뉴 갯수 및 총 가격을 한 줄로 출력하는 패널
            val timeAndMenuPanel = JPanel().apply {
                layout = FlowLayout(FlowLayout.LEFT, 0, 0)  // 수평으로 정렬
                isOpaque = false  // 배경 투명
                border = BorderFactory.createEmptyBorder(15, 0, 0, 0)  // 상하 여백 제거

                // 주문시간만 폰트 크기를 40으로 설정
                val orderTimeLabel = JLabel("${order.orderTime}분").apply {
                    font = MyFont.Bold(40f)  // 주문시간 폰트 크기 40
                    foreground = MyColor.BLACK
                }

                // 메뉴 갯수와 총 가격은 폰트 크기를 24로 설정
                val menuInfoLabel = JLabel("[메뉴 ${order.menuList.size}개] ${order.menuList.sumOf { it.price * it.count }}원").apply {
                    font = MyFont.Medium(24f)  // 메뉴 갯수와 총 가격 폰트 크기 24
                    foreground = MyColor.BLACK
                }

                // 수평으로 주문시간과 메뉴 정보 배치
                add(orderTimeLabel)
                add(Box.createRigidArea(Dimension(10, 0)))  // 주문시간과 메뉴 정보 사이에 간격 추가
                add(menuInfoLabel)
            }

            // 하단에만 검은색 테두리 추가
            val bottomBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, MyColor.LIGHT_GREY)  // 하단(border)만 1px 설정

            // headerPanel에 아이콘과 텍스트 패널을 수평으로 추가
            add(iconLabel)  // 아이콘을 먼저 추가 (최상단 왼쪽에 위치)
            add(timeAndMenuPanel)  // 아이콘 오른쪽에 시간 및 메뉴 정보 추가

            // 좌우 여백만 적용, 상하 여백 제거
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 0),  // 상하 여백 제거
                bottomBorder  // 하단 테두리
            )
        }

        // 2. [addressPanel] 주소 정보 (왼쪽 정렬)
        val addressPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)  // 가로로 배치 (수평)
            isOpaque = false  // 배경 투명
            preferredSize = Dimension(Int.MAX_VALUE, 20)  // BaseOrderPanel의 전체 너비에 맞춤
            maximumSize = Dimension(Int.MAX_VALUE, 20)

            // 주소 정보
            val addressItems = JLabel(order.address).apply {
                font = MyFont.Medium(20f)
                border = BorderFactory.createEmptyBorder(0, 0, 0, 0)  // 상하 여백 제거
                foreground = Color.GRAY
            }

            add(addressItems, BorderLayout.CENTER)
        }

        // 3. [menuDetailPanel] 메뉴 세부사항 (왼쪽 정렬)
        val menuDetailPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)  // 가로로 배치 (수평)
            isOpaque = false  // 배경 투명
            preferredSize = Dimension(Int.MAX_VALUE, 107)  // BaseOrderPanel의 전체 너비에 맞춤
            maximumSize = Dimension(Int.MAX_VALUE, 107)

            // 둥근 검은색 테두리 생성
            val roundedBorder = OutLineRoundBorder(MyColor.BLACK, 2, 15, 15)
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(1, 1, 1, 1),  // 상하 여백 제거
                roundedBorder  // 둥근 테두리 적용
            )

            // 메뉴 및 옵션 정보를 담을 수평 패널 생성
            val menuRowPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0)).apply {
                isOpaque = false  // 배경 투명
            }

            // 메뉴 목록을 순회하면서 각 메뉴와 옵션을 한 줄로 추가
            order.menuList.forEachIndexed { index, menu ->
                // 옵션 출력 (예: 곱빼기x1)
                val optionsText = if (menu.options.isNotEmpty()) {
                    menu.options.joinToString(", ") { it.optionName }
                } else {
                    ""
                }

                // 메뉴 이름과 옵션, 개수를 함께 출력
                val menuLabel = if (optionsText.isNotEmpty()) {
                    JLabel("${menu.menuName} (${optionsText}) x ${menu.count}").apply {
                        font = MyFont.Medium(24f)
                        border = BorderFactory.createEmptyBorder(10, if (index == 0) 10 else 0, 10, 10)  // 첫 메뉴만 왼쪽 여백 추가
                        foreground = MyColor.BLACK
                    }
                } else {
                    JLabel("${menu.menuName} x ${menu.count}").apply {
                        font = MyFont.Medium(24f)
                        border = BorderFactory.createEmptyBorder(10, if (index == 0) 10 else 0, 10, 10)  // 첫 메뉴만 왼쪽 여백 추가
                        foreground = MyColor.BLACK
                    }
                }
                menuRowPanel.add(menuLabel)

                // 여러 메뉴가 있을 경우, 메뉴 사이에 '/' 구분자 추가
                if (index < order.menuList.size - 1) {
                    val separatorLabel = JLabel(" / ").apply {
                        font = MyFont.Medium(24f)
                        foreground = MyColor.BLACK
                    }
                    menuRowPanel.add(separatorLabel)
                }
            }

            // 전체 패널에 메뉴 아이템을 왼쪽(WEST) 정렬로 추가
            add(menuRowPanel, BorderLayout.WEST)
        }

        // 4. [footerPanel] 수저/포크 선택 및 요청사항 (왼쪽 정렬)
        val footerPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)  // 가로로 배치 (수평)
            isOpaque = false  // 배경 투명
            preferredSize = Dimension(Int.MAX_VALUE, 100)  // BaseOrderPanel의 전체 너비에 맞춤
            maximumSize = Dimension(Int.MAX_VALUE, 100)

            // 수저/포크 정보 (둥근 테두리 안에 노란색 배경 + 흰색 텍스트)
            val spoonForkLabel = FillRoundedLabel(
                if (order.spoonFork) "수저/포크 O" else "수저/포크 X",  // 텍스트
                borderColor = MyColor.Yellow,  // 테두리 색상
                backgroundColor = MyColor.Yellow,  // 배경 색상 (노란색)
                textColor = Color.WHITE,  // 텍스트 색상
                borderRadius = 20,  // 둥근 정도
                borderWidth = 2 , // 테두리 두께
                textAlignment = SwingConstants.CENTER,
                padding = Insets(0, 0, 0, 0)
            ).apply {
                font = MyFont.Bold(22f)
                foreground = Color.WHITE  // 텍스트 색상
                alignmentX = Component.LEFT_ALIGNMENT  // 왼쪽 정렬
                preferredSize = Dimension(146, 70)  // 크기를 146x70으로 설정
                maximumSize = Dimension(146, 70)
                minimumSize = Dimension(146, 70)
            }

            val requestLabel = FillRoundedLabel(
                truncateText("요청사항: ${order.request}", 500, this),  // 텍스트 줄이기
                borderColor = Color(240, 240, 240),
                backgroundColor = Color(240, 240, 240),
                textColor = MyColor.BLACK,
                borderRadius = 20,
                borderWidth = 2,
                textAlignment = SwingConstants.LEFT,
                padding = Insets(0, 20, 0, 20)
            ).apply {
                font = MyFont.Bold(26f)
                foreground = Color.WHITE
                alignmentX = Component.LEFT_ALIGNMENT
                preferredSize = Dimension(Int.MAX_VALUE - 200, 70)
                maximumSize = Dimension(Int.MAX_VALUE - 200, 70)
                minimumSize = Dimension(Int.MAX_VALUE - 200, 70)
            }

            add(spoonForkLabel)  // 수저/포크 라벨 추가
            add(Box.createRigidArea(Dimension(20, 0)))  // 수저/포크와 요청사항 사이에 간격 추가
            add(requestLabel)  // 요청사항 라벨 추가
        }

        // 패널들 사이에 간격 제거
        add(headerPanel)
        add(Box.createRigidArea(Dimension(0, 15)))
        add(addressPanel)
        add(Box.createRigidArea(Dimension(0, 15)))
        add(menuDetailPanel)
        add(footerPanel)

        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color(27, 43, 66), 2),  // 테두리 설정
            BorderFactory.createEmptyBorder(0, 20, 0, 20)  // 바깥쪽 여백 설정
        )

    }

    private fun truncateText(text: String, maxWidth: Int, component: JComponent): String {
        val fontMetrics: FontMetrics = component.getFontMetrics(component.font)
        var truncatedText = text
        val ellipsis = "..."

        while (fontMetrics.stringWidth(truncatedText) > maxWidth) {
            truncatedText = truncatedText.dropLast(1)
        }

        if (truncatedText.length < text.length) {
            truncatedText += ellipsis
        }

        return truncatedText
    }
}
