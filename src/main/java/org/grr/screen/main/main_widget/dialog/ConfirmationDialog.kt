package org.grr.screen.main.main_widget.dialog

import CustomRoundedDialog
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.FillRoundedButton
import java.awt.*
import javax.swing.*

class ConfirmationDialog(
    parent: JDialog,
    title: String,
    endTime: String,
    private val callback: (Boolean) -> Unit
) : CustomRoundedDialog(parent, title, 500, 300) {

    init {
        // 다이얼로그 내용 구성
        background = Color.WHITE

        // 가운데 정렬된 라벨
        // 중앙 패널 (GridBagLayout 사용)
        val centerPanel = JPanel(GridBagLayout()).apply {
            background = Color.WHITE

            val constraints = GridBagConstraints().apply {
                gridx = 0
                fill = GridBagConstraints.HORIZONTAL
                anchor = GridBagConstraints.CENTER
                insets = Insets(10, 0, 10, 0) // 위아래 간격 조정
            }

            // endTimeLabel
            val endTimeLabel = JLabel(endTime).apply {
                horizontalAlignment = SwingConstants.CENTER
                font = MyFont.Bold(26f)
                border = BorderFactory.createEmptyBorder(0,0,0,10)
            }
            constraints.gridy = 0
            add(endTimeLabel, constraints)

            // messageLabel
            val messageLabel = JLabel("까지 임시정지 하시겠습니까 ?").apply {
                horizontalAlignment = SwingConstants.CENTER
                font = MyFont.SemiBold(20f)
                border = BorderFactory.createEmptyBorder(0,0,0,20)
            }
            constraints.gridy = 1
            add(messageLabel, constraints)
        }


        // 버튼 패널 (하단)
        val buttonPanel = JPanel().apply {
            background = Color.WHITE

            val yesButton = FillRoundedButton(
                text = "네",
                borderColor = MyColor.DARK_RED,
                backgroundColor = MyColor.DARK_RED,
                textColor = Color.WHITE,
                borderRadius = 20,
                borderWidth = 1,
                textAlignment = SwingConstants.CENTER,
                padding = Insets(10, 20, 10, 20),
                buttonSize = Dimension(130, 50),
                customFont = MyFont.Bold(22f)
            ).apply {
                addActionListener {
                    callback(true) // 사용자 취소
                    dispose() // 다이얼로그 닫기
                }
            }


            val noButton = FillRoundedButton(
                text = "아니오",
                borderColor = MyColor.GREY500,
                backgroundColor = MyColor.GREY500,
                textColor = Color.WHITE,
                borderRadius = 20,
                borderWidth = 1,
                textAlignment = SwingConstants.CENTER,
                padding = Insets(10, 20, 10, 20),
                buttonSize = Dimension(130, 50),
                customFont = MyFont.Bold(22f)
            ).apply {
                addActionListener {
                    callback(false) // 사용자 취소
                    dispose() // 다이얼로그 닫기
                }
            }

            add(yesButton)
            add(noButton)
        }

        // 다이얼로그 레이아웃 설정
        add(centerPanel, BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.SOUTH)

        // 다이얼로그 설정
        setSize(500, 300)
        setLocationRelativeTo(parent) // 부모를 기준으로 위치 설정
        isVisible = true
    }
}
