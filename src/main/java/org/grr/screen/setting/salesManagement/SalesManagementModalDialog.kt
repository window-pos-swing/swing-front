package org.grr.screen.setting.salesManagement

import CustomRoundedDialog
import org.grr.util.MyFont
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants

class SalesManagementModalDialog(
    parent: JFrame,
    title: String,
    callback: ((Boolean) -> Unit)? = null
) : CustomRoundedDialog(parent, title, 1350, 890, callback)  {

    init {
        setSize(1350, 890)
        setLocationRelativeTo(parent)
        background = Color.WHITE

        // 원하는 UI 추가
        val label = JLabel("매출 관리 화면").apply {
            font = MyFont.Bold(24f)
            foreground = Color.BLACK
            horizontalAlignment = SwingConstants.CENTER
        }

        contentPane.add(label, BorderLayout.CENTER)
    }
}