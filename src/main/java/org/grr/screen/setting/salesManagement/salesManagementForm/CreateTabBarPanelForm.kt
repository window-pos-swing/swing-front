package org.grr.screen.setting.salesManagement.salesManagementForm

import com.github.lgooddatepicker.components.DatePicker
import com.github.lgooddatepicker.components.DatePickerSettings
import org.grr.screen.setting.centerPanel.holidayPanel.holiday_modal.temporaryHoliday.RoundedBorder
import org.grr.screen.setting.salesManagement.ShareData
import org.grr.screen.setting.salesManagement.getTodayDate
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.RoundedButton
import java.awt.*
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.swing.*
import javax.swing.border.AbstractBorder

class CreateTabBarPanelForm : JPanel() {
    val yesterdayButton = RoundedButton("어제")
    val todayButton = RoundedButton("오늘")
    val startEndDatePicker: DatePicker
    var startDate = getTodayDate()
    var endDate = getTodayDate()

    // 버튼 리스트를 관리하기 위한 리스트
    private val buttons = mutableListOf<RoundedButton>()

    // 아이콘 파일 로드
    private val resourceUrl = File("src/main/resources/Vector.png").toURI().toURL()
    private val imageIcon = ImageIcon(resourceUrl)
    private val scaledIcon = ImageIcon(imageIcon.image.getScaledInstance(23, 23, Image.SCALE_SMOOTH))

    private val whiteIconUrl = File("src/main/resources/Vector_white.png").toURI().toURL()
    private val whiteImageIcon = ImageIcon(whiteIconUrl)
    private val whiteScaledIcon = ImageIcon(whiteImageIcon.image.getScaledInstance(23, 23, Image.SCALE_SMOOTH))

    // == [날짜 선택 전역 선언] ==
    val roundedBorder = SalesDateRoundedBorder("날짜 선택")
    val dateSettings = DatePickerSettings().apply {
        setFormatForDatesCommonEra("yyyy-MM-dd")
        fontValidDate = MyFont.Bold(18f)
        fontCalendarDateLabels = MyFont.Bold(16f)
        isOpaque = false
    }
    val datePicker = DatePicker(dateSettings).apply {
        isOpaque = false
        componentDateTextField.isVisible = false
    }
    var toggleButton = datePicker.componentToggleCalendarButton

    init {
        layout = FlowLayout(FlowLayout.LEFT, 10, 0) // 버튼 간의 간격 설정
        background = Color.WHITE

        startEndDatePicker = createStartEndDatePicker()

        // 초기화 함수 호출
        initializeComponents()
    }

    private fun initializeComponents() {
        // 버튼 색상 초기화 함수
        fun resetButtonColors() {
            buttons.forEach {
                it.setCustomBackground(MyColor.LIGHT_GREY)
                it.foreground = Color.GRAY // 기본 글씨 색상
            }
        }

        yesterdayButton.apply {
            preferredSize = Dimension(150, 60)
            font = MyFont.Bold(22f)
            setCustomBackground(MyColor.LIGHT_GREY)
            foreground = Color.GRAY

            addActionListener {
                resetButtonColors()
                setCustomBackground(MyColor.LIGHT_BLUE)
                foreground = Color.WHITE // 선택된 상태 글씨 색상
                ShareData.selectedDateLabel.text = "어제"
                roundedBorder.setModelText("날짜 선택", Color.BLACK , MyColor.LIGHT_GREY)
                toggleButton.repaint()
            }
        }

        todayButton.apply {
            preferredSize = Dimension(150, 60)
            font = MyFont.Bold(22f)
            setCustomBackground(MyColor.LIGHT_BLUE)
            foreground = Color.WHITE

            addActionListener {
                resetButtonColors()
                setCustomBackground(MyColor.LIGHT_BLUE)
                foreground = Color.WHITE
                ShareData.selectedDateLabel.text = "오늘"
                roundedBorder.setModelText("날짜 선택", Color.BLACK , MyColor.LIGHT_GREY)
                toggleButton.repaint()
            }
        }

        // 버튼 리스트에 추가
        buttons.add(yesterdayButton)
        buttons.add(todayButton)

        // 요소 추가
        add(yesterdayButton)
        add(todayButton)
        add(startEndDatePicker)
    }

    fun createStartEndDatePicker() : DatePicker{
        toggleButton = datePicker.componentToggleCalendarButton
        toggleButton.preferredSize = Dimension(370, 60)
        toggleButton.isOpaque = false
        toggleButton.isContentAreaFilled = false

        // RoundedBorder 설정

        toggleButton.border = roundedBorder

        var isFirstSelection = true
        // 날짜 선택 리스너
        datePicker.addDateChangeListener { dateEvent ->
            val selectedDate = dateEvent.newDate
            if (selectedDate != null) {
                if (isFirstSelection) {
                    startDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    isFirstSelection = false
                    roundedBorder.setModelText(selectedDate.format(DateTimeFormatter.ofPattern(startDate)), Color.GRAY , MyColor.LIGHT_GREY)
                    SwingUtilities.invokeLater {
                        datePicker.openPopup()
                    }
                } else {
                    endDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    // ✅ "종료 날짜가 시작 날짜보다 이전일 경우 선택 불가"
                    if (!isBefore(startDate, endDate)) {
                        JOptionPane.showMessageDialog(
                            null,
                            "🚨 종료 날짜는 시작 날짜보다 이전일 수 없습니다.\n다시 선택해주세요.",
                            "날짜 선택 오류",
                            JOptionPane.WARNING_MESSAGE
                        )
                        isFirstSelection = true  // 다시 시작 날짜 선택하게 유도
                        return@addDateChangeListener
                    }
                    roundedBorder.setModelText(selectedDate.format(DateTimeFormatter.ofPattern( "${startDate} ~ ${endDate}" )),Color.WHITE , MyColor.LIGHT_BLUE)
                    // 어제, 오늘 버튼 스타일 리셋
                    buttons.forEach {
                        it.setCustomBackground(MyColor.LIGHT_GREY)
                        it.foreground = Color.GRAY // 기본 글씨 색상
                    }
                    isFirstSelection = true
                    ShareData.selectedDateLabel.text = "${startDate} - ${endDate}"
//                    println("선택 날짜 : ${startDate} - ${endDate}")
                }
            }
            toggleButton.repaint()
        }
        return datePicker
    }

    fun isBefore(startDate: String, endDate: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val start = LocalDate.parse(startDate, formatter)
        val end = LocalDate.parse(endDate, formatter)
        return start.isBefore(end) || start.isEqual(end)
    }

    class SalesDateRoundedBorder(
        private var model: String
    ) : AbstractBorder() {
        private var textColor: Color = Color.GRAY  // ✅ 기본 텍스트 색상
        private var backgroundColor: Color = MyColor.LIGHT_GREY  // ✅ 기본 텍스트 색상

        fun setModelText(text: String , _textColor : Color, _backgroundColor : Color) {
            model = text
            textColor = _textColor
            backgroundColor = _backgroundColor
        }

        override fun paintBorder(
            c: Component?, g: Graphics?, x: Int, y: Int, width: Int, height: Int
        ) {
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            g2.color = backgroundColor
            g2.fillRoundRect(0, 0, width, height, 30, 30)

            // 텍스트 그리기
            g2.font = MyFont.Bold(20f) // 폰트 설정
            g2.color = textColor // 텍스트 색상 설정
            val fm = g2.fontMetrics
            val textWidth = fm.stringWidth(model)
            val textX = (width - textWidth) / 2  // 텍스트를 가운데 정렬
            val textY = (height + fm.ascent) / 2 - 2
            g2.drawString(model, textX, textY)  // 텍스트 그리기
        }
    }
}