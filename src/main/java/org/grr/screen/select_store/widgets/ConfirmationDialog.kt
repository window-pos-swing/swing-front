package org.grr.screen.select_store.widgets

import CustomRoundedDialog
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.FillRoundedButton
import java.awt.*
import javax.swing.*

class SelectStoreConfirmationDialog(
    parent: Window,
    title: String,
    storeName: String,
    private val callback: (Boolean) -> Unit
) : CustomRoundedDialog(parent, title, 500, 500) {

    init {
        // 다이얼로그 내용 구성
        background = Color.WHITE

        // 중앙 패널 (GridBagLayout 사용)
        val centerPanel = JPanel(GridBagLayout()).apply {
            background = Color.WHITE

            val constraints = GridBagConstraints().apply {
                gridx = 0
                fill = GridBagConstraints.HORIZONTAL
                anchor = GridBagConstraints.CENTER
                insets = Insets(10, 0, 10, 0) // 위아래 간격 조정
            }

// storeNameLabel
            val storeNameLabel = JLabel(
                "<html><div style='text-align:center; font-family: ${MyFont.Bold(40f).name}; font-size: 40px;'>$storeName</div></html>"
            ).apply {
                horizontalAlignment = SwingConstants.CENTER
                border = BorderFactory.createEmptyBorder(0, 0, 0, 10)
                maximumSize = Dimension(450, Int.MAX_VALUE) // 최대 너비 설정 (500 - 패딩 고려)
            }
            constraints.gridy = 0
            add(storeNameLabel, constraints)



            // messageLabel
            val messageLabel = JLabel("영업을 시작하시려면 확인해주세요").apply {
                horizontalAlignment = SwingConstants.CENTER
                font = MyFont.SemiBold(24f)
                border = BorderFactory.createEmptyBorder(10, 0, 0, 0)
            }
            constraints.gridy = 1
            add(messageLabel, constraints)
        }

        // 버튼 패널
        val buttonPanel = JPanel().apply {
            background = Color.WHITE
            layout = FlowLayout(FlowLayout.CENTER, 20, 10)

            // "확인했습니다" 버튼
            val yesButton = FillRoundedButton(
                text = "확인했습니다",
                borderColor = MyColor.DARK_RED,
                backgroundColor = MyColor.DARK_RED,
                textColor = Color.WHITE,
                borderRadius = 20,
                borderWidth = 1,
                textAlignment = SwingConstants.CENTER,
                padding = Insets(10, 20, 10, 20),
                buttonSize = Dimension(300, 60),
                customFont = MyFont.Bold(22f)
            ).apply {
                addActionListener {
                    callback(true) // 사용자 확인
                    dispose() // 다이얼로그 닫기
                }
            }
            add(yesButton)
        }

        // 하단 패널에 여백 추가
        val bottomPanel = JPanel(BorderLayout()).apply {
            background = Color.WHITE
            add(buttonPanel, BorderLayout.CENTER)
            border = BorderFactory.createEmptyBorder(0, 0, 20, 0) // 하단 여백 추가
        }

        // 다이얼로그 레이아웃 설정
        add(centerPanel, BorderLayout.CENTER)
        add(bottomPanel, BorderLayout.SOUTH)

        // 다이얼로그 설정
        setSize(500, 500)
        setLocationRelativeTo(parent) // 부모를 기준으로 위치 설정
        isVisible = true
    }

    /**
     * HTML로 텍스트 포맷팅 및 줄바꿈 처리
     */
    private fun formatHtmlText(text: String, width: Int): String {
        val htmlText = "<html><div style='width: ${width}px; text-align: center;'>$text</div></html>"
        return htmlText
    }
}
