package org.example.screen.setting.bottomPanel

import PrinterSettingDialog
import org.example.screen.setting.bottomPanel.SoftwarePanel.Software
import org.example.screen.setting.bottomPanel.printPanel.Print
import org.example.screen.setting.bottomPanel.soundControlPanel.SoundControl
import org.example.screen.setting.centerPanel.RoundedPanel
import org.example.screen.setting.centerPanel.cookingCompltePanel.CookingCompletionTime
import org.example.screen.setting.centerPanel.deliveryMethodTimePanel.DeliveryMethodTime
import org.example.style.MyColor
import java.awt.*
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingConstants

open class RoundedPanel(private val radius: Int) : JPanel() {
    init {
        isOpaque = false  // 투명 배경 설정
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)  // 안티앨리어싱 설정

        // 배경색을 가져와서 그리기
        g2.color = background
        g2.fillRoundRect(0, 0, width - 1, height - 1, radius, radius)  // 모서리를 둥글게 그리기

        super.paintComponent(g)
    }
}

class BottomPanelForm : RoundedPanel(30) {
    init {
        layout = GridBagLayout()
        background = MyColor.LOGIN_TITLEBAR
        preferredSize = Dimension(1380, 205)
        maximumSize = Dimension(Int.MAX_VALUE, 205)  // 최대 크기도 설정


        val gbc = GridBagConstraints().apply {
            gridy = 0
            gridheight = 1
            fill = GridBagConstraints.BOTH
            insets = Insets(0, 10, 0, 10)  // 여백 설정
        }

        // Software - 왼쪽
        gbc.gridx = 0
        gbc.gridwidth = 1
        gbc.weightx = 0.49  // Software와 SoundControl이 같은 비율로 공간 차지
        gbc.weighty = 0.0  // 높이 비율
        add(Software().apply {
            preferredSize = Dimension(0, 0)  // 내부 컴포넌트가 너비를 강제로 설정하지 않게
        }, gbc)

        // Vertical Separator
        gbc.gridx = 1
        gbc.gridwidth = 1
        gbc.weightx = 0.02  // 최소한의 공간 차지
        gbc.weighty = 0.0  // 높이 비율
        gbc.insets = Insets(15, 10, 5, 10)
        add(createSeparator(SwingConstants.VERTICAL, 1, 80), gbc)

        // SoundControl - 오른쪽
        gbc.gridx = 2
        gbc.gridwidth = 1
        gbc.weightx = 0.49  // Software와 동일한 비율로 공간 차지
        gbc.weighty = 0.0  // 높이 비율
        gbc.insets = Insets(0, 10, 0, 10)
        add(SoundControl().apply {
            preferredSize = Dimension(0, 0)  // 내부 컴포넌트가 너비를 강제로 설정하지 않게
        }, gbc)

        // 경계선 추가 (배달 방법과 브레이크 타임 사이)
        gbc.gridx = 0
        gbc.gridy = 1
        gbc.gridwidth = 3  // 전체 너비 차지
        gbc.weightx = 1.0
        gbc.weighty = 1.0  // 높이 비율
        add(createSeparator(SwingConstants.HORIZONTAL, 1440, 1), gbc)

//        프린트 출력 설정
        gbc.gridy = 2
        gbc.gridwidth = 3  // 전체 너비 차지
        gbc.weightx = 1.0
        gbc.weighty = 1.0  // 높이 비율
        gbc.insets = Insets(0, 10, 10, 10)  // 여백 설정
        add(Print(), gbc)
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