package org.grr.screen.setting.centerPanel

import org.grr.screen.setting.centerPanel.breakTimePanel.BreakTime
import org.grr.screen.setting.centerPanel.cookingCompltePanel.CookingCompletionTime
import org.grr.screen.setting.centerPanel.deliveryMethodTimePanel.DeliveryMethodTime
import org.grr.screen.setting.centerPanel.holidayPanel.HolidayPanel
import org.grr.screen.setting.centerPanel.operateTimePanel.OperateTime
import org.grr.style.MyColor
import org.grr.widgets.RoundedPanel
import java.awt.*
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingConstants

class CenterPanelForm : JPanel() {
    init {
        layout = BorderLayout()
        background = MyColor.DARK_NAVY
        border = BorderFactory.createEmptyBorder(20, 0, 0, 0)

        // 둥근 패널 생성
        val roundedPanel = RoundedPanel(30, 30).apply {
            layout = GridBagLayout()
            background = MyColor.LOGIN_TITLEBAR // 둥근 패널 배경색
            preferredSize = Dimension(1380, 480)
            maximumSize = Dimension(Int.MAX_VALUE, 480)  // 최대 크기도 설정
            border = BorderFactory.createEmptyBorder(5, 20, 5, 20) // 내부 여백 설정
        }

        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.BOTH
            anchor = GridBagConstraints.NORTH  // 컴포넌트를 상단에 고정
        }

        // 조리 완료 시간 패널 배치
        gbc.gridx = 0  // 첫 번째 열
        gbc.gridy = 0  // 첫 번째 행
        gbc.gridwidth = 1  // 1칸 차지
        gbc.weightx = 0.47
        gbc.insets = Insets(15, 10, 0, 10)
        roundedPanel.add(CookingCompletionTime(), gbc)

        // 경계선 추가 (조리 완료 시간과 배달 예정 시간 사이)
        gbc.gridx = 1
        gbc.gridy = 0
        gbc.gridwidth = 1
        gbc.weightx = 0.01
        gbc.insets = Insets(15, 10, 15, 0)
        roundedPanel.add(createSeparator(SwingConstants.VERTICAL, 1, 150), gbc)

        // 배달 방법 및 배달 예정 시간 패널 배치
        gbc.gridx = 2  // 두 번째 열
        gbc.gridy = 0  // 첫 번째 행
        gbc.gridwidth = 1  // 1칸 차지
        gbc.weightx = 0.47
        gbc.insets = Insets(15, 10, 0, 10)
        roundedPanel.add(DeliveryMethodTime(), gbc)

        // 경계선 추가 (배달 방법과 브레이크 타임 사이)
        gbc.gridx = 0
        gbc.gridy = 1
        gbc.gridwidth = 3  // 전체 너비 차지
        gbc.weightx = 0.0
        gbc.insets = Insets(0, 10, 0, 10)
        roundedPanel.add(createSeparator(SwingConstants.HORIZONTAL, 1340, 1), gbc)

        // 브레이크 타임 패널 추가
        gbc.gridx = 0
        gbc.gridy = 1
        gbc.gridwidth = 3
        gbc.weighty = 0.0
        gbc.insets = Insets(5, 10, 0, 10)
        roundedPanel.add(BreakTime(), gbc)

        // 브레이크 타임 밑에 경계선 추가
        gbc.gridx = 0
        gbc.gridy = 2
        gbc.gridwidth = 3  // 패널 아래 경계선이 전체 가로를 차지하게 설정
        gbc.insets = Insets(0, 10, 0, 10)
        roundedPanel.add(createSeparator(SwingConstants.HORIZONTAL, 1340, 1), gbc)

        // 영업 시간 패널 추가
        gbc.gridx = 0
        gbc.gridy = 2
        gbc.gridwidth = 3
        gbc.weighty = 0.0
        gbc.insets = Insets(5, 10, 0, 10)
        roundedPanel.add(OperateTime(), gbc)

        // 브레이크 타임 밑에 경계선 추가
        gbc.gridx = 0
        gbc.gridy = 3
        gbc.gridwidth = 3  // 패널 아래 경계선이 전체 가로를 차지하게 설정
        gbc.insets = Insets(0, 10, 0, 10)
        roundedPanel.add(createSeparator(SwingConstants.HORIZONTAL, 1340, 1), gbc)

        // 휴무일 패널 추가
        gbc.gridx = 0
        gbc.gridy = 3
        gbc.gridwidth = 3
        gbc.weighty = 0.0
        gbc.insets = Insets(5, 10, 0, 10)
        roundedPanel.add(HolidayPanel(), gbc)

        add(roundedPanel, BorderLayout.NORTH)
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