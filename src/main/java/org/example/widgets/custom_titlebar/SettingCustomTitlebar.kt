package org.example.widgets.custom_titlebar

import org.example.MainForm
import org.example.MyFont
import org.example.style.MyColor
import org.example.widgets.IconRoundBorder
import java.awt.*
import javax.swing.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class SettingCustomTitlebar(private val parentFrame: JFrame) : JPanel() {

    private var mouseX = 0
    private var mouseY = 0
    private var isMaximized = false

    init {

        // 타이틀바 디자인
        layout = BorderLayout()
        background = MyColor.LOGIN_TITLEBAR
        preferredSize = Dimension(parentFrame.width, 70)

        // 왼쪽 버튼 패널 생성
        val leftButtonPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.LEFT, 10, 15)  // FlowLayout으로 버튼을 왼쪽 정렬
            background = MyColor.LOGIN_TITLEBAR
        }

        // 아이콘이 적용된 버튼들
        val backButtonPanel = IconRoundBorder.createRoundedButton("/back_icon.png").apply {
            preferredSize = Dimension(100, 40)  // 버튼 크기 설정
        }

        // '이전' 버튼 클릭 시 MainForm으로 이동
        backButtonPanel.addActionListener {
            // MainForm 인스턴스 생성 및 표시
            val mainForm = MainForm()
            mainForm.isVisible = true
            // 현재 창 닫기 (parentFrame이 SettingCustomTitlebar에 전달된 JFrame을 의미)
            parentFrame.dispose()
        }

        leftButtonPanel.add(backButtonPanel)


        // 타이틀 텍스트 추가
        val titleLabel = JLabel("POS 꼬르륵 설정").apply {
            foreground = Color.WHITE
            font = MyFont.Bold(30f)
            horizontalAlignment = SwingConstants.CENTER
        }
        add(titleLabel, BorderLayout.WEST)

        // 아이콘이 적용된 버튼들
        val minimizeButton = IconRoundBorder.createRoundedButton("/minimize_icon.png").apply {
            preferredSize = Dimension(40, 40)  // 버튼 크기 설정
        }
        val maximizeButton = IconRoundBorder.createRoundedButton("/maximize_icon.png").apply {
            preferredSize = Dimension(40, 40)  // 버튼 크기 설정
        }
        val closeButton = IconRoundBorder.createRoundedButton("/close_icon.png").apply {
            preferredSize = Dimension(40, 40)  // 버튼 크기 설정
        }

        // 오른쪽 버튼 패널
        val buttonPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.RIGHT, 10, 15)
            background = MyColor.LOGIN_TITLEBAR
        }

        // 최소화 버튼 이벤트
        minimizeButton.addActionListener {
            parentFrame.extendedState = JFrame.ICONIFIED
        }

        // 최대화 버튼 이벤트
        maximizeButton.addActionListener {
            if (isMaximized) {
                parentFrame.extendedState = JFrame.NORMAL
                isMaximized = false
            } else {
                parentFrame.extendedState = JFrame.MAXIMIZED_BOTH
                isMaximized = true
            }
        }

        // 닫기 버튼 이벤트
        closeButton.addActionListener {
            parentFrame.dispose()
        }

        // 버튼 패널에 추가
        buttonPanel.add(minimizeButton)
        buttonPanel.add(maximizeButton)
        buttonPanel.add(closeButton)

        add(leftButtonPanel, BorderLayout.WEST)
        add(titleLabel, BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.EAST)


        // 창 이동을 위한 마우스 리스너
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                mouseX = e.x
                mouseY = e.y
            }
        })

        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                parentFrame.setLocation(e.xOnScreen - mouseX, e.yOnScreen - mouseY)
            }
        })
    }
}