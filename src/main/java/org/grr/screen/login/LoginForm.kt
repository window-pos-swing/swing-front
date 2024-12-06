package org.grr.screen.login

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.grr.api.LoginToServer
import org.grr.`object`.Storage
import org.grr.screen.main.MainForm
import org.grr.style.MyColor
import org.grr.util.LoadImage.loadImage
import org.grr.util.MyFont
import org.grr.widgets.JCheckBoxCustom
import org.grr.widgets.PasswordField
import org.grr.widgets.TextField
import org.grr.widgets.custom_titlebar.LoginCustomTitleBar
import java.awt.*
import javax.swing.*

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

        // 저장된 로그인 정보 확인
        val (savedEmail, savedPassword, autoCheck) = Storage.getLoginInfo()
        if (autoCheck) {
            // 저장된 아이디를 입력란에 표시
            idField = TextField(savedEmail, Color.WHITE)

            // 비밀번호 입력란에 비밀번호 길이만큼 * 표시
            passwordField = PasswordField("*".repeat(savedPassword!!.length), Color.WHITE)

//            자동로그인 체크란
            autoLoginCheckBox = JCheckBoxCustom().apply {
                isSelected = autoCheck
            }
            GlobalScope.launch {
                println("자동 로그인 시도 중...")

                delay(1000) // 1초 대기

                val loginToServer = LoginToServer()
                val (isSuccess, message) = loginToServer.loginToServer(savedEmail!!, savedPassword!!)

                delay(1000) // 1초 대기

                SwingUtilities.invokeLater {
                    if (isSuccess) {
                        // 새로운 토큰 저장
                        Storage.saveToken(message)

                        // 메인 화면으로 이동
                        val mainForm = MainForm()
                        mainForm.isVisible = true
                        this@LoginForm.dispose() // 로그인 창 닫기
                    } else {
                        // 로그인 실패 시 기본 로그인 화면 표시
                        JOptionPane.showMessageDialog(this@LoginForm, message, "오류", JOptionPane.ERROR_MESSAGE)
                    }
                }
            }
        } else {
            idField = TextField("가맹점 웹 아이디", Color.WHITE)
            passwordField = PasswordField("가맹점 웹 비밀번호", Color.WHITE)
            autoLoginCheckBox = JCheckBoxCustom()
        }

        // 커스텀 타이틀바 추가
        val loginCustomTitleBar = LoginCustomTitleBar(this)
        add(loginCustomTitleBar, BorderLayout.NORTH)  // 타이틀바를 명확하게 NORTH에 추가

        mainPanel = JPanel()
        loginButton = JButton("로그인")

        val customFont = MyFont.ExtraBold(48f)
        val fontFamily = customFont.fontName
        titleLabel = JLabel(
            """
    <html>
        <table>
            <tr>
                <td style='font-family:$fontFamily; font-size:36px;'>“<span style='color:#D10C1D;'>사장님 사이트 계정</span>으로</td>
            </tr>
            <tr>
                <td style='font-family:$fontFamily; font-size:36px; padding-left:20px;'><span style='color:#D10C1D;'>로그인</span> 해주세요”</td>
            </tr>
        </table>
    </html>
""".trimIndent()
        )
//        로그인버튼 클릭 시 로그인 api 작동시키는 구문
        loginButton.addActionListener {
            val email = idField.text
            val password = String(passwordField.password)

            if (email.isBlank() || password.isBlank()) {
                JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE)
            } else {
                val loginToServer = LoginToServer() // LoginToServer 인스턴스 생성
                val (isSuccess, message) = loginToServer.loginToServer(email, password)

                if (isSuccess) {
                    val autoLoginCheck = autoLoginCheckBox.isSelected
                    Storage.saveLoginInfo(email, password, autoLoginCheck)

//                    로그인 시 토큰 저장 후 메인페이지 이동
                    Storage.saveToken(message)
                    val mainForm = MainForm()
                    mainForm.isVisible = true
                    this.dispose()
                } else {
//                    실패시 즉, this가 false일 경우
                    JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE)
                }
            }
        }

// 로고 이미지 설정
        val logoIcon = loadImage("/Logo.png", 100, 100) // org.grr.util.LoadImage 함수 사용
        logoLabel = JLabel(logoIcon)
        findInfoLabel = JLabel("아이디/비밀번호 찾기")

        footerLabel = JLabel("꼬르륵 콜센터 1600 - 1234")
        footerLabel.font = MyFont.Medium(20f)
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
        mainPanel.background = MyColor.DARK_NAVY
        mainPanel.layout = GridBagLayout()

        // 로고 패널 생성 (로고만 담음)
        val logoPanel = JPanel()
        logoPanel.layout = BorderLayout()
        logoPanel.background = MyColor.DARK_NAVY

        // 로고 설정
        val logoIcon = loadImage("/Logo.png", 100, 100) // org.grr.util.LoadImage 함수 사용
        val logoLabel = JLabel(logoIcon)
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
        logoAndLoginPanel.background = MyColor.DARK_NAVY

        // 로그인 패널 추가
        val loginPanel = JPanel()
        loginPanel.preferredSize = Dimension(650, 650)
        loginPanel.background = MyColor.DARK_NAVY
        loginPanel.layout = GridBagLayout()
        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
        }
        loginPanel.border = BorderFactory.createLineBorder(MyColor.DARK_RED, 5)

        // titleLabel 설정
        titleLabel.font = MyFont.ExtraBold(40f)
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
//        loginButton.addActionListener {
//            val mainForm = MainForm()
//            mainForm.isVisible = true
//            this.dispose()
//        }
    }

    private fun addFooterPanel() {
        // 하단 패널 생성
        val footerPanel = JPanel()
        footerPanel.layout = BorderLayout()
        footerPanel.background = MyColor.DARK_NAVY

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
