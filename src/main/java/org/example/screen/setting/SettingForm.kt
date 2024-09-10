package org.example

import PrinterSettingDialog
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants

class SettingForm : JFrame() {

    init {
        // JFrame 기본 설정
        title = "설정 폼"  // 창 제목 설정
        setSize(1300, 800)  // 창 크기 설정 (너비: 1300, 높이: 800)
        defaultCloseOperation = EXIT_ON_CLOSE  // 창을 닫을 때 프로그램 종료 설정
        setLocationRelativeTo(null)  // 창을 화면 중앙에 위치시키도록 설정

        // JLabel 생성 및 설정
        val welcomeLabel = JLabel("환영합니다!", SwingConstants.CENTER)  // "환영합니다!" 텍스트와 중앙 정렬로 라벨 생성
        welcomeLabel.font = MyFont.Regular(24f)  // MyFont에서 Regular 폰트를 크기 24로 설정
        add(welcomeLabel)

        // 프린트 설정 버튼 추가
        val printSettingsButton = JButton("프린트 설정")  // 프린트 설정 버튼 생성
        printSettingsButton.font = MyFont.Regular(18f)  // 버튼 폰트 설정
        printSettingsButton.setBounds(550, 700, 200, 50)  // 버튼 위치 및 크기 설정
        add(printSettingsButton)

        // 버튼 클릭 이벤트 처리
        printSettingsButton.addActionListener {
            val printSettingModal = PrinterSettingDialog()  // PrintSettingModal 인스턴스 생성 및 표시
            printSettingModal.isVisible = true  // 모달 다이얼로그 표시
        }

        layout = null  // 절대 레이아웃 사용
    }
}

fun main() {
    val settingForm = SettingForm()  // SettingForm 인스턴스 생성
    settingForm.isVisible = true  // 창을 화면에 표시
}
