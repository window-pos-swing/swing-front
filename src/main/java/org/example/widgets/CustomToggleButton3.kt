import org.example.widgets.CustomToggleButton2
import java.awt.*
import javax.swing.*
import javax.swing.border.AbstractBorder

class CustomToggleButton3(text: String) : JToggleButton(text) {

    init {
        preferredSize = Dimension(200, 50)
        font = Font("Arial", Font.BOLD, 16)
        isFocusPainted = false
        background = Color(240, 240, 240) // 기본 배경색
        border = BorderFactory.createEmptyBorder(10, 20, 10, 20) // 여백 설정
        horizontalAlignment = SwingConstants.CENTER

        // 선택 시 배경색 및 텍스트 색상 변경
        addChangeListener {
            background = if (isSelected) Color(27, 43, 66) else Color(240, 240, 240)
            foreground = if (isSelected) Color.WHITE else Color.DARK_GRAY
        }

        // 둥근 모서리 및 테두리 설정
        border = RoundedBorder(30)
    }

    // 둥근 테두리를 위한 클래스
    private class RoundedBorder(private val radius: Int) : AbstractBorder() {
        override fun paintBorder(
            c: Component?, g: Graphics?, x: Int, y: Int, width: Int, height: Int
        ) {
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            g2.color = Color.GRAY
            g2.stroke = BasicStroke(2f)
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius)
        }

        override fun getBorderInsets(c: Component?): Insets {
            return Insets(radius / 2, radius / 2, radius / 2, radius / 2)
        }

        override fun isBorderOpaque(): Boolean {
            return false
        }
    }
}

// 메인 함수
fun main() {
    SwingUtilities.invokeLater {
        val frame = JFrame("Direct Toggle Switch").apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            layout = FlowLayout(FlowLayout.LEFT, 0, 0)
            preferredSize = Dimension(680, 50)
        }

        // 세 개의 커스텀 토글 버튼 추가
        val button1 = CustomToggleButton3("전체선택")
        val button2 = CustomToggleButton3("평일/주말 선택")
        val button3 = CustomToggleButton3("요일별 선택")

        // 세 버튼을 버튼 그룹에 추가하여 동시에 하나만 선택 가능하게 설정
        val buttonGroup = ButtonGroup().apply {
            add(button1)
            add(button2)
            add(button3)
        }

        // 프레임에 버튼 추가
        frame.add(button1)
        frame.add(button2)
        frame.add(button3)

        // 프레임 설정
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }
}
