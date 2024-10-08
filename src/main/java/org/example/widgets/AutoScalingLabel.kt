package org.example.widgets

import org.example.MyFont
import java.awt.FontMetrics
import java.awt.Graphics
import javax.swing.JLabel
import javax.swing.SwingUtilities

class AutoScalingLabel(text: String, private var maxFontSize: Float = 28f, private val minFontSize: Float = 12f) : JLabel(text) {

    init {
        font = MyFont.Bold(maxFontSize) // 초기 글자 크기 설정
        SwingUtilities.invokeLater { adjustFontSizeToFit() } // 초기화 후 글자 크기 조정
    }

    private fun adjustFontSizeToFit() {
        var currentFontSize = maxFontSize
        var fm: FontMetrics

        // 텍스트가 JLabel 너비보다 작아질 때까지 글자 크기 줄임
        while (currentFontSize >= minFontSize) {
            font = MyFont.Bold(currentFontSize)
            fm = this.getFontMetrics(font)  // 컴포넌트의 폰트 메트릭스 가져오기

            val textWidth = fm.stringWidth(text)
            if (textWidth <= this.width) {
                // 텍스트 너비가 JLabel의 너비에 맞으면 루프 종료
                break
            }

            // 글자 크기 줄임
            currentFontSize -= 1f
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        SwingUtilities.invokeLater { adjustFontSizeToFit() }  // 컴포넌트 크기 변경 시 글자 크기 재조정
    }
}
