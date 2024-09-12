package org.example.widgets.custom_titlebar

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.Timer

class CustomTitleBar(private val parentFrame: JFrame) : JPanel() {

    private var mouseX = 0
    private var mouseY = 0
    private var isMaximized = false
    private var isDragging = false

    private val animationSpeed = 10 // 애니메이션 속도
    private val resizeIncrement = 40 // 크기 증가/감소 단위

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
            addActionListener { parentFrame.extendedState = JFrame.ICONIFIED }
        }

        // 최대화/복원 버튼
        val maximizeButton = JButton("□").apply {
            preferredSize = Dimension(40, 40)
            isContentAreaFilled = false
            border = null
            isFocusPainted = false
            addActionListener {
                if (isMaximized) {
                    // 창을 자연스럽게 작아지게 하는 함수 호출
                    animateWindowRestore(parentFrame, Toolkit.getDefaultToolkit().screenSize, Dimension(800, 600))
                    isMaximized = false
                } else {
                    // 창을 자연스럽게 커지게 하는 함수 호출
                    animateWindowResize(parentFrame, parentFrame.size, Toolkit.getDefaultToolkit().screenSize)
                    isMaximized = true
                }
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

        // 버튼 패널에 버튼 추가
        buttonPanel.add(minimizeButton)
        buttonPanel.add(maximizeButton)
        buttonPanel.add(closeButton)
        add(buttonPanel, BorderLayout.EAST)

        // 창 이동 기능을 위한 마우스 리스너 추가
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
                if (SwingUtilities.isLeftMouseButton(e) && e.clickCount == 2) {
                    if (isMaximized) {
                        animateWindowRestore(parentFrame, Toolkit.getDefaultToolkit().screenSize, Dimension(800, 600))
                        isMaximized = false
                    } else {
                        animateWindowResize(parentFrame, parentFrame.size, Toolkit.getDefaultToolkit().screenSize)
                        isMaximized = true
                    }
                }
            }
        })

        // 창 이동을 위한 마우스 드래그 리스너 추가
        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                if (isDragging && SwingUtilities.isLeftMouseButton(e)) {
                    if (parentFrame.extendedState == JFrame.MAXIMIZED_BOTH) {
                        parentFrame.extendedState = JFrame.NORMAL
                        isMaximized = false
                        mouseX = e.x / 2
                        mouseY = e.y / 2
                    }
                    parentFrame.location = Point(e.xOnScreen - mouseX, e.yOnScreen - mouseY)
                }
            }
        })
    }

    // 창 크기 애니메이션을 위한 함수 (자연스럽게 커지는 애니메이션)
    private fun animateWindowResize(frame: JFrame, startSize: Dimension, targetSize: Dimension) {
        val timer = Timer(animationSpeed) {
            val currentSize = frame.size

            // 목표 크기와 현재 크기 간의 차이 계산
            val widthDifference = targetSize.width - currentSize.width
            val heightDifference = targetSize.height - currentSize.height

            // 목표 크기 도달 시 타이머 중지
            if (Math.abs(widthDifference) < 1 && Math.abs(heightDifference) < 1) {
                frame.size = targetSize
                (it.source as Timer).stop()
            }

            // 새로운 크기 계산
            val newWidth = currentSize.width + Math.signum(widthDifference.toDouble()).toInt() * resizeIncrement
            val newHeight = currentSize.height + Math.signum(heightDifference.toDouble()).toInt() * resizeIncrement

            // 창 크기 설정
            frame.size = Dimension(newWidth.coerceAtMost(targetSize.width), newHeight.coerceAtMost(targetSize.height))
        }
        timer.start()
    }

    // 창 크기 애니메이션을 위한 함수 (자연스럽게 작아지는 애니메이션)
    private fun animateWindowRestore(frame: JFrame, startSize: Dimension, restoredSize: Dimension) {
        val timer = Timer(animationSpeed) {
            val currentSize = frame.size

            // 목표 크기와 현재 크기 간의 차이 계산
            val widthDifference = restoredSize.width - currentSize.width
            val heightDifference = restoredSize.height - currentSize.height

            // 목표 크기 도달 시 타이머 중지
            if (Math.abs(widthDifference) < 1 && Math.abs(heightDifference) < 1) {
                frame.size = restoredSize
                (it.source as Timer).stop()
            }

            // 새로운 크기 계산
            val newWidth = currentSize.width + Math.signum(widthDifference.toDouble()).toInt() * resizeIncrement
            val newHeight = currentSize.height + Math.signum(heightDifference.toDouble()).toInt() * resizeIncrement

            // 창 크기 설정
            frame.size = Dimension(newWidth.coerceAtLeast(restoredSize.width), newHeight.coerceAtLeast(restoredSize.height))
        }
        timer.start()
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
