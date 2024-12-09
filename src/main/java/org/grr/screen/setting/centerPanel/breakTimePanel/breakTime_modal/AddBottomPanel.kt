package org.grr.screen.setting.centerPanel.breakTimePanel.breakTime_modal

import org.grr.util.MyFont
import org.grr.widgets.IconRoundBorder2
import org.grr.widgets.RoundedButton
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel

class AddBottomPanel(
    private val bottomPanel: JPanel, // 부모 패널
    private val onDeleteAction: (String, String) -> Unit // 삭제 버튼 클릭 시 호출될 콜백
) {

    fun createPanel(labelText: String, timeRangeText: String): JPanel {
        return JPanel().apply {
            preferredSize = Dimension(940, 60)
            maximumSize = Dimension(940, 60)
            minimumSize = Dimension(940, 60)
            layout = BorderLayout()
            background = Color.WHITE
            border = BorderFactory.createLineBorder(Color.GRAY, 1)

            // 라벨 생성
            val label = IconRoundBorder2.createRoundedLabel(labelText, Color(255, 177, 177), 20).apply {
                foreground = Color.WHITE
                preferredSize = Dimension(150, 40)
            }

            // 시간 범위 라벨 생성
            val timeRangeLabel = JLabel(timeRangeText).apply {
                font = MyFont.Bold(24f)
            }

            // 삭제 버튼 생성
            val deleteButton = RoundedButton("삭제").apply {
                font = MyFont.Bold(18f)
                preferredSize = Dimension(100, 35)

                addActionListener {
                    // 삭제 콜백 호출
                    onDeleteAction(labelText, timeRangeText)
                    // 부모 패널에서 제거
                    bottomPanel.remove(this@apply.parent)
                    bottomPanel.revalidate()
                    bottomPanel.repaint()
                }
            }

            // 오른쪽 삭제 버튼 패널
            val rightPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 10, 10)).apply {
                background = Color.WHITE
                add(deleteButton)
            }

            // 왼쪽 라벨 패널
            val centerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 10, 10)).apply {
                background = Color.WHITE
                add(label)
                add(timeRangeLabel)
            }

            // 전체 구성
            add(centerPanel, BorderLayout.CENTER)
            add(rightPanel, BorderLayout.EAST)
        }
    }
}
