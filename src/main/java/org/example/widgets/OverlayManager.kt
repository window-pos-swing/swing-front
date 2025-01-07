package org.example.widgets

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.*

class OverlayManager(private val targetFrame: JFrame, private val targetPanel: JPanel) {
    private var overlayPanel: JPanel? = null

    // 투명한 검은색 레이어를 추가하는 함수 (GlassPane 사용)
    fun addOverlayPanel() {
        overlayPanel = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val g2 = g as Graphics2D
                g2.composite = AlphaComposite.SrcOver.derive(1f)  // 투명도 설정
                g2.color = Color(0, 0, 0, 200)  // 반투명 검정색
                g2.fillRect(0, 0, width, height)
            }
        }.apply {
            isOpaque = false
            layout = null

            // 카드 패널의 위치와 크기를 따라가는 설정
            val panelBounds = targetPanel.bounds
            val convertedPoint = SwingUtilities.convertPoint(targetPanel.parent, panelBounds.location, targetFrame.glassPane)

            setBounds(convertedPoint.x, convertedPoint.y, panelBounds.width, panelBounds.height)
            isVisible = true
        }

        val glassPane = targetFrame.glassPane as JPanel
        glassPane.layout = null
        glassPane.add(overlayPanel)
        glassPane.isVisible = true
        glassPane.repaint()
    }

    // 투명한 검은색 레이어를 제거하는 함수
    fun removeOverlayPanel() {
        overlayPanel?.let {
            val glassPane = targetFrame.glassPane as JPanel
            glassPane.remove(it)
            glassPane.isVisible = false
            glassPane.repaint()
            overlayPanel = null
        }
    }
}