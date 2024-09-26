import javax.swing.*
import javax.swing.plaf.basic.BasicComboBoxUI
import java.awt.*
import javax.swing.border.EmptyBorder

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
        // 선택된 값 배경을 투명하게 설정하여 회색 박스를 없앱니다.
        g?.color = Color.WHITE
        g?.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 30, 30)  // 둥근 배경
    }

    override fun paintCurrentValue(g: Graphics?, bounds: Rectangle, hasFocus: Boolean) {
        g?.color = Color.GRAY  // 선택된 텍스트의 색상 설정
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
class RoundedComboBox(model: ComboBoxModel<String>) : JComboBox<String>(model) {
    init {
        setUI(CustomComboBoxUI())  // 커스텀 UI 적용
        border = BorderFactory.createEmptyBorder(5, 15, 5, 15)  // 안쪽 여백 설정
        background = Color.WHITE  // 배경색 설정
        foreground = Color.DARK_GRAY  // 텍스트 색상 설정
        preferredSize = Dimension(100, 40)  // 크기 설정 (너비 조정)
        isOpaque = false  // 불투명 설정 비활성화
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 둥근 배경 그리기
        g2.color = background
        g2.fillRoundRect(0, 0, width, height, 30, 30)

        // 텍스트와 화살표를 포함하여 기본 렌더링
        super.paintComponent(g)
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
