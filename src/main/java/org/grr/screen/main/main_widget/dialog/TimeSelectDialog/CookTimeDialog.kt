package org.grr.screen.main.main_widget.dialog.TimeSelectDialog

import OrderController
import org.grr.model.Order
import org.grr.widgets.OverlayManager
import javax.swing.*


class CookTimeDialog(
    private val parent: JFrame,
    private val cardPanel: JPanel,
    private val order: Order,
    private val orderController: OrderController,
    private val overlayManager: OverlayManager
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
        DeliveryTimeDialog(parent, cardPanel, order, orderController , selectedTime ,overlayManager).isVisible = true
    }

    override fun handleCloseButtonAction() {
        overlayManager.removeOverlayPanel()
        dispose()
    }
}
