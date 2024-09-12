package org.example.widgets.custom_titlebar

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import kotlin.concurrent.thread

class CustomTitleBar(private val parentFrame: JFrame) : JPanel() {

    private var mouseX = 0
    private var mouseY = 0
    private var isMaximized = false
    private var isDragging = false
    private var lastLocation = Point(0, 0) // 최대화 전 창의 위치 저장
    private var lastSize = Dimension(800, 600) // 최대화 전 창의 크기 저장

    private val animationSpeed = 10 // 애니메이션 속도
    private val resizeIncrement = 30 // 크기 증가/감소 단위 (더 부드럽게 조정)

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
                if (isMaximized) {
                    // 창을 자연스럽게 복원
                    animateWindowRestore(parentFrame, parentFrame.size, lastSize, lastLocation)
                    isMaximized = false
                } else {
                    // 최대화 전의 크기와 위치 저장
                    lastLocation = parentFrame.location
                    lastSize = parentFrame.size

                    // 스레드를 사용해 자연스럽게 창을 커지게 하고 이동
                    animateWindowResizeAndMove(parentFrame, parentFrame.size, Toolkit.getDefaultToolkit().screenSize)
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
                        animateWindowRestore(parentFrame, parentFrame.size, lastSize, lastLocation)
                        isMaximized = false
                    } else {
                        lastLocation = parentFrame.location
                        lastSize = parentFrame.size
                        animateWindowResizeAndMove(parentFrame, parentFrame.size, Toolkit.getDefaultToolkit().screenSize)
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

    // 창 크기 및 위치를 동시에 애니메이션하는 함수 (스레드 사용)
    private fun animateWindowResizeAndMove(frame: JFrame, startSize: Dimension, targetSize: Dimension) {
        val initialLocation = frame.location

        thread {
            var currentWidth = startSize.width
            var currentHeight = startSize.height
            var currentX = initialLocation.x
            var currentY = initialLocation.y

            while (currentWidth < targetSize.width || currentHeight < targetSize.height ||
                currentX > 0 || currentY > 0) {
                // 창 크기 증가
                currentWidth = (currentWidth + resizeIncrement).coerceAtMost(targetSize.width)
                currentHeight = (currentHeight + resizeIncrement).coerceAtMost(targetSize.height)
                frame.setSize(currentWidth, currentHeight)

                // 창 위치 이동 (0, 0을 향해)
                currentX = (currentX - (currentX * 0.1)).toInt().coerceAtLeast(0)
                currentY = (currentY - (currentY * 0.1)).toInt().coerceAtLeast(0)
                frame.setLocation(currentX, currentY)

                // 애니메이션 속도 조절
                Thread.sleep(animationSpeed.toLong())
            }

            // 최종적으로 (0, 0)에 위치하고 크기는 최대화 크기로 설정
            frame.setLocation(0, 0)
            frame.setSize(targetSize)
        }
    }

    // 창 크기 및 위치 복원을 위한 함수
    private fun animateWindowRestore(frame: JFrame, startSize: Dimension, restoredSize: Dimension, restoredLocation: Point) {
        val timer = Timer(animationSpeed) {
            val currentSize = frame.size
            val currentLocation = frame.location

            // 목표 크기와 현재 크기 및 위치 간의 차이 계산
            val widthDifference = restoredSize.width - currentSize.width
            val heightDifference = restoredSize.height - currentSize.height
            val xDifference = restoredLocation.x - currentLocation.x
            val yDifference = restoredLocation.y - currentLocation.y

            // 목표 크기와 위치 도달 시 타이머 중지
            if (Math.abs(widthDifference) < 1 && Math.abs(heightDifference) < 1 &&
                Math.abs(xDifference) < 1 && Math.abs(yDifference) < 1) {
                frame.size = restoredSize
                frame.location = restoredLocation
                (it.source as Timer).stop()
            }

            // 새로운 크기 및 위치 계산
            val newWidth = currentSize.width + Math.signum(widthDifference.toDouble()).toInt() * resizeIncrement
            val newHeight = currentSize.height + Math.signum(heightDifference.toDouble()).toInt() * resizeIncrement
            frame.size = Dimension(newWidth.coerceAtLeast(restoredSize.width), newHeight.coerceAtLeast(restoredSize.height))

            val newX = currentLocation.x + Math.signum(xDifference.toDouble()).toInt() * resizeIncrement
            val newY = currentLocation.y + Math.signum(yDifference.toDouble()).toInt() * resizeIncrement
            frame.location = Point(newX.coerceAtMost(restoredLocation.x), newY.coerceAtMost(restoredLocation.y))
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
