package org.grr.screen.setting.salesManagement.salesManagementForm

import com.github.lgooddatepicker.components.DatePicker
import com.github.lgooddatepicker.components.DatePickerSettings
import org.grr.screen.setting.centerPanel.holidayPanel.holiday_modal.temporaryHoliday.RoundedBorder
import org.grr.screen.setting.salesManagement.ShareData
import org.grr.screen.setting.salesManagement.getTodayDate
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.RoundedButton
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Image
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JPanel

class CreateTabBarPanelForm : JPanel() {
    val yesterdayButton = RoundedButton("어제")
    val todayButton = RoundedButton("오늘")
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

    init {
        layout = FlowLayout(FlowLayout.LEFT, 10, 0) // 버튼 간의 간격 설정
        background = Color.WHITE

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
            // 날짜 선택 버튼 아이콘을 원래대로 복원
            ShareData.datePickerButton.icon = scaledIcon
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
            }
        }

        // 날짜 선택 버튼
        ShareData.datePickerButton = RoundedButton("$startDate ~ $endDate").apply {
            preferredSize = Dimension(370, 60)
            font = MyFont.Bold(22f)
            setCustomBackground(MyColor.LIGHT_GREY)
            foreground = Color.GRAY

            icon = scaledIcon

            addActionListener {
                resetButtonColors().apply {
                    createDatePicker()
                }
                setCustomBackground(MyColor.LIGHT_BLUE)
                foreground = Color.WHITE // 선택된 상태 글씨 색상
                ShareData.selectedDateLabel.text = "$startDate ~ $endDate"
                icon = whiteScaledIcon
            }
        }

        // 버튼 리스트에 추가
        buttons.add(yesterdayButton)
        buttons.add(todayButton)
        buttons.add(ShareData.datePickerButton)

        // 요소 추가
        add(yesterdayButton)
        add(todayButton)
        add(ShareData.datePickerButton)
    }

    private fun createDatePicker() {
        val settings = DatePickerSettings().apply {
            setFormatForDatesCommonEra("yyyy-MM-dd")
            fontValidDate = MyFont.Bold(18f)
            fontCalendarDateLabels = MyFont.Bold(16f)
            isOpaque = false
        }

        // 첫 번째 캘린더 (시작 날짜 선택)
        val startDatePicker = DatePicker(settings).apply {
            isOpaque = false
            componentDateTextField.isVisible = false
        }

        // 두 번째 캘린더 (종료 날짜 선택)
        val endDatePicker = DatePicker(settings).apply {
            isOpaque = false
            componentDateTextField.isVisible = false
        }

        // 첫 번째 캘린더에서 날짜 선택 시 두 번째 캘린더 띄우기
        startDatePicker.addDateChangeListener { dateEvent ->
            val selectedDate = dateEvent.newDate
            if (selectedDate != null) {
                startDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                // 두 번째 캘린더 띄우기
                JOptionPane.showMessageDialog(null, endDatePicker, "종료 날짜 선택", JOptionPane.PLAIN_MESSAGE)

                val endSelectedDate = endDatePicker.date
                if (endSelectedDate != null) {
                    endDate = endSelectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                }

                // 버튼 텍스트 업데이트
                ShareData.datePickerButton.text = "$startDate ~ $endDate"
            }
        }

        // 첫 번째 캘린더 띄우기
        JOptionPane.showMessageDialog(null, startDatePicker, "시작 날짜 선택", JOptionPane.PLAIN_MESSAGE)
    }
}