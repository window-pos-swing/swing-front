import org.example.MyFont
import org.example.widgets.IconRoundBorder
import java.awt.*
import java.awt.geom.RoundRectangle2D
import javax.swing.*
import javax.swing.border.EmptyBorder

// 공통 둥근 테두리 다이얼로그 클래스
open class CustomRoundedDialog(
    parent: JFrame,
    title: String,
    dialogWidth: Int = 400,
    dialogHeight: Int = 300,
    private val callback: ((Boolean) -> Unit)? = null  // 콜백 함수 추가
) : JDialog(parent, title, true) {

    init {
        // 타이틀 바 제거 및 둥근 테두리 적용
        isUndecorated = true
        layout = BorderLayout()

        // 다이얼로그 모양을 둥근 직사각형으로 설정
        shape = RoundRectangle2D.Float(0f, 0f, dialogWidth.toFloat(), dialogHeight.toFloat(), 30f, 30f)

        // 타이틀 패널
        val titlePanel = JPanel(BorderLayout()).apply {
            background = Color.WHITE

            val titleLabel = JLabel(title, SwingConstants.CENTER).apply {
                font = MyFont.Bold(32f)
                border = BorderFactory.createEmptyBorder(0, 40, 15, 0)
            }

            val closeButton = IconRoundBorder.createRoundedButton("/close_red_icon.png").apply {
                preferredSize = Dimension(45, 45)  // 버튼 크기 설정
                maximumSize = Dimension(45, 45)
                minimumSize = Dimension(45, 45)

            }

            // 버튼 기능 추가
            closeButton.addActionListener {
                callback?.invoke(false)
                dispose()
            }

            val buttonContainer = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)  // 수직 레이아웃 설정
                isOpaque = false
                add(Box.createVerticalGlue())  // 상단 여백
                add(closeButton)  // 버튼 패널 추가
                border = BorderFactory.createEmptyBorder(0, 0, 10, 0)
            }

            val bottomBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 30, 0, 30),
                bottomBorder
            )

            add(titleLabel, BorderLayout.CENTER)
            add(buttonContainer, BorderLayout.EAST)
        }
        add(titlePanel, BorderLayout.NORTH)  // 타이틀 패널을 상단에 추가
    }
}