package org.example.screen.login

import org.example.MainForm
import org.example.MyFont
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
    private val helpLabel: JLabel // 고객센터 정보 라벨

    // 색상 정의
    private val MyColor_orange = Color(255, 153, 51) // 오렌지 색상
    private val MyColor_dark_grey = Color(86, 86, 86) // 어두운 회색

    init { // 초기화 블록
        // 기존 타이틀바 제거
        isUndecorated = true

        // JFrame의 레이아웃을 명시적으로 BorderLayout으로 설정
        layout = BorderLayout()

        // 커스텀 타이틀바 추가
        val customTitleBar = CustomTitleBar(this)
        add(customTitleBar, BorderLayout.NORTH)  // 타이틀바를 명확하게 NORTH에 추가

        mainPanel = JPanel()
        idField = TextField("가맹점 웹 아이디", Color(220, 220, 220))
        passwordField = PasswordField("가맹점 웹 비밀번호", Color(220, 220, 220))
        autoLoginCheckBox = JCheckBoxCustom()
        loginButton = JButton("로그인")
        titleLabel = JLabel("사장님 사이트 계정으로 로그인해주세요")
        findInfoLabel = JLabel("아이디 / 비밀번호 찾기")
        helpLabel = JLabel("고객센터 콜센터 1600 - 1234")

        initializeUI()
        add(mainPanel, BorderLayout.CENTER) // 메인 패널을 CENTER에 명확하게 추가
        setSize(800, 600)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null) // 화면 중앙에 배치
    }

    private fun initializeUI() {
        mainPanel.background = Color(220, 220, 220)
        mainPanel.layout = GridBagLayout()

        val loginPanel = JPanel()
        loginPanel.preferredSize = Dimension(300, 300)
        loginPanel.background = Color(220, 220, 220)
        loginPanel.layout = GridBagLayout()
        val gbc = GridBagConstraints().apply {
            insets = Insets(10, 10, 10, 10)
            fill = GridBagConstraints.HORIZONTAL
        }

        titleLabel.font = MyFont.Bold(16f)
        titleLabel.horizontalAlignment = SwingConstants.CENTER
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridwidth = 2
        loginPanel.add(titleLabel, gbc)

        idField.font = MyFont.Regular(14f)
        gbc.gridy = 1
        gbc.gridwidth = 2
        loginPanel.add(idField, gbc)

        passwordField.font = MyFont.Regular(14f)
        gbc.gridy = 2
        gbc.gridwidth = 2
        loginPanel.add(passwordField, gbc)

        autoLoginCheckBox.background = MyColor_orange
        autoLoginCheckBox.text = "자동 로그인"
        autoLoginCheckBox.font = MyFont.Regular(11f)
        gbc.gridy = 3
        gbc.gridx = 0
        gbc.gridwidth = 1
        gbc.anchor = GridBagConstraints.WEST
        gbc.fill = GridBagConstraints.NONE
        loginPanel.add(autoLoginCheckBox, gbc)

        findInfoLabel.font = MyFont.Regular(11f)
        findInfoLabel.foreground = MyColor_dark_grey
        findInfoLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        gbc.gridx = 1
        gbc.weightx = 1.0
        gbc.anchor = GridBagConstraints.EAST
        gbc.fill = GridBagConstraints.NONE
        loginPanel.add(findInfoLabel, gbc)

        loginButton.font = MyFont.Medium(16f)
        loginButton.background = MyColor_orange
        loginButton.foreground = Color.WHITE
        gbc.gridx = 0
        gbc.gridy = 4
        gbc.gridwidth = 2
        gbc.weightx = 0.0
        gbc.fill = GridBagConstraints.HORIZONTAL
        loginPanel.add(loginButton, gbc)

        loginButton.addActionListener {
            val mainForm = MainForm()
            mainForm.isVisible = true
            this.dispose()
        }

        helpLabel.font = MyFont.Regular(12f)
        helpLabel.horizontalAlignment = SwingConstants.CENTER
        gbc.gridy = 5
        gbc.gridwidth = 2
        loginPanel.add(helpLabel, gbc)

        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridwidth = 1
        mainPanel.add(loginPanel, gbc)
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
