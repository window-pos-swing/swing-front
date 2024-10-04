package org.example.screen.setting.centerPanel

import org.example.screen.setting.centerPanel.breakTimePanel.BreakTime
import org.example.screen.setting.centerPanel.cookingComplte.CookingCompletionTime
import org.example.screen.setting.centerPanel.deliveryMethodTime.DeliveryMethodTime
import org.example.screen.setting.centerPanel.holidayPanel.HolidayPanel
import org.example.screen.setting.centerPanel.operateTimePanel.OperateTime
import org.example.style.MyColor
import java.awt.*
import javax.swing.*

open class RoundedPanel(private val radius: Int) : JPanel() {
    init {
        isOpaque = false  // 투명 배경 설정
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)  // 안티앨리어싱 설정

        // 배경색을 가져와서 그리기
        g2.color = background
        g2.fillRoundRect(0, 0, width - 1, height- 1, radius, radius)  // 모서리를 둥글게 그리기

        super.paintComponent(g)
    }
}

class CenterPanelForm : RoundedPanel(30) {
    init {

        layout = GridBagLayout()
        background = MyColor.LOGIN_TITLEBAR
        preferredSize = Dimension(1480, 430)
        maximumSize = Dimension(1480, 430)  // 최대 크기도 설정
        minimumSize = Dimension(1480, 430)  // 최소 크기도 설정

        val gbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            gridwidth = 1
            gridheight = 1
            weightx = 1.0
            weighty = 0.0
            fill = GridBagConstraints.BOTH
            insets = Insets(0, 10, 0, 10)  // 여백 설정
            anchor = GridBagConstraints.CENTER  // 컴포넌트를 상단에 고정
        }

        // 조리 완료 시간 패널 배치
        gbc.gridx = 0  // 첫 번째 열
        gbc.gridy = 0  // 첫 번째 행
        gbc.gridwidth = 1  // 1칸 차지
        gbc.insets = Insets(10, 0, 0, 0)  // 여백 설정
        add(CookingCompletionTime(), gbc)

        // 경계선 추가 (조리 완료 시간과 배달 예정 시간 사이)
        gbc.gridx = 1
        gbc.gridy = 0
        gbc.gridwidth = 1
        add(createSeparator(SwingConstants.VERTICAL, 1, 80), gbc)

        // 배달 방법 및 배달 예정 시간 패널 배치
        gbc.gridx = 1  // 두 번째 열
        gbc.gridy = 0  // 첫 번째 행
        gbc.gridwidth = 1  // 1칸 차지
        gbc.insets = Insets(10, 0, 0, 0)  // 여백 설정
        add(DeliveryMethodTime(), gbc)

        // 경계선 추가 (배달 방법과 브레이크 타임 사이)
        gbc.gridx = 0
        gbc.gridy = 1
        gbc.gridwidth = 3  // 전체 너비 차지
        add(createSeparator(SwingConstants.HORIZONTAL, 1440, 1), gbc)

        // 브레이크 타임 패널 추가
        gbc.gridx = 0
        gbc.gridy = 1
        gbc.gridwidth = 3
        gbc.weighty = 0.0
        add(BreakTime(), gbc)

        // 브레이크 타임 밑에 경계선 추가
        gbc.gridx = 0
        gbc.gridy = 2
        gbc.gridwidth = 3  // 패널 아래 경계선이 전체 가로를 차지하게 설정
        add(createSeparator(SwingConstants.HORIZONTAL, 1440, 1), gbc)

        // 영업 시간 패널 추가
        gbc.gridx = 0
        gbc.gridy = 2
        gbc.gridwidth = 3
        gbc.weighty = 0.0
        add(OperateTime(), gbc)

        // 브레이크 타임 밑에 경계선 추가
        gbc.gridx = 0
        gbc.gridy = 3
        gbc.gridwidth = 3  // 패널 아래 경계선이 전체 가로를 차지하게 설정
        add(createSeparator(SwingConstants.HORIZONTAL, 1440, 1), gbc)

        // 휴무일 패널 추가
        gbc.gridx = 0
        gbc.gridy = 3
        gbc.gridwidth = 3
        gbc.weighty = 0.0
        add(HolidayPanel(), gbc)

        // 빈 공간 추가: 상단 고정을 위해 아래쪽에 빈 패널을 추가하여 남은 공간을 차지하게 함
        gbc.gridy = 4
        gbc.weighty = 1.0  // 빈 공간이 남은 공간을 차지하도록 설정
        add(JPanel().apply { background = MyColor.LOGIN_TITLEBAR }, gbc)  // 빈 패널 추가
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