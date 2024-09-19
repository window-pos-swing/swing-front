package org.example.widgets.custom_titlebar

import org.example.MyFont
import org.example.style.MyColor
import org.example.widgets.RoundBorder
import org.example.widgets.RoundedSettingButton
import org.example.widgets.custom_titlebar.component_resizer.ComponentResizer
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*

class CustomTitleBar(private val parentFrame: JFrame, private val showMainTitleBar: Boolean) : JPanel() {

    private var mouseX = 0
    private var mouseY = 0
    private var isMaximized = false
    private var isDragging = false

    val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 hh:mm a")  // 원하는 형식으로 설정
    val currentTime = dateFormat.format(Date())  // 현재 시간 가져오기

    init {
        layout = BorderLayout()
        if (showMainTitleBar) {
            background = Color(255, 255, 255)
        } else {
            background = MyColor.LOGIN_TITLEBAR
        }

        preferredSize = Dimension(parentFrame.width, 70)

        if (showMainTitleBar) {
            // 설정 버튼을 감싸는 패널 생성
            val settingsPanel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS) // 수직 레이아웃 설정
                isOpaque = false
                border = BorderFactory.createEmptyBorder(0, 10, 0, 0) // 좌측 여백 10 추가
            }

            // 설정 버튼 생성
            val settingsButton = createSettingButton()

            // 버튼 클릭 이벤트
            settingsButton.addActionListener {
                println("설정 버튼 클릭됨")
            }

            settingsPanel.add(Box.createVerticalGlue())
            settingsPanel.add(settingsButton)
            settingsPanel.add(Box.createVerticalGlue())

            // 설정 패널을 WEST에 추가
            add(settingsPanel, BorderLayout.WEST)

            // 타이틀 라벨
            val titleLabel = JLabel(currentTime).apply {
                font = MyFont.Bold(20f)
                foreground = Color.WHITE
                horizontalAlignment = SwingConstants.CENTER
            }
            add(titleLabel, BorderLayout.CENTER)
        } else {
            val titleLabel = JLabel(currentTime).apply {
                font = MyFont.Bold(20f)
                foreground = Color.WHITE
                horizontalAlignment = SwingConstants.LEFT
                border = BorderFactory.createEmptyBorder(0, 30, 0, 0)
            }
            add(titleLabel, BorderLayout.WEST)
        }

        // 오른쪽 버튼 패널 (FlowLayout을 사용하여 수평으로 가운데 정렬)
        val buttonPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.RIGHT, 20, 0)  // 좌우 여백은 10, 상하 여백은 0
            isOpaque = false  // 배경을 투명하게 설정
        }

        // 버튼들을 상하 가운데 정렬하기 위해 패널을 감싸는 중간 컨테이너
        val buttonContainer = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)  // 수직 레이아웃 설정
            isOpaque = false
            add(Box.createVerticalGlue())  // 상단 여백
            add(buttonPanel)  // 버튼 패널 추가
            border = BorderFactory.createEmptyBorder(0, 0, 0, 10)  // 오른쪽에 마진 10 추가
        }

        // 둥근 테두리와 흰색 배경을 가진 버튼 생성 및 아이콘 적용
        val minimizeButton = RoundBorder.createRoundedButton("/minimize_icon.png").apply {
            preferredSize = Dimension(40, 40)  // 버튼 크기 설정
        }
        val maximizeButton = RoundBorder.createRoundedButton("/maximize_icon.png").apply {
            preferredSize = Dimension(40, 40)  // 버튼 크기 설정
        }
        val closeButton = RoundBorder.createRoundedButton("/close_icon.png").apply {
            preferredSize = Dimension(40, 40)  // 버튼 크기 설정
        }

        // 버튼 기능 추가
        minimizeButton.addActionListener {
            parentFrame.extendedState = JFrame.ICONIFIED
        }

        maximizeButton.addActionListener {
            toggleMaximizeRestore()
        }

        closeButton.addActionListener {
            parentFrame.dispose()
        }

        // 버튼 패널에 버튼 추가
        buttonPanel.add(minimizeButton)
        buttonPanel.add(maximizeButton)
        buttonPanel.add(closeButton)

        // 오른쪽에 버튼 컨테이너 추가
        add(buttonContainer, BorderLayout.EAST)

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

        // ComponentResizer 적용
        val componentResizer = ComponentResizer()
        componentResizer.registerComponent(parentFrame)
    }

    private fun toggleMaximizeRestore() {
        if (isMaximized) {
            parentFrame.extendedState = JFrame.NORMAL
            isMaximized = false
        } else {
            parentFrame.extendedState = JFrame.MAXIMIZED_BOTH
            isMaximized = true
        }
    }

    // 설정 버튼 생성 함수
    fun createSettingButton(): JButton {
        val originalIcon = ImageIcon(RoundedSettingButton::class.java.getResource("/setting_icon.png"))
        val resizedIcon = ImageIcon(originalIcon.image.getScaledInstance(20, 20, Image.SCALE_SMOOTH))

        return RoundedSettingButton("설정", resizedIcon).apply {
            preferredSize = Dimension(100, 30)
            font = MyFont.Bold(15f)
            foreground = Color.WHITE
        }
    }
}
