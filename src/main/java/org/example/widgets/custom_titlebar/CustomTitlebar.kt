package org.example.widgets.custom_titlebar

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class CustomTitleBar(private val parentFrame: JFrame) : JPanel() {

    private var mouseX = 0
    private var mouseY = 0
    private var isMaximized = false
    private var isDragging = false

    init {
        layout = BorderLayout()
        background = Color(255, 255, 255)
        preferredSize = Dimension(parentFrame.width, 50)

        // 타이틀 라벨 설정
        val titleLabel = JLabel("POS 꼬르륵").apply {
            font = Font("Arial", Font.BOLD, 18)
            foreground = Color(255, 153, 51)
            horizontalAlignment = SwingConstants.LEFT
            border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
        }
        add(titleLabel, BorderLayout.WEST)

        // 오른쪽 버튼 패널 생성
        val buttonPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.RIGHT, 0, 0)
            background = Color(255, 255, 255)
        }

        // 최소화 버튼
        val minimizeButton = JButton("—").apply {
            preferredSize = Dimension(40, 40)
            isContentAreaFilled = false
            border = null
            isFocusPainted = false
            addActionListener {
                parentFrame.extendedState = JFrame.ICONIFIED
            }
        }

        // 최대화/복원 버튼
        val maximizeButton = JButton("□").apply {
            preferredSize = Dimension(40, 40)
            isContentAreaFilled = false
            border = null
            isFocusPainted = false
            addActionListener {
                toggleMaximizeRestore()
            }
        }

        // 닫기 버튼
        val closeButton = JButton("X").apply {
            preferredSize = Dimension(40, 40)
            isContentAreaFilled = false
            border = null
            isFocusPainted = false
            addActionListener { parentFrame.dispose() } // 창 닫기
        }

        buttonPanel.add(minimizeButton)
        buttonPanel.add(maximizeButton)
        buttonPanel.add(closeButton)
        add(buttonPanel, BorderLayout.EAST)

        // 타이틀바 더블 클릭 시 최대화/복원 기능 추가
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (parentFrame.extendedState != JFrame.MAXIMIZED_BOTH) {
                    mouseX = e.x
                    mouseY = e.y
                }
                isDragging = true
            }

            override fun mouseReleased(e: MouseEvent) {
                isDragging = false
            }

            override fun mouseClicked(e: MouseEvent) {
                // 타이틀바 더블 클릭 시 최대화/복원
                if (e.clickCount == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    toggleMaximizeRestore()
                }
            }
        })

        // 창 이동을 위한 마우스 드래그 리스너 추가
        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                if (isDragging && SwingUtilities.isLeftMouseButton(e)) {
                    parentFrame.location = Point(e.xOnScreen - mouseX, e.yOnScreen - mouseY)
                }
            }
        })
    }

    // 최대화/복원 동작을 토글하는 함수
    private fun toggleMaximizeRestore() {
        if (isMaximized) {
            parentFrame.extendedState = JFrame.NORMAL
            isMaximized = false
        } else {
            parentFrame.extendedState = JFrame.MAXIMIZED_BOTH
            isMaximized = true
        }
    }
}

fun main() {
    SwingUtilities.invokeLater {
        val frame = JFrame().apply {
            isUndecorated = true // 기본 타이틀바 제거
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            size = Dimension(800, 600)
            setLocationRelativeTo(null)

            val titleBar = CustomTitleBar(this)
            add(titleBar, BorderLayout.NORTH)

            // 콘텐츠 패널
            val contentPanel = JPanel().apply {
                background = Color(230, 230, 230)
            }
            add(contentPanel, BorderLayout.CENTER)

            isVisible = true
        }
    }
}
