import javax.swing.* // 스윙 라이브러리 임포트
import java.awt.* // AWT 라이브러리 임포트
import java.io.IOException
import javax.imageio.ImageIO

class LogoTestForm : JFrame() {

    init {
        // 창 크기 설정
        setSize(400, 400)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null) // 화면 중앙에 배치

        // 메인 패널 설정
        val mainPanel = JPanel()
        mainPanel.layout = BorderLayout()

        // 로고 이미지 설정
        val logoIcon = try {
            val imagePath = "/Logo.png"
            val logoImage = javaClass.getResourceAsStream(imagePath)?.let {
                ImageIcon(ImageIO.read(it)).image.getScaledInstance(200, 200, Image.SCALE_SMOOTH) // 크기를 적절히 조정
            }
            if (logoImage != null) {
                println("로고 이미지를 성공적으로 로드했습니다: $imagePath")
                ImageIcon(logoImage)
            } else {
                throw IOException("로고 이미지를 찾을 수 없습니다: $imagePath")
            }
        } catch (e: Exception) {
            println("로고 로드 실패: ${e.message}")
            e.printStackTrace()
            // 로고 로드 실패 시 기본 또는 대체 이미지 설정
            ImageIcon(javaClass.getResource("/fallback_logo.png").getPath()) // 대체 이미지 경로
        }

        // 로고 라벨 생성
        val logoLabel = JLabel(logoIcon)
        logoLabel.horizontalAlignment = SwingConstants.CENTER // 중앙 정렬
        logoLabel.verticalAlignment = SwingConstants.CENTER // 세로도 중앙 정렬

        // 로고 라벨을 메인 패널에 추가
        mainPanel.add(logoLabel, BorderLayout.CENTER)

        // 메인 패널을 프레임에 추가
        add(mainPanel)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SwingUtilities.invokeLater {
                val frame = LogoTestForm()
                frame.isVisible = true
            }
        }
    }
}
