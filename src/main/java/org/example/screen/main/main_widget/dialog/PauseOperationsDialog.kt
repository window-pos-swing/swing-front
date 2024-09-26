package org.example.screen.main.main_widget.dialog

import CustomRoundedDialog
import org.example.MyFont
import org.example.style.MyColor
import org.example.widgets.FillRoundedButton
import org.example.widgets.RoundedPanel
import java.awt.*
import javax.swing.*
import java.awt.event.ActionListener
import javax.swing.border.EmptyBorder

class PauseOperationsDialog(
    parent: JFrame,
    title: String,
    private val callback: (Boolean) -> Unit
) : CustomRoundedDialog(parent, title, 1000, 465) {

    // 패널 선언
    private lateinit var thirtyMinutePanel: JPanel
    private lateinit var timeSpecifiedPanel: JPanel

    // 버튼 및 라벨 선언 (클래스 레벨로 이동)
    private lateinit var amButton: FillRoundedButton
    private lateinit var pmButton: FillRoundedButton
    private lateinit var untilLabel: JLabel

    private var cookingTime: Int = 30  // 기본 조리 시간 30분
    private var deliveryTime: Int = 30  // 기본 배달 시간 30분

    init {
        isModal = true

        // 조리시간 및 배달시간 선택 UI 생성
        val timeSelectionPanel = JPanel(GridLayout(1, 2, 20, 10)).apply {
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(20, 30, 30, 30)
            // 패널 생성 및 저장
            thirtyMinutePanel = createTimeSelectionPanel("30분 단위", "영업 임시 중지 시간을 선택해 주세요", true)
            timeSpecifiedPanel = createTimeSpecifiedPanel("시간지정", "영업 임시 중지 시간을 선택해 주세요")

            // 패널을 추가
            add(thirtyMinutePanel)
            add(timeSpecifiedPanel)
        }

        // 기본적으로 30분 단위 패널이 선택된 상태로 보이게 설정
        activatePanel(thirtyMinutePanel)
        deactivatePanel(timeSpecifiedPanel)

        // 패널 클릭 리스너 등록
        thirtyMinutePanel.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent?) {
                activatePanel(thirtyMinutePanel)
                deactivatePanel(timeSpecifiedPanel)
            }
        })

        timeSpecifiedPanel.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent?) {
                activatePanel(timeSpecifiedPanel)
                deactivatePanel(thirtyMinutePanel)
            }
        })

        // 시간 접수 버튼 생성
        val confirmButton = JButton("임시 중지").apply {
            preferredSize = Dimension(300, 62)
            maximumSize = Dimension(300, 62)
            minimumSize = Dimension(300, 62)
            font = MyFont.Bold(24f)
            background = Color.WHITE
            foreground = MyColor.DARK_RED
            border = BorderFactory.createLineBorder(MyColor.DARK_RED)
            addActionListener {
                println("조리시간: $cookingTime 분, 배달시간: $deliveryTime 분")
                callback(true)
                dispose()  // 다이얼로그 닫기
            }
        }

        // 패널에 버튼을 추가하고, BoxLayout 또는 FlowLayout을 사용
        val buttonPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER)  // 버튼을 중앙에 배치
            background = Color.WHITE
            add(confirmButton)
            border = BorderFactory.createEmptyBorder(0, 0, 20, 0)
        }

        // 다이얼로그 UI 배치
        add(timeSelectionPanel, BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.SOUTH)

        // 다이얼로그 설정
        setSize(1000, 465)
        setLocationRelativeTo(parent)
        isVisible = true
    }

    // 패널 활성화 함수
    private fun activatePanel(panel: JPanel) {
        panel.background = MyColor.DARK_NAVY
        setPanelInteraction(panel, true)
        setPanelTextColor(panel, Color.WHITE) // 선택된 패널의 텍스트는 흰색
        setPanelBorderColor(panel, MyColor.DARK_NAVY) // 선택된 패널의 테두리 색상

        if (panel == timeSpecifiedPanel) {
            // 오전/오후 버튼 활성화 스타일
            amButton.backgroundColor = MyColor.DARK_RED
            amButton.textColor = Color.WHITE
            amButton.borderColor = MyColor.DARK_RED  // 배경색에 맞춘 테두리 색상
            amButton.repaint()

            pmButton.backgroundColor = Color.WHITE
            pmButton.textColor = MyColor.UNSELECTED_TEXT_COLOR
            pmButton.borderColor = Color.WHITE
            pmButton.repaint()

            // untilLabel 색상 변경
            untilLabel.foreground = Color.WHITE
            untilLabel.repaint()
        }

        if (panel == thirtyMinutePanel) {
            // 30분단위 패널 내 buttonPanel, timeLabel, decreaseButton, increaseButton 복구
            val buttonPanel = findButtonPanel(thirtyMinutePanel)
            if (buttonPanel != null) {
                buttonPanel.background = Color.WHITE
                setPanelBorderColor(buttonPanel, Color.WHITE)

                // decreaseButton, timeLabel, increaseButton 복구
                val decreaseButton = findComponent<JButton>(buttonPanel, "/minus_unselect_icon.png")
                val increaseButton = findComponent<JButton>(buttonPanel, "/plus_unselect_icon.png")
                val timeLabel = findComponent<JLabel>(buttonPanel, "분")

                decreaseButton?.icon = ImageIcon(javaClass.getResource("/minus_icon.png"))
                increaseButton?.icon = ImageIcon(javaClass.getResource("/plus_icon.png"))
                timeLabel?.foreground = Color.BLACK

                // Repaint components
                decreaseButton?.repaint()
                increaseButton?.repaint()
                timeLabel?.repaint()
                buttonPanel.repaint()
            }
        }

    }

    // 패널 비활성화 함수
    private fun deactivatePanel(panel: JPanel) {
        panel.background = MyColor.UNSELECTED_BACKGROUND_COLOR
        setPanelInteraction(panel, false)
        setPanelTextColor(panel, MyColor.UNSELECTED_TEXT_COLOR) // 선택되지 않은 패널의 텍스트는 MyColor.UNSELECTED_TEXT_COLOR
        setPanelBorderColor(panel, MyColor.UNSELECTED_BACKGROUND_COLOR) // 선택되지 않은 패널의 테두리 색상

        if (panel == timeSpecifiedPanel) {
            // 오전/오후 버튼 비활성화 스타일
            amButton.backgroundColor = MyColor.BRIGHTER_GREY
            amButton.textColor = MyColor.UNSELECTED_TEXT_COLOR
            amButton.borderColor = MyColor.BRIGHTER_GREY  // 배경색에 맞춘 테두리 색상
            amButton.repaint()

            pmButton.backgroundColor = MyColor.BRIGHTER_GREY
            pmButton.textColor = MyColor.UNSELECTED_TEXT_COLOR
            pmButton.borderColor = MyColor.BRIGHTER_GREY  // 배경색에 맞춘 테두리 색상
            pmButton.repaint()

            // untilLabel 색상 변경
            untilLabel.foreground = MyColor.UNSELECTED_TEXT_COLOR
            untilLabel.repaint()
        }

        if (panel == thirtyMinutePanel) {
            // 30분단위 패널 내 buttonPanel, timeLabel, decreaseButton, increaseButton 업데이트
            val buttonPanel = findButtonPanel(thirtyMinutePanel)
            if (buttonPanel != null) {
                buttonPanel.background = MyColor.BRIGHTER_GREY
                setPanelBorderColor(buttonPanel, MyColor.BRIGHTER_GREY)

                // decreaseButton, timeLabel, increaseButton 업데이트
                val decreaseButton = findComponent<JButton>(buttonPanel, "/minus_icon.png")
                val increaseButton = findComponent<JButton>(buttonPanel, "/plus_icon.png")
                val timeLabel = findComponent<JLabel>(buttonPanel, "분")

                decreaseButton?.icon = ImageIcon(javaClass.getResource("/minus_unselect_icon.png"))
                increaseButton?.icon = ImageIcon(javaClass.getResource("/plus_unselect_icon.png"))
                timeLabel?.foreground = MyColor.UNSELECTED_TEXT_COLOR

                // Repaint components
                decreaseButton?.repaint()
                increaseButton?.repaint()
                timeLabel?.repaint()
                buttonPanel.repaint()
            }
        }
    }

    // buttonPanel 안에서 특정 컴포넌트를 찾는 제네릭 함수
    private inline fun <reified T : Component> findComponent(container: Container, identifier: String): T? {
        return container.components.filterIsInstance<T>().find {
            when (it) {
                is JButton -> it.icon?.toString()?.contains(identifier) == true
                is JLabel -> it.text.contains(identifier)
                else -> false
            }
        }
    }

    private fun findButtonPanel(panel: JPanel): JPanel? {
        return panel.components.filterIsInstance<JPanel>().find { it.layout is BorderLayout }
    }


    // 패널 내 컴포넌트 상호작용 활성/비활성화 함수
    private fun setPanelInteraction(panel: JPanel, isEnabled: Boolean) {
        for (component in panel.components) {
            component.isEnabled = isEnabled
        }
    }

    // 패널 내 텍스트 색상 설정 함수
    private fun setPanelTextColor(panel: JPanel, color: Color) {
        for (component in panel.components) {
            if (component is JLabel) {
                component.foreground = color
            }
        }
    }

    // 패널 테두리 색상 설정 함수
    private fun setPanelBorderColor(panel: JPanel, color: Color) {
        panel.foreground  = color
    }
    // 시간 지정 패널 생성 함수
    private fun createTimeSpecifiedPanel(titleText: String, subtitleText: String): JPanel {
        // 오전/오후 선택 버튼 그룹
        val amPmButtonGroup = ButtonGroup()

        // 오전 버튼 생성 (클래스 멤버 변수로 설정)
        amButton = FillRoundedButton(
            text = "오전",
            borderColor = Color(0, 0, 0),
            backgroundColor = MyColor.DARK_RED,  // 기본 선택된 상태
            textColor = Color.WHITE,
            borderRadius = 40,
            borderWidth = 1,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(10, 20, 10, 20),
            buttonSize = Dimension(130, 40),
            customFont = MyFont.Bold(20f)
        )

        // 오후 버튼 생성 (클래스 멤버 변수로 설정)
        pmButton = FillRoundedButton(
            text = "오후",
            borderColor = Color(0, 0, 0),
            backgroundColor = Color.WHITE,
            textColor = MyColor.UNSELECTED_TEXT_COLOR,
            borderRadius = 40,
            borderWidth = 1,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(10, 20, 10, 20),
            buttonSize = Dimension(130, 40),
            customFont = MyFont.Bold(20f)
        )

        // 기본적으로 오전 버튼을 선택 상태로 설정
        updateButtonState(amButton, pmButton)

        // 오전 버튼 클릭 리스너 추가
        amButton.addActionListener {
            updateButtonState(amButton, pmButton)  // 오전 버튼을 선택
        }

        // 오후 버튼 클릭 리스너 추가
        pmButton.addActionListener {
            updateButtonState(pmButton, amButton)  // 오후 버튼을 선택
        }

        // 드롭다운 메뉴
        val hourComboBox = JComboBox<String>().apply {
            font = MyFont.SemiBold(16f)
            background = Color.WHITE
            foreground = MyColor.DARK_NAVY
            model = DefaultComboBoxModel(arrayOf("-시", "1시", "2시", "3시", "4시", "5시", "6시"))
            preferredSize = Dimension(100, 40)
        }

        // "까지 중지" 라벨 (클래스 멤버 변수로 설정)
        untilLabel = JLabel("까지 중지").apply {
            font = MyFont.SemiBold(16f)
            foreground = Color.WHITE
        }

        // 메인 패널을 둥글게 만들기 위해 RoundedPanel 사용
        val mainPanel = RoundedPanel(30, 30).apply {
            background = MyColor.DARK_NAVY
            preferredSize = Dimension(460, 258)  // 패널 크기를 설정
            layout = GridBagLayout()
            border = BorderFactory.createEmptyBorder(20, 15, 20, 15)  // 상하 20, 좌우 15의 마진 추가

            val gbc = GridBagConstraints().apply {
                gridx = 0
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(10, 0, 10, 0)  // 컴포넌트 간의 수직 간격
            }

            // titleText
            gbc.gridy = 0
            add(JLabel(titleText, SwingConstants.CENTER).apply {
                font = MyFont.Bold(24f)
                foreground = Color.WHITE
            }, gbc)

            // subtitleText
            gbc.gridy = 1
            add(JLabel(subtitleText, SwingConstants.CENTER).apply {
                font = MyFont.SemiBold(16f)
                foreground = Color.WHITE
            }, gbc)

            // 오전/오후 버튼 배치
            val buttonPanel = JPanel().apply {
                isOpaque = false
                layout = FlowLayout(FlowLayout.CENTER, 20, 10)  // 버튼 간격 설정
                background = MyColor.DARK_NAVY
                add(amButton)
                add(pmButton)
            }

            gbc.gridy = 2
            add(buttonPanel, gbc)

            // 드롭다운과 "까지 중지" 라벨 배치
            val comboPanel = JPanel().apply {
                isOpaque = false
                layout = FlowLayout(FlowLayout.CENTER, 10, 10)
                background = MyColor.DARK_NAVY
                add(hourComboBox)
                add(untilLabel)
            }

            gbc.gridy = 3
            add(comboPanel, gbc)
        }

        return mainPanel
    }

    // 오전/오후 선택 버튼 상태 업데이트 함수
    private fun updateButtonState(selectedButton: FillRoundedButton, deselectedButton: FillRoundedButton) {
        // 선택된 버튼 스타일 설정
        selectedButton.backgroundColor = MyColor.DARK_RED
        selectedButton.textColor = Color.WHITE
        selectedButton.borderColor = MyColor.DARK_RED  // 테두리 색상 설정
        selectedButton.repaint()

        // 선택되지 않은 버튼 스타일 설정
        deselectedButton.backgroundColor = Color.WHITE
        deselectedButton.textColor = MyColor.UNSELECTED_TEXT_COLOR
        deselectedButton.borderColor = MyColor.UNSELECTED_TEXT_COLOR  // 테두리 색상 설정
        deselectedButton.repaint()
    }

    // 시간 선택 패널 생성 함수
    private fun createTimeSelectionPanel(titleText: String, subtitleText: String, isCookingTime: Boolean): JPanel {
        // 시간 값 결정 (조리 시간/배달 시간)
        val timeValue = if (isCookingTime) cookingTime else deliveryTime

        val timeLabel = JLabel("${timeValue}분", SwingConstants.CENTER).apply {
            font = MyFont.Bold(38f)
            foreground = Color.BLACK
        }

        // 시간 증가/감소 버튼 생성
        val decreaseButton = JButton(ImageIcon(javaClass.getResource("/minus_icon.png"))).apply {
            font = MyFont.Bold(36f)
            isContentAreaFilled = false  // 버튼 배경색 제거
            isFocusPainted = false
            border = BorderFactory.createEmptyBorder()  // 테두리 제거
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)  // 커서를 손 모양으로 변경
            addActionListener { adjustTime(timeLabel, -5, isCookingTime) }  // 5분 감소
        }
        val increaseButton = JButton(ImageIcon(javaClass.getResource("/plus_icon.png"))).apply {
            font = MyFont.Bold(36f)
            isContentAreaFilled = false  // 버튼 배경색 제거
            isFocusPainted = false
            border = BorderFactory.createEmptyBorder()  // 테두리 제거
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)  // 커서를 손 모양으로 변경
            addActionListener { adjustTime(timeLabel, 5, isCookingTime) }  // 5분 증가
        }

        // 둥근 시간 선택 패널 구성
        val buttonPanel = RoundedPanel(30, 30).apply {
            background = Color.WHITE  // 배경을 흰색으로 설정
            border = EmptyBorder(0, 15, 0, 15)
            preferredSize = Dimension(305, 90)  // 패널 크기를 305x90으로 설정
            layout = BorderLayout()

            add(decreaseButton, BorderLayout.WEST)
            add(timeLabel, BorderLayout.CENTER)
            add(increaseButton, BorderLayout.EAST)
        }

        // 메인 패널을 둥글게 만들기 위해 RoundedPanel 사용
        val mainPanel = RoundedPanel(30, 30).apply {
            background = MyColor.DARK_NAVY
            preferredSize = Dimension(460, 258)  // 패널 크기를 설정
            layout = GridBagLayout()
            border = BorderFactory.createEmptyBorder(20, 15, 20, 15)  // 상하 20, 좌우 15의 마진 추가

            val gbc = GridBagConstraints().apply {
                gridx = 0
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(10, 0, 10, 0)  // 컴포넌트 간의 수직 간격
            }

            // titleText
            gbc.gridy = 0
            add(JLabel(titleText, SwingConstants.CENTER).apply {
                font = MyFont.Bold(24f)
                foreground = Color.WHITE
            }, gbc)

            // subtitleText
            gbc.gridy = 1
            add(JLabel(subtitleText, SwingConstants.CENTER).apply {
                font = MyFont.SemiBold(16f)
                foreground = Color.WHITE
            }, gbc)

            // 버튼 패널
            gbc.gridy = 2
            add(buttonPanel, gbc)
        }

        return mainPanel
    }


    // 시간 조정 함수 (timeLabel의 값을 업데이트)
    private fun adjustTime(timeLabel: JLabel, delta: Int, isCookingTime: Boolean) {
        val currentTime = timeLabel.text.replace("분", "").toInt()
        val newTime = (currentTime + delta).coerceAtLeast(5)  // 최소 5분으로 제한

        // 시간 값 업데이트
        if (isCookingTime) {
            cookingTime = newTime
        } else {
            deliveryTime = newTime
        }

        // 라벨 업데이트
        timeLabel.text = "${newTime}분"
    }

}