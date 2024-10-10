package org.example.screen.setting.centerPanel.breakTimePanel.breakTime_modal.weekDaysAndWeekEnds

import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class WeekDaysAndWeekEnds : JPanel() {
    init {
        layout = BorderLayout()
        background = Color.WHITE
        add(JLabel("평일/주말 선택 화면", SwingConstants.CENTER), BorderLayout.CENTER)
    }
}