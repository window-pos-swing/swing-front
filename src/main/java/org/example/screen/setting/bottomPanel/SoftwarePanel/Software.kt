package org.example.screen.setting.bottomPanel.SoftwarePanel

import org.example.MyFont
import org.example.widgets.RoundedButton
import java.awt.*
import javax.swing.*

class Software: JPanel()  {
    init {
        layout = GridBagLayout()
//        background = Color.RED  // 배경색 설정
        isOpaque = false

        val gbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(17, 5, 0, 20)  // 여백 설정
        }

        val leftPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            alignmentX = Component.LEFT_ALIGNMENT
        }

        val leftLabel = JLabel("PC접수 버전 0.1.2 최신버전입니다.").apply {
            foreground = Color(146, 146, 146)
            font = MyFont.Bold(14f)
            alignmentX = Component.LEFT_ALIGNMENT
            border = BorderFactory.createEmptyBorder(3, 17, 0, 0)
        }

        val panel = JPanel().apply {
            layout = FlowLayout(FlowLayout.LEFT)  // 한 줄로 배치
            isOpaque = false
            alignmentX = Component.LEFT_ALIGNMENT
        }

        // 아이콘 경로 로드
        val watchIconPath = ImageIcon(javaClass.getResource("/software.png"))
        val storeLabel = JLabel(watchIconPath).apply {
            border = BorderFactory.createEmptyBorder(0, 10, 0, 10)  // 아이콘과 텍스트 사이 여백 추가
            alignmentX = Component.LEFT_ALIGNMENT
        }

        // "소프트웨어" 라벨
        val label = JLabel("소프트웨어").apply {
            font = MyFont.Bold(26f)
            foreground = Color.WHITE
            alignmentX = Component.LEFT_ALIGNMENT
        }

        panel.add(storeLabel)
        panel.add(label)

        leftPanel.add(panel)
        leftPanel.add(leftLabel)

        // panel을 왼쪽 끝에 배치
        gbc.anchor = GridBagConstraints.WEST  // 왼쪽 정렬
        add(leftPanel, gbc)

        // 설정 버튼을 오른쪽 끝에 배치
        gbc.gridx = 2
        gbc.anchor = GridBagConstraints.EAST  // 오른쪽 정렬
        gbc.weightx = 1.0  // 오른쪽 끝까지 공간을 차지하도록
        gbc.fill = GridBagConstraints.NONE
        val setButton = RoundedButton("업데이트").apply {
            preferredSize = Dimension(130, 40)  // 버튼 크기 설정
        }
        add(setButton, gbc)
    }
}