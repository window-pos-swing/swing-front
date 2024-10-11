package org.example.screen.main.main_widget.tab_manager.rejected_sub_tabs

import org.example.CustomTabbedPane
import org.example.command.RejectedReasonType
import org.example.style.MyColor
import org.example.view.states.PendingState
import org.example.view.states.RejectedState
import org.example.widgets.SelectButtonRoundedBorder
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

class RejectedSubTabs(private val tabbedPane: CustomTabbedPane) : JPanel() {

    // 현재 선택된 버튼을 저장할 변수
    private var selectedButton: SelectButtonRoundedBorder? = null

    // 상태별 주문 개수를 저장할 변수
    private var totalCount = 0
    private var customerCancelCount = 0
    private var storeRejectCount = 0
    private var storeCancelCount = 0

    // 버튼들을 클래스 멤버 변수로 선언
    val allRejectedButton = SelectButtonRoundedBorder(50)
    private val customerCancelButton = SelectButtonRoundedBorder(50)
    private val storeRejectButton = SelectButtonRoundedBorder(50)
    private val storeCancelButton = SelectButtonRoundedBorder(50)


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
            allRejectedButton.apply {
                createRoundedButton(
                    "거절전체",
                    MyColor.DARK_RED, //선택 버튼 배경 색상
                    MyColor.UNSELECTED_BACKGROUND_COLOR,
                    MyColor.SELECTED_TEXT_COLOR,
                    MyColor.GREY600,
                    Dimension(160, 60)
                )
            }
            customerCancelButton.apply {
                createRoundedButton(
                    "고객취소",
                    MyColor.DARK_RED,//선택 버튼 배경 색상
                    MyColor.UNSELECTED_BACKGROUND_COLOR,
                    MyColor.SELECTED_TEXT_COLOR,
                    MyColor.GREY600,
                    Dimension(160, 60)
                )
            }
            storeRejectButton.apply {
                createRoundedButton(
                    "가게거절",
                    MyColor.DARK_RED,//선택 버튼 배경 색상
                    MyColor.UNSELECTED_BACKGROUND_COLOR,
                    MyColor.SELECTED_TEXT_COLOR,
                    MyColor.GREY600,
                    Dimension(160, 60)
                )
            }
            storeCancelButton.apply {
                createRoundedButton(
                    "가게취소",
                    MyColor.DARK_RED,//선택 버튼 배경 색상
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

            // 버튼에 클릭 리스너 추가
            allRejectedButton.button.addActionListener {
                selectButton(allRejectedButton)
                tabbedPane.filterRejectedOrders()  // 거절 전체 보기 호출
            }
            customerCancelButton.button.addActionListener {
                selectButton(customerCancelButton)
                tabbedPane.filterRejectedOrders(RejectedReasonType.CUSTOMER_CANCEL)  // 고객 취소만 필터링
            }
            storeRejectButton.button.addActionListener {
                selectButton(storeRejectButton)
                tabbedPane.filterRejectedOrders(RejectedReasonType.STORE_REJECT)  // 가게 거절만 필터링
            }
            storeCancelButton.button.addActionListener {
                selectButton(storeCancelButton)
                tabbedPane.filterRejectedOrders(RejectedReasonType.STORE_CANCEL)  // 가게 취소만 필터링
            }

            // 초기 선택된 버튼 설정 (거절 전체보기)
            selectButton(allRejectedButton)
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
        RejectedSubTabsUpdateCounts()
    }

    fun selectButton(button: SelectButtonRoundedBorder) {
        selectedButton?.setButtonStyle(false)  // 이전 선택된 버튼을 선택 해제 상태로 설정
        button.setButtonStyle(true)  // 현재 선택된 버튼을 선택 상태로 설정
        selectedButton = button
    }

    fun RejectedSubTabsUpdateCounts() {
        // 모든 주문 중 현재 PendingState(접수대기) 상태인 것들만 필터링
        val RejectedOrders = tabbedPane.getAllOrders().filter { it.state is RejectedState }

        // 전체보기: 모든 접수대기 상태의 주문 개수
        totalCount = RejectedOrders.size

        // 배달: 접수대기 상태 중 배달 타입인 주문 개수
        customerCancelCount = RejectedOrders.filter {
            (it.state as RejectedState).rejectType == RejectedReasonType.CUSTOMER_CANCEL
        }.size

        // 포장: 접수대기 상태 중 포장 타입인 주문 개수
        storeRejectCount = RejectedOrders.filter {
            (it.state as RejectedState).rejectType == RejectedReasonType.STORE_REJECT
        }.size

        storeCancelCount = RejectedOrders.filter {
            (it.state as RejectedState).rejectType == RejectedReasonType.STORE_CANCEL
        }.size

        // 버튼의 텍스트 업데이트
        allRejectedButton.button.text = "거절전체  $totalCount"
        customerCancelButton.button.text = "고객취소  $customerCancelCount"
        storeRejectButton.button.text = "가게거절  $storeRejectCount"
        storeCancelButton.button.text = "가게취소  $storeCancelCount"
    }
}