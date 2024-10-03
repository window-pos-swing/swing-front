import javax.swing.*
import java.awt.*
import java.awt.geom.RoundRectangle2D


class CustomProgressBar(private val totalTime: Int, private var elapsedTime: Int) : JPanel() {

    init {
        preferredSize = Dimension(400, 200)
        isOpaque = false
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 배경 그리기
        val backgroundColor = Color(27, 43, 66) // 어두운 배경색
        g2.color = backgroundColor
        g2.fillRoundRect(0, 0, width, height, 20, 20)

        // 남은 시간 텍스트 그리기
        g2.color = Color.WHITE
        g2.font = Font("Arial", Font.BOLD, 48)
        val timeText = "${elapsedTime}분"
        val textWidth = g2.fontMetrics.stringWidth(timeText)
        val textHeight = g2.fontMetrics.ascent
        g2.drawString(timeText, (width - textWidth) / 2, height / 2 - textHeight / 2)

        // 프로그레스바 그리기
        val progressBarHeight = 40
        val progressBarWidth = width - 80
        val progress = (progressBarWidth * (elapsedTime / totalTime.toFloat())).toInt()

        // 프로그레스바 배경
        val progressBarBackground = Color(255, 255, 255) // 흰색 배경
        g2.color = progressBarBackground
        g2.fillRoundRect(40, height - progressBarHeight - 40, progressBarWidth, progressBarHeight, 10, 10)

        // 진행률
        val progressBarForeground = Color(255, 182, 193) // 핑크색 진행률
        g2.color = progressBarForeground
        g2.fillRoundRect(40, height - progressBarHeight - 40, progress, progressBarHeight, 10, 10)
    }

    // 진행 시간을 업데이트할 수 있는 함수
    fun updateElapsedTime(newElapsedTime: Int) {
        this.elapsedTime = newElapsedTime
        repaint() // 다시 그리기
    }
}

