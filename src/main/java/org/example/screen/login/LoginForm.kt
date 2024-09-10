package org.example.screen.login

import org.example.MainForm
import org.example.MyFont
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
        mainPanel = JPanel() // 메인 패널 생성
        idField = TextField("가맹점 웹 아이디", Color(220, 220, 220)) // ID 입력 필드 생성 및 설정
        passwordField = PasswordField("가맹점 웹 비밀번호", Color(220, 220, 220)) // 비밀번호 입력 필드 생성 및 설정
        autoLoginCheckBox = JCheckBoxCustom() // 커스텀 체크박스 생성
        loginButton = JButton("로그인") // 로그인 버튼 생성
        titleLabel = JLabel("사장님 사이트 계정으로 로그인해주세요") // 제목 라벨 생성
        findInfoLabel = JLabel("아이디 / 비밀번호 찾기") // 아이디/비밀번호 찾기 라벨 생성
        helpLabel = JLabel("고객센터 콜센터 1600 - 1234") // 고객센터 정보 라벨 생성

        initializeUI() // UI 초기화 메서드 호출
        contentPane = mainPanel // 메인 패널을 컨텐츠 팬으로 설정
        title = "로그인" // 창 제목 설정
        setSize(800, 600) // 창 크기 설정
        defaultCloseOperation = EXIT_ON_CLOSE // 닫기 버튼 클릭 시 프로그램 종료 설정
        setLocationRelativeTo(null) // 창을 화면 중앙에 위치하도록 설정
    }

    private fun initializeUI() {
        // 메인 패널 설정
        mainPanel.background = Color(220, 220, 220) // 메인 패널 배경색 설정
        mainPanel.layout = GridBagLayout() // 메인 패널 레이아웃을 GridBagLayout으로 설정

        // 로그인 패널 생성 및 설정
        val loginPanel = JPanel() // 새로운 패널 생성
        loginPanel.preferredSize = Dimension(300, 300) // 패널의 선호 크기 설정
        loginPanel.background = Color(220, 220, 220) // 패널 배경색 설정
        loginPanel.layout = GridBagLayout() // 패널 레이아웃을 GridBagLayout으로 설정
        val gbc = GridBagConstraints().apply { // GridBagConstraints 객체 생성 및 설정
            insets = Insets(10, 10, 10, 10) // 컴포넌트 여백 설정
            fill = GridBagConstraints.HORIZONTAL // 컴포넌트가 수평으로 확장되도록 설정
        }

        // 제목 라벨 설정
        titleLabel.font = MyFont.Bold(16f) // 제목 라벨 폰트 설정
        titleLabel.horizontalAlignment = SwingConstants.CENTER // 라벨 수평 정렬을 중앙으로 설정
        gbc.gridx = 0 // 그리드의 첫 번째 열에 배치
        gbc.gridy = 0 // 그리드의 첫 번째 행에 배치
        gbc.gridwidth = 2 // 두 개의 열을 차지하도록 설정
        loginPanel.add(titleLabel, gbc) // 로그인 패널에 제목 라벨 추가

        // ID 입력 필드 설정
        idField.font = MyFont.Regular(14f) // ID 입력 필드 폰트 설정
        gbc.gridy = 1 // 그리드의 두 번째 행에 배치
        gbc.gridwidth = 2 // 두 개의 열을 차지하도록 설정
        loginPanel.add(idField, gbc) // 로그인 패널에 ID 입력 필드 추가

        // 비밀번호 입력 필드 설정
        passwordField.font = MyFont.Regular(14f) // 비밀번호 입력 필드 폰트 설정
        gbc.gridy = 2 // 그리드의 세 번째 행에 배치
        gbc.gridwidth = 2 // 두 개의 열을 차지하도록 설정
        loginPanel.add(passwordField, gbc) // 로그인 패널에 비밀번호 입력 필드 추가

        // 자동 로그인 체크박스 설정
        autoLoginCheckBox.background = MyColor_orange // 체크박스 배경색 설정
        autoLoginCheckBox.text = "자동 로그인" // 체크박스 텍스트 설정
        autoLoginCheckBox.font = MyFont.Regular(11f) // 체크박스 폰트 설정
        gbc.gridy = 3 // 그리드의 네 번째 행에 배치
        gbc.gridx = 0 // 그리드의 첫 번째 열에 배치
        gbc.gridwidth = 1 // 한 개의 열만 차지하도록 설정
        gbc.anchor = GridBagConstraints.WEST // 왼쪽 정렬
        gbc.fill = GridBagConstraints.NONE // 컴포넌트 크기 고정
        loginPanel.add(autoLoginCheckBox, gbc) // 로그인 패널에 자동 로그인 체크박스 추가

        // 아이디/비밀번호 찾기 라벨 설정
        findInfoLabel.font = MyFont.Regular(11f) // 라벨 폰트 설정
        findInfoLabel.foreground = MyColor_dark_grey // 라벨 텍스트 색상 설정
        findInfoLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) // 마우스 커서를 손가락 모양으로 설정
        gbc.gridx = 1 // 그리드의 두 번째 열에 배치
        gbc.weightx = 1.0 // 남은 공간을 차지하도록 설정
        gbc.anchor = GridBagConstraints.EAST // 오른쪽 정렬
        gbc.fill = GridBagConstraints.NONE // 컴포넌트 크기 고정
        loginPanel.add(findInfoLabel, gbc) // 로그인 패널에 아이디/비밀번호 찾기 라벨 추가

        // 로그인 버튼 설정
        loginButton.font = MyFont.Medium(16f) // 버튼 폰트 설정
        loginButton.background = MyColor_orange // 버튼 배경색 설정
        loginButton.foreground = Color.WHITE // 버튼 텍스트 색상 설정
        gbc.gridx = 0 // 그리드의 첫 번째 열에 배치
        gbc.gridy = 4 // 그리드의 다섯 번째 행에 배치
        gbc.gridwidth = 2 // 두 개의 열을 차지하도록 설정
        gbc.weightx = 0.0 // 남은 공간 차지 설정 초기화
        gbc.fill = GridBagConstraints.HORIZONTAL // 버튼 너비 확장
        loginPanel.add(loginButton, gbc) // 로그인 패널에 로그인 버튼 추가

        // 로그인 버튼 클릭 시 MainForm으로 이동
        loginButton.addActionListener {
            val mainForm = MainForm() // MainForm 인스턴스 생성
            mainForm.isVisible = true // MainForm을 화면에 표시
            this.dispose() // 현재 LoginForm 닫기
        }

        // 고객센터 정보 라벨 설정
        helpLabel.font = MyFont.Regular(12f) // 라벨 폰트 설정
        helpLabel.horizontalAlignment = SwingConstants.CENTER // 라벨 수평 정렬을 중앙으로 설정
        gbc.gridy = 5 // 그리드의 여섯 번째 행에 배치
        gbc.gridwidth = 2 // 두 개의 열을 차지하도록 설정
        loginPanel.add(helpLabel, gbc) // 로그인 패널에 고객센터 정보 라벨 추가

        // 메인 패널에 로그인 패널 추가
        gbc.gridx = 0 // 그리드의 첫 번째 열에 배치
        gbc.gridy = 0 // 그리드의 첫 번째 행에 배치
        gbc.gridwidth = 1 // 한 개의 열만 차지하도록 설정
        mainPanel.add(loginPanel, gbc) // 메인 패널에 로그인 패널 추가
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                // Look and Feel 설정을 시스템 기본으로 변경
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName())
            } catch (e: Exception) {
                e.printStackTrace() // 예외가 발생하면 스택 트레이스 출력
            }

            // 스윙 애플리케이션의 스레드를 시작하여 LoginForm을 생성 및 표시
            SwingUtilities.invokeLater {
                val frame = LoginForm() // LoginForm 인스턴스 생성
                frame.isVisible = true // LoginForm을 화면에 표시
            }
        }
    }
}
