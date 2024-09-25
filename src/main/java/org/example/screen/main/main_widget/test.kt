import java.awt.*
import java.awt.geom.RoundRectangle2D
import javax.swing.*

class RoundedDialog(parent: JFrame) : JDialog(parent, true) {

    init {
        title = "Rounded Dialog"
        setSize(300, 200)
        isUndecorated = true // 기본 타이틀 바 제거

        // 다이얼로그 모양을 둥글게 설정
        shape = RoundRectangle2D.Float(0f, 0f, width.toFloat(), height.toFloat(), 30f, 30f)

        val panel = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

                // 둥근 배경 그리기
                g2.color = Color.WHITE
                g2.fillRoundRect(0, 0, width, height, 30, 30)

                g2.color = Color.GRAY
                g2.stroke = BasicStroke(3f)
                g2.drawRoundRect(0, 0, width, height, 30, 30)
            }
        }

        panel.layout = BorderLayout()
        panel.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        add(panel)

        val closeButton = JButton("닫기")
        closeButton.addActionListener { dispose() }
        panel.add(closeButton, BorderLayout.SOUTH)
    }
}

fun main() {
    val frame = JFrame().apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isVisible = true
    }

    val dialog = RoundedDialog(frame)
    dialog.setLocationRelativeTo(null)
    dialog.isVisible = true
}
