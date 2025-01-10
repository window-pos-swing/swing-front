package org.grr.`object`

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

object OverlayManager {
    private var parentFrame: JFrame? = null
    private var targetPanel: JPanel? = null
    private var overlayPanel: JPanel? = null

    /**
     * 초기화 여부를 확인하는 메서드.
     */
    private fun ensureInitialized() {
        if (parentFrame == null || targetPanel == null) {
            throw IllegalStateException("OverlayManager is not initialized. Please call initialize() first.")
        }
    }

    /**
     * OverlayManager 초기화.
     *
     * @param parentFrame 부모 JFrame
     * @param targetPanel 대상 JPanel
     */
    @Synchronized
    fun initialize(parentFrame: JFrame, targetPanel: JPanel) {
        if (OverlayManager.parentFrame == parentFrame && OverlayManager.targetPanel == targetPanel) {
            return
        }
        OverlayManager.parentFrame = parentFrame
        OverlayManager.targetPanel = targetPanel
    }

    /**
     * OverlayManager 상태를 업데이트합니다.
     *
     * @param parentFrame 새 부모 JFrame
     * @param targetPanel 새 대상 JPanel
     */
    @Synchronized
    fun update(parentFrame: JFrame, targetPanel: JPanel) {
        OverlayManager.parentFrame = parentFrame
        OverlayManager.targetPanel = targetPanel
    }

    /**
     * 투명한 검은색 레이어를 추가합니다.
     */
    fun addOverlayPanel() {
        ensureInitialized()
        SwingUtilities.invokeLater {
            overlayPanel = object : JPanel() {
                override fun paintComponent(g: Graphics) {
                    super.paintComponent(g)
                    val g2 = g as Graphics2D
                    g2.composite = AlphaComposite.SrcOver.derive(0.7f)  // 투명도 설정 (0.7)
                    g2.color = Color(0, 0, 0, 200)  // 반투명 검정색
                    g2.fillRect(0, 0, width, height)
                }
            }.apply {
                isOpaque = false
                layout = null

                targetPanel?.revalidate()
                targetPanel?.repaint()
                parentFrame?.revalidate()
                parentFrame?.repaint()

                val panelBounds = targetPanel!!.bounds
                val convertedPoint = SwingUtilities.convertPoint(
                    targetPanel!!.parent,
                    panelBounds.location,
                    parentFrame!!.glassPane
                )

                setBounds(convertedPoint.x, convertedPoint.y, panelBounds.width, panelBounds.height)
                isVisible = true
            }

            val glassPane = parentFrame!!.glassPane as JPanel
            if (!glassPane.isVisible) {
                glassPane.isVisible = true
                glassPane.revalidate()
                glassPane.repaint()
            }

            glassPane.layout = null
            glassPane.add(overlayPanel)
            glassPane.repaint()
        }
    }

    /**
     * 투명한 검은색 레이어를 제거합니다.
     */
    fun removeOverlayPanel() {
        ensureInitialized()

        overlayPanel?.let {
            val glassPane = parentFrame!!.glassPane as JPanel
            glassPane.remove(it)
            glassPane.isVisible = false
            glassPane.repaint()
            overlayPanel = null
        }
    }

    /**
     * OverlayManager 상태를 리셋합니다.
     */
    fun reset() {
        parentFrame = null
        targetPanel = null
        overlayPanel = null
    }
}

