import org.example.MyFont
import org.example.style.MyColor
import org.example.widgets.SelectButtonRoundedBorder
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

class OrderRejectCancelDialog(
    parent: JFrame,
    title: String,
    labelText: String,
    buttonText: String,
    private val onReject: (String) -> Unit
) : CustomRoundedDialog(parent, title, 1000, 465) {


    private var selectedButton: SelectButtonRoundedBorder? = null
    private var selectedReason: String? = "가게 사정"  // 선택된 거절 사유 저장

    init {
        // SelectButtonRoundedBorder를 사용한 둥근 버튼 생성
        val selectButtons = listOf(
            SelectButtonRoundedBorder(30),
            SelectButtonRoundedBorder(30),
            SelectButtonRoundedBorder(30),
            SelectButtonRoundedBorder(30),
            SelectButtonRoundedBorder(30),
            SelectButtonRoundedBorder(30),
            SelectButtonRoundedBorder(30)
        )

        val rejectReasonButtons = listOf(
            selectButtons[0].createRoundedButton("가게 사정", MyColor.SELECTED_BACKGROUND_COLOR, MyColor.UNSELECTED_BACKGROUND_COLOR, MyColor.SELECTED_TEXT_COLOR, MyColor.GREY600, Dimension(300, 62)),
            selectButtons[1].createRoundedButton("재료 소진", MyColor.SELECTED_BACKGROUND_COLOR, MyColor.UNSELECTED_BACKGROUND_COLOR, MyColor.SELECTED_TEXT_COLOR, MyColor.GREY600, Dimension(300, 62)),
            selectButtons[2].createRoundedButton("조리 지연", MyColor.SELECTED_BACKGROUND_COLOR, MyColor.UNSELECTED_BACKGROUND_COLOR, MyColor.SELECTED_TEXT_COLOR, MyColor.GREY600, Dimension(300, 62)),
            selectButtons[3].createRoundedButton("배달원 부재", MyColor.SELECTED_BACKGROUND_COLOR, MyColor.UNSELECTED_BACKGROUND_COLOR, MyColor.SELECTED_TEXT_COLOR, MyColor.GREY600, Dimension(300, 62)),
            selectButtons[4].createRoundedButton("배달 불가 지역", MyColor.SELECTED_BACKGROUND_COLOR, MyColor.UNSELECTED_BACKGROUND_COLOR, MyColor.SELECTED_TEXT_COLOR, MyColor.GREY600, Dimension(300, 62)),
            selectButtons[5].createRoundedButton("메뉴 또는 가격 변동", MyColor.SELECTED_BACKGROUND_COLOR, MyColor.UNSELECTED_BACKGROUND_COLOR, MyColor.SELECTED_TEXT_COLOR, MyColor.GREY600, Dimension(300, 62)),
            selectButtons[6].createRoundedButton("요청 사항 적용 불가", MyColor.SELECTED_BACKGROUND_COLOR, MyColor.UNSELECTED_BACKGROUND_COLOR, MyColor.SELECTED_TEXT_COLOR, MyColor.GREY600, Dimension(300, 62))
        )

        // 기본으로 첫 번째 버튼을 선택된 상태로 설정하고 selectedButton에도 저장
        selectButtons[0].setButtonStyle(true)
        selectedButton = selectButtons[0]

        // 각 버튼 클릭 시 setButtonStyle 호출
        rejectReasonButtons.forEachIndexed { index, button ->
            button.addActionListener {
                selectedButton?.setButtonStyle(false)  // 이전 버튼 스타일 초기화
                selectedButton = selectButtons[index]  // 새로운 선택된 버튼 설정
                selectedButton?.setButtonStyle(true)  // 선택된 버튼 스타일 설정
                selectedReason = button.text  // 선택된 사유 저장
            }
        }

        val buttonPanel = JPanel(GridBagLayout()).apply {
            background = Color.WHITE
            border = EmptyBorder(10, 15, 0, 15) // 상하 여백을 없애고 좌우만 설정
//            border = EmptyBorder(0, 0, 0, 0)

            val gbc = GridBagConstraints().apply {
                gridx = 0
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(10, 10, 10, 10) // 버튼들 사이 간격
            }

            val introLabel = JLabel(labelText).apply {
                font = MyFont.Medium(16f)
            }

            gbc.gridx = 0
            gbc.gridy = 0
            gbc.gridwidth = 3
            add(introLabel, gbc)

            // 버튼 패널에 버튼 추가
            gbc.gridwidth = 1
            gbc.gridx = 0
            gbc.gridy = 1
            add(rejectReasonButtons[0], gbc)

            gbc.gridx = 1
            add(JLabel(), gbc)

            gbc.gridx = 2
            add(JLabel(), gbc)

            gbc.gridy = 2
            gbc.gridx = 0
            add(rejectReasonButtons[1], gbc)

            gbc.gridx = 1
            add(rejectReasonButtons[2], gbc)

            gbc.gridx = 2
            add(rejectReasonButtons[3], gbc)

            gbc.gridy = 3
            gbc.gridx = 0
            add(rejectReasonButtons[4], gbc)

            gbc.gridx = 1
            add(rejectReasonButtons[5], gbc)

            gbc.gridx = 2
            add(rejectReasonButtons[6], gbc)

            // 공백 패널 추가 (전체 레이아웃이 위로 밀리도록)
            gbc.gridy = 4
            gbc.gridx = 0
            gbc.gridwidth = 3
            gbc.weighty = 1.0 // 세로로 남은 공간을 모두 차지하게 설정
            add(JPanel().apply { isOpaque = false }, gbc) // 빈 패널을 추가하여 공백을 만듦
        }

        add(buttonPanel, BorderLayout.CENTER)

        val cancelButton = JButton(buttonText).apply {
            preferredSize = Dimension(300, 62)
            maximumSize = Dimension(300, 62)
            minimumSize = Dimension(300, 62)
            background = Color.WHITE
            foreground = MyColor.DARK_RED
            font = MyFont.Bold(24f)
            border = BorderFactory.createLineBorder(MyColor.DARK_RED)

            // 주문 거절 버튼을 클릭했을 때 onReject 실행
            addActionListener {
                selectedReason?.let {
                    onReject(it)  // 거절 사유를 콜백으로 전달
                    dispose()  // 다이얼로그 닫기
                }
            }

        }

        val bottomPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER)  // 버튼을 중앙에 배치
            background = Color.WHITE
            add(cancelButton)
            border = BorderFactory.createEmptyBorder(0, 0, 20, 0)
        }

        add(bottomPanel, BorderLayout.SOUTH)

        // 다이얼로그 크기 및 기본 설정
        setSize(1000, 465)
        setLocationRelativeTo(parent)
        isVisible = true
    }
}