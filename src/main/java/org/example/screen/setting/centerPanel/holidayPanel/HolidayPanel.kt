package org.example.screen.setting.centerPanel.holidayPanel

import org.example.MyFont
import org.example.style.MyColor
import org.example.widgets.RoundedButton
import java.awt.*
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel

class HolidayPanel: JPanel()  {
    init {
        layout = GridBagLayout()
//        background = Color.RED  // 배경색 설정
        background = MyColor.LOGIN_TITLEBAR

        val gbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(10, 10, 0, 10)  // 여백 설정
        }

        val panel = JPanel().apply {
            layout = FlowLayout(FlowLayout.LEFT)  // 한 줄로 배치
            background = MyColor.LOGIN_TITLEBAR
//            background = Color.RED
        }

        // 아이콘 경로 로드
        val watchIconPath = ImageIcon(javaClass.getResource("/closedDay.png"))
        val storeLabel = JLabel(watchIconPath).apply {
            border = BorderFactory.createEmptyBorder(0, 10, 0, 10)  // 아이콘과 텍스트 사이 여백 추가
        }

        // "브레이크 타임" 라벨
        val label = JLabel("휴무일").apply {
            font = MyFont.Bold(26f)
            foreground = Color.WHITE
        }

        panel.add(storeLabel)
        panel.add(label)

        // panel을 왼쪽 끝에 배치
        gbc.anchor = GridBagConstraints.WEST  // 왼쪽 정렬
        add(panel, gbc)

        // 시간 라벨을 가운데에 배치
        gbc.gridx = 1
        gbc.anchor = GridBagConstraints.CENTER  // 가운데 정렬
        val breakTimeLabel = JLabel("매월 둘째, 셋째 토요일").apply {
            font = MyFont.Bold(32f)
            foreground = Color.PINK
        }
        add(breakTimeLabel, gbc)

        // 설정 버튼을 오른쪽 끝에 배치
        gbc.gridx = 2
        gbc.anchor = GridBagConstraints.EAST  // 오른쪽 정렬
        gbc.weightx = 1.0  // 오른쪽 끝까지 공간을 차지하도록
        gbc.fill = GridBagConstraints.NONE
        val setButton = RoundedButton("설정")
        add(setButton, gbc)
    }
}