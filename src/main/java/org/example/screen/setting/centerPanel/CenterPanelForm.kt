package org.example.screen.setting.centerPanel

import org.example.screen.setting.centerPanel.cookingComplte.CookingCompletionTime
import org.example.screen.setting.centerPanel.deliveryMethodTime.DeliveryMethodTime
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
        g2.fillRoundRect(0, 0, width, height, radius, radius)  // 모서리를 둥글게 그리기

        super.paintComponent(g)
    }
}

class CenterPanelForm : RoundedPanel(30) {
    init {

        layout = GridBagLayout()
        background = MyColor.LOGIN_TITLEBAR
        preferredSize = Dimension(1440, 460)

        val gbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            gridwidth = 1
            gridheight = 1
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.BOTH
            insets = Insets(10, 10, 10, 10)  // 여백 설정
        }

        // 조리 완료 시간 패널 배치
        gbc.gridx = 0  // 첫 번째 열
        gbc.gridy = 0  // 첫 번째 행
        gbc.gridwidth = 1  // 1칸 차지
        add(CookingCompletionTime(), gbc)

        // 배달 방법 및 배달 예정 시간 패널 배치
        gbc.gridx = 1  // 두 번째 열
        gbc.gridy = 0  // 첫 번째 행
        gbc.gridwidth = 1  // 1칸 차지
        add(DeliveryMethodTime(), gbc)

//        add(titlePanel)

        // 브레이크 타임 패널 추가
        gbc.gridy = 1
        gbc.gridwidth = 1
//        add(createBreakTimePanel(), gbc)

        // 영업 시간 패널 추가
        gbc.gridy = 2
//        add(createOperationTimePanel(), gbc)

        // 휴무일 패널 추가
        gbc.gridy = 3
//        add(createHolidayPanel(), gbc)
    }
}