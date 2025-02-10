package org.grr.screen.setting.salesManagement.salesManagementForm

import org.grr.screen.setting.salesManagement.ShareData
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.RoundedButton
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Image
import java.io.File
import javax.swing.*

class CreateOrderTotalLabelPanelForm: JPanel() {

    init {
        layout = BorderLayout()
        background = Color.WHITE // 배경 설정
        isOpaque = true

        val labelPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            background = Color.WHITE
        }

        val orderLabel = JLabel("매출 요약").apply {
            font = MyFont.Bold(24f)

            // 아이콘 설정
            val resourceUrl = File("src/main/resources/bx_receipt.png").toURI().toURL()
            val imageIcon = ImageIcon(resourceUrl)
            val scaledIcon = ImageIcon(
                imageIcon.image.getScaledInstance(30, 30, Image.SCALE_SMOOTH)
            ) // 이미지 크기 조정
            icon = scaledIcon
            horizontalAlignment = SwingConstants.LEFT // 텍스트와 아이콘의 정렬
            horizontalTextPosition = SwingConstants.RIGHT // 텍스트를 아이콘 오른쪽에 위치
            iconTextGap = 10 // 아이콘과 텍스트 간 간격 설정
        }

        ShareData.selectedDateLabel = JLabel("오늘").apply {
            font = MyFont.Bold(24f)
            foreground = MyColor.LIGHT_BLUE
        }

        val completeLabel = JLabel("완료 기준 : 4건 132,000원").apply {
            font = MyFont.Bold(24f)
        }

        labelPanel.add(orderLabel)
        labelPanel.add(Box.createHorizontalStrut(10))
        labelPanel.add(ShareData.selectedDateLabel)
        labelPanel.add(Box.createHorizontalStrut(10))
        labelPanel.add(completeLabel)

        val printButton = RoundedButton("인쇄하기").apply {
            preferredSize = Dimension(135, 45)
            font = MyFont.Bold(22f)
            foreground = Color.WHITE
            setCustomBackground(MyColor.DARK_NAVY)
        }

        add(labelPanel, BorderLayout.WEST)
        add(printButton, BorderLayout.EAST)
    }
}