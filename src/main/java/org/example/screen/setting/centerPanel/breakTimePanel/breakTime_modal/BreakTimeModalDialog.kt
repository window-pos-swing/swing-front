package org.example.screen.setting.centerPanel.breakTimePanel.breakTime_modal

import CustomRoundedDialog
import java.awt.BorderLayout
import java.awt.Color
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JFrame
import javax.swing.JPanel

class BreakTimeModalDialog(parent: JFrame, title: String, callback: ((Boolean) -> Unit)? = null) : CustomRoundedDialog(
    parent,
    title,
    1000,
    580,
    callback
)  {

    init {
        setSize(1000, 580)  // 다이얼로그 크기 설정
        setLocationRelativeTo(parent)

        // 설정 패널 구성 (전체 레이아웃은 GridBagLayout)
        val mainPanel = JPanel(GridBagLayout()).apply {
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(20, 15, 30, 15)  // 패널 마진 추가 (좌우 20, 상하 20)
        }


        val contentPanel = JPanel(BorderLayout()).apply {
            add(mainPanel, BorderLayout.CENTER)
        }

        add(contentPanel)

        isVisible = true
    }
}