package org.grr.screen.setting.salesManagement.salesManagementForm

import org.grr.screen.setting.salesManagement.ShareData
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.RoundedButton
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Image
import java.io.File
import javax.swing.ImageIcon
import javax.swing.JPanel

class CreateTabBarPanelForm : JPanel() {

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

        val yesterdayButton = RoundedButton("어제").apply {
            preferredSize = Dimension(150, 60)
            font = MyFont.Bold(22f)
            setCustomBackground(MyColor.LIGHT_GREY)
            foreground = Color.GRAY

            addActionListener {
                resetButtonColors()
                setCustomBackground(MyColor.LIGHT_BLUE)
                foreground = Color.WHITE // 선택된 상태 글씨 색상
                ShareData.selectedDateLabel.text = "어제"
                println("어제 버튼 클릭됨")
            }
        }

        val todayButton = RoundedButton("오늘").apply {
            preferredSize = Dimension(150, 60)
            font = MyFont.Bold(22f)
            setCustomBackground(MyColor.LIGHT_GREY)
            foreground = Color.GRAY

            addActionListener {
                resetButtonColors()
                setCustomBackground(MyColor.LIGHT_BLUE)
                foreground = Color.WHITE // 선택된 상태 글씨 색상
                ShareData.selectedDateLabel.text = "오늘"
                println("오늘 버튼 클릭됨")
            }
        }

        // 날짜 선택 버튼
        ShareData.datePickerButton = RoundedButton("2025-01-24 ~ 2025-01-24").apply {
            preferredSize = Dimension(370, 60)
            font = MyFont.Bold(22f)
            setCustomBackground(MyColor.LIGHT_GREY)
            foreground = Color.GRAY

            icon = scaledIcon

            addActionListener {
                resetButtonColors().apply {

                }
                setCustomBackground(MyColor.LIGHT_BLUE)
                foreground = Color.WHITE // 선택된 상태 글씨 색상
                ShareData.selectedDateLabel.text = "2025-01-24 ~ 2025-01-24"

                icon = whiteScaledIcon

                println("날짜 선택 버튼 클릭됨")
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
}