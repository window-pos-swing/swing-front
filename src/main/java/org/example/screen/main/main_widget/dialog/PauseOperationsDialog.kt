package org.example.screen.main.main_widget.dialog

import CustomRoundedDialog
import RoundedComboBox
import org.example.MyFont
import org.example.style.MyColor
import org.example.widgets.FillRoundedButton
import org.example.widgets.CHRoundedPanel
import java.awt.*
import javax.swing.*
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
    private lateinit var titleTextLabel: JLabel
    private lateinit var subtitleTextLabel: JLabel

    private lateinit var hourComboBox: RoundedComboBox

    private var cookingTime: Int = 30  // 기본 조리 시간 30분
    private var deliveryTime: Int = 30  // 기본 배달 시간 30분

    init {
        isModal = true

        // 콤보박스 초기화
        hourComboBox = RoundedComboBox(DefaultComboBoxModel(arrayOf("1시", "2시", "3시", "4시", "5시", "6시", "7시", "8시", "9시", "10시", "11시", "12시"))).apply {
            // 크기 및 UI 적용
            preferredSize = Dimension(90, 40)
        }

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

            // 오전/오후 버튼 활성화
            amButton.isEnabled = true
            pmButton.isEnabled = true
            hourComboBox.isEnabled = true // 드롭다운 활성화

            // 오전/오후 버튼 활성화 스타일
            amButton.backgroundColor = MyColor.DARK_RED
            amButton.textColor = Color.WHITE
            amButton.borderColor = MyColor.DARK_RED  // 배경색에 맞춘 테두리 색상
            amButton.repaint()

            pmButton.backgroundColor = Color.WHITE
            pmButton.textColor = MyColor.GREY600
            pmButton.borderColor = Color.WHITE
            pmButton.repaint()

            // untilLabel 색상 변경
            untilLabel.foreground = Color.WHITE
            untilLabel.repaint()

            val buttonPanel = findButtonPanel(thirtyMinutePanel)
            if (buttonPanel != null) {
                val decreaseButton = findComponent<JButton>(buttonPanel, "/minus_unselect_icon.png")
                val increaseButton = findComponent<JButton>(buttonPanel, "/plus_unselect_icon.png")
                decreaseButton?.isEnabled = false
                increaseButton?.isEnabled = false
            }

            // 시간 지정 콤보박스의 배경색을 흰색으로 설정
            updateComponentColors(timeSpecifiedPanel, Color.WHITE, Color.WHITE) // 콤보박스 배경색을 흰색으로 변경
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
                decreaseButton?.isEnabled = true
                increaseButton?.isEnabled = true

            }
            // 시간 지정 패널의 오전/오후 버튼 및 드롭다운 상호작용 비활성화 (색상 변경 없이)
            amButton.isEnabled = false
            pmButton.isEnabled = false
            hourComboBox.isEnabled = false
        }

    }

    // 패널 비활성화 함수
    private fun deactivatePanel(panel: JPanel) {
        panel.background = MyColor.UNSELECTED_BACKGROUND_COLOR
        setPanelBorderColor(panel, MyColor.UNSELECTED_BACKGROUND_COLOR) // 선택되지 않은 패널의 테두리 색상

        // 패널 내 모든 컴포넌트의 색상을 회색으로 변경
        updateComponentColors(panel, MyColor.GREY600, MyColor.GREY100)

        if (panel == thirtyMinutePanel) {
            // 30분단위 패널의 buttonPanel 배경색을 BRIGHTER_GREY로 변경
            val buttonPanel = findButtonPanel(thirtyMinutePanel)
            buttonPanel?.let {
                it.background = MyColor.GREY100
                setPanelBorderColor(it, MyColor.GREY100)

                // decreaseButton, timeLabel, increaseButton 색상도 변경
                val decreaseButton = findComponent<JButton>(buttonPanel, "/minus_icon.png")
                val increaseButton = findComponent<JButton>(buttonPanel, "/plus_icon.png")
                val timeLabel = findComponent<JLabel>(buttonPanel, "분")

                decreaseButton?.icon = ImageIcon(javaClass.getResource("/minus_unselect_icon.png"))
                increaseButton?.icon = ImageIcon(javaClass.getResource("/plus_unselect_icon.png"))
                timeLabel?.foreground = MyColor.GREY600

                decreaseButton?.isEnabled = false
                increaseButton?.isEnabled = false

                // Repaint components
                decreaseButton?.repaint()
                increaseButton?.repaint()
                timeLabel?.repaint()
                buttonPanel.repaint()
            }
        }

        if (panel == timeSpecifiedPanel) {
            // 오전/오후 버튼 비활성화 스타일
            amButton.backgroundColor = MyColor.GREY100
            amButton.textColor = MyColor.GREY600
            amButton.borderColor = MyColor.GREY100  // 테두리 색상
            amButton.repaint()

            pmButton.backgroundColor = MyColor.GREY100
            pmButton.textColor = MyColor.GREY600
            pmButton.borderColor = MyColor.GREY100  // 테두리 색상
            pmButton.repaint()
        }

    }

    // hourComboBox의 renderer를 업데이트하는 함수
    private fun updateComponentColors(container: Container, textColor: Color, backgroundColor: Color) {
        for (component in container.components) {
            when (component) {
                is JLabel -> {
                    component.foreground = textColor // JLabel의 텍스트 색상 변경
                }
                is JButton -> {
                    component.background = backgroundColor // JButton의 배경색 변경
                    component.foreground = textColor // JButton의 텍스트 색상 변경
                }
                is JComboBox<*> -> {
                    component.background = backgroundColor // JComboBox의 배경색 변경
                    component.foreground = MyColor.GREY600 // JComboBox의 텍스트 색상 변경
                }
                is JPanel -> {
                    component.background = Color(217,217,217) // JPanel의 배경색 변경
                    updateComponentColors(component, textColor, backgroundColor) // 재귀적으로 자식 컴포넌트들에 대해 색상 변경 적용
                }
            }
            component.repaint() // 변경 사항을 반영하기 위해 다시 그리기
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
// 시간 지정 패널 생성 함수
    private fun createTimeSpecifiedPanel(titleText: String, subtitleText: String): JPanel {

        // 오전 버튼 생성 (클래스 멤버 변수로 설정)
        amButton = FillRoundedButton(
            text = "오전",
            borderColor = Color(0, 0, 0),
            backgroundColor = MyColor.DARK_RED,  // 기본 선택된 상태
            textColor = Color.WHITE,
            borderRadius = 40,
            borderWidth = 1,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(8, 16, 8, 16),  // 패딩 줄이기
            buttonSize = Dimension(120, 35),
            customFont = MyFont.Bold(22f)  // 버튼 글자 크기 줄임
        )

        // 오후 버튼 생성 (클래스 멤버 변수로 설정)
        pmButton = FillRoundedButton(
            text = "오후",
            borderColor = Color(0, 0, 0),
            backgroundColor = Color.WHITE,
            textColor = MyColor.GREY600,
            borderRadius = 40,
            borderWidth = 1,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(8, 16, 8, 16),  // 패딩 줄이기
            buttonSize = Dimension(120, 35),
            customFont = MyFont.Bold(22f)  // 버튼 글자 크기 줄임
        )

        // 기본적으로 오전 버튼을 선택 상태로 설정
        updateButtonState(amButton, pmButton)

        amButton.addActionListener {
            updateButtonState(amButton, pmButton)  // 오전 버튼을 선택
        }

        pmButton.addActionListener {
            updateButtonState(pmButton, amButton)  // 오후 버튼을 선택
        }

        // 드롭다운 메뉴
        hourComboBox = RoundedComboBox(DefaultComboBoxModel(arrayOf("1시", "2시", "3시", "4시", "5시", "6시", "7시", "8시", "9시", "10시", "11시", "12시"))).apply {
            // 크기 및 UI 적용
            preferredSize = Dimension(80, 40)  // 드롭다운 크기 줄이기
        }

        // "까지 중지" 라벨 (클래스 멤버 변수로 설정)
        untilLabel = JLabel("까지 중지").apply {
            font = MyFont.SemiBold(16f)  // 글자 크기 줄이기
            foreground = Color.WHITE
        }

        // 메인 패널을 둥글게 만들기 위해 RoundedPanel 사용
        val mainPanel = CHRoundedPanel(30, 30).apply {
            background = MyColor.DARK_NAVY
            preferredSize = Dimension(460, 230)  // 패널 크기 조정
            layout = GridBagLayout()
            border = BorderFactory.createEmptyBorder(15, 10, 15, 10)  // 상하좌우 여백 줄이기

            val gbc = GridBagConstraints().apply {
                gridx = 0
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(5, 0, 5, 0)  // 컴포넌트 간의 수직 간격을 줄임
            }

            // titleTextLabel
            titleTextLabel = JLabel(titleText, SwingConstants.CENTER).apply {
                font = MyFont.Bold(24f)  // 글자 크기 줄이기
                foreground = Color.WHITE
            }
            gbc.gridy = 0
            add(titleTextLabel, gbc)

            // subtitleTextLabel
            subtitleTextLabel = JLabel(subtitleText, SwingConstants.CENTER).apply {
                border = BorderFactory.createEmptyBorder(10, 0, 0, 0)  // 상하좌우 여백 줄이기
                font = MyFont.SemiBold(16f)  // 글자 크기 줄이기
                foreground = Color.WHITE
            }
            gbc.gridy = 1
            add(subtitleTextLabel, gbc)

            // 오전/오후 버튼 배치
            val buttonPanel = JPanel().apply {
                isOpaque = false
                layout = FlowLayout(FlowLayout.CENTER, 15, 5)  // 버튼 간격 조정
                background = MyColor.DARK_NAVY
                add(amButton)
                add(pmButton)
            }

            gbc.gridy = 2
            add(buttonPanel, gbc)

            // 드롭다운과 "까지 중지" 라벨 배치
            val comboPanel = JPanel().apply {
                isOpaque = false
                layout = FlowLayout(FlowLayout.CENTER, 5, 5)  // 여백 줄이기
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
        deselectedButton.textColor = MyColor.GREY600
        deselectedButton.borderColor = MyColor.GREY600  // 테두리 색상 설정
        deselectedButton.repaint()
    }

    // 30분단위 선택 패널 생성 함수
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
        val buttonPanel = CHRoundedPanel(30, 30).apply {
            background = Color.WHITE  // 배경을 흰색으로 설정
            border = EmptyBorder(0, 15, 0, 15)
            preferredSize = Dimension(305, 90)  // 패널 크기를 305x90으로 설정
            layout = BorderLayout()

            add(decreaseButton, BorderLayout.WEST)
            add(timeLabel, BorderLayout.CENTER)
            add(increaseButton, BorderLayout.EAST)
        }

        // 메인 패널을 둥글게 만들기 위해 RoundedPanel 사용
        val mainPanel = CHRoundedPanel(30, 30).apply {
            background = MyColor.DARK_NAVY
            preferredSize = Dimension(460, 258)  // 패널 크기를 설정
            layout = GridBagLayout()
            border = BorderFactory.createEmptyBorder(20, 15, 20, 15)  // 상하 20, 좌우 15의 마진 추가

            val gbc = GridBagConstraints().apply {
                gridx = 0
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(10, 0, 10, 0)  // 컴포넌트 간의 수직 간격
            }

            // titleTextLabel
            titleTextLabel = JLabel(titleText, SwingConstants.CENTER).apply {
                font = MyFont.Bold(24f)
                foreground = Color.WHITE
            }
            gbc.gridy = 0
            add(titleTextLabel, gbc)

            // subtitleTextLabel
            subtitleTextLabel = JLabel(subtitleText, SwingConstants.CENTER).apply {
                font = MyFont.SemiBold(16f)
                foreground = Color.WHITE
            }
            gbc.gridy = 1
            add(subtitleTextLabel, gbc)

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
