package org.example.widgets

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLayer
import javax.swing.JPanel
import javax.swing.plaf.LayerUI

class TransparentLayerUI : LayerUI<JComponent>() {
    override fun paint(g: Graphics, c: JComponent) {
        super.paint(g, c)
        val g2 = g.create() as Graphics2D
        g2.composite = AlphaComposite.SrcOver.derive(0.5f)  // 투명도 설정 (0.0 ~ 1.0)
        g2.color = Color(0, 0, 0, 150)  // 반투명 검정색
        g2.fillRect(0, 0, c.width, c.height)
        g2.dispose()
    }
}

class OverlayManager(private val targetFrame: JFrame) {
    private var overlayPanel: JPanel? = null

    // 투명한 검은색 레이어를 추가하는 함수
    fun addOverlayPanel() {
        overlayPanel = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val g2 = g as Graphics2D
                g2.composite = AlphaComposite.SrcOver.derive(0.5f)  // 투명도 설정
                g2.color = Color(0, 0, 0, 150)  // 반투명 검정색
                g2.fillRect(0, 0, width, height)
            }
        }.apply {
            isOpaque = false
            layout = null
            bounds = targetFrame.bounds
        }

        targetFrame.glassPane = overlayPanel
        overlayPanel?.isVisible = true
        targetFrame.repaint()
    }

    // 투명한 검은색 레이어를 제거하는 함수
    fun removeOverlayPanel() {
        overlayPanel?.let {
            it.isVisible = false
            overlayPanel = null
        }
    }
}