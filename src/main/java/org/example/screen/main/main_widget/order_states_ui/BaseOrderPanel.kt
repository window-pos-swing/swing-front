package org.example.view.components

import org.example.MyFont
import org.example.model.Order
import org.example.style.MyColor
import org.example.widgets.FillRoundedLabel
import org.example.widgets.OutLineRoundedLabel
import javax.swing.*
import java.awt.*

class BaseOrderPanel(order: Order) : JPanel() {
    // 패널들을 protected로 선언
    private val headerPanel = JPanel()
    private val addressPanel = JPanel()
    private val menuDetailPanel = JPanel()
    private val footerPanel = JPanel()

    // getter 메서드 추가
    fun getHeaderPanel(): JPanel = headerPanel
    fun getAddressPanel(): JPanel = addressPanel
    fun getMenuDetailPanel(): JPanel = menuDetailPanel
    fun getFooterPanel(): JPanel = footerPanel

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)  // 모든 내용을 수직으로 정렬
        background = Color.WHITE
        preferredSize = Dimension(Int.MAX_VALUE, 350)  // 패널 크기 설정
        border = BorderFactory.createEmptyBorder(0, 0, 0, 0)

        // 1. [headerPanel] 주문시간 + 메뉴 갯수와 총 가격
         headerPanel.apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)  // 가로로 배치 (수평)
            isOpaque = false  // 배경 투명
            preferredSize = Dimension(Int.MAX_VALUE, 80)
            maximumSize = Dimension(Int.MAX_VALUE, 80)
            alignmentX = Component.LEFT_ALIGNMENT  // 왼쪽 정렬

            // 아이콘 로드
            val iconLabel = JLabel(LoadImage.loadImage(if (order.orderType == "DELIVERY") "/delivery_state.png" else "/takeout_state.png", 56, 80)).apply {
                border = BorderFactory.createEmptyBorder(0, 0, 0, 15)  // 오른쪽에 여백 추가
                alignmentX = Component.LEFT_ALIGNMENT  // 왼쪽 정렬
            }

            // 주문시간과 메뉴 갯수 및 총 가격을 한 줄로 출력하는 패널
            val timeAndMenuPanel = JPanel().apply {
                layout = FlowLayout(FlowLayout.LEFT, 0, 0)  // 수평으로 정렬 (왼쪽 정렬)
                isOpaque = false  // 배경 투명
                border = BorderFactory.createEmptyBorder(10, 0, 0, 0)  // 상하 여백 제거
                alignmentX = Component.LEFT_ALIGNMENT  // 왼쪽 정렬

                val orderTimeLabel = JLabel("${order.orderTime}분").apply {
                    font = MyFont.Bold(40f)  // 폰트 크기 40
                    foreground = MyColor.GREY900
                    alignmentX = Component.LEFT_ALIGNMENT  // 왼쪽 정렬
                }

                val menuInfoLabel = JLabel("[메뉴 ${order.menuList.size}개] ${order.menuList.sumOf { it.price * it.count }}원").apply {
                    font = MyFont.Medium(24f)  // 폰트 크기 24
                    foreground = MyColor.GREY900
                    alignmentX = Component.LEFT_ALIGNMENT  // 왼쪽 정렬
                }

                // 패널에 요소 추가
                add(orderTimeLabel)
                add(Box.createRigidArea(Dimension(10, 0)))  // 간격 추가
                add(menuInfoLabel)
            }

            // 하단에만 검은색 테두리 추가
            val bottomBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, MyColor.GREY200)  // 하단(border)만 1px 설정
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 0),  // 상하 여백 제거
                bottomBorder  // 하단 테두리
            )

            // headerPanel에 아이콘과 텍스트 패널 추가
            add(iconLabel)
            add(timeAndMenuPanel)
        }

        // 2. [addressPanel] 주소 정보 (왼쪽 정렬)
        addressPanel.apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)  // 가로로 배치 (수평)
            isOpaque = false  // 배경 투명
            preferredSize = Dimension(Int.MAX_VALUE, 20)
            maximumSize = Dimension(Int.MAX_VALUE, 20)
            alignmentX = Component.LEFT_ALIGNMENT  // 왼쪽 정렬

            // 주소 정보
            val addressItems = JLabel(order.address).apply {
                font = MyFont.Medium(20f)
                border = BorderFactory.createEmptyBorder(0, 0, 0, 0)  // 상하 여백 제거
                foreground = Color.GRAY
                alignmentX = Component.LEFT_ALIGNMENT  // 왼쪽 정렬
            }

            add(addressItems)
        }

        // 기본 텍스트 설정
        val originalText = order.menuList.joinToString(" / ") { menu ->
            val optionsText = if (menu.options.isNotEmpty()) {
                "(${menu.options.joinToString(", ") { it.optionName }})"
            } else ""
            "${menu.menuName} $optionsText x ${menu.count}"
        }

        // 3. [menuDetailPanel] 메뉴 세부사항 (왼쪽 정렬)
        menuDetailPanel.apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)  // 가로로 배치 (수평)
            isOpaque = false  // 배경 투명
            preferredSize = Dimension(Int.MAX_VALUE, 107)
            maximumSize = Dimension(Int.MAX_VALUE, 107)
            alignmentX = Component.LEFT_ALIGNMENT  // 왼쪽 정렬

            // OutLineRoundedLabel을 menuLabel로 사용
            val menuLabel = OutLineRoundedLabel(
                originalText,
                borderColor = Color.BLACK,  // 검은색 테두리
                borderRadius = 20,  // 둥근 정도
                borderWidth = 1,  // 테두리 두께
                textColor = MyColor.GREY900,  // 텍스트 색상
                padding = Insets(10, 20, 10, 20)  // 패딩 설정
            ).apply {
                font = MyFont.Medium(24f)
                alignmentX = Component.LEFT_ALIGNMENT  // 왼쪽 정렬
                horizontalAlignment = SwingConstants.LEFT  // 텍스트 왼쪽 정렬
                minimumSize = Dimension(100, 107)  // 최소 크기 설정
                maximumSize = Dimension(Int.MAX_VALUE, 107)  // 최대 크기 설정

                // ComponentListener 추가: 창 크기 변경 시 텍스트를 다시 계산
                addComponentListener(object : java.awt.event.ComponentAdapter() {
                    override fun componentResized(e: java.awt.event.ComponentEvent) {
                        text = truncateTextForTwoLines(originalText, this@apply)
                        revalidate()
                        repaint()
                    }
                })
            }

            add(menuLabel)  // menuLabel을 menuDetailPanel에 추가
        }

        // 4. [footerPanel] 수저/포크 선택 및 요청사항 (왼쪽 정렬)
         footerPanel.apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)  // 가로로 배치 (수평)
            isOpaque = false  // 배경 투명
            preferredSize = Dimension(Int.MAX_VALUE, 100)
            maximumSize = Dimension(Int.MAX_VALUE, 100)
            alignmentX = Component.LEFT_ALIGNMENT  // 왼쪽 정렬

            // 수저/포크 정보 라벨
            val spoonForkLabel = FillRoundedLabel(
                if (order.spoonFork) "수저/포크 O" else "수저/포크 X",  // 텍스트
                borderColor = MyColor.Yellow,  // 테두리 색상
                backgroundColor = MyColor.Yellow,  // 배경 색상 (노란색)
                textColor = Color.WHITE,  // 텍스트 색상
                borderRadius = 20,  // 둥근 정도
                borderWidth = 2, // 테두리 두께
                textAlignment = SwingConstants.CENTER,
                padding = Insets(0, 0, 0, 0)
            ).apply {
                font = MyFont.Bold(22f)
                preferredSize = Dimension(146, 70)  // 크기를 146x70으로 설정
                maximumSize = Dimension(146, 70)
                alignmentX = Component.LEFT_ALIGNMENT  // 왼쪽 정렬
            }

            // 요청사항 라벨
            val requestLabel = FillRoundedLabel(
                "요청사항: ${order.request}",  // 텍스트
                borderColor = Color(240, 240, 240),
                backgroundColor = Color(240, 240, 240),
                textColor = MyColor.GREY900,
                borderRadius = 20,
                borderWidth = 2,
                textAlignment = SwingConstants.LEFT,
                padding = Insets(0, 20, 0, 20)
            ).apply {
                font = MyFont.Bold(26f)
                alignmentX = Component.LEFT_ALIGNMENT  // 왼쪽 정렬
                minimumSize = Dimension(100, 70)  // 최소 크기 설정
                maximumSize = Dimension(Int.MAX_VALUE, 70)  // 최대 크기 설정
            }

            // 텍스트 넘칠 경우 ... 처리
            requestLabel.addComponentListener(object : java.awt.event.ComponentAdapter() {
                override fun componentResized(e: java.awt.event.ComponentEvent) {
                    val availableWidth = requestLabel.width - 60  // 패딩을 고려한 실제 너비 계산
                    requestLabel.text = requestTruncateText("요청사항: ${order.request}", availableWidth, requestLabel)
                }
            })

            // 패널에 요소 추가
            add(spoonForkLabel)
            add(Box.createRigidArea(Dimension(20, 0)))  // 간격 추가
            add(requestLabel)
        }

        // 패널들 추가
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


    // 텍스트가 넘칠 경우 자르고 ...을 추가하는 함수
    private fun requestTruncateText(text: String, maxWidth: Int, component: JComponent): String {
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

    private fun truncateTextForTwoLines(text: String, component: JComponent): String {
        val fontMetrics: FontMetrics = component.getFontMetrics(component.font)
        val maxWidth = component.width - component.insets.left - component.insets.right  // 패딩을 고려한 최대 너비
        val words = text.split(" ")

        var firstLine = ""
        var secondLine = ""
        var isFirstLineComplete = false

        for (word in words) {
            if (!isFirstLineComplete) {
                // 첫 번째 줄이 넘치지 않으면 계속 추가
                if (fontMetrics.stringWidth(firstLine + " " + word) < maxWidth) {
                    firstLine += if (firstLine.isEmpty()) word else " $word"
                } else {
                    // 첫 번째 줄이 넘치면 두 번째 줄로 이동
                    isFirstLineComplete = true
                    secondLine += word
                }
            } else {
                // 두 번째 줄 처리
                if (fontMetrics.stringWidth(secondLine + " " + word) < maxWidth) {
                    secondLine += if (secondLine.isEmpty()) word else " $word"
                } else {
                    // 두 번째 줄이 넘치면 ...으로 처리
                    secondLine = secondLine.take(maxWidth / fontMetrics.charWidth(' ')) + "..."
                    break
                }
            }
        }

        // 두 줄을 합쳐서 반환, HTML을 사용하여 줄바꿈 처리
        return "<html>$firstLine<p style='margin-top:10;'>$secondLine</p></html>"
    }

}
