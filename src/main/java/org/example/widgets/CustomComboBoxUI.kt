import org.example.style.MyColor
import javax.swing.*
import javax.swing.plaf.basic.BasicComboBoxUI
import java.awt.*

// 커스텀 콤보박스 UI 클래스
class CustomComboBoxUI : BasicComboBoxUI() {
    override fun createArrowButton(): JButton {
        return JButton().apply {
            // 아이콘 로드
            val iconUrl = Thread.currentThread().contextClassLoader.getResource("custom_arrow_icon.png")
            if (iconUrl != null) {
                val originalIcon = ImageIcon(iconUrl)
                // 아이콘 크기 변경 (15x15)
                icon = ImageIcon(originalIcon.image.getScaledInstance(13, 10, Image.SCALE_SMOOTH))
            } else {
                println("Icon not found: custom_arrow_icon.png")
            }
            isContentAreaFilled = false  // 배경 칠하기 비활성화
            isFocusPainted = false  // 포커스 페인팅 비활성화
            border = BorderFactory.createEmptyBorder(0, 5, 0, 5)  // 패딩 조정하여 오른쪽 공간 줄이기
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)  // 커서 설정
            background = Color.WHITE  // 버튼 배경을 투명하게
        }
    }

    override fun paintCurrentValueBackground(g: Graphics?, bounds: Rectangle, hasFocus: Boolean) {
        // 선택된 항목의 배경을 그리지 않음
    }

    override fun paintCurrentValue(g: Graphics?, bounds: Rectangle, hasFocus: Boolean) {
        g?.color = comboBox.foreground // 선택된 텍스트의 색상을 설정
        super.paintCurrentValue(g, bounds, false)  // 텍스트 및 아이콘 그리기, hasFocus를 false로 처리해 포커스 방지
    }

    override fun installUI(c: JComponent?) {
        super.installUI(c)
        // 포커스 제거
        comboBox?.isFocusable = false
    }

    override fun paint(g: Graphics?, c: JComponent?) {
        super.paint(g, c)
        c?.border = BorderFactory.createEmptyBorder(5, 15, 5, 5)  // 안쪽 패딩 설정, 오른쪽 패딩 축소
    }
}

// 둥근 콤보박스 클래스
class RoundedComboBox(model: ComboBoxModel<String>,
                      private val borderColor: Color = Color(86, 86, 86),  // 테두리 색상 (기본값 설정)
                      private val borderWidth: Float = 1f  // 테두리 두께 (기본값 설정)
    ) : JComboBox<String>(model) {
    init {
        setUI(CustomComboBoxUI())  // 커스텀 UI 적용
        border = BorderFactory.createEmptyBorder(5, 15, 5, 15)  // 안쪽 여백 설정
        background = Color.white  // 배경색 설정
        foreground = Color.DARK_GRAY  // 텍스트 색상 설정
        preferredSize = Dimension(100, 40)  // 크기 설정 (너비 조정)
        isOpaque = false  // 불투명 설정 비활성화

        // 선택된 값의 배경을 없애는 커스텀 렌더러 적용
        renderer = object : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: JList<out Any>?,
                value: Any?,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
            ): Component {
                val label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel
                label.isOpaque = false  // 배경을 완전히 투명하게
                label.foreground = Color.WHITE  // 선택된 텍스트의 색상을 설정
                return label
            }
        }
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 둥근 배경 그리기
        g2.color = if (!isEnabled) MyColor.GREY100 else background
        g2.fillRoundRect(0, 0, width, height, 30, 30)

        // 테두리 그리기 (외부에서 설정된 색상과 두께 사용 또는 기본값 사용)
        g2.color = borderColor  // 외부에서 설정한 테두리 색상 사용
        g2.stroke = BasicStroke(borderWidth)  // 외부에서 설정한 테두리 두께 사용
        g2.drawRoundRect(0, 0, width - 1, height - 1, 30, 30)  // 둥근 테두리 그리기

        // 텍스트와 화살표를 포함하여 기본 렌더링
        super.paintComponent(g)
    }
}

// 둥근 콤보박스 클래스
class RoundedComboBox2(
    private val model: String,
    private val borderColor: Color = Color(86, 86, 86),  // 테두리 색상 (기본값 설정)
    private val borderWidth: Float = 1f  // 테두리 두께 (기본값 설정)
) : JPanel() {
    init {
        preferredSize = Dimension(160, 50)  // 크기 설정
        background = Color.WHITE
        isOpaque = false  // 불투명 설정 비활성화
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 둥근 배경 그리기
        g2.color = if (!isEnabled) MyColor.GREY100 else background
        g2.fillRoundRect(0, 0, width, height, 30, 30)

        // 테두리 그리기 (외부에서 설정된 색상과 두께 사용 또는 기본값 사용)
        g2.color = borderColor  // 외부에서 설정한 테두리 색상 사용
        g2.stroke = BasicStroke(borderWidth)  // 외부에서 설정한 테두리 두께 사용
        g2.drawRoundRect(0, 0, width - 1, height - 1, 30, 30)  // 둥근 테두리 그리기

        // 텍스트 그리기
        g2.font = Font("Arial", Font.BOLD, 18)  // 폰트 설정
        g2.color = Color.DARK_GRAY  // 텍스트 색상 설정
        val fm = g2.fontMetrics
        val textX = 12  // 텍스트를 가운데 정렬
        val textY = (height + fm.ascent) / 2 - 2
        g2.drawString(model, textX, textY)  // 텍스트 그리기
    }
}

// 사용 예시
fun main() {
    // Swing 프레임 설정
    val frame = JFrame("Custom ComboBox Example")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.layout = FlowLayout()

    // 커스텀 콤보박스 생성
    val comboBox = RoundedComboBox(DefaultComboBoxModel(arrayOf("1시", "2시", "3시", "4시", "5시", "6시", "7시", "8시", "9시", "10시", "11시", "12시")))
    frame.add(comboBox)

    // 프레임 설정
    frame.setSize(300, 200)
    frame.isVisible = true
}
