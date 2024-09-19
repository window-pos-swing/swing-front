package org.example.screen.login

import org.example.MainForm
import org.example.MyFont
import org.example.style.MyColor
import org.example.widgets.custom_titlebar.CustomTitleBar
import org.example.widgets.JCheckBoxCustom // 커스텀 체크박스 위젯 임포트
import org.example.widgets.PasswordField // 커스텀 패스워드 필드 위젯 임포트
import org.example.widgets.TextField // 커스텀 텍스트 필드 위젯 임포트

import javax.swing.* // 자바 스윙 라이브러리 임포트
import java.awt.* // 자바 AWT 라이브러리 임포트

class LoginForm : JFrame() { // JFrame을 상속받아 LoginForm 클래스 정의
    private val mainPanel: JPanel // 메인 패널
    private val idField: TextField // ID 입력 필드
    private val passwordField: PasswordField // 비밀번호 입력 필드
    private val autoLoginCheckBox: JCheckBox // 자동 로그인 체크박스
    private val loginButton: JButton // 로그인 버튼
    private val titleLabel: JLabel // 제목 라벨
    private val findInfoLabel: JLabel // 아이디/비밀번호 찾기 라벨
    private val logoLabel: JLabel // 로고 라벨 추가
    private val footerLabel: JLabel // Footer 라벨 추가

    init { // 초기화 블록
        // 기존 타이틀바 제거
        isUndecorated = true

        // JFrame의 레이아웃을 명시적으로 BorderLayout으로 설정
        layout = BorderLayout()

        // 커스텀 타이틀바 추가
        val customTitleBar = CustomTitleBar(this, false)
        add(customTitleBar, BorderLayout.NORTH)  // 타이틀바를 명확하게 NORTH에 추가

        mainPanel = JPanel()
        idField = TextField("가맹점 웹 아이디", Color.WHITE)
        passwordField = PasswordField("가맹점 웹 비밀번호", Color.WHITE)
        autoLoginCheckBox = JCheckBoxCustom()
        loginButton = JButton("로그인")

// 폰트 로드 실패에 대한 예외 처리
        val customFont = try {
            MyFont.SCDreamBold(48f)
        } catch (e: Exception) {
            println("폰트를 로드할 수 없습니다: ${e.message}")
            MyFont.Regular(48f) // 폰트 로드 실패 시 기본 폰트로 대체
        }

// 폰트 이름을 가져오고, null 처리
        val fontFamily = customFont?.fontName ?: "Default"

        titleLabel = JLabel("""
    <html>
        <table>
            <tr>
                <td style='font-size:36px;'>“<span style='color:#D10C1D;'>사장님 사이트 계정</span>으로</td>
            </tr>
            <tr>
                <td style='font-size:36px; padding-left:20px;'><span style='color:#D10C1D;'>로그인</span> 해주세요”</td>
            </tr>
        </table>
    </html>
""".trimIndent())

// JLabel 자체에 폰트 적용
        titleLabel.font = customFont ?: MyFont.Regular(40f)

        // 로고 이미지 설정
        val logoIcon = ImageIcon(javaClass.getResource("/logo.png").getPath())
        val resizedIcon = ImageIcon(logoIcon.image.getScaledInstance(100, 100, Image.SCALE_SMOOTH))
        logoLabel = JLabel(resizedIcon)
        findInfoLabel = JLabel("아이디 / 비밀번호 찾기")

        footerLabel = JLabel("꼬르륵 콜센터 1600 - 1234")
        footerLabel.font = MyFont.Regular(20f)
        footerLabel.foreground = Color.WHITE

        initializeUI()
        add(mainPanel, BorderLayout.CENTER) // 메인 패널을 CENTER에 명확하게 추가

        // 하단 패널 추가
        addFooterPanel()

        setSize(1440, 1024)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null) // 화면 중앙에 배치
    }

    private fun initializeUI() {
        mainPanel.background = MyColor.LOGIN_BACKGROUND
        mainPanel.layout = GridBagLayout()

        // 로고 패널 생성 (로고만 담음)
        val logoPanel = JPanel()
        logoPanel.layout = BorderLayout()
        logoPanel.background = MyColor.LOGIN_BACKGROUND

        // 로고 설정
        val logoIcon = ImageIcon(javaClass.getResource("/logo.png").getPath())
        val resizedIcon = ImageIcon(logoIcon.image.getScaledInstance(100, 100, Image.SCALE_SMOOTH))
        val logoLabel = JLabel(resizedIcon)
        logoLabel.horizontalAlignment = JLabel.LEFT // 왼쪽 정렬
        logoPanel.add(logoLabel, BorderLayout.WEST) // 로고를 패널의 왼쪽에 배치

        // 로고 패널을 메인 패널에 추가 (맨 위)
        val logoGbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            gridwidth = 2
            anchor = GridBagConstraints.NORTHWEST // 왼쪽 위에 정렬
            insets = Insets(-60, -38, 0, 0) // 여백 설정
        }
        mainPanel.add(logoPanel, logoGbc)


        // 로그인 패널 설정 (기존 코드 유지)
        val logoAndLoginPanel = JPanel()
        logoAndLoginPanel.layout = BoxLayout(logoAndLoginPanel, BoxLayout.Y_AXIS)
        logoAndLoginPanel.background = MyColor.LOGIN_BACKGROUND

        // 로그인 패널 추가
        val loginPanel = JPanel()
        loginPanel.preferredSize = Dimension(650, 650)
        loginPanel.background = MyColor.LOGIN_BACKGROUND
        loginPanel.layout = GridBagLayout()
        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
        }
        loginPanel.border = BorderFactory.createLineBorder(MyColor.DARK_RED, 5)

        // titleLabel 설정
        titleLabel.font = MyFont.SCDreamBold(40f)
        titleLabel.horizontalAlignment = SwingConstants.LEFT
        titleLabel.foreground = Color.WHITE
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridwidth = 2
        gbc.insets = Insets(70, 60, 30, 60)  // 위쪽 마진 70, 아래쪽 마진 30
        gbc.anchor = GridBagConstraints.CENTER
        loginPanel.add(titleLabel, gbc)

        // idField 설정
        idField.font = MyFont.Regular(14f)
        idField.preferredSize = Dimension(521, 70)
        idField.minimumSize = Dimension(521, 70)
        idField.maximumSize = Dimension(521, 70)
        gbc.gridy = 1
        gbc.gridwidth = 2
        gbc.insets = Insets(0, 60, 30, 60)  // 아래쪽 마진 20
        gbc.anchor = GridBagConstraints.CENTER
        loginPanel.add(idField, gbc)

        // passwordField 설정
        passwordField.font = MyFont.Regular(14f)
        passwordField.preferredSize = Dimension(521, 70)
        passwordField.minimumSize = Dimension(521, 70)
        passwordField.maximumSize = Dimension(521, 70)
        gbc.gridy = 2
        gbc.gridwidth = 2
        gbc.insets = Insets(0, 60, 20, 60)  // 아래쪽 마진 20
        gbc.anchor = GridBagConstraints.CENTER
        loginPanel.add(passwordField, gbc)

        // autoLoginCheckBox와 findInfoLabel 설정
        autoLoginCheckBox.background = MyColor.DARK_RED
        autoLoginCheckBox.text = "자동 로그인"
        autoLoginCheckBox.foreground = Color.WHITE
        autoLoginCheckBox.font = MyFont.Regular(18f)
        gbc.gridy = 3
        gbc.gridx = 0
        gbc.gridwidth = 1
        gbc.anchor = GridBagConstraints.WEST
        gbc.insets = Insets(0, 60, 20, 10)  // 아래쪽 마진 20
        gbc.fill = GridBagConstraints.NONE
        loginPanel.add(autoLoginCheckBox, gbc)

        findInfoLabel.font = MyFont.Regular(18f)
        findInfoLabel.foreground = Color.WHITE
        findInfoLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        gbc.gridx = 1
        gbc.weightx = 1.0
        gbc.anchor = GridBagConstraints.EAST
        gbc.insets = Insets(0, 10, 20, 60)  // 아래쪽 마진 20
        gbc.fill = GridBagConstraints.NONE
        loginPanel.add(findInfoLabel, gbc)

        // loginButton 설정
        loginButton.font = MyFont.Bold(22f)
        loginButton.background = MyColor.DARK_RED
        loginButton.foreground = Color.WHITE
        loginButton.preferredSize = Dimension(521, 70)
        loginButton.minimumSize = Dimension(521, 70)
        loginButton.maximumSize = Dimension(521, 70)
        loginButton.border = BorderFactory.createLineBorder(MyColor.DARK_RED, 1)  // 테두리 설정
        gbc.gridx = 0
        gbc.gridy = 4
        gbc.gridwidth = 2
        gbc.weightx = 0.0
        gbc.insets = Insets(20, 60, 70, 60)  // 아래쪽 마진 70
        gbc.fill = GridBagConstraints.HORIZONTAL
        loginPanel.add(loginButton, gbc)

        // 로그인 패널을 서브 패널에 추가
        logoAndLoginPanel.add(loginPanel)

        // 메인 패널에 서브 패널 추가 (로고와 로그인 패널)
        val mainGbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 1 // 로그인 패널은 두 번째로 배치
            insets = Insets(0, 0, 0, 0) // 여백 없도록 설정
        }
        mainPanel.add(logoAndLoginPanel, mainGbc)

        // 로그인 버튼 리스너
        loginButton.addActionListener {
            val mainForm = MainForm()
            mainForm.isVisible = true
            this.dispose()
        }
    }

    private fun addFooterPanel() {
        // 하단 패널 생성
        val footerPanel = JPanel()
        footerPanel.layout = BorderLayout()
        footerPanel.background = MyColor.LOGIN_BACKGROUND

        // 하단 라벨을 우측 정렬
        footerLabel.horizontalAlignment = SwingConstants.RIGHT
        footerLabel.border = BorderFactory.createEmptyBorder(0, 0, 20, 20) // 아래와 오른쪽 여백 20 추가
        footerPanel.add(footerLabel, BorderLayout.EAST)

        // 하단 패널을 메인 프레임에 추가
        add(footerPanel, BorderLayout.SOUTH)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName())
            } catch (e: Exception) {
                e.printStackTrace()
            }

            SwingUtilities.invokeLater {
                val frame = LoginForm()
                frame.isVisible = true
            }
        }
    }
}
