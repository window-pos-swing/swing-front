import javazoom.jl.player.Player
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import java.awt.*
import java.io.FileInputStream
import java.io.InputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.FloatControl
import javax.sound.sampled.Port

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

                // 트랙의 볼륨 설정 부분 색상
                val filledWidth = (width - 20) * (value.toDouble() / maximum)  // 볼륨에 따른 트랙의 길이
                g2.color = Color(0, 255, 255)  // 설정된 볼륨의 트랙 색상
                g2.fillRoundRect(10, height / 2 - 5, filledWidth.toInt(), 10, 10, 10)

                // 트랙의 비설정 부분 색상
                g2.color = Color(255, 255, 255)  // 비설정된 볼륨의 트랙 색상
                g2.fillRoundRect(10 + filledWidth.toInt(), height / 2 - 5, (width - 20) - filledWidth.toInt(), 10, 10, 10)

                // 썸 색상
                g2.color = Color(0, 122, 255)  // 파란색
                val thumbX = 10 + ((width - 20) * (value.toDouble() / maximum)).toInt()
                g2.fillOval(thumbX - 10, height / 2 - 10, 20, 20)
            }
        }.apply {
            preferredSize = Dimension(300, 40)  // 슬라이더 크기 설정
//            background = Color(33, 43, 54)  // 배경색
            isOpaque = false  // 배경을 투명하게 설정

            // 볼륨 변경 시 이벤트 처리
            addChangeListener(object : ChangeListener {
                override fun stateChanged(e: ChangeEvent) {
                    val slider = e.source as JSlider
                    if (!slider.valueIsAdjusting) {
                        val volume = slider.value.toFloat() / 100 // 볼륨 값 계산
                        setSystemVolume(volume)  // 시스템 볼륨 설정
                        playMp3Sound("/sound_effect.mp3")  // MP3 파일 재생
                        println("볼륨: $volume")
                    }
                }
            })
        }

        // 컴포넌트들을 패널에 추가
        add(volumeSlider)
    }

    // MP3 파일을 재생하는 함수
    private fun playMp3Sound(resourcePath: String) {
        try {
            val inputStream: InputStream? = javaClass.getResourceAsStream(resourcePath)
            if (inputStream != null) {
                val player = Player(inputStream)
                Thread {
                    player.play(500)  // 0.5초 정도만 재생
                }.start()
            } else {
                println("파일을 찾을 수 없습니다: $resourcePath")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 시스템 볼륨을 설정하는 함수
    private fun setSystemVolume(volume: Float) {
        try {
            // 오디오 시스템에서 마스터 볼륨 컨트롤 가져오기
            val line = AudioSystem.getLine(Port.Info.SPEAKER) as Port
            line.open()

            // 볼륨 조절 가능한지 확인
            if (line.isControlSupported(FloatControl.Type.VOLUME)) {
                val volumeControl = line.getControl(FloatControl.Type.VOLUME) as FloatControl
                volumeControl.value = volume  // 볼륨 설정
            }
            line.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
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
