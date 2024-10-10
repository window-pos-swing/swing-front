package org.example.widgets.custom_titlebar

import org.example.MyFont
import org.example.SettingForm
import org.example.widgets.IconRoundBorder
import org.example.widgets.custom_titlebar.TitleDateUpdate.TitleDateUpdater
import org.example.widgets.custom_titlebar.component_resizer.ComponentResizer
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*

class MainCustomTitlebar(private val parentFrame: JFrame) : JPanel() {

    private var mouseX = 0
    private var mouseY = 0
    private var isMaximized = false
    private var isDragging = false

    val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 hh:mm a")  // 원하는 형식으로 설정
    val currentTime = dateFormat.format(Date())  // 현재 시간 가져오기

    init {
        layout = BorderLayout()

        // 커스텀 바 배경색 (어두운 네이비 색상)
        background = Color(35, 45, 75)

        // 커스텀 바 높이 설정
        preferredSize = Dimension(parentFrame.width, 70)

        // 왼쪽 패널: "000사장님" 텍스트 추가
        val nameLabel = JLabel("000점주님").apply {
            font = MyFont.Bold(30f)
            foreground = Color.WHITE
            border = BorderFactory.createEmptyBorder(0, 30, 0, 0)  // 좌측 여백 추가
        }
        add(nameLabel, BorderLayout.WEST)

        // 중앙에 현재 시간 표시
        val titleLabel = JLabel(currentTime).apply {
            font = MyFont.Bold(20f)
            foreground = Color.WHITE
            horizontalAlignment = SwingConstants.CENTER
        }
        add(titleLabel, BorderLayout.CENTER)

        //titlebar시간 TEXT UI업데이트 함수
        TitleDateUpdater(titleLabel)

        // 오른쪽 버튼 패널
        val buttonPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.RIGHT, 10, 0)  // 좌우 여백은 10, 상하 여백은 0
            isOpaque = false  // 배경을 투명하게 설정
        }

        // 버튼 컨테이너 패널 (수직 정렬)
        val buttonContainer = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)  // 수직 레이아웃 설정
            isOpaque = false
            add(Box.createVerticalGlue())  // 상단 여백
            add(buttonPanel)  // 버튼 패널 추가
            border = BorderFactory.createEmptyBorder(0, 0, 0, 10)  // 오른쪽에 마진 10 추가
        }

        // 둥근 테두리와 흰색 배경을 가진 버튼 생성 및 아이콘 적용
        val settingsButton = IconRoundBorder.createRoundedButton("/setting_icon.png").apply {
            preferredSize = Dimension(40, 40)  // 버튼 크기 설정
        }
        val minimizeButton = IconRoundBorder.createRoundedButton("/minimize_icon.png").apply {
            preferredSize = Dimension(40, 40)  // 버튼 크기 설정
        }
        val maximizeButton = IconRoundBorder.createRoundedButton("/maximize_icon.png").apply {
            preferredSize = Dimension(40, 40)  // 버튼 크기 설정
        }
        val closeButton = IconRoundBorder.createRoundedButton("/close_icon.png").apply {
            preferredSize = Dimension(40, 40)  // 버튼 크기 설정
        }

        //        세팅 화면으로 이동
        settingsButton.addActionListener {
            SettingForm().isVisible = true
            parentFrame.dispose()
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
        buttonPanel.add(settingsButton)
        buttonPanel.add(minimizeButton)
        buttonPanel.add(maximizeButton)
        buttonPanel.add(closeButton)

        // 오른쪽 버튼 컨테이너 패널 추가
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
}
