import javax.swing.*
import java.awt.*
import javax.swing.border.EmptyBorder

class OrderRejectCancelDialog(
    parent: JFrame,
    title: String,
    labelText: String,
    buttonText: String,
    private val onReject: (String) -> Unit  // 거절 사유를 전달받는 콜백 함수
) : JDialog(parent, title, true) {
    private var selectedButton: RoundedButton? = null
    private var selectedReason: String? = null  // 선택된 거절 사유를 저장할 변수

    init {
        layout = BorderLayout()

        // 상단 제목 설정
        val titleLabel = JLabel(labelText, SwingConstants.CENTER)
        titleLabel.font = Font("Arial", Font.BOLD, 16)
        add(titleLabel, BorderLayout.NORTH)

        // 버튼 클릭 시 스타일을 업데이트하는 함수
        fun setButtonStyle(button: RoundedButton) {
            selectedButton?.resetStyle()  // 이전에 선택된 버튼의 스타일을 초기화
            button.setSelectedStyle()  // 선택된 버튼의 스타일을 설정
            selectedButton = button
            selectedReason = button.text  // 선택된 버튼의 텍스트를 거절 사유로 저장
        }

        // 중간 버튼들 설정 (한글로 텍스트 수정)
        val rejectReasonButtons = listOf(
            RoundedButton("가게 사정"),
            RoundedButton("재료 소진"),
            RoundedButton("조리 지연"),
            RoundedButton("배달원 부재"),
            RoundedButton("배달 불가 지역"),
            RoundedButton("메뉴 또는 가격 변동"),
            RoundedButton("요청 사항 적용 불가")
        )

        // 첫 번째 버튼을 기본 선택된 상태로 설정
        setButtonStyle(rejectReasonButtons[0])

        // 각 버튼에 클릭 리스너 추가
        val buttonPanel = JPanel(GridLayout(4, 2, 10, 10)).apply {
            rejectReasonButtons.forEach { button ->
                button.addActionListener {
                    setButtonStyle(button)  // 클릭된 버튼의 스타일 업데이트
                }
                add(button)  // 패널에 버튼 추가
            }
        }
        add(buttonPanel, BorderLayout.CENTER)

        // 하단 버튼 설정
        val bottomPanel = JPanel()
        val cancelButton = JButton(buttonText).apply {
            background = Color(255, 153, 0)
            foreground = Color.WHITE
            font = Font("Arial", Font.BOLD, 14)
            addActionListener {
                // 선택된 거절 사유가 있는지 확인
                selectedReason?.let { reason ->
                    onReject(reason)  // 거절 사유를 전달하며 콜백 실행
                    dispose()  // 다이얼로그 닫기
                } ?: JOptionPane.showMessageDialog(this, "거절 사유를 선택해주세요.")
            }
        }
        bottomPanel.add(cancelButton)
        add(bottomPanel, BorderLayout.SOUTH)

        // 다이얼로그 크기 및 기본 설정
        setSize(400, 300)
        setLocationRelativeTo(parent)
        isVisible = true
    }

    // 둥근 모서리 버튼 클래스
    class RoundedButton(text: String) : JButton(text) {
        var isSelectedState = false

        init {
            font = Font("Arial", Font.BOLD, 14)
            isContentAreaFilled = false
            border = RoundedBorder(15, isSelectedState)
            preferredSize = Dimension(150, 50)
        }

        override fun paintComponent(g: Graphics) {
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            // 선택된 상태일 때 배경 색상을 검은색으로, 선택되지 않았을 때는 흰색으로 설정
            g2.color = if (isSelectedState) Color.BLACK else Color.WHITE

            // 둥근 사각형 그리기
            g2.fillRoundRect(0, 0, width, height, 15, 15)

            // 텍스트 색상 설정
            foreground = if (isSelectedState) Color.WHITE else Color.BLACK
            super.paintComponent(g)
        }

        // 버튼 스타일을 선택된 상태로 변경하는 함수
        fun setSelectedStyle() {
            isSelectedState = true
            border = RoundedBorder(15, isSelectedState)  // 선택된 상태에 따른 테두리 색상 변경
            repaint()
        }

        // 버튼 스타일을 초기 상태로 복구하는 함수
        fun resetStyle() {
            isSelectedState = false
            border = RoundedBorder(15, isSelectedState)  // 선택되지 않은 상태의 테두리 색상
            repaint()
        }
    }

    // 둥근 테두리 Border 클래스 (선택 상태에 따라 테두리 색상 변경)
    class RoundedBorder(private val radius: Int, private val isSelected: Boolean) : javax.swing.border.Border {
        override fun getBorderInsets(c: Component?): Insets {
            return Insets(radius + 1, radius + 1, radius + 2, radius)
        }

        override fun isBorderOpaque(): Boolean {
            return true
        }

        override fun paintBorder(c: Component?, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.color = if (isSelected) Color.BLACK else Color.LIGHT_GRAY  // 선택 상태에 따른 테두리 색상
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius)
        }
    }
}
