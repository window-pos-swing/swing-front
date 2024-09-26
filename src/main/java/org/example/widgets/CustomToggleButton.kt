import org.example.style.MyColor
import java.awt.*
import javax.swing.*
import javax.swing.plaf.basic.BasicButtonUI

class CustomToggleButton : JToggleButton() {
    init {
        // 토글 버튼의 초기 상태를 OFF (false)로 설정
        isOpaque = false
        background = MyColor.DARK_RED
        isSelected = true
        isFocusPainted = false
        preferredSize = Dimension(120, 40)
        border = null // 경계선을 없앰

        // 상태 변화에 따른 동작을 추가
        addItemListener { e ->
            if (isSelected) {
                println("ON 상태")
            } else {
                println("OFF 상태")
            }
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val g2d = g as Graphics2D
        val width = width
        val height = height
        // 안티앨리어싱 적용
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 배경 회색 라운드 박스 그리기
        g2d.color = MyColor.LIGHT_GREY
        g2d.fillRoundRect(0, 0, width, height, height, height)

        // 상태에 따라 오른쪽에 네이비 색상 그리기 (OFF 상태일 때)
        if (!isSelected) {
            // OFF 상태일 때 오른쪽을 네이비 색으로 그리기
            g2d.clipRect(width / 2, 0, width / 2, height)  // 오른쪽 절반 클립
            g2d.color = MyColor.DARK_NAVY
            g2d.fillRoundRect(0, 0, width, height, height, height)
        } else {
            // ON 상태일 때 왼쪽을 네이비 색으로 그리기
            g2d.clipRect(0, 0, width / 2, height)  // 왼쪽 절반 클립
            g2d.color = MyColor.DARK_NAVY
            g2d.fillRoundRect(0, 0, width, height, height, height)
        }

        // 클리핑 리셋
        g2d.clip = null

        // 텍스트 그리기
        g2d.color = if (isSelected)   Color.WHITE else Color(137, 137, 137)
        g2d.drawString("ON", width / 4 - g2d.fontMetrics.stringWidth("ON") / 2, height / 2 + g2d.fontMetrics.ascent / 2)

        g2d.color = if (isSelected) Color(137, 137, 137) else Color.WHITE
        g2d.drawString("OFF", width * 3 / 4 - g2d.fontMetrics.stringWidth("OFF") / 2, height / 2 + g2d.fontMetrics.ascent / 2)
    }
}

fun main() {
    SwingUtilities.invokeLater {
        val frame = JFrame("Custom Toggle Example")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.layout = FlowLayout()

        val toggleButton = CustomToggleButton()

        // 명시적으로 "OFF" 상태 설정
        toggleButton.isSelected = false // OFF로 시작
        frame.add(toggleButton)

        frame.setSize(300, 200)
        frame.isVisible = true
    }
}
