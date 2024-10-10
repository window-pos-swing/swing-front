package org.example.screen.main.main_widget.tab_manager.pandding_sub_tabs

import org.example.CustomTabbedPane
import org.example.style.MyColor
import org.example.view.states.PendingState
import org.example.widgets.SelectButtonRoundedBorder
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

// PendingSubTabs 클래스에서 버튼 생성 및 관리

class PendingSubTabs(private val tabbedPane: CustomTabbedPane) : JPanel() {

    // 현재 선택된 버튼을 저장할 변수
    var selectedButton: SelectButtonRoundedBorder? = null

    // 상태별 주문 개수를 저장할 변수
    private var totalCount = 0
    private var deliveryCount = 0
    private var takeoutCount = 0

    // 버튼들을 클래스 멤버 변수로 선언
    val allOrdersButton = SelectButtonRoundedBorder(50)
    private val deliveryButton = SelectButtonRoundedBorder(50)
    private val takeoutButton = SelectButtonRoundedBorder(50)

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
            allOrdersButton.apply {
                createRoundedButton(
                    "전체보기",
                    MyColor.SELECTED_BACKGROUND_COLOR,
                    MyColor.UNSELECTED_BACKGROUND_COLOR,
                    MyColor.SELECTED_TEXT_COLOR,
                    MyColor.GREY600,
                    Dimension(230, 60)
                )
            }
            deliveryButton.apply {
                createRoundedButton(
                    "배달",
                    MyColor.SELECTED_BACKGROUND_COLOR,
                    MyColor.UNSELECTED_BACKGROUND_COLOR,
                    MyColor.SELECTED_TEXT_COLOR,
                    MyColor.GREY600,
                    Dimension(230, 60)
                )
            }
            takeoutButton.apply {
                createRoundedButton(
                    "포장",
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


            // 버튼에 클릭 리스너 추가
            allOrdersButton.button.addActionListener {
                selectButton(allOrdersButton)
                tabbedPane.filterPendingOrders()
            }
            deliveryButton.button.addActionListener {
                selectButton(deliveryButton)
                tabbedPane.filterPendingOrders("DELIVERY")
            }
            takeoutButton.button.addActionListener {
                selectButton(takeoutButton)
                tabbedPane.filterPendingOrders("TAKEOUT")
            }

            // 초기 선택된 버튼 설정 (전체보기)
            selectButton(allOrdersButton)
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
        PendingSubTabsUpdateCounts()
    }

    // 버튼 선택 로직 함수 이름 변경
    fun selectButton(button: SelectButtonRoundedBorder) {
        selectedButton?.setButtonStyle(false)  // 이전 선택된 버튼을 선택 해제 상태로 설정
        button.setButtonStyle(true)  // 현재 선택된 버튼을 선택 상태로 설정
        selectedButton = button
    }

    fun PendingSubTabsUpdateCounts() {
        // 모든 주문 중 현재 PendingState(접수대기) 상태인 것들만 필터링
        val pendingOrders = tabbedPane.getAllOrders().filter { it.state is PendingState }

        // 전체보기: 모든 접수대기 상태의 주문 개수
        totalCount = pendingOrders.size

        // 배달: 접수대기 상태 중 배달 타입인 주문 개수
        deliveryCount = pendingOrders.filter { it.orderType == "DELIVERY" }.size

        // 포장: 접수대기 상태 중 포장 타입인 주문 개수
        takeoutCount = pendingOrders.filter { it.orderType == "TAKEOUT" }.size

        // 버튼의 텍스트 업데이트
        allOrdersButton.button.text = "전체보기  $totalCount"
        deliveryButton.button.text = "배달  $deliveryCount"
        takeoutButton.button.text = "포장  $takeoutCount"
    }
}