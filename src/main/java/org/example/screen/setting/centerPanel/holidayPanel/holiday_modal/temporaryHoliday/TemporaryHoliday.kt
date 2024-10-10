package org.example.screen.setting.centerPanel.holidayPanel.holiday_modal.temporaryHoliday

import RoundedComboBox2
import com.github.lgooddatepicker.components.DatePicker
import com.github.lgooddatepicker.components.DatePickerSettings
import org.example.MyFont
import org.example.widgets.IconRoundBorder2
import java.awt.*
import java.time.format.DateTimeFormatter
import javax.swing.*
import javax.swing.border.AbstractBorder

// 둥근 테두리를 위한 클래스
class RoundedBorder(
    private var model: String
) : AbstractBorder() {

    private var arrowIcon: ImageIcon? = null

    init {
        // 아이콘 로드
        val iconUrl = Thread.currentThread().contextClassLoader.getResource("custom_arrow_icon.png")
        if (iconUrl != null) {
            val originalIcon = ImageIcon(iconUrl)
            // 아이콘 크기 변경 (13x10)
            arrowIcon = ImageIcon(originalIcon.image.getScaledInstance(13, 10, Image.SCALE_SMOOTH))
        } else {
            println("Icon not found: custom_arrow_icon.png")
        }
    }

    fun setModelText(text: String) {
        model = text
    }

    override fun paintBorder(
        c: Component?, g: Graphics?, x: Int, y: Int, width: Int, height: Int
    ) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        g2.color = Color.WHITE
        g2.fillRoundRect(0, 0, width, height, 30, 30)

        g2.color = Color(13, 130, 191)
        g2.stroke = BasicStroke(2f)
        g2.drawRoundRect(0, 0, width - 1, height - 1, 30, 30)

        // 텍스트 그리기
        g2.font = Font("Arial", Font.BOLD, 18)  // 폰트 설정
        g2.color = Color.DARK_GRAY  // 텍스트 색상 설정
        val fm = g2.fontMetrics
        val textX = 12  // 텍스트를 가운데 정렬
        val textY = (height + fm.ascent) / 2 - 2
        g2.drawString(model, textX, textY)  // 텍스트 그리기

        // 화살표 아이콘 그리기
        arrowIcon?.let { icon ->
            val iconX = width - icon.iconWidth - 10  // 아이콘의 X 위치
            val iconY = (height - icon.iconHeight) / 2  // 아이콘의 Y 위치
            icon.paintIcon(c, g2, iconX, iconY)
        }
    }
}

class TemporaryHoliday : JPanel() {
    private val holidayListPanel = JPanel(GridBagLayout())  // 임시 휴무 리스트를 추가할 패널
    private var holidayCount = 0

    init {
        layout = GridBagLayout()
        background = Color.WHITE
        border = null

        val gbc = GridBagConstraints().apply {
            gridy = 0
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(5, 5, 5, 5)
        }
        val startDatePicker = createDatePicker()

        // 시작일과 종료일에 DatePicker 추가
        gbc.gridx = 0
        add(startDatePicker, gbc)

        gbc.gridx = 1
        add(JLabel("~").apply { font = Font("Arial", Font.BOLD, 20) }, gbc)

        val endDatePicker = createDatePicker()

        gbc.gridx = 2
        add(endDatePicker, gbc)

        // 추가 버튼
        gbc.gridx = 3
        gbc.fill = GridBagConstraints.NONE
        add(IconRoundBorder2.createRoundedButton("/+.png", Color(13, 130, 191)).apply {
            preferredSize = Dimension(50, 50)

            addActionListener {
                if (holidayCount >= 3) {
                    JOptionPane.showMessageDialog(null, "최대 3개의 임시 휴무만 설정할 수 있습니다.")
                    return@addActionListener
                }

                // DatePicker에서 선택된 날짜 가져오기
                val startDate = startDatePicker.date
                val endDate = endDatePicker.date

                if (startDate == null || endDate == null) {
                    JOptionPane.showMessageDialog(null, "날짜를 선택하세요.")
                    return@addActionListener
                }

                if (startDate.isAfter(endDate)) {
                    JOptionPane.showMessageDialog(null, "시작 날짜는 종료 날짜보다 앞서야 합니다.")
                    return@addActionListener
                }

                // 임시 휴무 추가
                addTemporaryHoliday(startDate.toString(), endDate.toString())
            }
        }, gbc)

        // 임시 휴무 리스트 패널 추가
        gbc.gridy = 1
        gbc.gridx = 0
        gbc.gridwidth = 4
        add(holidayListPanel, gbc)

        holidayListPanel.background = Color.WHITE
    }

    // DatePicker를 직접 UI에 추가하는 메서드
    private fun createDatePicker(): DatePicker {
        val settings = DatePickerSettings().apply {
            setFormatForDatesCommonEra("yyyy-MM-dd")
            fontValidDate = MyFont.Bold(18f)
            fontCalendarDateLabels = MyFont.Bold(16f)
            isOpaque = false
        }
        val datePicker = DatePicker(settings)
        datePicker.isOpaque = false

        // 텍스트 필드 숨기기
        datePicker.componentDateTextField.isVisible = false

        // 토글 버튼에 RoundedComboBox 스타일 적용
        val toggleButton = datePicker.componentToggleCalendarButton
        toggleButton.preferredSize = Dimension(160, 50)
        toggleButton.isOpaque = false
        toggleButton.foreground = Color.BLACK
        toggleButton.isContentAreaFilled = false

        // RoundedBorder 설정
        val roundedBorder = RoundedBorder("날짜 선택")
        toggleButton.border = roundedBorder

        // 날짜 선택 시 버튼에 날짜 표시
        datePicker.addDateChangeListener { dateEvent ->
            val selectedDate = dateEvent.newDate
            if (selectedDate != null) {
                roundedBorder.setModelText(selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            } else {
                roundedBorder.setModelText("날짜 선택")
            }
            toggleButton.repaint()
        }

        return datePicker
    }

    // 선택된 임시 휴무를 추가하는 메서드
    private fun addTemporaryHoliday(startDate: String, endDate: String) {
        val gbc = GridBagConstraints().apply {
            gridy = holidayCount
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(5, 5, 5, 5)
        }

        // 시작 날짜 라벨 추가
        val startDateLabel = RoundedComboBox2(startDate, Color(255, 177, 177), 2f)
        gbc.gridx = 0
        holidayListPanel.add(startDateLabel, gbc)

        // "~" 라벨 추가
        val tildeLabel = JLabel("~").apply {
            font = MyFont.Bold(20f)
        }
        gbc.gridx = 1
        holidayListPanel.add(tildeLabel, gbc)

        // 종료 날짜 라벨 추가
        val endDateLabel = RoundedComboBox2(endDate, Color(255, 177, 177), 2f)
        gbc.gridx = 2
        holidayListPanel.add(endDateLabel, gbc)

        // 삭제 버튼 추가
        val removeButton = IconRoundBorder2.createRoundedButton("/x.png", Color(255, 177, 177)).apply {
            preferredSize = Dimension(50, 50)
            addActionListener {
                holidayListPanel.remove(startDateLabel)
                holidayListPanel.remove(tildeLabel)
                holidayListPanel.remove(endDateLabel)
                holidayListPanel.remove(this)
                holidayCount--
                updateHolidayItems() // 삭제 후 레이아웃 갱신
            }
        }
        gbc.gridx = 3
        gbc.gridwidth = 1
        holidayListPanel.add(removeButton, gbc)

        holidayListPanel.revalidate()
        holidayListPanel.repaint()
        holidayCount++
    }

    // 전체 항목의 레이아웃을 다시 설정하는 메서드
    private fun updateHolidayItems() {
        val components = holidayListPanel.components
        holidayListPanel.removeAll() // 기존 항목 모두 제거
        holidayCount = 0 // 항목 개수 초기화

        // 다시 항목을 추가하며 레이아웃 갱신
        for (i in components.indices step 4) { // 4개의 컴포넌트 (startDateLabel, tildeLabel, endDateLabel, removeButton)를 한 세트로 처리
            val startDateLabel = components[i] as RoundedComboBox2
            val tildeLabel = components[i + 1] as JLabel
            val endDateLabel = components[i + 2] as RoundedComboBox2
            val removeButton = components[i + 3] as JButton
            val gbc = GridBagConstraints().apply {
                gridy = holidayCount
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(5, 5, 5, 5)
            }

            // 시작 날짜 라벨 다시 추가
            gbc.gridx = 0
            holidayListPanel.add(startDateLabel, gbc)

            // "~" 라벨 다시 추가
            gbc.gridx = 1
            holidayListPanel.add(tildeLabel, gbc)

            // 종료 날짜 라벨 다시 추가
            gbc.gridx = 2
            holidayListPanel.add(endDateLabel, gbc)

            // 삭제 버튼 다시 추가
            gbc.gridx = 3
            holidayListPanel.add(removeButton, gbc)

            holidayCount++ // 항목 수 증가
        }

        // 패널 레이아웃을 다시 계산하고 새로 고침
        holidayListPanel.revalidate()
        holidayListPanel.repaint()
    }
}
