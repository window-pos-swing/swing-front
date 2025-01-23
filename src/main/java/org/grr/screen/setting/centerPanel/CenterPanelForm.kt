package org.grr.screen.setting.centerPanel

import org.grr.screen.setting.centerPanel.breakTimePanel.BreakTime
import org.grr.screen.setting.centerPanel.cookingCompltePanel.CookingCompletionTime
import org.grr.screen.setting.centerPanel.deliveryMethodTimePanel.DeliveryMethodTime
import org.grr.screen.setting.centerPanel.holidayPanel.HolidayPanel
import org.grr.screen.setting.centerPanel.operateTimePanel.OperateTime
import org.grr.style.MyColor
import java.awt.*
import javax.swing.BorderFactory
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

class CenterPanelForm : RoundedPanel(30) {
    init {
        layout = GridBagLayout()
        background = MyColor.LOGIN_TITLEBAR
        border = BorderFactory.createEmptyBorder(20, 0, 0, 0)
        preferredSize = Dimension(1380, 460)
        maximumSize = Dimension(1380, 460)  // 최대 크기도 설정
        minimumSize = Dimension(1380, 460)

        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.BOTH
            anchor = GridBagConstraints.CENTER  // 컴포넌트를 상단에 고정
        }

        // 조리 완료 시간 패널 배치
        gbc.gridx = 0  // 첫 번째 열
        gbc.gridy = 0  // 첫 번째 행
        gbc.gridwidth = 1  // 1칸 차지
        gbc.weightx = 0.47
        gbc.insets = Insets(15, 10, 0, 10)
        add(CookingCompletionTime(), gbc)

        // 경계선 추가 (조리 완료 시간과 배달 예정 시간 사이)
        gbc.gridx = 1
        gbc.gridy = 0
        gbc.gridwidth = 1
        gbc.weightx = 0.01
        gbc.insets = Insets(15, 10, 15, 0)
        add(createSeparator(SwingConstants.VERTICAL, 1, 170), gbc)

        // 배달 방법 및 배달 예정 시간 패널 배치
        gbc.gridx = 2  // 두 번째 열
        gbc.gridy = 0  // 첫 번째 행
        gbc.gridwidth = 1  // 1칸 차지
        gbc.weightx = 0.47
        gbc.insets = Insets(15, 10, 0, 10)
        add(DeliveryMethodTime(), gbc)

        // 경계선 추가 (배달 방법과 브레이크 타임 사이)
        gbc.gridx = 0
        gbc.gridy = 1
        gbc.gridwidth = 3  // 전체 너비 차지
        gbc.weightx = 0.0
        gbc.insets = Insets(0, 10, 0, 10)
        add(createSeparator(SwingConstants.HORIZONTAL, 1340, 1), gbc)

        // 브레이크 타임 패널 추가
        gbc.gridx = 0
        gbc.gridy = 1
        gbc.gridwidth = 3
        gbc.weighty = 0.0
        gbc.insets = Insets(5, 10, 0, 10)
        add(BreakTime(), gbc)

        // 브레이크 타임 밑에 경계선 추가
        gbc.gridx = 0
        gbc.gridy = 2
        gbc.gridwidth = 3  // 패널 아래 경계선이 전체 가로를 차지하게 설정
        gbc.insets = Insets(0, 10, 0, 10)
        add(createSeparator(SwingConstants.HORIZONTAL, 1340, 1), gbc)

        // 영업 시간 패널 추가
        gbc.gridx = 0
        gbc.gridy = 2
        gbc.gridwidth = 3
        gbc.weighty = 0.0
        gbc.insets = Insets(5, 10, 0, 10)
        add(OperateTime(), gbc)

        // 브레이크 타임 밑에 경계선 추가
        gbc.gridx = 0
        gbc.gridy = 3
        gbc.gridwidth = 3  // 패널 아래 경계선이 전체 가로를 차지하게 설정
        gbc.insets = Insets(0, 10, 0, 10)
        add(createSeparator(SwingConstants.HORIZONTAL, 1340, 1), gbc)

        // 휴무일 패널 추가
        gbc.gridx = 0
        gbc.gridy = 3
        gbc.gridwidth = 3
        gbc.weighty = 0.0
        gbc.insets = Insets(5, 10, 0, 10)
        add(HolidayPanel(), gbc)
    }

    private fun createSeparator(orientation: Int, width: Int, height: Int): JSeparator {
        return JSeparator(orientation).apply {
            foreground = Color.WHITE
            preferredSize = Dimension(width, height)
            maximumSize = Dimension(width, height)
            minimumSize = Dimension(width, height)
        }
    }
}