package org.example

import org.example.screen.setting.centerPanel.CenterPanelForm
import org.example.screen.setting.headerPanel.HeaderPanelForm
import org.example.style.MyColor
import org.example.widgets.custom_titlebar.SettingCustomTitlebar
import java.awt.*
import javax.swing.*

class SettingForm : JFrame() {

    init {
        // 기본 타이틀바 제거
        isUndecorated = true

        // JFrame 기본 설정
        title = "POS 꼬르륵 설정"
        setSize(1440, 1024)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)

        // 레이아웃 설정
        layout = BorderLayout()

        // 상단에 커스텀 타이틀바 추가
        val customTitleBar = SettingCustomTitlebar(this)
        add(customTitleBar, BorderLayout.NORTH)

        // 메인 패널
        val settingsPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = MyColor.DARK_NAVY
            border = BorderFactory.createEmptyBorder(20, 50, 20, 50)
            add(Box.createVerticalStrut(20))
        }

        // 각 섹션 추가
        settingsPanel.add(HeaderPanelForm())  // 헤더

        // 경계선과 "가게 기본 설정" 텍스트를 추가하는 패널 생성
        val separatorPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)

            // 경계선(JSeparator) 추가
            add(JSeparator(SwingConstants.HORIZONTAL).apply {
                background = Color.WHITE  // 경계선 색상 설정
                preferredSize = Dimension(1440, 1)  // 경계선 두께 설정
                maximumSize = Dimension(1440, 1)  // 최대 크기도 설정
                minimumSize = Dimension(1440, 1)  // 최소 크기도 설정
            })

            // "가게 기본 설정" 텍스트 추가 패널 (왼쪽 정렬을 위해 FlowLayout 사용)
            val labelPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
                background = MyColor.DARK_NAVY
//                background = Color.RED
                preferredSize = Dimension(1440, 65)  // 경계선 두께 설정
                maximumSize = Dimension(1440, 65)  // 최대 크기도 설정
                minimumSize = Dimension(1440, 65)  // 최소 크기도 설정
                add(JLabel("가게 기본 설정").apply {
                    font = MyFont.Bold(28f)
                    foreground = Color.WHITE  // 텍스트 색상 설정
                    border = BorderFactory.createEmptyBorder(10, 0, 10, 0)  // 텍스트 위아래 여백

                })
            }

            // 경계선과 텍스트 패널을 추가
            add(labelPanel)
        }
        // 경계선과 텍스트가 포함된 패널 추가
        settingsPanel.add(separatorPanel)

        settingsPanel.add(CenterPanelForm())

        add(settingsPanel, BorderLayout.CENTER)

        isVisible = true
    }
}

fun main() {
    val settingForm = SettingForm()  // SettingForm 인스턴스 생성
    settingForm.isVisible = true  // 창을 화면에 표시
}
