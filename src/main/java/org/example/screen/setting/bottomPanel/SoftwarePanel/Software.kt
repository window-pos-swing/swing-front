package org.example.screen.setting.bottomPanel.SoftwarePanel

import org.example.MyFont
import org.example.widgets.RoundedButton
import java.awt.*
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel

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
            insets = Insets(17, 10, 0, 20)  // 여백 설정
        }

        val panel = JPanel().apply {
            layout = FlowLayout(FlowLayout.LEFT)  // 한 줄로 배치
            isOpaque = false
//            preferredSize = Dimension(190, 40)  // 원하는 크기로 고정
//            minimumSize = Dimension(190, 40)  // 최소 크기 고정
//            maximumSize = Dimension(190, 40)  // 최대 크기 고정
//            background = Color.RED
        }

        // 아이콘 경로 로드
        val watchIconPath = ImageIcon(javaClass.getResource("/software.png"))
        val storeLabel = JLabel(watchIconPath).apply {
            border = BorderFactory.createEmptyBorder(0, 10, 0, 10)  // 아이콘과 텍스트 사이 여백 추가
        }

        // "브레이크 타임" 라벨
        val label = JLabel("소프트웨어").apply {
            font = MyFont.Bold(26f)
            foreground = Color.WHITE
        }

        panel.add(storeLabel)
        panel.add(label)

        // panel을 왼쪽 끝에 배치
        gbc.anchor = GridBagConstraints.WEST  // 왼쪽 정렬
        add(panel, gbc)

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