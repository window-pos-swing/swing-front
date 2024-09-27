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

    private val titleLabel = JLabel(title, SwingConstants.CENTER).apply {
        font = MyFont.Bold(32f)
        border = BorderFactory.createEmptyBorder(0, 40, 15, 0)
    }

    init {
        // 타이틀 바 제거 및 둥근 테두리 적용
        isUndecorated = true
        layout = BorderLayout()

        // 다이얼로그 모양을 둥근 직사각형으로 설정
        shape = RoundRectangle2D.Float(0f, 0f, dialogWidth.toFloat(), dialogHeight.toFloat(), 30f, 30f)
        // 타이틀 패널
        val titlePanel = JPanel(BorderLayout()).apply {
            background = Color.WHITE

            add(titleLabel, BorderLayout.CENTER)
            addButtonsToTitleBar(this)  // 타이틀바에 버튼 추가

            // 테두리와 여백 설정
            val bottomBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 30, 0, 30),
                bottomBorder
            )
        }
        add(titlePanel, BorderLayout.NORTH)  // 타이틀 패널을 상단에 추가
    }

    // 버튼을 타이틀 바에 추가하는 메서드 (기본적으로 닫기 버튼만 추가)
    open fun addButtonsToTitleBar(panel: JPanel) {
        val closeButton = IconRoundBorder.createRoundedButton("/close_red_icon.png").apply {
            preferredSize = Dimension(45, 45)
            maximumSize = Dimension(45, 45)
            minimumSize = Dimension(45, 45)

            addActionListener {
                callback?.invoke(false)
                dispose()
            }
        }

        val buttonContainer = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            isOpaque = false
            border = EmptyBorder(0, 0, 10, 0)
            add(closeButton)  // 닫기 버튼만 기본적으로 추가
        }

        panel.add(buttonContainer, BorderLayout.EAST)
    }

    // 타이틀의 여백을 업데이트하는 함수 추가
    fun updateTitleBorder(leftPadding: Int) {
        titleLabel.border = BorderFactory.createEmptyBorder(0, leftPadding, 15, 0)
        titleLabel.revalidate()  // 레이아웃 갱신
        titleLabel.repaint()  // 다시 그리기
    }

}