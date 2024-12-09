package org.grr.screen.setting.centerPanel.operateTimePanel

import org.grr.`object`.Storage
import org.grr.`object`.TimeManager
import org.grr.screen.setting.centerPanel.operateTimePanel.operateTime_modal.OperateTimeModalDialog
import org.grr.util.MyFont
import org.grr.widgets.RoundedButton
import java.awt.*
import javax.swing.*

class OperateTime : JPanel() {
    init {
        layout = GridBagLayout()
//        background = Color.RED  // 배경색 설정
        isOpaque = false

        val gbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(15, 10, 15, 20)  // 여백 설정
        }

        val panel = JPanel().apply {
            layout = FlowLayout(FlowLayout.LEFT)  // 한 줄로 배치
            isOpaque = false
            preferredSize = Dimension(190, 40)  // 원하는 크기로 고정
            minimumSize = Dimension(190, 40)  // 최소 크기 고정
            maximumSize = Dimension(190, 40)  // 최대 크기 고정
//            background = Color.RED
        }

        /*
            회원정보 갖고오는 구문
        */
        val memberInfo = Storage.getMemberInfo()

        val operateTime = memberInfo
            ?.optJSONObject("setting")
            ?.optJSONObject("businessHour")

        operateTime?.let { TimeManager.initialize(operateTime) }
        val formattedOperateTime = TimeManager.getFormattedBreakTimes()

        println("운영 시간 ${formattedOperateTime}")

        // 아이콘 경로 로드
        val watchIconPath = ImageIcon(javaClass.getResource("/watch.png"))
        val storeLabel = JLabel(watchIconPath).apply {
            border = BorderFactory.createEmptyBorder(0, 10, 0, 10)  // 아이콘과 텍스트 사이 여백 추가
        }

        // "브레이크 타임" 라벨
        val label = JLabel("영업시간").apply {
            font = MyFont.Bold(26f)
            foreground = Color.WHITE
        }

        panel.add(storeLabel)
        panel.add(label).apply {
            border = BorderFactory.createEmptyBorder(0, 0, 10, 0)
        }

        // panel을 왼쪽 끝에 배치
        gbc.anchor = GridBagConstraints.WEST  // 왼쪽 정렬
        add(panel, gbc)

        // 시간 라벨을 가운데에 배치
        gbc.gridx = 1
        gbc.gridy = 0
        gbc.anchor = GridBagConstraints.CENTER  // 가운데 정렬
        gbc.weightx = 1.0
        gbc.weighty = 1.0  // 수직으로도 공간 차지
        gbc.fill = GridBagConstraints.BOTH  // 가로 세로 공간을 모두 차지하도록
        val breakTimeLabel = JLabel(formattedOperateTime).apply {
            font = MyFont.Bold(24f)
            foreground = Color.PINK
            horizontalAlignment = SwingConstants.CENTER
        }
        add(breakTimeLabel, gbc)

        // 설정 버튼을 오른쪽 끝에 배치
        gbc.gridx = 2
        gbc.anchor = GridBagConstraints.EAST  // 오른쪽 정렬
        gbc.weightx = 1.0  // 오른쪽 끝까지 공간을 차지하도록
        gbc.fill = GridBagConstraints.NONE
        val setButton = RoundedButton("설정").apply {
            addActionListener {
                val parentFrame = SwingUtilities.getWindowAncestor(this) as? JFrame
                if (parentFrame != null) {
                    OperateTimeModalDialog(parentFrame, "영업 시간 설정")
                }
            }
        }
        add(setButton, gbc)
    }
}