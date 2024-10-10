package org.example.screen.main.main_widget.tab_manager.processing_sub_tabs

import org.example.CustomTabbedPane
import org.example.style.MyColor
import org.example.view.states.PendingState
import org.example.view.states.ProcessingState
import org.example.widgets.SelectButtonRoundedBorder
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

class ProcessingSubTabs(private val tabbedPane: CustomTabbedPane) : JPanel() {

    // 현재 선택된 버튼을 저장할 변수
    private var selectedButton: SelectButtonRoundedBorder? = null

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
                println("Subtab Filter Changed: 전체보기")
                tabbedPane.filterProcessingOrders()  // 전체보기 호출
            }
            deliveryButton.button.addActionListener {
                selectButton(deliveryButton)
                println("Subtab Filter Changed: 배달")
                tabbedPane.filterProcessingOrders("DELIVERY")  // 배달 주문만 필터링
            }
            takeoutButton.button.addActionListener {
                selectButton(takeoutButton)
                println("Subtab Filter Changed: 포장")
                tabbedPane.filterProcessingOrders("TAKEOUT")  // 포장 주문만 필터링
            }

            // 초기 선택된 버튼 설정 (전체보기)
            selectButton(allOrdersButton)
        }

        add(buttonPanel)

        // 중복 생성 방지 로직 추가
        tabbedPane.processingOrdersPanel.removeAll()  // 기존 패널 초기화
        add(JScrollPane(tabbedPane.processingOrdersPanel).apply {
            border = BorderFactory.createEmptyBorder(0, 20, 20, 20)
            viewportBorder = null
            isOpaque = false
            viewport.isOpaque = false
        })

        // 기본 선택: 전체보기
        tabbedPane.filterProcessingOrders()
        ProcessingSubTabsUpdateCounts()
    }

    fun selectButton(button: SelectButtonRoundedBorder) {
        selectedButton?.setButtonStyle(false)  // 이전 선택된 버튼을 선택 해제 상태로 설정
        button.setButtonStyle(true)  // 현재 선택된 버튼을 선택 상태로 설정
        selectedButton = button
    }

    fun ProcessingSubTabsUpdateCounts() {
        println("[Pendding Sub Tab] updateCounts")
        // 모든 주문 중 현재 PendingState(접수대기) 상태인 것들만 필터링
        val ProcessingOrders = tabbedPane.getAllOrders().filter { it.state is ProcessingState }

        // 전체보기: 모든 접수대기 상태의 주문 개수
        totalCount = ProcessingOrders.size

        // 배달: 접수대기 상태 중 배달 타입인 주문 개수
        deliveryCount = ProcessingOrders.filter { it.orderType == "DELIVERY" }.size

        // 포장: 접수대기 상태 중 포장 타입인 주문 개수
        takeoutCount = ProcessingOrders.filter { it.orderType == "TAKEOUT" }.size

        // 버튼의 텍스트 업데이트
        allOrdersButton.button.text = "전체보기  $totalCount"
        deliveryButton.button.text = "배달  $deliveryCount"
        takeoutButton.button.text = "포장  $takeoutCount"
    }
}
