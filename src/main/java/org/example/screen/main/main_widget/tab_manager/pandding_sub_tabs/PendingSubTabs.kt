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

    // 선택된 색상 및 기본 색상 정의
    private val SELECTED_TEXT_COLOR = Color.WHITE
    private val UNSELECTED_TEXT_COLOR = Color(137, 137, 137)
    private val SELECTED_BACKGROUND_COLOR = MyColor.DARK_NAVY
    private val UNSELECTED_BACKGROUND_COLOR = MyColor.LIGHT_GREY

    // 현재 선택된 버튼을 저장할 변수
    private var selectedButton: JButton? = null

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


            // RoundedBorder2 클래스에서 버튼 생성
            val selectButtonRoundedBorder = SelectButtonRoundedBorder(30)
            val allOrdersButton = selectButtonRoundedBorder.createRoundedButton(
                "전체보기  41건",
                SELECTED_BACKGROUND_COLOR,
                UNSELECTED_BACKGROUND_COLOR,
                SELECTED_TEXT_COLOR,
                UNSELECTED_TEXT_COLOR
            )
            val deliveryButton = selectButtonRoundedBorder.createRoundedButton(
                "배달  26건",
                SELECTED_BACKGROUND_COLOR,
                UNSELECTED_BACKGROUND_COLOR,
                SELECTED_TEXT_COLOR,
                UNSELECTED_TEXT_COLOR
            )
            val takeoutButton = selectButtonRoundedBorder.createRoundedButton(
                "포장  15건",
                SELECTED_BACKGROUND_COLOR,
                UNSELECTED_BACKGROUND_COLOR,
                SELECTED_TEXT_COLOR,
                UNSELECTED_TEXT_COLOR
            )

            // 버튼 간 간격 추가
            add(allOrdersButton)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(deliveryButton)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(takeoutButton)

            // 버튼 선택 로직
            fun setSelectedButton(button: JButton) {
                selectedButton?.let {
                    // 이전 선택된 버튼을 기본 색상으로 변경
                    it.background = UNSELECTED_BACKGROUND_COLOR
                    it.foreground = UNSELECTED_TEXT_COLOR
                    it.repaint() // 배경 다시 그리기
                }
                // 현재 선택된 버튼을 선택된 색상으로 변경
                button.background = SELECTED_BACKGROUND_COLOR
                button.foreground = SELECTED_TEXT_COLOR
                button.repaint() // 배경 다시 그리기
                selectedButton = button
            }

            // 버튼에 클릭 리스너 추가
            allOrdersButton.addActionListener {
                setSelectedButton(allOrdersButton)
                tabbedPane.PendingshowAllOrders()
            }
            deliveryButton.addActionListener {
                setSelectedButton(deliveryButton)
                tabbedPane.PendingshowFilteredOrders("DELIVERY")
            }
            takeoutButton.addActionListener {
                setSelectedButton(takeoutButton)
                tabbedPane.PendingshowFilteredOrders("TAKEOUT")
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
        tabbedPane.PendingshowAllOrders()
    }
}

