package org.grr.screen.main.main_widget.dialog.TimeSelectDialog

import OrderController
import org.grr.command.AcceptOrderCommand
import org.grr.enum.OrderReceiveType
import org.grr.model.ReceiveOrderModel
import org.grr.model.SettingModel
import org.grr.`object`.OverlayManager
import javax.swing.*


class CookTimeDialog(
    private val parent: JFrame,
    private val cardPanel: JPanel,
    private val order: ReceiveOrderModel,
    private val orderController: OrderController,
    private val overlayManager: OverlayManager,
    private val takeType: String,
) : BaseTimeSelectionDialog(
    parent,
    cardPanel,
    "예상 시간 선택",
    defaultTime = 30,
    iconPath = "/cook_icon.png",
    mainText = "예상 조리시간 선택",
    subText = "조리 예상시간을 설정해주세요"
) {
    override fun onSubmit(selectedTime: Int) {
        dispose()
        if(takeType == OrderReceiveType.TAKEOUT.name){
            ///포장 주문 접수 API 호출 (요리시간만 보내면댐)
            AcceptOrderCommand(parent, cardPanel, order, orderController, takeType,selectedTime ).execute() // 주문 상태 변경
            handleCloseButtonAction() //오버레이 닫기
            return;
        }
        //배달일 경우
        if(SettingModel.deliveryTimeControl){
            order.deliveryTime = SettingModel.deliveryTime
            AcceptOrderCommand(parent, cardPanel, order, orderController,cookTime = selectedTime, takeType = "delivery").execute() // 주문 상태 변경
            handleCloseButtonAction()
        }else{
            DeliveryTimeDialog(parent, cardPanel, order, orderController , selectedTime ,overlayManager).isVisible = true
        }
    }

    //오버레이 닫기
    override fun handleCloseButtonAction() {
        overlayManager.removeOverlayPanel()
        dispose()
    }
}
