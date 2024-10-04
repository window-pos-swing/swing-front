
import javax.swing.*
import java.awt.*
import org.example.MyFont
import org.example.style.MyColor


class RoundedProgressBar(min: Int, max: Int) : JPanel() {
    private val progressBar: JProgressBar

    init {
        layout = GridBagLayout()  // 중앙 배치를 위해 GridBagLayout 사용
        isOpaque = false  // 패널을 투명하게 처리
        background = MyColor.DARK_NAVY // 둥근 테두리 패널의 배경색 설정
        preferredSize = Dimension(255, 155)  // 큰 패널 크기 설정
        border = BorderFactory.createEmptyBorder(15, 15, 15, 15)  // 패널 내 여백

        // GridBagConstraints 생성
        val gbc = GridBagConstraints().apply {
            gridx = 0
            fill = GridBagConstraints.NONE
            anchor = GridBagConstraints.CENTER  // 컴포넌트들을 중앙에 배치
            insets = Insets(5, 0, 15, 0)  // 위젯 사이에 여백을 추가
        }

        // 텍스트 (n분)
        val timeLabel = JLabel("0분").apply {
            foreground = Color.WHITE  // 텍스트 색상
            font = MyFont.Bold(34f)  // 텍스트 폰트 설정
            horizontalAlignment = SwingConstants.CENTER  // 텍스트를 가운데 정렬
        }

        // 가로형 네모난 프로그레스바 생성 (채워진 색상과 채워지지 않은 색상 설정)
        progressBar = object : JProgressBar(min, max) {
            override fun paintComponent(g: Graphics) {
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

                // 배경 박스 (흰색)
                g2.color = Color.WHITE
                g2.fillRoundRect(0, 0, width, height, 10, 10)  // 둥근 네모 박스

                // 프로그레스바 내부 (채워진 부분은 핑크, 채워지지 않은 부분은 흰색)
                val fillWidth = (width * percentComplete).toInt()
                g2.color = MyColor.PINK  // 채워진 부분 (핑크색)
                g2.fillRect(0, 0, fillWidth, height)

                g2.color = Color.WHITE // 채워지지 않은 부분 (흰색)
                g2.fillRect(fillWidth, 0, width - fillWidth, height)
            }
        }.apply {
            preferredSize = Dimension(214, 20)  // 프로그레스바 크기 설정
            maximumSize = Dimension(214, 20)
            isStringPainted = false  // 프로그레스바 위에 텍스트를 그리지 않음
        }

        // GridBagConstraints를 사용해 텍스트와 프로그레스바를 중앙에 배치
        add(timeLabel, gbc)  // 먼저 텍스트를 추가
        gbc.gridy = 1  // 두 번째 행으로 이동
        add(progressBar, gbc)  // 프로그레스바를 추가

    }

    // 프로그레스바 업데이트 함수
    fun updateProgress(elapsedTime: Int) {
        progressBar.value = elapsedTime
        (components[0] as JLabel).text = "${elapsedTime}분"  // 상단 텍스트 업데이트
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 둥근 패널 그리기 (배경)
        g2.color = background
        g2.fillRoundRect(0, 0, width, height, 30, 30)  // 둥근 테두리 패널 그리기
    }
}

