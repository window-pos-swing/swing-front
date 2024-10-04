import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import java.awt.*

class VolumeControlPanel : JPanel() {

    init {
        layout = FlowLayout(FlowLayout.LEFT)
        isOpaque = false

        // 슬라이더 추가
        val volumeSlider = object : JSlider(0, 100, 50) {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

                // 트랙 색상
                g2.color = Color(0, 255, 255)  // 밝은 파란색
                g2.fillRoundRect(10, height / 2 - 5, width - 20, 10, 10, 10)

                // 썸 색상
                g2.color = Color(0, 122, 255)  // 파란색
                val thumbX = 10 + ((width - 20) * (value.toDouble() / maximum)).toInt()
                g2.fillOval(thumbX - 10, height / 2 - 10, 20, 20)
            }
        }.apply {
            preferredSize = Dimension(330, 40)  // 슬라이더 크기 설정
//            background = Color(33, 43, 54)  // 배경색
            isOpaque = false  // 배경을 투명하게 설정

            // 볼륨 변경 시 이벤트 처리
            addChangeListener(object : ChangeListener {
                override fun stateChanged(e: ChangeEvent) {
                    val slider = e.source as JSlider
                    if (!slider.valueIsAdjusting) {
                        val volume = slider.value
                        // 볼륨 값을 처리하는 로직 추가
                        println("볼륨: $volume")
                    }
                }
            })
        }

        // 컴포넌트들을 패널에 추가
        add(volumeSlider)
    }
}

fun main() {
    SwingUtilities.invokeLater {
        val frame = JFrame("볼륨 조절")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.contentPane.add(VolumeControlPanel())
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }
}
