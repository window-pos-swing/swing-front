package org.grr.screen.main.main_widget.dialog.TimeSelectDialog

import OrderController
import org.grr.command.AcceptOrderCommand

import org.grr.model.Order
import org.grr.widgets.OverlayManager
import javax.swing.JFrame
import javax.swing.JPanel

class DeliveryTimeDialog(
    private val parent: JFrame,
    private val cardPanel: JPanel,
    private val order: Order,
    private val orderController: OrderController,
    private val cookTime: Int, // 이전 다이얼로그에서 전달받은 조리 시간
    private val overlayManager: OverlayManager
) : BaseTimeSelectionDialog(
    parent,
    cardPanel,
    "예상 시간 선택",
    defaultTime = 30,
    iconPath = "/delivery_icon.png",
    mainText = "배달 예상시간 선택",
    subText = "배달 도착 예상시간을 설정해주세요"
) {
    override fun onSubmit(selectedTime: Int) {
        order.deliveryTime = selectedTime
        order.cookTime = cookTime
        // 설정된 쿡타임과 배달시간을 출력
        println("[DeliveryTimeDialog] 설정된 조리 시간: ${order.cookTime}분")
        println("[DeliveryTimeDialog] 설정된 배달 시간: ${order.deliveryTime}분")
        overlayManager.removeOverlayPanel() 
        dispose() // 다이얼로그 닫기
        AcceptOrderCommand(parent, cardPanel, order, orderController).execute() // 주문 상태 변경
    }
}