package org.example.widgets

import org.example.style.MyColor
import java.awt.*
import javax.swing.JFrame
import javax.swing.JToggleButton
import javax.swing.SwingUtilities

class CustomToggleButton2 : JToggleButton() {
    init {
        preferredSize = Dimension(110, 35)  // 토글 스위치 크기 설정
        isContentAreaFilled = false  // 기본 버튼 영역을 그리지 않음
        isFocusPainted = false  // 포커스 표시 제거
        border = null  // 기본 테두리 제거

        // 클릭 시 상태 변화에 따라 텍스트와 색상 변경
        addActionListener {
            // 상태 업데이트를 위해 repaint 호출
            repaint()
        }
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)  // 안티 앨리어싱 적용

        // 배경색 설정 (직접 그리기)
        if (isSelected) {
            g2.color = Color.RED  // 'ON' 상태일 때 빨간 배경
        } else {
            g2.color = Color.WHITE  // 'OFF' 상태일 때 하얀 배경
        }
        g2.fillRoundRect(0, 0, width, height, height, height)  // 둥근 모서리 배경 그리기

        // 테두리 그리기
        g2.color = Color.GRAY  // 테두리 색상
        g2.stroke = BasicStroke(2f)  // 테두리 두께 설정
        g2.drawRoundRect(0, 0, width - 1, height - 1, height, height)  // 둥근 테두리 그리기

        // 텍스트 가운데 정렬
        g2.font = Font("Arial", Font.BOLD, 20)
        val fm = g2.fontMetrics
        val text = if (isSelected) "ON" else "OFF"
        val textWidth = fm.stringWidth(text)
        val textX = (width - textWidth) / 2
        val textY = (height + fm.ascent - fm.descent) / 2

        // 텍스트 색상 및 그리기
        if (isSelected) {
            g2.color = Color.WHITE  // 'ON' 상태일 때 텍스트 흰색
        } else {
            g2.color = Color.RED  // 'OFF' 상태일 때 텍스트 빨간색
        }
        g2.drawString(text, textX, textY)
    }
}

fun main() {
    SwingUtilities.invokeLater {
        val frame = JFrame("Direct Toggle Switch")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.layout = FlowLayout()

        // 커스텀 토글 버튼 추가
        val directToggle = CustomToggleButton2()
        frame.add(directToggle)

        frame.setSize(300, 200)
        frame.isVisible = true
    }
}