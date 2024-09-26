package org.example

import org.example.widgets.custom_titlebar.MainCustomTitlebar
import java.awt.*
import javax.swing.*


class MainForm : JFrame() {
    private val cardPanel = JPanel(CardLayout())  // 카드 패널 생성
    private val tabbedPane = CustomTabbedPane(this)  // CustomTabbedPane 생성

    init {
        // 기존 타이틀바 제거 및 창 리사이즈 가능 설정
        isUndecorated = true
        isResizable = true  // 창 리사이즈 가능

        // JFrame의 여백 제거
        rootPane.border = BorderFactory.createEmptyBorder()

        // JFrame의 레이아웃을 BorderLayout으로 설정
        layout = BorderLayout()

        // 좌측에 CustomTabbedPane 추가 (탭바)
        tabbedPane.preferredSize = Dimension(200, height)
        tabbedPane.border = BorderFactory.createEmptyBorder(0, 0, 0, 0)  // 여백 제거
        add(tabbedPane, BorderLayout.WEST)

        // 타이틀바와 콘텐츠를 구분하는 패널 생성
        val titleAndContentPanel = JPanel().apply {
            layout = BorderLayout()
            border = BorderFactory.createEmptyBorder(0, 0, 0, 0)  // 여백 제거
        }

        // 상단에 MainCustomTitlebar 추가
        val customTitleBar = MainCustomTitlebar(this)
        titleAndContentPanel.add(customTitleBar, BorderLayout.NORTH)

        // 중앙에 cardPanel 추가
        cardPanel.background = Color(245, 245, 245)  // 기본 배경색 설정
        cardPanel.border = BorderFactory.createEmptyBorder(0, 0, 0, 0)  // 여백 제거
        titleAndContentPanel.add(cardPanel, BorderLayout.CENTER)

        // 타이틀바와 cardPanel을 담은 패널을 우측에 배치
        add(titleAndContentPanel, BorderLayout.CENTER)

        // CustomTabbedPane에 cardPanel 전달
        tabbedPane.setCardPanel(cardPanel)  // **중요**: 이 부분을 가장 마지막에 호출하여 초기화 순서 문제 해결

        // 기본 창 설정
        setSize(1440, 1024)
        setLocationRelativeTo(null)  // 화면 중앙에 배치
        defaultCloseOperation = EXIT_ON_CLOSE
    }
}

fun main() {
    val mainForm = MainForm()
    mainForm.isVisible = true
}