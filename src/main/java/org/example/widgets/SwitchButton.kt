package org.example.widgets

import org.example.MyFont
import org.example.style.MyColor
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.Timer

class SwitchButton : JPanel() {

    private var switchColorOn: Color = Color(13, 130, 191)
    private var switchColorOff: Color = Color(255, 255, 255)  // 흰색
    private var switchBackgroundColorOn: Color = Color(13, 130, 191)
    private var switchBackgroundColorOff: Color = MyColor.DARK_NAVY
    private var switchOnBorderColor: Color = Color.WHITE
    private var switchOffBorderColor: Color = Color(13, 130, 191)
    private var textColor: Color = Color(255, 255, 255)  // 흰색 텍스트
    private var font: Font = MyFont.Bold(20f)
    private var borderSize: Int = 2

    private var on: Boolean = false
    private var animate: Float = if (on) 1f else 0f
    private var animationSpeed: Float = 0.05f  // 애니메이션 속도 설정
    private var isAnimating: Boolean = false
    private val events: MutableList<SwitchListener> = ArrayList()

    init {
        isOpaque = false
        background = Color(33, 43, 54)  // 배경색 지정
        foreground = Color(255, 255, 255)
        initMouseEvent()
    }

    fun toggleOn(on: Boolean) {
        if (!isAnimating) {
            this.on = on
            animateSwitch(on)
        }
    }

    private fun animateSwitch(on: Boolean) {
        val target = if (on) 1f else 0f
        isAnimating = true
        Timer(10) { e ->
            if (animate < target) {
                animate = minOf(animate + animationSpeed, target)
            } else {
                animate = maxOf(animate - animationSpeed, target)
            }
            repaint()

            if (animate == target) {
                (e.source as Timer).stop()
                isAnimating = false
                runEvent()
            }
        }.start()
    }

    fun addEventSwitchSelected(event: SwitchListener) {
        events.add(event)
    }

    private fun initMouseEvent() {
        val mouseAdapter: MouseAdapter = object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent) {
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            }

            override fun mouseExited(e: MouseEvent) {
                cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
            }

            override fun mouseReleased(e: MouseEvent) {
                if (isEnabled && SwingUtilities.isLeftMouseButton(e)) {
                    toggleOn(!on)
                }
            }
        }
        addMouseListener(mouseAdapter)
    }

    private fun runEvent() {
        for (event in events) {
            event.selectChange(on)
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 배경 그리기
        val width = width
        val height = height
        createBackground(g2, width, height)
        createSwitch(g2, width, height)
        createText(g2, width, height)

        g2.dispose()
    }

    private fun createBackground(g2: Graphics2D, width: Int, height: Int) {
        g2.color = if (on) switchBackgroundColorOn else switchBackgroundColorOff
        g2.fillRoundRect(0, 0, width, height, height, height)  // 둥근 배경
    }

    private fun createSwitch(g2: Graphics2D, width: Int, height: Int) {
        val size = height - 6  // 테두리 내 위치 지정
        val xPosition = (animate * (width - size - 8)).toInt() + 4  // ON일 때는 오른쪽으로 이동
        g2.color = if (on) switchColorOff else switchColorOn  // ON일 때 흰색, OFF일 때 빨간색
        g2.fillOval(xPosition, (height - size) / 2, size, size)  // 원 모양으로 그리기

        // 테두리 추가
        g2.stroke = BasicStroke(borderSize.toFloat())  // 테두리 두께 2로 설정
        g2.color = if (on) switchOnBorderColor else switchOffBorderColor
        g2.drawRoundRect(0, 0, width - 1, height - 1, height, height)
    }

    private fun createText(g2: Graphics2D, width: Int, height: Int) {
        g2.font = font
        g2.color = textColor
        val text = if (on) "ON" else "OFF"
        val fm = g2.fontMetrics
        val textWidth = fm.stringWidth(text)
        val textHeight = fm.ascent
        val x = (width - textWidth) / 2
        val y = (height + textHeight) / 2 - 4
        g2.drawString(text, x, y)
    }
}