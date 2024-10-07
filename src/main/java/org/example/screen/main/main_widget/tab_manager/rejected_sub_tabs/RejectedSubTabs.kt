package org.example.screen.main.main_widget.tab_manager.rejected_sub_tabs

import org.example.CustomTabbedPane
import org.example.style.MyColor
import org.example.widgets.SelectButtonRoundedBorder
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

class RejectedSubTabs(private val tabbedPane: CustomTabbedPane) : JPanel() {

    // 현재 선택된 버튼을 저장할 변수
    private var selectedButton: SelectButtonRoundedBorder? = null

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE

        val buttonPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT

            preferredSize = Dimension(900, 100)
            maximumSize = Dimension(900, 100)
            minimumSize = Dimension(900, 100)
            background = Color.WHITE

            // SelectButtonRoundedBorder 사용하여 버튼 생성
            val allRejectedButton = SelectButtonRoundedBorder(30).apply {
                createRoundedButton(
                    "거절전체",
                    MyColor.SELECTED_BACKGROUND_COLOR,
                    MyColor.UNSELECTED_BACKGROUND_COLOR,
                    MyColor.SELECTED_TEXT_COLOR,
                    MyColor.GREY600,
                    Dimension(160, 60)
                )
            }
            val customerCancelButton = SelectButtonRoundedBorder(30).apply {
                createRoundedButton(
                    "고객취소",
                    MyColor.SELECTED_BACKGROUND_COLOR,
                    MyColor.UNSELECTED_BACKGROUND_COLOR,
                    MyColor.SELECTED_TEXT_COLOR,
                    MyColor.GREY600,
                    Dimension(160, 60)
                )
            }
            val storeRejectButton = SelectButtonRoundedBorder(30).apply {
                createRoundedButton(
                    "가게거절",
                    MyColor.SELECTED_BACKGROUND_COLOR,
                    MyColor.UNSELECTED_BACKGROUND_COLOR,
                    MyColor.SELECTED_TEXT_COLOR,
                    MyColor.GREY600,
                    Dimension(160, 60)
                )
            }
            val storeCancelButton = SelectButtonRoundedBorder(30).apply {
                createRoundedButton(
                    "가게취소",
                    MyColor.SELECTED_BACKGROUND_COLOR,
                    MyColor.UNSELECTED_BACKGROUND_COLOR,
                    MyColor.SELECTED_TEXT_COLOR,
                    MyColor.GREY600,
                    Dimension(160, 60)
                )
            }

            // 버튼 간 간격 추가
            add(allRejectedButton.button)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(customerCancelButton.button)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(storeRejectButton.button)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(storeCancelButton.button)

            // 버튼 선택 로직
            fun setSelectedButton(button: SelectButtonRoundedBorder) {
                selectedButton?.setButtonStyle(false)  // 이전 선택된 버튼을 선택 해제 상태로 설정
                button.setButtonStyle(true)  // 현재 선택된 버튼을 선택 상태로 설정
                selectedButton = button
                // 필터 상태 변경 후 타이머 동작을 위한 필터 상태 확인
            }

            // 버튼에 클릭 리스너 추가
            allRejectedButton.button.addActionListener {
                setSelectedButton(allRejectedButton)
                println("Subtab Filter Changed: 거절전체")
                tabbedPane.filterRejectedOrders()  // 거절 전체 보기 호출
            }
            customerCancelButton.button.addActionListener {
                setSelectedButton(customerCancelButton)
                println("Subtab Filter Changed: 고객취소")
                tabbedPane.filterRejectedOrders("CUSTOMER_CANCEL")  // 고객 취소만 필터링
            }
            storeRejectButton.button.addActionListener {
                setSelectedButton(storeRejectButton)
                println("Subtab Filter Changed: 가게거절")
                tabbedPane.filterRejectedOrders("STORE_REJECT")  // 가게 거절만 필터링
            }
            storeCancelButton.button.addActionListener {
                setSelectedButton(storeCancelButton)
                println("Subtab Filter Changed: 가게취소")
                tabbedPane.filterRejectedOrders("STORE_CANCEL")  // 가게 취소만 필터링
            }

            // 초기 선택된 버튼 설정 (거절 전체보기)
            setSelectedButton(allRejectedButton)
        }

        add(buttonPanel)

        // 중복 생성 방지 로직 추가
        tabbedPane.rejectedOrdersPanel.removeAll()  // 기존 패널 초기화
        add(JScrollPane(tabbedPane.rejectedOrdersPanel).apply {
            border = BorderFactory.createEmptyBorder(0, 20, 20, 20)
            viewportBorder = null
            isOpaque = false
            viewport.isOpaque = false
        })

        // 기본 선택: 거절 전체보기
        tabbedPane.filterRejectedOrders()
    }
}