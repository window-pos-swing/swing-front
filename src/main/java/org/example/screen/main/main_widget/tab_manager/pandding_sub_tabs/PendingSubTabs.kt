package org.example.screen.main.main_widget.tab_manager.pandding_sub_tabs

import org.example.CustomTabbedPane
import org.example.style.MyColor
import org.example.widgets.SelectButtonRoundedBorder
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

// PendingSubTabs 클래스에서 버튼 생성 및 관리

class PendingSubTabs(private val tabbedPane: CustomTabbedPane) : JPanel() {

    // 현재 선택된 버튼을 저장할 변수
    private var selectedButton: SelectButtonRoundedBorder? = null

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE

        val buttonPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT

            preferredSize = Dimension(750, 100)
            maximumSize = Dimension(750, 100)
            minimumSize = Dimension(750, 100)
            background = Color.WHITE

            // SelectButtonRoundedBorder 사용하여 버튼 생성
            val allOrdersButton = SelectButtonRoundedBorder(50).apply {
                createRoundedButton(
                    "전체보기  41건",
                    MyColor.SELECTED_BACKGROUND_COLOR,
                    MyColor.UNSELECTED_BACKGROUND_COLOR,
                    MyColor.SELECTED_TEXT_COLOR,
                    MyColor.GREY600,
                    Dimension(230, 60)
                )
            }
            val deliveryButton = SelectButtonRoundedBorder(50).apply {
                createRoundedButton(
                    "배달  26건",
                    MyColor.SELECTED_BACKGROUND_COLOR,
                    MyColor.UNSELECTED_BACKGROUND_COLOR,
                    MyColor.SELECTED_TEXT_COLOR,
                    MyColor.GREY600,
                    Dimension(230, 60)
                )
            }
            val takeoutButton = SelectButtonRoundedBorder(50).apply {
                createRoundedButton(
                    "포장  15건",
                    MyColor.SELECTED_BACKGROUND_COLOR,
                    MyColor.UNSELECTED_BACKGROUND_COLOR,
                    MyColor.SELECTED_TEXT_COLOR,
                    MyColor.GREY600,
                    Dimension(230, 60)
                )
            }

            // 버튼 간 간격 추가
            add(allOrdersButton.button)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(deliveryButton.button)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(takeoutButton.button)

            // 버튼 선택 로직
            fun setSelectedButton(button: SelectButtonRoundedBorder) {
                selectedButton?.setButtonStyle(false)  // 이전 선택된 버튼을 선택 해제 상태로 설정
                button.setButtonStyle(true)  // 현재 선택된 버튼을 선택 상태로 설정
                selectedButton = button
            }

            // 버튼에 클릭 리스너 추가
            allOrdersButton.button.addActionListener {
                setSelectedButton(allOrdersButton)
                tabbedPane.filterPendingOrders()
            }
            deliveryButton.button.addActionListener {
                setSelectedButton(deliveryButton)
                tabbedPane.filterPendingOrders("DELIVERY")
            }
            takeoutButton.button.addActionListener {
                setSelectedButton(takeoutButton)
                tabbedPane.filterPendingOrders("TAKEOUT")
            }

            // 초기 선택된 버튼 설정 (전체보기)
            setSelectedButton(allOrdersButton)
        }

        add(buttonPanel)
        add(JScrollPane(tabbedPane.pendingOrdersPanel).apply {
            border = BorderFactory.createEmptyBorder(0, 20, 20, 20)  // JScrollPane 테두리 제거
            viewportBorder = null  // viewport 테두리 제거
            isOpaque = false  // JScrollPane 불투명도 설정
            viewport.isOpaque = false  // viewport 불투명도 설정
        })

        // 기본 선택: 전체보기
        tabbedPane.filterPendingOrders()
    }
}