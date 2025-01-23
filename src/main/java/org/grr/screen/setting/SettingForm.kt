package org.grr.screen.setting

import SoldOutManagementDialog
import com.sun.java.accessibility.util.AWTEventMonitor.addActionListener
import org.grr.screen.setting.bottomPanel.BottomPanelForm
import org.grr.screen.setting.centerPanel.CenterPanelForm
import org.grr.screen.setting.headerPanel.HeaderPanelForm
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.FillRoundedLabel
import org.grr.widgets.custom_titlebar.SettingCustomTitlebar
import java.awt.*
import javax.swing.*

private var selectedTab: JButton? = null

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
            border = BorderFactory.createEmptyBorder(10, 40, 20, 40)
            add(Box.createVerticalStrut(20))
        }

        // 각 섹션 추가
        settingsPanel.add(HeaderPanelForm())  // 헤더

        // CardLayout을 사용할 패널 생성
        val cardLayout = CardLayout()
        val contentPanel = JPanel(cardLayout)

        val tabBarPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.LEFT, 0, 0)
            background = MyColor.DARK_NAVY

            // 버튼 생성
            val storeSettingsButton = createTabButton("가게 기본 설정", contentPanel, cardLayout, "StoreSettings").apply {
                addActionListener {
                    try {
                        cardLayout.show(contentPanel, "StoreSettings")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val soldOutButton = createTabButton("품절 관리", contentPanel, cardLayout, "SoldOutManagement").apply {
                addActionListener {
                    try {
                        // CardLayout을 사용하여 "품절 관리" 화면으로 전환
                        cardLayout.show(contentPanel, "SoldOutManagement")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val deliveryButton = createTabButton("배달대행 연동", contentPanel, cardLayout, "DeliveryIntegration").apply {
                addActionListener { cardLayout.show(contentPanel, "DeliveryIntegration") }
            }
            val posSettingsButton = createTabButton("POS기 설정", contentPanel, cardLayout, "POSSettings").apply {
                addActionListener {
                    try {
                        // CardLayout을 사용하여 "품절 관리" 화면으로 전환
                        cardLayout.show(contentPanel, "POSSettings")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            // 버튼을 탭바에 추가
            add(storeSettingsButton)
            add(soldOutButton)
            add(deliveryButton)
            add(posSettingsButton)
        }

        settingsPanel.add(tabBarPanel)

        val deliveryIntegrationPanel = JPanel().apply {
            layout = BorderLayout()
            background = MyColor.DARK_NAVY
            add(JLabel("배달대행 연동 화면").apply {
                font = MyFont.Bold(28f)
                foreground = Color.WHITE
                horizontalAlignment = SwingConstants.CENTER
            }, BorderLayout.CENTER)
        }

        // CardLayout에 각 패널 추가
        val storeSettingsPanel = CenterPanelForm()
        contentPanel.add(storeSettingsPanel, "StoreSettings")

        // "품절 관리" 화면 추가
        val soldOutManagementPanel = SoldOutManagementDialog() // SoldOutManagementDialog 내용을 JPanel로 변경한 클래스
        contentPanel.add(soldOutManagementPanel, "SoldOutManagement")

        contentPanel.add(deliveryIntegrationPanel, "DeliveryIntegration")

        val posSettingsPanel = BottomPanelForm()
        contentPanel.add(posSettingsPanel, "POSSettings")

        settingsPanel.add(contentPanel)

//        // 경계선과 "가게 기본 설정" 텍스트를 추가하는 패널 생성
//        val separatorPanel = JPanel().apply {
//            layout = BorderLayout() // BorderLayout을 사용하여 왼쪽과 오른쪽 정렬
//            background = MyColor.DARK_NAVY
//
//            // "가게 기본 설정" 텍스트 (왼쪽 정렬)
//            val label = JLabel("가게 기본 설정").apply {
//                font = MyFont.Bold(28f)
//                foreground = Color.WHITE
//                border = BorderFactory.createEmptyBorder(10, 0, 10, 20) // 텍스트 여백 추가
//            }
//            add(label, BorderLayout.WEST) // 왼쪽에 배치
//
//            // "품절 관리" 버튼 (오른쪽 정렬)
//            val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
//                background = MyColor.DARK_NAVY // 패널 배경색 설정
//                add(FillRoundedLabel(
//                    text = "품절 관리",
//                    borderColor = MyColor.LIGHT_BLUE,
//                    backgroundColor = MyColor.LIGHT_BLUE,
//                    textColor = Color.WHITE,
//                    borderRadius = 30,
//                    borderWidth = 2,
//                    textAlignment = SwingConstants.CENTER,
//                    padding = Insets(5, 20, 5, 20) // 패딩 설정
//                ).apply {
//                    font = MyFont.Bold(22f)
//                    preferredSize = Dimension(200, 50) // 버튼 크기 설정
//                    maximumSize = Dimension(200, 50)
//                    // 클릭 이벤트 처리
//                })
//            }
//            add(buttonPanel, BorderLayout.EAST) // 버튼 패널을 오른쪽에 배치
//            preferredSize = Dimension(1440, 65) // 패널 크기 설정
//            maximumSize = Dimension(Int.MAX_VALUE, 65)
//        }
//
//        // 경계선과 텍스트가 포함된 패널 추가
//        settingsPanel.add(separatorPanel)
//
//        settingsPanel.add(CenterPanelForm())
//
//        // 경계선과 "가게 기본 설정" 텍스트를 추가하는 패널 생성
//        val separatorPanel2 = JPanel().apply {
//            layout = BoxLayout(this, BoxLayout.Y_AXIS)
//
//            // "가게 기본 설정" 텍스트 추가 패널 (왼쪽 정렬을 위해 FlowLayout 사용)
//            val labelPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
//                background = MyColor.DARK_NAVY
//                preferredSize = Dimension(1440, 65)
//                maximumSize = Dimension(Int.MAX_VALUE, 65)
//                add(JLabel("POS기 설정").apply {
//                    font = MyFont.Bold(28f)
//                    foreground = Color.WHITE  // 텍스트 색상 설정
//                    border = BorderFactory.createEmptyBorder(10, 0, 10, 0)  // 텍스트 위아래 여백
//
//                })
//            }
//
//            // 경계선과 텍스트 패널을 추가
//            add(labelPanel)
//        }

//        settingsPanel.add(separatorPanel2)

//        settingsPanel.add(BottomPanelForm())

        add(settingsPanel, BorderLayout.CENTER)

        isVisible = true
    }
}

private fun createTabButton(text: String, contentPanel: JPanel, cardLayout: CardLayout, panelName: String): JButton {
    return JButton(text).apply {
        preferredSize = Dimension(200, 50)
        font = MyFont.Bold(22f)
        isFocusPainted = false
        horizontalAlignment = SwingConstants.CENTER
        border = BorderFactory.createLineBorder(Color.WHITE, 1)

        // 기본 버튼 스타일
        background = if (text == "가게 기본 설정") Color.WHITE else MyColor.DARK_NAVY
        foreground = if (text == "가게 기본 설정") MyColor.LIGHT_BLUE else Color.WHITE

        addActionListener {
            // 선택된 탭 상태 업데이트
            selectedTab?.apply {
                background = MyColor.DARK_NAVY
                foreground = Color.WHITE
            }

            // 현재 클릭된 탭 스타일 업데이트
            background = Color.WHITE
            foreground = MyColor.LIGHT_BLUE

            // 선택된 버튼 업데이트
            selectedTab = this

            // 화면 전환
            cardLayout.show(contentPanel, panelName)
        }

        // 초기 선택 상태 처리 (가게 기본 설정 버튼은 기본 선택)
        if (text == "가게 기본 설정") {
            selectedTab = this
        }
    }
}

fun main() {
    val settingForm = SettingForm()  // SettingForm 인스턴스 생성
    settingForm.isVisible = true  // 창을 화면에 표시
}
