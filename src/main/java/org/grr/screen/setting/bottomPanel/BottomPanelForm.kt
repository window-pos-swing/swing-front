package org.grr.screen.setting.bottomPanel

import org.grr.screen.setting.bottomPanel.SoftwarePanel.Software
import org.grr.screen.setting.bottomPanel.printPanel.Print
import org.grr.screen.setting.bottomPanel.soundControlPanel.SoundControl
import org.grr.style.MyColor
import java.awt.*
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingConstants

class BottomPanelForm : JPanel() {
    init {
        layout = BorderLayout()
        background = MyColor.DARK_NAVY
        border = BorderFactory.createEmptyBorder(20, 0, 0, 0)

        // 둥근 패널 생성
        val roundedPanel = org.grr.widgets.RoundedPanel(30, 30).apply {
            layout = GridBagLayout()
            background = MyColor.LOGIN_TITLEBAR // 둥근 패널 배경색
            preferredSize = Dimension(1380, 220)
            maximumSize = Dimension(Int.MAX_VALUE, 220)  // 최대 크기도 설정
            border = BorderFactory.createEmptyBorder(5, 20, 5, 20) // 내부 여백 설정
        }

        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.BOTH
            anchor = GridBagConstraints.NORTH  // 컴포넌트를 상단에 고정
        }

        // Software - 왼쪽
        gbc.gridx = 0
        gbc.gridwidth = 1
        gbc.weightx = 0.49  // Software와 SoundControl이 같은 비율로 공간 차지
        gbc.weighty = 0.0  // 높이 비율
        roundedPanel.add(Software().apply {
            preferredSize = Dimension(0, 0)  // 내부 컴포넌트가 너비를 강제로 설정하지 않게
        }, gbc)

        // Vertical Separator
        gbc.gridx = 1
        gbc.gridwidth = 1
        gbc.weightx = 0.02  // 최소한의 공간 차지
        gbc.weighty = 0.0  // 높이 비율
        gbc.insets = Insets(15, 10, 5, 10)
        roundedPanel.add(createSeparator(SwingConstants.VERTICAL, 1, 80), gbc)

        // SoundControl - 오른쪽
        gbc.gridx = 2
        gbc.gridwidth = 1
        gbc.weightx = 0.49  // Software와 동일한 비율로 공간 차지
        gbc.weighty = 0.0  // 높이 비율
        gbc.insets = Insets(0, 10, 0, 10)
        roundedPanel.add(SoundControl().apply {
            preferredSize = Dimension(0, 0)  // 내부 컴포넌트가 너비를 강제로 설정하지 않게
        }, gbc)

        // 경계선 추가 (배달 방법과 브레이크 타임 사이)
        gbc.gridx = 0
        gbc.gridy = 1
        gbc.gridwidth = 3  // 전체 너비 차지
        gbc.weightx = 1.0
        gbc.weighty = 1.0  // 높이 비율
        roundedPanel.add(createSeparator(SwingConstants.HORIZONTAL, 1440, 1), gbc)

//        프린트 출력 설정
        gbc.gridy = 2
        gbc.gridwidth = 3  // 전체 너비 차지
        gbc.weightx = 1.0
        gbc.weighty = 1.0  // 높이 비율
        gbc.insets = Insets(0, 10, 10, 10)  // 여백 설정
        roundedPanel.add(Print(), gbc)

        add(roundedPanel, BorderLayout.NORTH)
    }

    private fun createSeparator(orientation: Int, width: Int, height: Int): JSeparator {
        return JSeparator(orientation).apply {
            foreground = Color.WHITE
            preferredSize = Dimension(width, height)
            minimumSize = Dimension(width, height)
            maximumSize = Dimension(width, height)
        }
    }
}