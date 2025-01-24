package org.grr.screen.setting.deliveryIntegration

import CustomToggleButton2
import com.sun.java.accessibility.util.AWTEventMonitor.addActionListener
import org.grr.screen.setting.deliveryIntegration.deliveryIntegration_modal.DeliveryIntegrationConnectModalDialog
import org.grr.screen.setting.deliveryIntegration.deliveryIntegration_modal.DeliveryIntegrationModalDialog
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.RoundedButton
import org.grr.widgets.RoundedPanel
import java.awt.*
import javax.swing.*

class DeliveryPanelForm : JPanel() {

    private lateinit var setButton: RoundedButton  // 배달대행사 추가버튼

    init {
        layout = BorderLayout()
        background = MyColor.DARK_NAVY
        border = BorderFactory.createEmptyBorder(20, 0, 0, 0)

        // 둥근 패널 생성
        val roundedPanel = RoundedPanel(30, 30).apply {
            layout = BorderLayout()
            background = MyColor.LOGIN_TITLEBAR // 둥근 패널 배경색
            preferredSize = Dimension(1380, 650)
            maximumSize = Dimension(Int.MAX_VALUE, 650)  // 최대 크기도 설정
            border = BorderFactory.createEmptyBorder(20, 30, 40, 30) // 내부 여백 설정
        }

        val roundedPanel2 = RoundedPanel(10, 10).apply {
            layout = BorderLayout()
            background = MyColor.DARK_NAVY // 둥근 패널 배경색
            preferredSize = Dimension(1300, 500)
            maximumSize = Dimension(Int.MAX_VALUE, 500)  // 최대 크기도 설정
            border = BorderFactory.createEmptyBorder(0, 10, 0, 10) // 내부 여백 설정
        }

        // 배달대행사 데이터를 표시할 패널
        val deliveryPanel: JPanel = JPanel().apply {
            layout = GridLayout(5, 1, 0, 5) // 고정된 5개의 행
            background = MyColor.DARK_NAVY // 배경색 설정
            border = BorderFactory.createEmptyBorder(10, 10, 0, 10) // 내부 여백
        }

        // 배달대행사 데이터 생성 함수
        fun createDeliveryAgencyRow(name: String): JPanel {
            val rowPanel = JPanel().apply {
                layout = BorderLayout()
                background = MyColor.DARK_NAVY // 배경색 설정
                border = BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE) // 하단 구분선
                preferredSize = Dimension(1300, 50) // 패널 크기 설정
            }

            val nameLabel = JLabel(name).apply {
                font = MyFont.Bold(22f)
                foreground = Color.WHITE
                verticalAlignment = SwingConstants.CENTER
                horizontalAlignment = SwingConstants.LEFT
            }

            val buttonPanel = JPanel().apply {
                layout = FlowLayout(FlowLayout.RIGHT, 20, 10)
                background = MyColor.DARK_NAVY
                border = BorderFactory.createEmptyBorder(10, 0, 0, 0)

                add(CustomToggleButton2().apply {
                    preferredSize = Dimension(330, 55)
                    addActionListener {
                        // 토글 버튼 상태 변경 처리 (선택 여부에 따라)
                        println("$name 상태: ${if (isSelected) "사용" else "미사용"}")
                    }
                })
                add(RoundedButton("삭제").apply {
                    preferredSize = Dimension(90, 50)
                    setCustomBackground(Color.LIGHT_GRAY)
                    foreground = Color.BLACK
                    font = MyFont.Regular(18f)
                    addActionListener {
                        deliveryPanel.remove(rowPanel) // 행 삭제
                        deliveryPanel.revalidate() // UI 갱신
                        deliveryPanel.repaint()
                    }
                })
            }

            rowPanel.add(nameLabel, BorderLayout.WEST) // 이름 레이블 왼쪽 배치
            rowPanel.add(buttonPanel, BorderLayout.CENTER) // 버튼 패널 오른쪽 배치

            return rowPanel
        }

        // 배달대행사 추가 함수
        fun addDeliveryAgency(name: String) {
            if (deliveryPanel.componentCount < 5) {
                val newAgencyRow = createDeliveryAgencyRow(name) // 새 배달대행사 행 생성
                deliveryPanel.add(newAgencyRow) // 패널에 추가
                deliveryPanel.revalidate() // UI 갱신
                deliveryPanel.repaint()
            }
        }

        setButton = RoundedButton("배달대행사 추가").apply {
            font = MyFont.Bold(22f)  // 폰트 설정
            preferredSize = Dimension(260, 60)  // 버튼 크기 설정
            addActionListener {
                if (deliveryPanel.componentCount >= 5) {
                    // 모달을 띄워 경고 메시지 표시
                    JOptionPane.showMessageDialog(
                        this@DeliveryPanelForm,
                        "최대 5개의 배달대행사만 추가할 수 있습니다.",
                        "추가 불가",
                        JOptionPane.WARNING_MESSAGE
                    )
                    return@addActionListener
                }

                val parentFrame = SwingUtilities.getWindowAncestor(this) as? JFrame
                if (parentFrame != null) {
                    // 배달대행사 선택 다이얼로그
                    DeliveryIntegrationModalDialog(parentFrame, "배달대행사 선택") { isConfirmed ->
                        addDeliveryAgency(isConfirmed) // 패널에 추가
                    }.isVisible = true
                }
            }
        }

        val topPanel = JPanel().apply {
            border = BorderFactory.createEmptyBorder(0, 0, 20, 0)
            layout = FlowLayout(FlowLayout.LEFT, 0, 0)  // 오른쪽 정렬
            isOpaque = false
            add(setButton)
        }

        // 예시 배달대행사 데이터 추가
        val deliveryAgencies = listOf("부릉", "스파이더", "젠딜리", "부릉부릉", "생각대로")
        deliveryAgencies.forEach {
            deliveryPanel.add(createDeliveryAgencyRow(it))
        }
        roundedPanel2.add(deliveryPanel, BorderLayout.CENTER)

        roundedPanel.add(topPanel, BorderLayout.NORTH)
        roundedPanel.add(roundedPanel2, BorderLayout.CENTER)

        add(roundedPanel, BorderLayout.NORTH)
    }
}