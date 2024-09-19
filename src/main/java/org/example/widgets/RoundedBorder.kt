package org.example.widgets

import org.example.style.MyColor
import java.awt.*
import javax.swing.BorderFactory
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.border.AbstractBorder

// 둥근 버튼 보더 정의
class RoundedBorder(private val radius: Int) : AbstractBorder() {
    override fun paintBorder(c: Component?, g: Graphics?, x: Int, y: Int, width: Int, height: Int) {
        val g2 = g as Graphics2D
        g2.color = c?.background
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius)
    }

    override fun getBorderInsets(c: Component?): Insets {
        return Insets(radius + 1, radius + 1, radius + 1, radius + 1)
    }
}

class RoundedSettingButton : JButton {

    constructor(text: String, icon: Icon) : super(text, icon) {
        // 버튼의 투명 배경을 설정
        isContentAreaFilled = false
        isFocusPainted = false
        isOpaque = false
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        // 버튼의 배경색 설정
        background = MyColor.ORANGE // 오렌지색 배경

        // 텍스트와 이미지의 정렬 설정
        horizontalAlignment = CENTER
        verticalAlignment = CENTER
        horizontalTextPosition = RIGHT  // 텍스트는 이미지 오른쪽에 배치
        verticalTextPosition = CENTER   // 텍스트는 이미지와 수직으로 중앙 정렬
        iconTextGap = 10  // 이미지와 텍스트 사이의 간격
    }

    // 둥근 배경을 그리기 위한 메서드 오버라이드
    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 배경을 둥글게 그리기
        g2.color = background
        g2.fillRoundRect(0, 0, width, height, 10, 10)

        // 기본 버튼 페인팅 호출 (텍스트와 아이콘)
        super.paintComponent(g)
    }

    // 둥근 테두리를 그리기 위한 메서드 오버라이드
    override fun paintBorder(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 테두리 색상 설정
        g2.color = MyColor.ORANGE
        g2.drawRoundRect(0, 0, width - 1, height - 1, 10, 10)
    }
}

class RoundBorder {
    companion object {
        // 둥근 테두리와 하얀 배경을 가진 버튼을 만드는 함수
        fun createRoundedButton(iconPath: String): JButton {
            val icon = ImageIcon(javaClass.getResource(iconPath))  // 아이콘 로드

            return object : JButton(icon) {
                init {
                    preferredSize = Dimension(40, 40)
                    isContentAreaFilled = false  // 기본 내용 배경 제거
                    isFocusPainted = false  // 포커스 표시 제거
                    border = null
                    cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                    margin = Insets(5, 5, 5, 5)  // 버튼과 테두리 사이에 여백 추가
                }

                override fun paintComponent(g: Graphics) {
                    val g2 = g as Graphics2D
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

                    // 버튼의 둥근 배경을 흰색으로 채움
                    g2.color = Color.WHITE
                    g2.fillRoundRect(0, 0, width, height, 20, 20)

                    // 버튼의 아이콘을 그린 후 테두리를 그려줌
                    super.paintComponent(g)

                    // 테두리 그리기
                    g2.color = Color.LIGHT_GRAY
                    g2.drawRoundRect(0, 0, width - 1, height - 1, 20, 20)
                }
            }
        }
    }
}