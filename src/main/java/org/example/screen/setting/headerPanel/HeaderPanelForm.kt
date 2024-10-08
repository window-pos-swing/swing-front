package org.example.screen.setting.headerPanel

import org.example.MyFont
import org.example.style.MyColor
import org.example.widgets.IconRoundBorder
import java.awt.*
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder

// 세팅에서 가장 상단 패널
class HeaderPanelForm : JPanel() {
    init {
        layout = BorderLayout()
        background = MyColor.DARK_NAVY
        preferredSize = Dimension(1440, 80)  // 패널 크기 강제 설정
        maximumSize = Dimension(Int.MAX_VALUE, 80)  // 최대 크기도 설정

        val titleLabel = JLabel("꼬르륵 식당 점주님").apply {
            font = MyFont.Bold(44f)
            foreground = Color.WHITE
        }

        val storeIcon = ImageIcon(javaClass.getResource("/store.png"))
        val storeLabel = JLabel(storeIcon).apply {
            border = BorderFactory.createEmptyBorder(0, 0, 0, 10)  // 아이콘과 텍스트 사이 여백 추가
        }

        val titlePanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.RIGHT, 0, 0)
            background = MyColor.DARK_NAVY
            add(storeLabel, BorderLayout.WEST)
            add(titleLabel, BorderLayout.CENTER)
        }

        val logoutButton = JButton("로그아웃").apply {
            preferredSize = Dimension(150, 50)
            background = MyColor.DARK_NAVY
            font = MyFont.Bold(24f)
            isOpaque = true
        }

        // 로그아웃 버튼을 패널에 넣어 정렬을 유지
        val buttonPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.RIGHT, 0, 0)
            background = MyColor.DARK_NAVY
            add(logoutButton)
        }

        add(titlePanel, BorderLayout.WEST)
        add(buttonPanel, BorderLayout.EAST)
    }
}