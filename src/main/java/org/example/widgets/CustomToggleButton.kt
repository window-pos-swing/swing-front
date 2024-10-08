import org.example.style.MyColor
import java.awt.*
import javax.swing.*

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
        val g2d = g as Graphics2D
        val width = width
        val height = height
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 배경 색 그리기 (테두리 효과 방지)
        g2d.color = MyColor.GREY200
        g2d.fillRoundRect(0, 0, width, height, height, height)

        // ON/OFF 상태에 따른 배경색 그리기
        if (isSelected) {
            g2d.clipRect(0, 0, width / 2, height)  // ON: 왼쪽
            g2d.color = MyColor.DARK_NAVY
            g2d.fillRoundRect(0, 0, width, height, height, height)
        } else {
            g2d.clipRect(width / 2, 0, width / 2, height)  // OFF: 오른쪽
            g2d.color = MyColor.DARK_NAVY
            g2d.fillRoundRect(0, 0, width, height, height, height)
        }

        // 클립 리셋 및 텍스트 색상 처리
        g2d.clip = null
        g2d.color = if (isSelected) Color.WHITE else Color(137, 137, 137)
        g2d.drawString("ON", width / 4 - g2d.fontMetrics.stringWidth("ON") / 2, height / 2 + g2d.fontMetrics.ascent / 2)

        g2d.color = if (!isSelected) Color.WHITE else Color(137, 137, 137)
        g2d.drawString("OFF", width * 3 / 4 - g2d.fontMetrics.stringWidth("OFF") / 2, height / 2 + g2d.fontMetrics.ascent / 2)

        // 포커스와 관련된 그리기 동작을 제거
        isFocusPainted = false
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
