package org.grr.screen.setting.salesManagement.salesManagementForm

import org.grr.util.MyFont
import java.awt.Color
import java.awt.FlowLayout
import java.awt.Image
import java.io.File
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class CreateOrderLabelPanelForm: JPanel() {
    init {
        layout = FlowLayout(FlowLayout.LEFT, 0, 0) // 좌측 정렬
        background = Color.WHITE // 배경 설정
        isOpaque = true

        val orderLabel = JLabel("주문 목록").apply {
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

        add(orderLabel) // JLabel 추가
    }
}