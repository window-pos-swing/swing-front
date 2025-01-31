package org.grr.screen.setting

import SoldOutManagementDialog
import com.sun.java.accessibility.util.AWTEventMonitor.addActionListener
import org.grr.screen.setting.bottomPanel.BottomPanelForm
import org.grr.screen.setting.centerPanel.CenterPanelForm
import org.grr.screen.setting.deliveryIntegration.DeliveryPanelForm
import org.grr.screen.setting.headerPanel.HeaderPanelForm
import org.grr.screen.setting.salesManagement.SalesManagementModalDialog
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.FillRoundedLabel
import org.grr.widgets.NoneRoundedButton
import org.grr.widgets.RoundedButton
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
                addActionListener {
                    try {
                        // CardLayout을 사용하여 "품절 관리" 화면으로 전환
                        cardLayout.show(contentPanel, "DeliveryIntegration")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
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
            add(Box.createHorizontalGlue()) // 남은 공간을 밀어냄
        }

        val salesManagementPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.RIGHT, 0, 0) // 버튼을 오른쪽으로 정렬
            background = MyColor.DARK_NAVY

            val salesManagementButton = NoneRoundedButton("매출 관리").apply {
                preferredSize = Dimension(200, 50) // 버튼 크기 설정
                setCustomBackground(MyColor.DARK_NAVY)
                foreground = Color.WHITE // 텍스트 색상
                font = MyFont.Bold(22f) // 폰트 설정
                isFocusPainted = false

                // 이미지 추가
                val resourceUrl = javaClass.classLoader.getResource("solar_round-graph-broken.png") // 이미지 리소스 경로
                val imageIcon = ImageIcon(resourceUrl)
                val scaledIcon = ImageIcon(
                    imageIcon.image.getScaledInstance(31, 31, Image.SCALE_SMOOTH)
                ) // 이미지 크기 조정
                icon = scaledIcon // 버튼 아이콘 설정
                iconTextGap = 10 // 텍스트와 이미지 간격 설정
                horizontalAlignment = SwingConstants.CENTER // 텍스트 및 아이콘 정렬
                addActionListener {
                    val parentFrame = SwingUtilities.getWindowAncestor(this) as? JFrame
                    if (parentFrame != null) {
                        SalesManagementModalDialog(parentFrame, "매출 관리") { isConfirmed ->

                        }.isVisible = true
                    }
                }
            }
            add(salesManagementButton)
        }

        val combinedTabPanel = JPanel().apply {
            layout = BorderLayout()
            background = MyColor.DARK_NAVY

            add(tabBarPanel, BorderLayout.CENTER) // 기존 탭 버튼
            add(salesManagementPanel, BorderLayout.EAST) // 매출 관리 버튼을 오른쪽에 배치
        }

        settingsPanel.add(combinedTabPanel) // 상단 탭바 패널 추가

        // CardLayout에 각 패널 추가
        val storeSettingsPanel = CenterPanelForm()
        contentPanel.add(storeSettingsPanel, "StoreSettings")

        // "품절 관리" 화면 추가
        val soldOutManagementPanel = SoldOutManagementDialog() // SoldOutManagementDialog 내용을 JPanel로 변경한 클래스
        contentPanel.add(soldOutManagementPanel, "SoldOutManagement")

        val deliveryIntegrationPanel = DeliveryPanelForm()
        contentPanel.add(deliveryIntegrationPanel, "DeliveryIntegration")

        val posSettingsPanel = BottomPanelForm()
        contentPanel.add(posSettingsPanel, "POSSettings")

        settingsPanel.add(contentPanel)

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
