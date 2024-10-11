package org.example.screen.main.main_widget.dialog.TimeSelectDialog

import CustomRoundedDialog
import org.example.style.MyColor
import org.example.util.LoadImage
import org.example.util.MyFont
import org.example.widgets.CHRoundedPanel
import javax.swing.*
import java.awt.*
import javax.swing.border.EmptyBorder

abstract class BaseTimeSelectionDialog(
    parent: JFrame,
    cardPanel: JPanel,
    title: String,
    private val defaultTime: Int,
    private val iconPath: String,
    private val mainText: String,
    private val subText: String
) : CustomRoundedDialog(parent, title, 1000, 465) {

    protected var selectedTime: Int = defaultTime

    init {
        val mainPanel = JPanel(BorderLayout()).apply {
            background = Color.WHITE
            border = EmptyBorder(20, 20, 20, 20)
        }

        val timeSelectionPanel = createTimeSelectionPanel()

        // 버튼 패널 생성
        val buttonPanel = JPanel().apply {
            background = Color.WHITE
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = EmptyBorder(20, 0, 0, 0)
            add(Box.createVerticalGlue())
            add(createSubmitButton())
        }

        mainPanel.add(timeSelectionPanel, BorderLayout.CENTER)
        mainPanel.add(buttonPanel, BorderLayout.SOUTH)

        add(mainPanel, BorderLayout.CENTER)
        pack()
        setSize(1000, 465)
        setLocationRelativeTo(cardPanel)
    }

    // 시간 선택 패널 생성
    private fun createTimeSelectionPanel(): JPanel {
        return CHRoundedPanel(30, 30).apply {
            layout = GridLayout(1, 2, 10, 0)
            background = MyColor.DARK_NAVY
            preferredSize = Dimension(900, 230)
            border = EmptyBorder(20, 20, 20, 20)
            add(createIconLabelPanel())
            add(createTimeControlPanel())
        }
    }

    // 아이콘 및 타이틀 라벨 생성 패널
    private fun createIconLabelPanel(): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = MyColor.DARK_NAVY
            preferredSize = Dimension(460, 250)
            border = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.WHITE)

            val iconLabel = JLabel().apply {
                val imageIcon = LoadImage.loadImage(iconPath, 50, 50)
                icon = imageIcon
                alignmentX = Component.CENTER_ALIGNMENT
            }

            val mainLabel = JLabel(mainText, SwingConstants.CENTER).apply {
                font = MyFont.SemiBold(30f)
                foreground = Color.WHITE
                alignmentX = Component.CENTER_ALIGNMENT
                border = EmptyBorder(10, 0, 10, 0)
            }

            val subLabel = JLabel(subText, SwingConstants.CENTER).apply {
                font = MyFont.Medium(16f)
                foreground = Color.WHITE
                alignmentX = Component.CENTER_ALIGNMENT
            }

            add(Box.createVerticalGlue())
            add(iconLabel)
            add(Box.createVerticalStrut(5))
            add(mainLabel)
            add(Box.createVerticalStrut(0))
            add(subLabel)
            add(Box.createVerticalGlue())
        }
    }

    // 시간 컨트롤 패널 생성 (증가/감소 버튼 포함)
    private fun createTimeControlPanel(): JPanel {

        // 외부 패널 생성
        val labelPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = MyColor.DARK_NAVY
            alignmentX = Component.CENTER_ALIGNMENT
            preferredSize = Dimension(460, 250)
            border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
        }

        // 시간 표시 라벨 생성
        val timeLabel = JLabel("${selectedTime}분", SwingConstants.CENTER).apply {
            font = MyFont.Bold(38f)
            foreground = Color.BLACK
            alignmentX = Component.CENTER_ALIGNMENT
        }

        // 시간 감소 버튼 생성
        val decreaseButton = JButton(ImageIcon(javaClass.getResource("/minus_icon.png"))).apply {
            font = MyFont.Bold(36f)
            isContentAreaFilled = false
            isFocusPainted = false
            border = BorderFactory.createEmptyBorder()
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            preferredSize = Dimension(70, 70)
            addActionListener { adjustTime(timeLabel, -5) }
        }

        // 시간 증가 버튼 생성
        val increaseButton = JButton(ImageIcon(javaClass.getResource("/plus_icon.png"))).apply {
            font = MyFont.Bold(36f)
            isContentAreaFilled = false
            isFocusPainted = false
            border = BorderFactory.createEmptyBorder()
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            preferredSize = Dimension(70, 70)
            addActionListener { adjustTime(timeLabel, 5) }
        }

        // 둥근 시간 선택 패널 구성
        val buttonPanel = CHRoundedPanel(30, 30).apply {
            background = Color.WHITE
            border = EmptyBorder(0, 15, 0, 15)
            preferredSize = Dimension(305, 90)
            maximumSize = Dimension(305, 90)
            minimumSize = Dimension(305, 90)
            layout = BorderLayout()

            // 감소 버튼을 왼쪽에 추가
            add(decreaseButton, BorderLayout.WEST)

            // 시간 라벨을 중앙에 추가
            add(timeLabel, BorderLayout.CENTER)

            // 증가 버튼을 오른쪽에 추가
            add(increaseButton, BorderLayout.EAST)
        }

        // labelPanel에 buttonPanel 추가
        labelPanel.add(Box.createVerticalGlue())
        labelPanel.add(buttonPanel)
        labelPanel.add(Box.createVerticalGlue())

        return labelPanel
    }

    // 시간 조절 함수
    private fun adjustTime(timeLabel: JLabel, adjustment: Int) {
        selectedTime = (selectedTime + adjustment).coerceAtLeast(5)
        timeLabel.text = "${selectedTime}분"
    }

    // '시간 접수' 버튼 생성
    private fun createSubmitButton(): JButton {
        return JButton("시간 접수").apply {
            preferredSize = Dimension(300, 62)
            maximumSize = Dimension(300, 62)
            minimumSize = Dimension(300, 62)
            font = MyFont.Bold(24f)
            foreground = MyColor.DARK_RED
            background = Color.WHITE
            alignmentX = Component.CENTER_ALIGNMENT
            border = BorderFactory.createLineBorder(MyColor.DARK_RED, 1)
            isFocusable = false
            addActionListener {
                onSubmit(selectedTime)
                dispose()
            }
        }
    }

    // 추상 메서드: 서브클래스가 구현해야 함
    abstract fun onSubmit(selectedTime: Int)
}
